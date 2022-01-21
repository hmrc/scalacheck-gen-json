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

import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json._

class BooleanPropertySpec extends AnyFlatSpec with should.Matchers with ScalaCheckPropertyChecks with OptionValues {

  def typeProp(value: String) = "type" -> JsString(value)
  val extract = BooleanProperty.gen _

  it should "skip when type not boolean" in {
    extract(JsObject(Map(typeProp("unknown")))) shouldBe None
  }

  it should "produce a boolean property" in {

    forAll(extract(JsObject(Map(typeProp("boolean")))).value) { b =>
      b should be (a[JsBoolean])
    }
  }
}
