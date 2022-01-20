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

package scalacheckgenjson.properties

import org.scalacheck.Gen
import play.api.libs.json.{JsObject, JsValue}
import scalacheckgenjson.config.Config
import scalacheckgenjson.schema.Schema

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