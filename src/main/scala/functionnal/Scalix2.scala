package functionnal

import org.json4s._
import org.json4s.native.JsonMethods._
import scalix.Collaboration.{collaboration, collaborations, mostCollaboration}
import scalix.FindActorId.{actorsId, findActorId}
import scalix.FindActorMovie.findActorMovies
import scalix.FindMovieDirector.findMovieDirector
import scalix.FullName
import java.nio.file.{Files, Paths}
import scala.io.Source

object Config {
  val api_key = "48d02d2803f669be5643367e3307dd43"
}


object Scalix2 extends App {
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

  println("Actor's id")
  val actor = new Actor(findActorId("Brad", "Pitt").getOrElse(0), FullName("Brad", "Pitt"))
  println(actor)
  println(new Actor(findActorId("Brad", "Pitt").getOrElse(0), FullName("Brad", "Pitt")))

  println(actorsId)
  println("==============================================================================")
  println("Actor's movies")

  actor.addMovie(findActorMovies(actor.id))
  println(actor)
  println(findActorMovies(actor.id))

  println(findActorMovies(1136678))
 // Files.delete(Paths.get("data/actor1136678.json"))
  println("==============================================================================")
  println("Movie's director")

  val movie: Movie = new Movie(628, "Interview with the Vampire")
  movie.setDirector(findMovieDirector(628))

  println(movie)
  println(findMovieDirector(628))

  println(findMovieDirector(602580))
  Files.delete(Paths.get("data/movie602580.json"))
  println("==============================================================================")
  println("Collaborations")

  val newCollab: Collaboration = new Collaboration(actor, new Actor(0, FullName("Robert", "Downey")),
    collaboration(FullName("Brad", "Pitt"), FullName("Robert", "Downey"))
      .map(x => {
        val y = new Movie(x._1, x._2._1)
        y.setDirector(new Director(1, FullName(x._2._2.split(" ")(0), x._2._2.split(" ")(1))))
        y
      }).toList)

  println(newCollab)
  println(collaboration(FullName("Brad", "Pitt"), FullName("Robert", "Downey")))
  println("==============================================================================")
  println("Most collaborations")
  println(mostCollaboration())

}