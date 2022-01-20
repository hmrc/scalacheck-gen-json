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

package scalacheckgenjson.schema

import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsObject, Json}


class LocatorSpec extends AnyFlatSpec with should.Matchers with ScalaCheckPropertyChecks with OptionValues {

  val json = Json.parse(
    """{
      | "person": {
      |   "address": {
      |     "country": {
      |       "name": "UK"
      |     }
      |   },
      |   "age": 36
      | }
      |}""".stripMargin).as[JsObject]

  it should "return toplevel object for path" in {
    Locator.find(json, "#/person") shouldBe (json \ "person").toOption
  }

  it should "return a nested object for path" in {
    Locator.find(json, "#/person/address/country") shouldBe (json \ "person" \ "address" \ "country").toOption
  }

  it should "return non when object not found" in {
    Locator.find(json, "#/person/siblings") shouldBe None
  }

  it should "return none when a non object is found" in {
    Locator.find(json, "#/person/age") shouldBe None
  }
}