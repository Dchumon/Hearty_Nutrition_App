package Dchumon.view

import javafx.fxml.FXML
import javafx.scene.control.{Menu, MenuItem}
import Dchumon.MainApp
import Dchumon.util.Session
import javafx.scene.layout.BorderPane
import javafx.scene.Scene

class RootLayoutController {

  @FXML
  private var HomeButton: Menu = _
  @FXML
  private var AboutButton: Menu = _
  @FXML
  private var LoginButton: Menu = _
  @FXML 
  private var LoginMenuItem: MenuItem = _
  @FXML
  private var rootPane: BorderPane = _

  private var darkModeEnabled = true // start with DarkMode.css

  @FXML
  private def handleToggleMode(): Unit = {
    val scene: Scene = rootPane.getScene
    if (scene != null) {
      if (darkModeEnabled) {
        // Remove Dark Mode
        scene.getStylesheets.clear()
      } else {
        // Apply Dark Mode
        scene.getStylesheets.add(
          getClass.getResource("/AppPages/DarkMode.css").toExternalForm
        )
      }
      darkModeEnabled = !darkModeEnabled
    }
  }

  @FXML
  def initialize(): Unit = {
    updateLoginButton()
  }

  @FXML
  def handleHome(): Unit = {
    MainApp.showHomePage()
  }

  @FXML
  def handleAbout(): Unit = {
    MainApp.showAboutPage()
  }

  @FXML
  def handleLogin(): Unit = {
    updateLoginButton()
    if (Session.isAuthenticated) {
      // Logout
      Session.isAuthenticated = false
      Session.currentUser = None

      println("User logged out successfully.")
    } else {
      // Show login
      MainApp.showLoginPage()
    }
  }

  private def updateLoginButton(): Unit = {
    if (Session.isAuthenticated) {
      LoginMenuItem.setText("Logout")
    } else {
      LoginMenuItem.setText("Login")
    }
  }
}
