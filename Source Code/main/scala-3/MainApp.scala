package Dchumon

import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes.*
import scalafx.scene.image.Image
import scalafx.stage.{Modality, Stage}
import Dchumon.util.Session

object MainApp extends JFXApp3 {
  var rootPane: Option[javafx.scene.layout.BorderPane] = None
  var cssResource = Option(getClass.getResource("AppPages/DarMode.css"))
  var mealTrackerPane: Option[javafx.scene.layout.Pane] = None
  var mealTrackerController: Option[Dchumon.view.MealTrackerController] = None

  override def start(): Unit = {
    try {
      // Load root layout
      val rootLayoutResource = getClass.getResource("/AppPages/RootLayout.fxml")
      require(rootLayoutResource != null, "RootLayout.fxml resource not found")

      val loader = new FXMLLoader(rootLayoutResource)
      val rootLayout = loader.load[javafx.scene.layout.BorderPane]()

      rootPane = Option(rootLayout)

      // Create main stage
      stage = new PrimaryStage {
        title = "Hearty Nutrition Database"
        icons += new Image(getClass.getResource("/Images/nutritionlogo.jpeg").toExternalForm)
        scene = new Scene() {
          root = rootPane.get
          cssResource.foreach(css => stylesheets.add(css.toExternalForm))
        }
      }

      showLoginPage() // Show login page first.

    } catch {
      case e: Exception =>
        e.printStackTrace()
        System.err.println("Failed to initialize the application.")
        System.exit(1)
    }
  }

  def navigateTo(page: String): Unit = {
    val resource = getClass.getResource(page)
    if (resource == null) {
      System.err.println(s"$page resource not found.")
      return
    }

    try {
      val loader = new FXMLLoader(resource)
      val pane = loader.load[javafx.scene.layout.Pane]()
      rootPane match {
        case Some(root) =>
          root.setCenter(pane)
          // Re-apply current theme
          root.getScene.getStylesheets.clear()
          cssResource.foreach(css => root.getScene.getStylesheets.add(css.toExternalForm))
          println(s"Successfully loaded $page and set to center of BorderPane.")
        case None =>
          println("rootPane is not initialized.")
      }
    } catch {
      case e: Exception =>
        System.err.println(s"Unable to load page: $page")
        e.printStackTrace()
    }
  }

  def showLoginPage(): Unit = navigateTo("/AppPages/Login.fxml")

  def showHomePage(): Unit = if (Session.isAuthenticated) navigateTo("/AppPages/HomePage.fxml")
  else
    showLoginPage()

  def showAboutPage(): Unit = navigateTo("/AppPages/About.fxml")

  def showMealTracker(): Unit = {
    if (mealTrackerPane.isEmpty) {
      val resource = getClass.getResource("/AppPages/MealTracker.fxml")
      val loader = new FXMLLoader(resource)
      val pane = loader.load[javafx.scene.layout.Pane]()

      mealTrackerController = Some(loader.getController[Dchumon.view.MealTrackerController])
      mealTrackerPane = Some(pane)

      println(s"Loaded MealTrackerController hash: ${System.identityHashCode(mealTrackerController.get)}")
    }

    rootPane.foreach(_.setCenter(mealTrackerPane.get))
  }

  def showFoodSearch(): Unit = {
    mealTrackerController match {
      case Some(mealCtrl) =>
        val resource = getClass.getResource("/AppPages/FoodSearch.fxml")
        val loader = new FXMLLoader(resource)
        val pane = loader.load[javafx.scene.layout.Pane]()

        val foodSearchController = loader.getController[Dchumon.view.FoodSearchController]
        foodSearchController.setMealTrackerController(mealCtrl)

        println(s"Passing MealTrackerController hash: ${System.identityHashCode(mealCtrl)}")
        rootPane.foreach(_.setCenter(pane))
      case None =>
        println("MealTrackerController not loaded yet. Loading now...")
        showMealTracker()
    }
  }

  def showDonationHunger(): Unit = navigateTo("/AppPages/DonationHunger.fxml")

}