package scalacheckjsonfaker.properties

import org.scalacheck.Gen.{alphaNumStr, chooseNum, listOf}
import org.scalacheck.{Gen, Shrink}
import org.scalatest._
import org.scalatest.prop.PropertyChecks
import play.api.libs.json.{JsArray, JsNumber, JsObject, JsString}
import scalacheckjsonfaker.config.StringConfig

class StringPropertySpec extends FlatSpec with Matchers with PropertyChecks with OptionValues {

  implicit val noStringShrink: Shrink[String] = Shrink.shrinkAny
  implicit val noIntShrink: Shrink[Int] = Shrink.shrinkAny

  val stringType = typeProp("string")
  val simpleStringType = JsObject(Map(stringType))

  def typeProp(value: String) = "type" -> JsString(value)
  def minType(min: Int) = simpleStringType + ("minLength" -> JsNumber(min))
  def maxType(max: Int) = simpleStringType + ("maxLength" -> JsNumber(max))
  def enumType(values: List[String]) = simpleStringType + ("enum" -> JsArray(values.map(JsString)))
  def patternType(value: String) = simpleStringType + ("pattern" -> JsString(value))

  val rangeGen: Gen[(Int, Int)] =
    for {
      i <- chooseNum(1, 100)
      j <- chooseNum(1, 100)
    } yield {
      (Math.min(i, j), Math.max(i, j))
    }

  val extract = StringProperty.gen(StringConfig.default) _

  it should "skip when type not string" in {
    extract(JsObject(Map(typeProp("unknown")))) shouldBe None
  }

  it should "produce a string property" in {

    forAll(extract(simpleStringType).value) { s =>
      s should be(a[JsString])
    }
  }

  it should "respect min length property" in {

    forAll(chooseNum(1, 100)) { n =>
      forAll(extract(minType(n)).value) { s =>
        s.as[String] should fullyMatch regex "^.{" + n + ",}$"
      }
    }
  }

  it should "respect max length property" in {

    forAll(chooseNum(1, 100)) { n =>
      forAll(extract(maxType(n)).value) { s =>
        s.as[String] should fullyMatch regex "^.{0," + n + "}$"
      }
    }
  }

  it should "respect min and max length properties" in {

    forAll(rangeGen) { case (i, j) =>
      forAll(extract(minType(i) ++ maxType(j)).value) { s =>
        s.as[String] should fullyMatch regex "^.{" + i + "," + j +"}$"
      }
    }
  }

  it should "respect the enum property" in {

    forAll(listOf(alphaNumStr).suchThat(_.nonEmpty)) { xs =>
      forAll(extract(enumType(xs)).value) { s =>
        xs should contain (s.as[String])
      }
    }
  }

  it should "respect the pattern property" in {

    val pattern = "^[A-Z]{2}[a-z]{2}$"

    forAll(extract(patternType(pattern)).value) { s =>
      s.as[String] should fullyMatch regex pattern
    }
  }
}