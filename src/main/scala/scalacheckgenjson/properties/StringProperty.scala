package scalacheckgenjson.properties

import org.scalacheck.Gen
import play.api.libs.json.{JsObject, JsString, JsValue}
import scalacheckgenjson.config.StringConfig
import scalacheckgenjson.schema.Schema
import scalacheckgenjson.values._
import wolfendale.scalacheck.regexp.RegexpGen

object StringProperty {

  def gen(config: StringConfig)(obj: JsObject): Option[Gen[JsValue]] = {
    if(Type(obj).is("string")) {

      val min = MinLength(obj).getOrDefault(config.minLength)
      val max = MaxLength(obj).getOrDefault(config.maxLength)
      val enum = Enum(obj).asOpt
      val pattern = Pattern(obj).asOpt

      val pattenGen = pattern.map(p => RegexpGen.from(p))
      val enumGen = enum.map(xs => Gen.oneOf(xs))
      val minMaxGen = Gen.chooseNum(min, max).flatMap { n =>
        Gen.listOfN(n, Gen.alphaNumChar).map(_.mkString)
      }

      Some(pattenGen.orElse(enumGen).getOrElse(minMaxGen).map(JsString))
    } else {
      None
    }
  }
}