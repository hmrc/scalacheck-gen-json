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