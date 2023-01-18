package scalix

import scalix.FindActorId.findActorId
import scalix.FindActorMovie.findActorMovies
import scalix.FindMovieDirector.findMovieDirector

object Collaboration extends App {
  var collaborations: Map[(FullName, FullName), Map[Int, (String, String)]] = Map()
  def collaboration(actor1: FullName, actor2: FullName): Map[Int, (String, String)] = {
    if (collaborations == null) {
      collaborations = Map()
    }
    if (collaborations.contains(actor1, actor2)) {
      collaborations((actor1, actor2))
    } else {
      val actor1Id = findActorId(actor1.surname, actor1.name).getOrElse(0)
      val actor2Id = findActorId(actor2.surname, actor2.name).getOrElse(0)
      if (actor1Id == 0 || actor2Id == 0) {
        Set()
      }
      val actor1Movies = findActorMovies(actor1Id)
      val actor2Movies = findActorMovies(actor2Id)
      if (actor1Movies.isEmpty || actor2Movies.isEmpty) {
        Set()
      }
      val commonMovies = actor1Movies.intersect(actor2Movies)
      if (commonMovies.isEmpty) {
        Set()
      }
      val commonMoviesId = commonMovies.map(_._1)
      val commonMoviesTitle = commonMovies.map(_._2)
      val commonMoviesDirector = commonMoviesId.flatMap(findMovieDirector)
      val commonMoviesDirectorName = commonMoviesDirector.map(_._2)

      val moviesWithDirector = commonMoviesTitle.zip(commonMoviesDirectorName)
      val moviesWithDirectorId = commonMoviesId.zip(moviesWithDirector).toMap

      collaborations += ((actor1, actor2) -> moviesWithDirectorId)
      collaborations((actor1, actor2))
    }
  }

  println("Collaborations")
  println(collaboration(FullName("Brad", "Pitt"), FullName("Robert", "Downey")))

  def mostCollaboration(): Set[(FullName, FullName, Int)] = {
    collaborations.map(x => (x._1._1, x._1._2, x._2.size)).toSet
  }

  println("Most collaborations")
  println(mostCollaboration())
}
