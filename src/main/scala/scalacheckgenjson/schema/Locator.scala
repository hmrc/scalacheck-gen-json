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