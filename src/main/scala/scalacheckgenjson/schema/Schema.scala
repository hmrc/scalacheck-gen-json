package scalacheckgenjson.schema

import play.api.libs.json.JsObject

case class Schema(json: JsObject) {

  def find(path: String): Option[JsObject] =
    Locator.find(json, path)
}
