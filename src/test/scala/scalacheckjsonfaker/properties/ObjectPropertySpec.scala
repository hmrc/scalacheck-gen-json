package scalacheckjsonfaker.properties

import org.scalacheck.Shrink
import org.scalatest._
import org.scalatest.prop.PropertyChecks
import play.api.libs.json._
import scalacheckjsonfaker.config.Config
import scalacheckjsonfaker.schema.Schema

class ObjectPropertySpec extends FlatSpec with Matchers with PropertyChecks with OptionValues {

  implicit val noStringShrink: Shrink[String] = Shrink.shrinkAny
  implicit val noIntShrink: Shrink[JsValue] = Shrink.shrinkAny

  def typeProp(value: String) = "type" -> JsString(value)
  val objectType = JsObject(Map(typeProp("object")))
  val booleanType = JsObject(Map(typeProp("boolean")))
  val stringType = JsObject(Map(typeProp("string")))
  val booleanProperties = JsObject(Map("properties" -> JsObject(Map("b" -> booleanType))))
  val stringProperties = JsObject(Map("properties" -> JsObject(Map("s" -> stringType))))
  val generators = new Generators(Schema(JsObject.empty), Config.default)
  val extract = ObjectProperty.gen(generators) _

  it should "skip when type not object" in {
    extract(JsObject(Map(typeProp("unknown")))) shouldBe None
  }

  it should "produce an object property" in {

    forAll(extract(objectType ++ booleanProperties).value) { arr =>
      arr should be (a[JsObject])
    }
  }

  it should "create boolean items" in {

    forAll(extract(objectType ++ booleanProperties).value) { obj =>
      obj.asInstanceOf[JsObject].value.get("b").map(_ should be (a[JsBoolean]))
    }
  }

  it should "create string items" in {

    forAll(extract(objectType ++ stringProperties).value) { obj =>
      obj.asInstanceOf[JsObject].value.get("s").map(_ should be (a[JsString]))
    }
  }

  it should "create string and boolean items" in {

    val booleanProp = JsObject(Map("b" -> booleanType))
    val stringProp = JsObject(Map("s" -> stringType))
    val properties = JsObject(Map("properties" -> (booleanProp ++ stringProp)))

    forAll(extract(objectType ++ properties).value) { obj =>
      obj.asInstanceOf[JsObject].value.get("b").map(_ should be (a[JsBoolean]))
      obj.asInstanceOf[JsObject].value.get("s").map(_ should be (a[JsString]))
    }
  }

  it should "respect the required property" in {

    val requiredProp = JsObject(Map("required" -> JsArray(List(JsString("b")))))
    val booleanProp = JsObject(Map("b" -> booleanType))
    val stringProp = JsObject(Map("s" -> stringType))
    val properties = JsObject(Map("properties" -> (booleanProp ++ stringProp)))

    forAll(extract(objectType ++ properties ++ requiredProp).value) { obj =>
      obj.asInstanceOf[JsObject].value.get("b").value should be (a[JsBoolean])
      obj.asInstanceOf[JsObject].value.get("s").map(_ should be (a[JsString]))
    }
  }
}