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