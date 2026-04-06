package bench

import org.scalameter.{KeyValue, KeyWithDefaultValue}

object ScalameterImplicits {
  implicit def toKeyValue[A](tuple: (KeyWithDefaultValue[A], A)): KeyValue.OfType[A] = KeyValue(tuple)
}
