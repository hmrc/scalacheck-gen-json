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

import org.scalacheck.Gen.chooseNum
import org.scalacheck.{Gen, Shrink}
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json._
import scalacheckgenjson.config.{ArrayConfig, Config}
import scalacheckgenjson.schema.Schema

class ArrayPropertySpec extends AnyFlatSpec with should.Matchers with ScalaCheckPropertyChecks with OptionValues {

  implicit val noStringShrink: Shrink[String] = Shrink.shrinkAny
  implicit val noIntShrink: Shrink[JsValue] = Shrink.shrinkAny

  def typeProp(value: String) = "type" -> JsString(value)
  val arrayType = JsObject(Map(typeProp("array")))
  val arrayItems = JsObject(Map("items" -> JsObject(Map("type" -> JsString("string")))))
  def minType(min: Int) = JsObject(Map("minItems" -> JsNumber(min)))
  def maxType(max: Int) = JsObject(Map("maxItems" -> JsNumber(max)))

  val generators = new Generators(Schema(JsObject.empty), Config.default)

  val rangeGen: Gen[(Int, Int)] =
    for {
      i <- chooseNum(1, 100)
      j <- chooseNum(1, 100)
    } yield {
      (Math.min(i, j), Math.max(i, j))
    }

  val extract = ArrayProperty.gen(generators, ArrayConfig.default) _

  it should "skip when type not array" in {
    extract(JsObject(Map(typeProp("unknown")))) shouldBe None
  }

  it should "produce an array property" in {

    forAll(extract(arrayType ++ arrayItems).value) { arr =>
      arr should be (a[JsArray])
    }
  }

  it should "create items of type string" in {

    forAll(extract(arrayType ++ arrayItems).value) { arr =>
      arr.as[List[String]].map(_ should be (a[String]))
    }
  }

  it should "create items of type boolean" in {

    val arrayItems = JsObject(Map("items" -> JsObject(Map("type" -> JsString("boolean")))))

    forAll(extract(arrayType ++ arrayItems).value) { arr =>
      arr.as[List[Boolean]].map(_ should be (a[java.lang.Boolean]))
    }
  }

  it should "respect the minItems property" in {

    forAll(chooseNum(1, 100)) { i =>
      forAll(extract(arrayType ++ arrayItems ++ minType(i)).value) { arr =>

        arr.as[List[String]].length should be >= i
      }
    }
  }

  it should "respect the maxItems property" in {

    forAll(chooseNum(1, 100)) { case i =>
      forAll(extract(arrayType ++ arrayItems ++ maxType(i)).value) { arr =>

        arr.as[List[String]].length should be <= i
      }
    }
  }

  it should "respect both minItems and maxItems property" in {

    forAll(rangeGen) { case (i, j) =>
      forAll(extract(arrayType ++ arrayItems ++ minType(i) ++ maxType(j)).value) { arr =>

        arr.as[List[String]].length should be >= i
        arr.as[List[String]].length should be <= j
      }
    }
  }
}