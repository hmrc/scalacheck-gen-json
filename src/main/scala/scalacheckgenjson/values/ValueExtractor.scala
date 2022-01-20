/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scalacheckgenjson.values

import play.api.libs.json.{JsObject, Reads}

sealed trait ValueExtractor[T] {

  val obj: JsObject
  val key: String

  def asOpt(implicit ev: Reads[T]): Option[T] =
    obj.value.get(key).flatMap(_.asOpt[T])

  def getOrDefault(default: T)(implicit ev: Reads[T]): T =
    asOpt.getOrElse(default)
}

case class Minimum(obj: JsObject) extends ValueExtractor[Long] {
  val key = "minimum"
}

case class Maximum(obj: JsObject) extends ValueExtractor[Long] {
  val key = "maximum"
}

case class MinLength(obj: JsObject) extends ValueExtractor[Int] {
  val key = "minLength"
}

case class MaxLength(obj: JsObject) extends ValueExtractor[Int] {
  val key = "maxLength"
}

case class Enum(obj: JsObject) extends ValueExtractor[Seq[String]] {
  val key = "enum"
}

case class Pattern(obj: JsObject) extends ValueExtractor[String] {
  val key = "pattern"
}

case class MinItems(obj: JsObject) extends ValueExtractor[Int] {
  val key = "minItems"
}

case class MaxItems(obj: JsObject) extends ValueExtractor[Int] {
  val key = "maxItems"
}

case class Items(obj: JsObject) extends ValueExtractor[JsObject] {
  val key = "items"
}

case class Ref(obj: JsObject) extends ValueExtractor[String] {
  val key = "$ref"
}

case class Type(obj: JsObject) extends ValueExtractor[String] {

  val key = "type"

  def is(s: String): Boolean = asOpt.contains(s)
}

case class Required(obj: JsObject) extends ValueExtractor[Seq[String]] {
  val key = "required"
}

case class Properties(obj: JsObject) extends ValueExtractor[JsObject] {
  val key = "properties"
}