package scalix

import org.json4s.DefaultReaders.{IntReader, JArrayReader, JObjectReader, StringReader}
import org.json4s.native.Json
import org.json4s.native.JsonMethods.parse
import org.json4s.{DefaultFormats, JArray, JObject, JValue}
import scalix.Config.api_key
import scalix.Scalix.{getFromFile, getFromUrl}

import java.io.PrintWriter
import java.nio.file.{Files, Paths}

object FindMovieDirector extends App {

  var movieDirector: Map[Int, String] = Map()
  def findMovieDirector(movieId: Int): Option[(Int, String)] = {
    if(movieDirector == null) {
      movieDirector = Map()
    }
    val director = Option[(Int, String)](null)
    val file = new java.io.File(s"data/movie$movieId.json")
    if (movieDirector.contains(movieId)) {
      Some(movieId, movieDirector(movieId))
    } else {
      if (file.exists()) {
        val contents: String = getFromFile(s"data/movie$movieId.json")
        if (contents.nonEmpty) {
          val json = parse(contents)
          val director = json.as[JObject].values
        }
      } else {
        file.createNewFile()
      }

      if (director == null) {
        Option(director.head._1.toInt, director.head._2.toString)
      } else {
        val url = s"https://api.themoviedb.org/3/movie/$movieId/credits?api_key=$api_key&language=en-US"
        val json: JValue = getFromUrl(url)

        val director = (json \ "crew").as[JArray].arr.find(x => (x \ "job").as[String] == "Director")

        if (director.isEmpty) {

          None
        } else {
          val directorId = (director.get \ "id").as[Int]
          val directorName = (director.get \ "name").as[String]


          movieDirector += (movieId -> directorName)

          val out = new PrintWriter(s"data/movie$movieId.json")
          val parsed = Map[Int, String](directorId -> directorName)
          out.write(Json(DefaultFormats).write(parsed))
          out.close()

          Some(directorId, directorName)
        }
      }
    }
  }

  println("Movie's director")
  println(findMovieDirector(335700))

  println(findMovieDirector(335702))
  Files.delete(Paths.get("data/movie335700.json"))
}
