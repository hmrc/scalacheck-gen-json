package scalacheckjsonfaker

import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.{JSONObject, JSONTokener}
import org.scalacheck.Shrink
import org.scalactic.source.Position
import org.scalatest.exceptions.TestFailedException
import org.scalatest.{FlatSpec, Matchers, OptionValues}
import org.scalatest.prop.PropertyChecks
import play.api.libs.json.JsValue

import scala.io.{BufferedSource, Source}
import scala.util.Try

class JsonGenSpec extends FlatSpec with Matchers with PropertyChecks with OptionValues {

  implicit val noShrinkJsVal: Shrink[JsValue] = Shrink.shrinkAny
  implicit val noShrinkString: Shrink[String] = Shrink.shrinkAny

  case class Loader(path: String) {
    val source = Source.fromResource(path)

    val data: Option[String] =
      Try(source.getLines().mkString).toOption

    def close() = source.close()
  }

  def loadPath(path: String)(implicit pos: Position): String = {
    val loader = Loader(path)
    loader.close()
    loader.data.getOrElse(throw new TestFailedException(_ => Some("Unable to load data"), None, pos))
  }

  def loadSchema(schema: String): Schema = {
    val rawSchema = new JSONObject(new JSONTokener(schema))
    SchemaLoader.load(rawSchema)
  }

  List(
    "simpleSchema.json",
    "schemaWithRefs.json"
  ).foreach { schemaPath =>

    it should s"generate valid data for $schemaPath" in {
      val data = loadPath(schemaPath)
      val schema = loadSchema(data)

      forAll(JsonGen.from(data)) { gen =>
        schema.validate(new JSONObject(gen))
      }
    }
  }
}