package de.riskident.models

final case class Order(arrivalTime: ArrivalTime, cookingDuration: CookingDuration)

object Order:
  def fromString(input: String): Option[Order] =
    input.split(" ").map(_.trim) match
      case Array(arrivalTime, cookingDuration) =>
        for
          arrivalTime     <- ArrivalTime.fromString(arrivalTime)
          cookingDuration <- CookingDuration.fromString(cookingDuration)
        yield Order(arrivalTime, cookingDuration)
      case _ => None
