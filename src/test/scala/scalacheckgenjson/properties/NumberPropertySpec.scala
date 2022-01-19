package scalacheckgenjson.properties

import org.scalacheck.Gen.chooseNum
import org.scalatest._
import org.scalatest.prop.PropertyChecks
import play.api.libs.json._
import scalacheckgenjson.config.NumberConfig
import scalacheckgenjson.schema.Schema

class NumberPropertySpec extends FlatSpec with Matchers with PropertyChecks with OptionValues {

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
