package scalix

import scala.io.Source
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.DefaultJsonFormats.*

object Scalix extends App {

  private val api_key = "48d02d2803f669be5643367e3307dd43"
  /*private val url = s"https://api.themoviedb.org/3/movie/550?api_key=$api_key"
  private val source = Source.fromURL(url)
  private val contents = source.mkString
  println(contents)
  val json = parse(contents)
  println(json)*/

  // create type FullName
  private case class FullName(firstName: String, lastName: String)

  private def findActorId(name: String, surname: String): Option[Int] = {
    // returns the integer identifying an actor from its first and last name (TMDB Search People query)

    val url = s"https://api.themoviedb.org/3/search/person?api_key=$api_key&query=$name+$surname"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)

    if (json \ "total_results" == JInt(0)) None

    else Option((json \ "results")(0) \ "id" match {
      case JInt(id) => id.toInt
      case _ => throw new Exception("Error: id is not an integer")
    })

  }

  private val acteurID = findActorId("Brad", "Pitt").getOrElse(0)
  println("ID de l'acteur : " + acteurID)
  println("----------------------------------------")

  private def findActorMovies(actorId: Int): Set[(Int, String)] = {
    val url = s"https://api.themoviedb.org/3/person/$actorId/movie_credits?api_key=$api_key"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)

    val movies = (json \ "cast").children.map(movie => (movie \ "id", movie \ "title"))
    movies.map(movie => (movie._1 match {
      case JInt(id) => id.toInt
      case _ => throw new Exception("Error: id is not an integer")
    }, movie._2 match {
      case JString(title) => title
      case _ => throw new Exception("Error: title is not a string")
    })).toSet

  }

  private val movies = findActorMovies(acteurID).toSeq.sortBy(_._2)
  println("----------------------------------------")
  println("Films de l'acteur : ")
  movies.foreach(println)
  println("Nombre de films : " + movies.length)
  println("----------------------------------------")

  def findMovieDirector(movieId: Int): Option[(Int, String)] = {
    val url = s"https://api.themoviedb.org/3/movie/$movieId/credits?api_key=$api_key"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)

    val director = (json \ "crew").children.find(crew => (crew \ "job") == JString("Director"))
    director match {
      case Some(director) => Option((director \ "id" match {
        case JInt(id) => id.toInt
        case _ => throw new Exception("Error: id is not an integer")
      }, director \ "name" match {
        case JString(name) => name
        case _ => throw new Exception("Error: name is not a string")
      }))
      case None => None
    }

  }

  private val director = findMovieDirector(movies(0)._1).getOrElse((0, ""))
  println("Réalisateur du premier film : " + director._2)
  println("----------------------------------------")




  private def collaboration(actor1: FullName, actor2: FullName): Set[(String, String)] = {
    // returns the set of movies in which both actors have played

    val actor1Id = findActorId(actor1.firstName, actor1.lastName).getOrElse(0)
    val actor2Id = findActorId(actor2.firstName, actor2.lastName).getOrElse(0)

    val actor1Movies = findActorMovies(actor1Id)
    val actor2Movies = findActorMovies(actor2Id)

    val collaboration = actor1Movies.intersect(actor2Movies)

    collaboration.map(movie => (movie._1.toString, movie._2))

  }

  private val collaboration : Set[(String, String)] = collaboration(FullName("Brad", "Pitt"), FullName("Angelina", "Jolie")).toSeq.sortBy(_._2).toSet
  println("Collaboration : ")
  collaboration.foreach(println)
  println("----------------------------------------")

  /* ---------------- */

  // Results of queries to TMDB in files
  /* the results of queries to TMDB in files actor$id.json (for a People: Get Movie Credits query) and
     movie$id.json (for a Movies: Get Credits query) stored in a data directory of the project where $id
     identifies the actor in the first case and the film in the second
  */

  private def addActorToFile(actorID: Int): Unit = {

    val file = new java.io.File(s"data/actor$actorID.json")
    if (!file.exists())
      file.createNewFile()
    // write the content of the file without removing the previous content (append mode) go to next line
    val writer = new java.io.PrintWriter(new java.io.FileWriter(file, true))
    writer.println(acteurID.toString)
    writer.close()

  }

  private def addMoviesToFile(movies: Set[(Int, String)]): Unit = {

    val file = new java.io.File(s"data/movies$acteurID.json")
    if (!file.exists())
      file.createNewFile()
    // write the content of the file without removing the previous content (append mode) go to next line
    val writer = new java.io.PrintWriter(new java.io.FileWriter(file, true))
    movies.foreach(movie => writer.println(movie._1.toString + " " + movie._2))
    writer.close()

  }

  // add the result of the query to the data directory
  private def findActorId (name: String, surname: String, save: Boolean): Unit= {
    val url = s"https://api.themoviedb.org/3/search/person?api_key=$api_key&query=$name+$surname"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)

    if (save) {
      addActorToFile((json \ "results")(0) \ "id" match {
        case JInt(id) => id.toInt
        case _ => throw new Exception("Error: id is not an integer")
      })
    }
  }

  findActorId("Brad", "Pitt", true)

  private def finMovifindActorMovies2(actorId: Int, save: Boolean): Unit = {
    val url = s"https://api.themoviedb.org/3/person/$actorId/movie_credits?api_key=$api_key"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)

    if (save) {
      addMoviesToFile((json \ "cast").children.map(movie => (movie \ "id", movie \ "title"))
        .map(movie => (movie._1 match {
          case JInt(id) => id.toInt
          case _ => throw new Exception("Error: id is not an integer")
        }, movie._2 match {
          case JString(title) => title
          case _ => throw new Exception("Error: title is not a string")
        })).toSet)
    }
  }

  finMovifindActorMovies2(287, true)

  /* ---------------- */
  // the results of the different TMDB methods in a dictionary (or map) whose keys correspond to the input data and the values to the results, for example: Map[(String, String), Int] for findActorId.

  println("Dictionary ------------------------")
  private val actorIdMap = scala.collection.mutable.Map[(String, String), Int]()
  private val actorMoviesMap = scala.collection.mutable.Map[Int, Set[(Int, String)]]()
  private val movieDirectorMap = scala.collection.mutable.Map[Int, Option[(Int, String)]]()
  private val collaborationMap = scala.collection.mutable.Map[(FullName, FullName), Set[(Int, String)]]()

  private def findActorId2(name: String, surname: String): Int = {
    val url = s"https://api.themoviedb.org/3/search/person?api_key=$api_key&query=$name+$surname"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)

    val actorId = (json \ "results")(0) \ "id" match {
      case JInt(id) => id.toInt
      case _ => throw new Exception("Error: id is not an integer")
    }

    actorIdMap += ((name, surname) -> actorId)
    actorId
  }

  println("actorIdMap : ")
  findActorId2("Brad", "Pitt")
  actorIdMap.foreach(println)

  private def findActorMovies2(actorId: Int): Set[(Int, String)] = {
    val url = s"https://api.themoviedb.org/3/person/$actorId/movie_credits?api_key=$api_key"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)

    val actorMovies = (json \ "cast").children.map(movie => (movie \ "id", movie \ "title"))
      .map(movie => (movie._1 match {
        case JInt(id) => id.toInt
        case _ => throw new Exception("Error: id is not an integer")
      }, movie._2 match {
        case JString(title) => title
        case _ => throw new Exception("Error: title is not a string")
      })).toSet

    actorMoviesMap += (actorId -> actorMovies)
    actorMovies
  }
  findActorMovies2(287)
  println("")
  println("findActorMovies2 : ")
  println(actorMoviesMap(287).foreach(println))

  private def findMovieDirector2(movieId: Int): Option[(Int, String)] = {
    val url = s"https://api.themoviedb.org/3/movie/$movieId/credits?api_key=$api_key"
    val source = Source.fromURL(url)
    val contents = source.mkString
    val json = parse(contents)

    val movieDirector = (json \ "crew").children.find(crew => (crew \ "job") match {
      case JString(job) => job == "Director"
      case _ => throw new Exception("Error: job is not a string")
    }).map(crew => (crew \ "id", crew \ "name"))
      .map(crew => (crew._1 match {
        case JInt(id) => id.toInt
        case _ => throw new Exception("Error: id is not an integer")
      }, crew._2 match {
        case JString(name) => name
        case _ => throw new Exception("Error: name is not a string")
      }))

    movieDirectorMap += (movieId -> movieDirector)
    movieDirector
  }

  findMovieDirector2(550)
  println("")
  println("findMovieDirector2 : ")
  println(movieDirectorMap(550).foreach(println))

  private def findCollaboration2(actor1: FullName, actor2: FullName): Set[(Int, String)] = {
    val actor1Id = findActorId2(actor1.firstName, actor1.lastName)
    val actor2Id = findActorId2(actor2.firstName, actor2.lastName)

    val actor1Movies = findActorMovies2(actor1Id)
    val actor2Movies = findActorMovies2(actor2Id)

    val collaboration = actor1Movies.intersect(actor2Movies)

    collaborationMap += ((actor1, actor2) -> collaboration)
    collaboration
  }

  findCollaboration2(FullName("Brad", "Pitt"), FullName("Angelina", "Jolie"))
  println("")
  println("findCollaboration2 : ")
  println(collaborationMap((FullName("Brad", "Pitt"), FullName("Angelina", "Jolie"))).foreach(println))



}


