package scalacheckgenjson.properties

import org.scalatest._
import org.scalatest.prop.PropertyChecks
import play.api.libs.json._
import scalacheckgenjson.schema.Schema

class BooleanPropertySpec extends FlatSpec with Matchers with PropertyChecks with OptionValues {

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
