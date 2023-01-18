package functionnal

class Collaboration(val actor1: Actor, val actor2: Actor, val movies: List[Movie]) {

  override def toString: String = s"$actor1 and $actor2 in $movies"
}
