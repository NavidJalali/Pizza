package de.riskident.models

opaque type CustomerCount = Int

object CustomerCount:
  def apply(value: Int): CustomerCount = value
  def fromString(input: String): Option[CustomerCount] =
    input.toIntOption.filter(_ >= 0).map(apply)
  extension (count: CustomerCount) def value: Int = count
