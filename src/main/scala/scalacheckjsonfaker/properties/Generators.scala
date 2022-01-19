package scalacheckjsonfaker.properties

import org.scalacheck.Gen
import play.api.libs.json.{JsObject, JsValue}
import scalacheckjsonfaker.config.Config
import scalacheckjsonfaker.schema.Schema

class Generators(schema: Schema, config: Config) {

  val generators: List[JsObject => Option[Gen[JsValue]]] =
    List(
      ArrayProperty.gen(this, config.arrayConfig),
      BooleanProperty.gen,
      NumberProperty.gen(config.numberConfig),
      ObjectProperty.gen(this),
      StringProperty.gen(config.stringConfig),
      RefProperty.gen(schema, this)
    )

  def generate(obj: JsObject): Option[Gen[JsValue]] =
    generators.foldLeft(None: Option[Gen[JsValue]]) { case (acc, f) =>
      acc orElse f(obj)
    }
}