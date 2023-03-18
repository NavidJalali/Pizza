package de.riskident

import de.riskident.models._

import java.io.InputStream
import scala.annotation.tailrec
import scala.collection.mutable
import scala.io.Source
import scala.util.Using

object OrderProcessor:

  /**
   * Read the next line from the iterator and if it is empty wrap it into a
   * parsing error.
   */
  private def readNextLine(
    iter: Iterator[String]
  ): Either[ParsingError.UnexpectedEndOfStream.type, String] =
    iter.nextOption.toRight(ParsingError.UnexpectedEndOfStream)

  /**
   * Read the number of customers from the iterator and if it is empty or in an
   * unexpected format wrap it into a parsing error.
   */
  private def readCustomerCount(iter: Iterator[String]): Either[ParsingError, CustomerCount] =
    readNextLine(iter).flatMap(raw =>
      CustomerCount.fromString(raw).toRight(ParsingError.InvalidCustomerCount(raw))
    )

  /**
   * Read the next `customerCount` lines and try to parse them into Orders.
   * Short circuit on the first error
   */
  private def readOrdersUnordered(
    iter: Iterator[String],
    customerCount: CustomerCount
  ): Either[ParsingError, Seq[Order]] =
    def readOrder(iter: Iterator[String]): Either[ParsingError, Order] =
      readNextLine(iter).flatMap(raw =>
        Order.fromString(raw).toRight(ParsingError.InvalidOrderEntry(raw))
      )

    @tailrec
    def go(acc: List[Order], remaining: Int): Either[ParsingError, Seq[Order]] =
      if (remaining == 0) Right(acc)
      else
        readOrder(iter) match
          case Right(value) => go(value :: acc, remaining - 1)
          case Left(error)  => Left(error)

    go(Nil, customerCount.value)

  /**
   * Calculate the sum total of waiting times for all Orders using the
   * non-preemptive shortest job first algorithm.
   */
  private def totalWaitingDuration(sortedOrders: Seq[Order]): Duration =
    val enqueuedOrders: mutable.PriorityQueue[Order] =
      mutable.PriorityQueue.empty[Order](Ordering.by(_.cookingDuration.value * -1))

    def waitingDuration(order: Order, startCookingAt: Time): Duration =
      Duration(startCookingAt.value + order.cookingDuration.value - order.arrivalTime.value)

    def timeAfterCooking(order: Order, startCookingAt: Time): Time =
      Time(startCookingAt.value + order.cookingDuration.value)

    @tailrec
    def go(
      ordersNotPlaced: List[Order],
      now: Time,
      accumulatedWaitingDuration: Duration
    ): Duration =
      ordersNotPlaced match
        case Nil =>
          if (enqueuedOrders.isEmpty) accumulatedWaitingDuration
          else
            val nextOrder: Order = enqueuedOrders.dequeue()
            go(
              Nil,
              timeAfterCooking(nextOrder, now),
              accumulatedWaitingDuration + waitingDuration(nextOrder, now)
            )

        case nextOrder :: rest =>
          if (nextOrder.arrivalTime.value <= now.value)
            enqueuedOrders.enqueue(nextOrder)
            go(rest, now, accumulatedWaitingDuration)
          else if (enqueuedOrders.isEmpty)
            val fastForwardedTime: Time = Time(nextOrder.arrivalTime.value)
            go(
              rest,
              timeAfterCooking(nextOrder, fastForwardedTime),
              accumulatedWaitingDuration + waitingDuration(nextOrder, fastForwardedTime)
            )
          else
            val nextShortestOrder: Order = enqueuedOrders.dequeue
            go(
              ordersNotPlaced,
              timeAfterCooking(nextShortestOrder, now),
              accumulatedWaitingDuration + waitingDuration(nextShortestOrder, now)
            )

    go(sortedOrders.toList, now = Time(0), accumulatedWaitingDuration = Duration(0))

  /**
   * @param in
   *   An InputStream, which contains the following input: A line containing a
   *   single number: The number of guests N, Followed by N lines containing two
   *   numbers Ti and Li separated by space. There may be a trailing newline. Ti
   *   ist the ordering time for Ni, Li is the time it takes to bake Ni's pizza.
   *   0 <= N <= 100000 0 <= Ti <= 1000000000 1 <= Li <= 1000000000
   * @return
   *   A Right containing the integer part of the average waiting time if the
   *   input is valid. A Left containing a syntax error description otherwise.
   */
  def process(in: InputStream): Either[String, Long] =
    Using.resource(Source.fromInputStream(in)) { source =>
      val lines: Iterator[String] = source.getLines

      val averageWaitingTime: Either[ParsingError, Long] = for
        customerCount   <- readCustomerCount(lines)
        ordersUnordered <- readOrdersUnordered(lines, customerCount)
        orders           = ordersUnordered.sortBy(_.arrivalTime.value)
        waitingTime      = totalWaitingDuration(orders)
      yield if (customerCount.value == 0) 0L else waitingTime.value / customerCount.value

      averageWaitingTime.left.map(_.toString)
    }
