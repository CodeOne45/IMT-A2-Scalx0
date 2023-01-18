package scalix

import org.json4s.DefaultReaders.{IntReader, JArrayReader, JObjectReader, StringReader}
import org.json4s.native.Json
import org.json4s.native.JsonMethods.parse
import org.json4s.{DefaultFormats, JArray, JObject, JValue}
import scalix.Config.api_key
import scalix.Scalix.{getFromFile, getFromUrl}
import java.io.PrintWriter
import java.nio.file.{Files, Paths}

object FindActorMovie extends App {
  var actorMovies: Map[Int, Set[(Int, String)]] = Map()

  def findActorMovies(actorId: Int): Set[(Int, String)] = {
    if(actorMovies == null) {
      actorMovies = Map()
    }
    val movies = Set(): Set[(Int, String)]
    val file = new java.io.File(s"data/actor$actorId.json")
    if (actorMovies.contains(actorId)) {
      actorMovies(actorId)
    } else {
      if (file.exists()) {
        val contents = getFromFile(s"data/actor$actorId.json")
        if (contents.nonEmpty) {
          val json = parse(contents)
          val movies = json.as[JObject].foldField(List(): List[(Int, String)])((acc, field) => {
            val movieId = field._1.toInt
            val movieTitle = field._2.as[String]
            (movieId, movieTitle) :: acc
          }).toSet
        }
      } else {
        file.createNewFile()
      }

      if (movies.isEmpty) {
        val url = s"https://api.themoviedb.org/3/person/$actorId/movie_credits?api_key=$api_key&language=en-US"
        val json: JValue = getFromUrl(url)

        val movies = (json \ "cast").as[JArray].arr
        val moviesId = movies.map(movie => (movie \ "id").as[Int])
        val moviesTitle = movies.map(movie => (movie \ "title").as[String])

        val parsed = movies.map(x => (x \ "id").as[Int] -> (x \ "title").as[String]).toMap
        val out = new PrintWriter(s"data/actor$actorId.json")
        out.write(Json(DefaultFormats).write(parsed))
        out.close()

        actorMovies += (actorId -> parsed.toSet)

        moviesId.zip(moviesTitle).toSet
      } else {
        movies.toSet
      }
    }
  }

  println("Actor's movies")
  println(findActorMovies(287))

  println(findActorMovies(288))
  Files.delete(Paths.get("data/actor288.json"))
}
