package scalacheckjsonfaker.properties

import org.scalacheck.Gen
import play.api.libs.json.{JsObject, JsValue}
import scalacheckjsonfaker.schema.Schema
import scalacheckjsonfaker.values.Ref


object RefProperty {

  def gen(schema: Schema, generators: Generators)(obj: JsObject): Option[Gen[JsValue]] = {

    for {
      ref  <- Ref(obj).asOpt
      o    <- schema.find(ref)
      next <- generators.generate(o)
    } yield next
  }
}