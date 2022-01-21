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

package scalacheckgenjson.config

case class Config(
  arrayConfig: ArrayConfig,
  numberConfig: NumberConfig,
  stringConfig: StringConfig
)

object Config {

  val default = Config(ArrayConfig.default, NumberConfig.default, StringConfig.default)
}

case class ArrayConfig(
  minItems: Int,
  maxItems: Int
)

object ArrayConfig {

  val default = ArrayConfig(0, 100)
}

case class NumberConfig(
  minimum: Long,
  maximum: Long
)

object NumberConfig {

  val default = NumberConfig(Long.MinValue, Long.MaxValue)
}

case class StringConfig(
  minLength: Int,
  maxLength: Int,
)

object StringConfig {

  def default = StringConfig(0, 2000)
}