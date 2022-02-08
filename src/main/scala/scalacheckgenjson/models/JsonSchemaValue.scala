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

package scalacheckgenjson.models

import play.api.libs.json.JsObject
import scalacheckgenjson.config.Config
import scalacheckgenjson.schema.Schema
import scalacheckgenjson.values._

sealed trait JsonSchemaValue

case class JsonSchemaArray(items: JsonSchemaValue, minItems: Int, maxItems: Int) extends JsonSchemaValue

case object JsonSchemaBoolean extends JsonSchemaValue

case class JsonSchemaNumber(minimum: Long, maximum: Long) extends JsonSchemaValue

case class JsonSchemaObject(required: List[String], properties: Map[String, JsonSchemaValue]) extends JsonSchemaValue

case class JsonSchemaString(minLength: Int, maxLength: Int, enum: List[String], pattern: Option[String]) extends JsonSchemaValue

object JsonSchemaValue {

  def make(obj: JsObject)(implicit config: Config, schema: Schema): JsonSchemaValue = {

    obj match {
      case IsRef(ref)        => schema.find(ref).map(make).getOrElse(throw new Exception(s"Cannot find ref: $ref"))
      case IsType("array")   =>
        JsonSchemaArray(Items(obj).asOpt.map(make).getOrElse(throw new Exception("Items is required for array")), MinItems(obj).getOrDefault(config.arrayConfig.minItems), MaxItems(obj).getOrDefault(config.arrayConfig.maxItems))
      case IsType("boolean") => JsonSchemaBoolean
      case IsType("number")  => JsonSchemaNumber(Minimum(obj).getOrDefault(config.numberConfig.minimum), Maximum(obj).getOrDefault(config.numberConfig.maximum))
      case IsType("object")  => JsonSchemaObject(Required(obj).getOrDefault(List()), Properties(obj).values.mapValues(make))
      case IsType("string")  =>
        JsonSchemaString(MinLength(obj).getOrDefault(config.stringConfig.minLength), MaxLength(obj).getOrDefault(config.stringConfig.maxLength), Enum(obj).getOrDefault(List()), Pattern(obj).asOpt)
      case _                 => throw new RuntimeException("Unknown schema type")
    }
  }

  object IsRef {
    def unapply(obj: JsObject): Option[String] = Ref(obj).asOpt
  }

  object IsType {
    def unapply(obj: JsObject): Option[String] = Type(obj).asOpt
  }
}