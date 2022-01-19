package scalacheckgenjson.properties

import org.scalacheck.Gen
import play.api.libs.json.{JsArray, JsObject, JsValue}
import scalacheckgenjson.config.ArrayConfig
import scalacheckgenjson.values._

object ArrayProperty {

  def gen(generators: Generators, config: ArrayConfig)(obj: JsObject): Option[Gen[JsValue]] = {

    if(Type(obj).is("array")) {
      val items = Items(obj).asOpt.getOrElse(throw new Exception("Items is required"))
      val itemGen = generators.generate(items).getOrElse(throw new Exception("Unknown object type"))

      val min = MinItems(obj).getOrDefault(config.minItems)
      val max = MaxItems(obj).getOrDefault(config.maxItems)

      Some(Gen.chooseNum(min, max).flatMap(n => Gen.listOfN(n, itemGen)).map(xs => JsArray(xs)))
    } else {
      None
    }
  }
}