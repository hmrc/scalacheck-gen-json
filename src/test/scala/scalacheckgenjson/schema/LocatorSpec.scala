package scalacheckgenjson.schema

import org.scalatest.{FlatSpec, Matchers, OptionValues}
import org.scalatest.prop.PropertyChecks
import play.api.libs.json.{JsObject, Json}


class LocatorSpec extends FlatSpec with Matchers with PropertyChecks with OptionValues {

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