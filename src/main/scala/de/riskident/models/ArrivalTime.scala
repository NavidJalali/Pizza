package de.riskident.models

opaque type ArrivalTime = Long

object ArrivalTime:
  def apply(value: Long): ArrivalTime                = value
  def fromString(input: String): Option[ArrivalTime] = input.toLongOption.map(ArrivalTime.apply)
  extension (time: ArrivalTime) def value: Long      = time
