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

import org.scalacheck.{Gen, Shrink}
import org.scalacheck.Gen.{alphaNumStr, chooseNum, listOf}
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json._
import scalacheckgenjson.config.Config
import scalacheckgenjson.schema.Schema


class JsonSchemaGeneratorSpec extends AnyFlatSpec with should.Matchers with ScalaCheckPropertyChecks with OptionValues {

  implicit val config = Config.default
  implicit val noShrinkInt: Shrink[Int] = Shrink.shrinkAny[Int]

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
      |     },
      |     "required": ["age"]
      |   }
      | }
      |}""".stripMargin).as[JsObject]

  def jsObject(name: String, value: JsValue): JsObject = jsObject(name -> value)
  def jsObject(tup: (String, JsValue)*): JsObject = JsObject(tup.toMap)
  def jsObject(name: String, value: String): JsObject = jsObject(name -> JsString(value))
  def jsObject(typeName: String): JsObject = jsObject("type" -> JsString(typeName))

  def rangeGen: Gen[(Int, Int)] =
    for {
      i <- chooseNum(1, 100)
      j <- chooseNum(1, 100)
    } yield {
      (Math.min(i, j), Math.max(i, j))
    }

  def generate(jsObject: JsObject) =
    JsonSchemaGenerator.generate(JsonSchemaValue.make(jsObject)(implicitly, Schema(schema)))

  it should "produce a boolean property" in {

    forAll(generate(jsObject("boolean"))) { b =>
      List(true, false) should contain(b.as[Boolean])
    }
  }

  it should "respect minimum and maximum property value for numbers" in {

    forAll(rangeGen) { case(i, j) =>

      val min = "minimum" -> JsNumber(i)
      val max = "maximum" -> JsNumber(j)

      forAll(generate(jsObject("number") + max + min)) { n =>
        n.as[Int] should be >= i
        n.as[Int] should be <= j
      }
    }
  }

  it should "use a string definition for a $ref property" in {
    forAll(generate(jsObject("$ref", "#/defs/description"))) { s =>
      s.as[String].length should be >=1
      s.as[String].length should be <=50
    }
  }

  it should "use an object definition for a $ref property" in {
    forAll(generate(jsObject("$ref", "#/defs/person"))) { p =>
      val age = (p.as[JsObject] \ "age").toOption.map(_.as[Int])
      age.nonEmpty shouldBe true
      age.map(_ should be >= 18)
      age.map(_ should be <= 99)
    }
  }

  it should "create string items in an array" in {

    val arrayItems = "items" -> jsObject("string")

    forAll(generate(jsObject("array") + arrayItems)) { arr =>
      arr.as[List[String]].map(_ shouldBe a[String])
    }
  }

  it should "create boolean items in an array" in {

    val arrayItems = "items" -> jsObject("boolean")

    forAll(generate(jsObject("array") + arrayItems)) { arr =>
      arr.as[List[Boolean]].map(b => List(true, false) should contain(b))
    }
  }

  it should "respect both minItems and maxItems property for arrays" in {

    val arrayItems = "items" -> jsObject("boolean")

    forAll(rangeGen) { case (i, j) =>

      val minItems = "minItems" -> JsNumber(i)
      val maxItems = "maxItems" -> JsNumber(j)

      forAll(generate(jsObject("array") + arrayItems + minItems + maxItems)) { arr =>

        arr.as[List[Boolean]].length should be >= i
        arr.as[List[Boolean]].length should be <= j
      }
    }
  }

  it should "create string and boolean items for objects" in {

    val booleanProp = "b" -> jsObject("boolean")
    val stringProp = "s" -> jsObject("string")
    val properties = "properties" -> jsObject(booleanProp, stringProp)

    forAll(generate(jsObject("object") + properties)) { obj =>
      obj.asInstanceOf[JsObject].value.get("b").map(_ should be (a[JsBoolean]))
      obj.asInstanceOf[JsObject].value.get("s").map(_ should be (a[JsString]))
    }
  }

  it should "respect the required property for objects" in {

    val requiredProp = "required" -> JsArray(List(JsString("b")))
    val booleanProp = "b" -> jsObject("boolean")
    val stringProp = "s" -> jsObject("string")
    val properties = "properties" -> jsObject(booleanProp, stringProp)

    forAll(generate(jsObject("object") + properties + requiredProp)) { obj =>
      obj.asInstanceOf[JsObject].value.get("b").value should be (a[JsBoolean])
      obj.asInstanceOf[JsObject].value.get("s").map(_ should be (a[JsString]))
    }
  }

  it should "respect minLength and maxLength properties for strings" in {

    forAll(rangeGen) { case (i, j) =>

      val min = "minLength" -> JsNumber(i)
      val max = "maxLength" -> JsNumber(j)

      forAll(generate(jsObject("string") + min + max)) { s =>
        s.as[String] should fullyMatch regex "^.{" + i + "," + j +"}$"
      }
    }
  }

  it should "respect the enum property for strings" in {

    forAll(listOf(alphaNumStr).suchThat(_.nonEmpty)) { xs =>
      val enum = "enum" -> JsArray(xs.map(JsString))

      forAll(generate(jsObject("string") + `enum`)) { s =>
        xs should contain (s.as[String])
      }
    }
  }

  it should "respect the pattern property on strings" in {

    val r       = "^[A-Z]{2}[a-z]{2}$"
    val pattern = "pattern" -> JsString(r)

    forAll(generate(jsObject("string") + pattern)) { s =>
      s.as[String] should fullyMatch regex r
    }
  }
}
