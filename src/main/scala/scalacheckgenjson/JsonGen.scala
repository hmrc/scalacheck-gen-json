package scalacheckgenjson

import org.scalacheck.Gen
import play.api.libs.json.{JsObject, JsValue, Json}
import scalacheckgenjson.config.Config
import scalacheckgenjson.properties.Generators
import scalacheckgenjson.schema.Schema

object JsonGen {

  private val defaultConfig = Config.default

  def from(s: String): Gen[String] = from(s, defaultConfig)

  def from(s: String, config: Config): Gen[String] = {
    Json.parse(s) match {
      case o@JsObject(_) => from(o, config).map(_.toString())
      case _             => throw new Exception("unable to read json")
    }
  }

  def from(o: JsObject): Gen[JsValue] = from(o, defaultConfig)

  def from(o: JsObject, config: Config): Gen[JsValue] = {
    val properties = new Generators(Schema(o), config)
    properties.generate(o).getOrElse(throw new Exception("Unknown type"))
  }
}