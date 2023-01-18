package scalix

import org.json4s.DefaultReaders.{IntReader, JArrayReader}
import org.json4s.{JArray, JInt, JValue}
import scalix.Config.api_key
import scalix.Scalix.getFromUrl

object FindActorId extends App {
  var actorsId: Map[(String, String), Int] = Map()

  def findActorId(name: String, surname: String): Option[Int] = {
    if (actorsId == null) {
      actorsId = Map()
    }
    if (actorsId.contains((surname, name))) {
      Some(actorsId((surname, name)))
    } else {
      val urlActor = s"https://api.themoviedb.org/3/search/person?api_key=$api_key&language=en-US&query=$surname+$name"
      val json: JValue = getFromUrl(urlActor)

      if (json \ "total_results" == JInt(0)) {
        None
      }
      else {
        val id = (json \ "results" \ "id").as[JArray].arr.head.as[Int]
        actorsId += ((surname, name) -> id)
        Option(id)
      }
    }
  }

  println("Actor's id")
  println(findActorId("Brad", "Pitt"))

  println(actorsId)
}
