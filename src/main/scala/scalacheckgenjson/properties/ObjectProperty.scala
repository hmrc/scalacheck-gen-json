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
import play.api.libs.json.{JsArray, JsObject, JsString, JsValue}
import scalacheckgenjson.values.{Properties, Required, Type}

object ObjectProperty {

  def gen(generators: Generators)(obj: JsObject): Option[Gen[JsValue]] = {

    if(Type(obj).is("object")) {
      val required = Required(obj).getOrDefault(List())

      val props = Properties(obj).asOpt.map(_.value).getOrElse(throw new Exception("Properties is required for object"))

      require(props.keys.toSet.intersect(required.toSet) == required.toSet, "not all required fields found in properties")

      val propGen = props.keys.map { k =>
        val obj = props.get(k).collect {
          case obj@JsObject(_) => obj
          case _               => throw new Exception("object property must be an object")
        }.get

        generators.generate(obj).getOrElse(throw new Exception("unknown data type"))
          .flatMap { js =>
            val objWithKey = JsObject(Map(k -> js))
            Gen.option(js).map(_.fold(objWithKey)(_ => objWithKey))
          }
      }

      val mapGen =
        Gen.sequence[List[JsValue], JsValue](propGen).map { a =>
          a.filter(_.isInstanceOf[JsObject]).map(_.asInstanceOf[JsObject])
            .foldLeft(JsObject.empty)((a: JsObject, b: JsObject) => a ++ b)
        }

      Some(mapGen)
    } else {
      None
    }
  }
}