package Dchumon.view

import javafx.fxml.FXML
import javafx.scene.control.{Button, TextField}
import javafx.scene.text.Text

class DonationHungerController {

  @FXML
  private var donationAmount: TextField = _ // Reference to the `TextField` in FXML

  @FXML
  private var feedbackMessage: Text = _ // Reference to the `Text` element for feedback 

  @FXML
  private var donateNowButton: Button = _
  
  @FXML
  private var RM10btn: Button = _
  
  @FXML
  private var RM20btn: Button = _
  
  @FXML
  private var RM50btn: Button = _

  @FXML
  def handleDonateNow(): Unit = {
    // Retrieve the entered donation amount
    val amountText = donationAmount.getText.trim

    // Validate if the entered amount is a valid number and greater than 0
    try {
      val amount = amountText.toDouble
      if (amount > 0) {
        // Display success message
        feedbackMessage.setText("Thank you for donating RM" + amount +  " !")
        feedbackMessage.setFill(javafx.scene.paint.Color.SKYBLUE)
        feedbackMessage.setVisible(true)
      } else {
        // Display error message for invalid amount
        feedbackMessage.setText("Please enter a valid amount greater than zero.")
        feedbackMessage.setFill(javafx.scene.paint.Color.FIREBRICK)
        feedbackMessage.setVisible(true)
      }
    } catch {
      case _: NumberFormatException =>
        // Display error message for invalid input
        feedbackMessage.setText("Please enter a numeric donation amount.")
        feedbackMessage.setFill(javafx.scene.paint.Color.FIREBRICK)
        feedbackMessage.setVisible(true)
    }
  }

  @FXML
  def handleRM10btnAction(): Unit = {
    feedbackMessage.setText("Thank you for donating RM10!")
    feedbackMessage.setFill(javafx.scene.paint.Color.SKYBLUE)
    feedbackMessage.setVisible(true)
  }

  @FXML
  def handleRM20btnAction(): Unit = {
    feedbackMessage.setText("Thank you for donating RM20!")
    feedbackMessage.setFill(javafx.scene.paint.Color.SKYBLUE)
    feedbackMessage.setVisible(true)
  }

  @FXML
  def handleRM50btnAction(): Unit = {
    feedbackMessage.setText("Thank you for donating RM50!!")
    feedbackMessage.setFill(javafx.scene.paint.Color.SKYBLUE)
    feedbackMessage.setVisible(true)
  }
}