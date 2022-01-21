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
import scalacheckgenjson.schema.Schema
import scalacheckgenjson.values.Ref


object RefProperty {

  def gen(schema: Schema, generators: Generators)(obj: JsObject): Option[Gen[JsValue]] = {

    for {
      ref  <- Ref(obj).asOpt
      o    <- schema.find(ref)
      next <- generators.generate(o)
    } yield next
  }
}