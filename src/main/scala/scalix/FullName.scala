package scalix

case class FullName(name: String, surname: String) {
  override def toString: String = s"$name $surname"
}