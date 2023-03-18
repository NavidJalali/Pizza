package de.riskident.models

opaque type CookingDuration = Long

object CookingDuration:
  def apply(value: Long): CookingDuration = value
  def fromString(input: String): Option[CookingDuration] =
    input.toLongOption.map(CookingDuration.apply)
  extension (duration: CookingDuration) def value: Long = duration
