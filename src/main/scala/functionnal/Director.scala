package functionnal

import scalix.FullName

class Director(id: Int, name: FullName) extends Actor(id, name) {

  override def toString: String = s"$id: $name, Movies: $movies"
}
