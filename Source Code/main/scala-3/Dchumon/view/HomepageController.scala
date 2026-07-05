package Dchumon.view

import javafx.fxml.FXML
import javafx.scene.control.Button
import Dchumon.MainApp

class HomePageController {

  @FXML
  private var searchButton: Button = _

  @FXML
  private var mealTrackerButton: Button = _

  @FXML
  private var donateButton: Button = _

  @FXML
  def handleSearchButton(): Unit = {
    MainApp.showFoodSearch() // Navigate to the Food Search page
  }
  @FXML
  def handleMealTrackerButton(): Unit = {
    MainApp.showMealTracker()
  }
  // Method triggered when the meal tracker button is clicked

  // Method triggered when the donate button is clicked
  @FXML
  def handleDonateButton(): Unit = {
    MainApp.showDonationHunger() // Show the donate page
  }
}

