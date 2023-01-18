package functionnal

import scalix.FullName

class Movie(val id: Int, val title: String){
  var director: Director = new Director(0, new FullName("Unknown",""))

  def setDirector(director: Option[(Int, String)]): Unit = {
    if (director.nonEmpty){
      this.director = new Director(director.get._1, new FullName(director.get._2.split(" ")(0), director.get._2.split(" ")(1)))
      this.director.addMovie(Set((this.id, this.title)))
    }
  }

  def setDirector(director: Director): Unit = {
    if (director != null) {
      this.director = director
      this.director.addMovie(Set((this.id, this.title)))
    }
  }
  override def toString: String = {
    var str = s"$id: $title"
    if (director.id != 0) {
      str += s"\nDirector ($director)"
    }
    str
  }
}