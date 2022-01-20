/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scalacheckgenjson

import org.everit.json.schema.Schema
import org.everit.json.schema.loader.SchemaLoader
import org.json.{JSONObject, JSONTokener}
import org.scalacheck.Shrink
import org.scalactic.source.Position
import org.scalatest.OptionValues
import org.scalatest.exceptions.TestFailedException
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.JsValue

import scala.io.Source
import scala.util.Try

class JsonGenSpec extends AnyFlatSpec with should.Matchers with ScalaCheckPropertyChecks with OptionValues {

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