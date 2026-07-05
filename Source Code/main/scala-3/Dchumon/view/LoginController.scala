package Dchumon.view

import javafx.fxml.FXML
import javafx.scene.control.{Button, PasswordField, TextField, Label}
import Dchumon.MainApp
import Dchumon.model.{User, UserProfile}
import Dchumon.util.Session

class LoginController {

  @FXML
  private var emailField: TextField = _
  @FXML
  private var passwordField: PasswordField = _
  @FXML
  private var loginButton: Button = _
  @FXML
  private var errorMessage: Label = _

  // Method to handle user login
  @FXML
  def handleLogin(): Unit = {
    if (emailField == null || passwordField == null || errorMessage == null) {
      System.err.println("FXML components are not initialized. Check your FXML bindings.")
      return
    }

    // Normalize email for case-insensitive comparison
    val email = Option(emailField.getText).map(_.trim.toLowerCase).getOrElse("")
    val password = Option(passwordField.getText).getOrElse("")

    if (email.isEmpty || password.isEmpty) {
      errorMessage.setText("Email and password cannot be empty.")
      return
    }

    try {
      if (User.login(email, password)) {
        Session.isAuthenticated = true
        Session.currentUser = Some(new UserProfile("John", "Cena", email)) // Example user

        MainApp.showHomePage()
      } else {
        errorMessage.setText("Invalid email or password.")
        errorMessage.setTextFill(javafx.scene.paint.Color.RED)
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        errorMessage.setText("An unexpected error occurred. Please try again.")
        errorMessage.setTextFill(javafx.scene.paint.Color.RED)
    }
  }
}
