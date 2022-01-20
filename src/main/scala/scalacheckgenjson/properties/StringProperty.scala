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