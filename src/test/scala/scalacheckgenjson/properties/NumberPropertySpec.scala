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
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json._
import scalacheckgenjson.config.NumberConfig

class NumberPropertySpec extends AnyFlatSpec with should.Matchers with ScalaCheckPropertyChecks with OptionValues {

  val numberType = JsObject(Map(typeProp("number")))

  def typeProp(value: String) = "type" -> JsString(value)
  def maxType(max: Long) = numberType + ("maximum" -> JsNumber(max))
  def minType(max: Long) = numberType + ("minimum" -> JsNumber(max))

  val extract = NumberProperty.gen(NumberConfig.default) _

  it should "skip when type not number" in {
    extract(JsObject(Map(typeProp("unknown")))) shouldBe None
  }

  it should "produce a number property" in {

    forAll(extract(numberType).value) { n =>
      n should be (a[JsNumber])
    }
  }

  it should "respect maximum property" in {

    forAll(chooseNum(1L, 9999L)) { i =>
      forAll(extract(maxType(i)).value) { n =>
        n.as[Long] should be <= i
      }
    }
  }

  it should "respect minimum property" in {

    forAll(chooseNum(1L, 9999L)) { i =>
      forAll(extract(minType(i)).value) { n =>
        n.as[Long] should be >= i
      }
    }
  }
}
