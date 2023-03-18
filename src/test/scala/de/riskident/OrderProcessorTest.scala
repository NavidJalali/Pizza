package de.riskident

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.StandardCharsets
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

class OrderProcessorTest extends AnyFunSuite with Matchers with TypeCheckedTripleEquals {
  test("calculates the correct average time") {
    val input =
      """3
        |0 3
        |1 9
        |2 6
        |""".stripMargin
    val inStream: InputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    OrderProcessor.process(inStream) must ===(Right(9L))
  }

  test("works on unsorted input") {
    val input =
      """3
        |2 6
        |0 3
        |1 9
        |""".stripMargin
    val inStream: InputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    OrderProcessor.process(inStream) must ===(Right(9L))
  }

  test("works on time-shifted input") {
    val input =
      """3
        |10 3
        |12 6
        |11 9
        |""".stripMargin
    val inStream: InputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    OrderProcessor.process(inStream) must ===(Right(9L))
  }

  test("works with zero guests") {
    val input =
      """0
        |""".stripMargin
    val inStream: InputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    OrderProcessor.process(inStream) must ===(Right(0L))
  }

  test("does not read more input than specified") {
    val input =
      """3
        |0 3
        |1 9
        |2 6
        |this is not part of the input
        |""".stripMargin
    val inStream: InputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    OrderProcessor.process(inStream) must ===(Right(9L))
  }

  test("reports unexpected end of stream") {
    val input1 =
      """3
        |0 3
        |1 9
        |""".stripMargin
    val in1Stream: InputStream = new ByteArrayInputStream(input1.getBytes(StandardCharsets.UTF_8))

    val input2                 = ""
    val in2Stream: InputStream = new ByteArrayInputStream(input2.getBytes(StandardCharsets.UTF_8))
    OrderProcessor.process(in1Stream) must ===(Left("Unexpected end of stream"))
    OrderProcessor.process(in2Stream) must ===(Left("Unexpected end of stream"))
  }

  test("report invalid orders") {
    val input =
      """3
        |0 3
        |1 9 oh no this is broken
        |2 6
        |""".stripMargin
    val inStream: InputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    OrderProcessor.process(inStream) must ===(Left("Invalid order entry: 1 9 oh no this is broken"))
  }

  test("handles very large input with large cooking times") {
    val size        = 100_000
    val cookingTime = 1_000_000_000L
    val input =
      s"""$size
         |${(0 until size).map(i => s"0 $cookingTime").mkString("\n")}
         |""".stripMargin

    val expected = cookingTime * (size + 1) / 2

    val inStream: InputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    OrderProcessor.process(inStream) must ===(Right(expected))
  }

  test("reports unparsable customer count") {
    val input =
      """this is not a number
        |0 3
        |1 9
        |2 6
        |""".stripMargin
    val inStream: InputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    OrderProcessor.process(inStream) must ===(Left("Invalid customer count: this is not a number"))
  }
}
