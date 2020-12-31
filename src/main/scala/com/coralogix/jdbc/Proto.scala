package com.coralogix.jdbc

import com.google.protobuf.struct.{ NullValue, Value }

object Proto {

  val `null`: Value = Value().withNullValue(NullValue.NULL_VALUE)

  def str(s: String): Value =
    Value().withStringValue(s)

  def bool(b: Boolean): Value =
    Value().withBoolValue(b)

  def num(i: Int): Value =
    Value().withNumberValue(i.toDouble)

}
