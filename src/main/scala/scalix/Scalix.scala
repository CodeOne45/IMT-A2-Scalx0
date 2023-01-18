package scalix

import org.json4s._
import org.json4s.native.JsonMethods._
import scalix.Collaboration.{collaboration, mostCollaboration}
import scalix.FindActorId.{actorsId, findActorId}
import scalix.FindActorMovie.findActorMovies
import scalix.FindMovieDirector.findMovieDirector
import java.nio.file.{Files, Paths}
import scala.io.Source

object Config {
  val api_key = "48d02d2803f669be5643367e3307dd43"
}


object Scalix extends App{
  implicit val formats: DefaultFormats.type = DefaultFormats

  def getFromUrl(url: String) = {
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)
    json
  }

  def getFromFile(path: String) = {
    val source = Source.fromFile(path)
    val contents = source.mkString
    contents
  }

  println("Actor's Id")

  println(findActorId("Brad", "Pitt"))
  println(findActorId("Brad", "Pitt"))

  println(actorsId)
  println("==============================================================================")
  println("Actor's Movies")

  println(findActorMovies(287))
  println(findActorMovies(287))

  println(findActorMovies(350))
  //Files.delete(Paths.get("data/actor350.json"))
  println("==============================================================================")
  println("Movie's director")

  println(findMovieDirector(335700))
  println(findMovieDirector(335700))

  println(findMovieDirector(335702))
  Files.delete(Paths.get("data/movie335702.json"))
  println("==============================================================================")
  println("Collaborations")

  println(collaboration(FullName("Brad", "Pitt"), FullName("Robert", "Downey")))
  println(collaboration(FullName("Brad", "Pitt"), FullName("Robert", "Downey")))

  println("==============================================================================")
  println("Most collaboration")

  println(mostCollaboration())

}