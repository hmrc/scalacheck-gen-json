package scalacheckjsonfaker.config

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