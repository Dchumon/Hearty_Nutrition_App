package Dchumon.model
import Dchumon.view.LoginController
import Dchumon.MainApp

class UserProfile(val firstName: String, val lastName: String, val email: String)

case class Password(val password: String)

object User {
  def login(email: String, password: String): Boolean = {
    val normalizedEmail = email.trim.toLowerCase
    val storedEmail = "oop@gmail.com" // stored in lowercase for matching
    val storedPassword = "sunway2025"    // case-sensitive

    normalizedEmail == storedEmail && password == storedPassword
  }
}
