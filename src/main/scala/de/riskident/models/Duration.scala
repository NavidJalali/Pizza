package de.riskident.models

import scala.annotation.targetName

opaque type Duration = Long

object Duration:
  def apply(value: Long): Duration = value

  extension (duration: Duration)
    def value: Long = duration

    @targetName("plus")
    infix def +(other: Duration): Duration = Duration(duration.value + other.value)
