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

package scalacheckgenjson.schema

import play.api.libs.json.JsObject

object Locator {

  def find(schema: JsObject, path: String): Option[JsObject] = {
    path
      .replace('#', ' ')
      .split('/')
      .map(_.trim)
      .filter(_.nonEmpty)
      .foldLeft(Option(schema)) {
        case (acc, curr) =>
          acc.map(_ \ curr).flatMap(_.toOption).collect {
            case o@JsObject(_) => o
          }
      }
  }
}