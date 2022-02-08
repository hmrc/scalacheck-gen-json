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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsObject, JsString, JsValue}
import scalacheckgenjson.config.Config
import scalacheckgenjson.schema.Schema


class JsonSchemaValueSpec extends AnyFlatSpec with should.Matchers {

  implicit val defaultConfig = Config.default
  implicit val defaultSchema = Schema(JsObject.empty)

  val defaultString = JsonSchemaString(defaultConfig.stringConfig.minLength, defaultConfig.stringConfig.maxLength, List(), None)
  val defaultBoolean = JsonSchemaBoolean
  val defaultNumber = JsonSchemaNumber(defaultConfig.numberConfig.minimum, defaultConfig.numberConfig.maximum)
  val defaultObject = JsonSchemaObject(List(), Map())
  def defaultArray(value: JsonSchemaValue) = JsonSchemaArray(value, defaultConfig.arrayConfig.minItems, defaultConfig.arrayConfig.maxItems)

  def jsObject(name: String, value: JsValue): JsObject = JsObject(Map(name -> value))
  def jsObject(name: String, value: String): JsObject = jsObject(name, JsString(value))
  def jsObject(typeName: String): JsObject = jsObject("type", JsString(typeName))

  it should "fail when the type is not known" in {

    intercept[RuntimeException] {
      JsonSchemaValue.make(jsObject("unknown"))
    }
  }

  it should "return resolve the ref when passed a ref object" in {
    val obj = jsObject("$ref", "/person")
    val schema = jsObject("person", jsObject("boolean"))

    JsonSchemaValue.make(obj)(Config.default, Schema(schema)) shouldBe defaultBoolean
  }

  List(
    ("array", defaultArray(defaultBoolean), List(jsObject("items", jsObject("boolean")))),
    ("boolean", defaultBoolean, List()),
    ("number", defaultNumber, List()),
    ("object", defaultObject, List()),
    ("string", defaultString, List())
  ).foreach {
    case (typeName, expectedResult, additionalProps) =>

      it should s"return a valid value when passed an '${typeName}' object" in {
        val obj = additionalProps.foldLeft(jsObject(typeName))(_++_)
        JsonSchemaValue.make(obj) shouldBe expectedResult
      }
  }
}