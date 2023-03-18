package de.riskident.models

import scala.annotation.targetName

opaque type Time = Long

object Time:
  def apply(value: Long): Time = value
  extension (time: Time)
    def value: Long = time

    @targetName("plus")
    def +(other: Time): Time = time + other
