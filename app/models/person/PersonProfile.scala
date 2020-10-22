package models.person

import java.time.LocalDate

class PersonProfile(
  name: PersonName,
  sex: PersonSex,
  birthDay: Option[LocalDate],
)