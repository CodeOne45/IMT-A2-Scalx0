package functionnal

import scalix.FullName

class Actor(val id: Int, val name: FullName) {
  var movies: List[Movie] = List()

  def addMovie(movies: Set[(Int, String)] ): Unit = {
    movies.foreach(x => this.movies = new Movie(x._1, x._2) :: this.movies)
  }

  override def toString: String = s"$id: $name\nMovies: $movies"
}