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

package scalacheckgenjson.properties

import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import scalacheckgenjson.config.Config
import scalacheckgenjson.schema.Schema


class RefPropertySpec extends AnyFlatSpec with should.Matchers with ScalaCheckPropertyChecks with OptionValues {

  val schema = Json.parse(
    """{
      | "defs": {
      |   "description": {
      |     "type": "string",
      |     "minLength": 1,
      |     "maxLength": 50
      |   },
      |   "person": {
      |     "type": "object",
      |     "properties": {
      |       "age": {
      |         "type": "number",
      |         "minimum": 18,
      |         "maximum": 99
      |       }
      |     }
      |   }
      | }
      |}""".stripMargin).as[JsObject]

  val generators = new Generators(Schema(schema), Config.default)
  val extract = RefProperty.gen(Schema(schema), generators) _

  def refProperty(path: String) = JsObject(Map("$ref" -> JsString(path)))

  it should "skip when no $ref property exists" in {
    extract(JsObject(Map[String, JsValue]())) shouldBe None
  }

  it should "use a string definition" in {
    forAll(extract(refProperty("#/defs/description")).value) { s =>
      s.as[String].length should be >=1
      s.as[String].length should be <=50
    }
  }

  it should "use an object definition" in {
    forAll(extract(refProperty("#/defs/person")).value) { p =>
      val age = (p.as[JsObject] \ "age").toOption.map(_.as[Int])
      age.nonEmpty shouldBe true
      age.map(_ should be >= 18)
      age.map(_ should be <= 99)
    }
  }
}