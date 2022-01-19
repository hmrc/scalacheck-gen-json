package scalacheckjsonfaker.properties

import org.scalatest.{FlatSpec, Matchers, OptionValues}
import org.scalatest.prop.PropertyChecks
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import scalacheckjsonfaker.config.Config
import scalacheckjsonfaker.schema.Schema


class RefPropertySpec extends FlatSpec with Matchers with PropertyChecks with OptionValues {

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