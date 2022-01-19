package scalacheckjsonfaker.properties

import org.scalacheck.Gen
import play.api.libs.json.{JsNumber, JsObject, JsValue}
import scalacheckjsonfaker.config.NumberConfig
import scalacheckjsonfaker.values._

object NumberProperty {

  def gen(config: NumberConfig)(obj: JsObject): Option[Gen[JsValue]] = {

    if(Type(obj).is("number")) {
      val min = Minimum(obj).getOrDefault(config.minimum)
      val max = Maximum(obj).getOrDefault(config.maximum)

      Some(Gen.chooseNum(min, max).map(n => JsNumber(n)))
    } else {
      None
    }
  }
}