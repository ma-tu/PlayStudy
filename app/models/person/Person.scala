package models.person

case class Person(
  id : Option[Long],
  profile: PersonProfile,
  contact: Option[PersonContact],
  children: Seq[PersonProfile]
)
