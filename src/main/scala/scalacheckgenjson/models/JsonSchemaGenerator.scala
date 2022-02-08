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

package scalacheckgenjson.models

import org.scalacheck.Gen
import play.api.libs.json._
import wolfendale.scalacheck.regexp.RegexpGen

object JsonSchemaGenerator {

  def generate: JsonSchemaValue => Gen[JsValue] = {
    case JsonSchemaArray(items, min, max)          => generateArray(items, min, max)
    case JsonSchemaBoolean                         => Gen.oneOf(true, false).map(JsBoolean)
    case JsonSchemaNumber(min, max)                => Gen.chooseNum(min, max).map(n => JsNumber(n))
    case JsonSchemaObject(req, props)              => generateObject(req, props)
    case JsonSchemaString(min, max, enum, pattern) => generateString(min, max, enum, pattern)
  }

  def generateArray(items: JsonSchemaValue, min: Int, max: Int): Gen[JsValue] = for {
      n <- Gen.chooseNum(min, max)
      xs <- Gen.listOfN(n, generate(items))
    } yield {
      JsArray(xs)
    }


  def generateObject(req: List[String], props: Map[String, JsonSchemaValue]): Gen[JsValue] = {

    val r = props.filterKeys(req.contains).mapValues(v => Gen.some(generate(v)))
    val o = props.filterKeys(k => !req.contains(k)).mapValues(v => Gen.option(generate(v)))

    val genObject = (r ++ o).toList.map { case (k, v) =>
      v.map(_.map(g => JsObject(Map(k -> g))))
    }

    Gen.sequence[List[Option[JsObject]], Option[JsObject]](genObject).map {
      _.collect { case Some(v) => v }
       .foldLeft(JsObject.empty)(_++_)
    }
  }

  def generateString(min: Int, max: Int, enum: List[String], pattern: Option[String]): Gen[JsValue] = {
    val pattenGen = pattern.map(p => RegexpGen.from(p))
    val enumGen = enum.headOption.map(_ => Gen.oneOf(enum))
    val minMaxGen = Gen.chooseNum(min, max).flatMap { n =>
      Gen.listOfN(n, Gen.alphaNumChar).map(_.mkString)
    }

    pattenGen.orElse(enumGen).getOrElse(minMaxGen).map(JsString)
  }
}