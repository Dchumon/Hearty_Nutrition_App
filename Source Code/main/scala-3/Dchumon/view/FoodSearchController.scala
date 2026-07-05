package Dchumon.view

import scalafx.collections.ObservableBuffer
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.scene.layout.HBox
import Dchumon.model.Food
import scalafx.geometry.Pos
import javafx.collections.transformation.FilteredList
import java.io.FileNotFoundException
import scala.io.Source
import javafx.scene.control.{Alert, ButtonType, Dialog, TextInputDialog}
import java.util.Optional
import javafx.scene.control.{Dialog, ButtonType}
import javafx.scene.layout.GridPane
import javafx.scene.control.{TextField, Label}
import scala.jdk.OptionConverters.*

class FoodSearchController {

  @FXML private var foodTableView: TableView[Food] = _
  @FXML private var nameColumn: TableColumn[Food, String] = _
  @FXML private var caloriesColumn: TableColumn[Food, java.lang.Integer] = _
  @FXML private var proteinColumn: TableColumn[Food, java.lang.Double] = _
  @FXML private var fatColumn: TableColumn[Food, java.lang.Double] = _
  @FXML private var carbsColumn: TableColumn[Food, java.lang.Double] = _
  @FXML private var quantityColumn: TableColumn[Food, Number] = _
  @FXML private var addToMealColumn: TableColumn[Food, Void] = _
  @FXML private var searchField: TextField = _

  private val foodList: ObservableBuffer[Food] = ObservableBuffer()
  private var mealTrackerController: Option[MealTrackerController] = None

  def setMealTrackerController(controller: MealTrackerController): Unit = {
    println(s"setMealTrackerController called with: $controller")
    mealTrackerController = Option(controller)
    mealTrackerController match {
      case Some(_) => println(s"MealTrackerController successfully assigned: $controller")
      case None    => println("MealTrackerController is NULL!")
    }
  }

  // Fallback: Retrieve from AppContext if missing
  def getMealTrackerController: MealTrackerController = {
    mealTrackerController.getOrElse({
      val fallbackController = AppContext.getMealTrackerController
      println(s"Retrieved MealTrackerController from AppContext: $fallbackController")
      fallbackController
    })
  }

  /** Adds selected food to MealTrackerController */
  def addSelectedFoodToMealTracker(selectedFood: Food): Unit = {
   println(s"Setting MealTrackerController: ${mealTrackerController.toString}")
   println(s"Selected food: ${selectedFood.toString}")
   println(s"MealTrackerController is set: ${mealTrackerController.isDefined}")
    mealTrackerController match {
      case Some(controller) =>
        controller.addFoodFromSearch(selectedFood)
        println(s"Food item '${selectedFood.nameProperty.get}' added to MealTrackerController.")
      case None =>
        println("MealTrackerController is not set!")
    }
  }

  /** Initializes the food search window */
  def initialize(): Unit = {
    try {
      // Load CSV food data
      val filePath = "src/main/resources/CSV_Files/FoodNutrition.csv"
      loadFoodDataFromCSV(filePath)

      // Bind table columns to Food properties
      bindColumns()

      // Configure the Add-to-Meal buttons
      setupAddToMealColumn()

      // Setup filtered food list for search
      setupFilteredList()

      println(s"MealTrackerController instance: ${System.identityHashCode(this)}")
    } catch {
      case _: FileNotFoundException =>
        println("Error: FoodNutrition.csv not found!")
      case e: Exception =>
        println(s"Error during initialization: ${e.getMessage}")
        e.printStackTrace()
    }
  }

  /** Loads food data from a CSV file */
  private def loadFoodDataFromCSV(filePath: String): Unit = {
    try {
      val source = Source.fromFile(filePath)
      val lines = source.getLines().toList
      source.close()

      val dataLines = lines.drop(1) // Skip header row
      dataLines.foreach { line =>
        val columns = line.split(",").map(_.trim)
        if (columns.length >= 11) {
          try {
            val food = Food(
              name = columns(2),
              calories = columns(3).toIntOption.getOrElse(0),
              protein = columns(10).toDoubleOption.getOrElse(0.0),
              fat = columns(4).toDoubleOption.getOrElse(0.0),
              carbohydrates = columns(8).toDoubleOption.getOrElse(0.0)
            )
            foodList += food
          } catch {
            case _: Exception => println(s"Skipping invalid line: $line")
          }
        }
      }
    } catch {
      case _: FileNotFoundException =>
        println("Error: CSV file not found!")
      case e: Exception =>
        println(s"Error loading CSV data: ${e.getMessage}")
        e.printStackTrace()
    }
  }

  /** Binds table columns to food properties */
  private def bindColumns(): Unit = {
    nameColumn.setCellValueFactory(cd => cd.getValue.nameProperty)
    caloriesColumn.setCellValueFactory(cd => cd.getValue.caloriesProperty.asObject)
    proteinColumn.setCellValueFactory(cd => cd.getValue.proteinProperty.asObject)
    fatColumn.setCellValueFactory(cd => cd.getValue.fatProperty.asObject)
    carbsColumn.setCellValueFactory(cd => cd.getValue.carbsProperty.asObject)

    // Bind quantity column to `quantityProperty`
    quantityColumn.setCellValueFactory(cd => cd.getValue.quantityProperty)
  }

  /** Configures the Add-to-Meal column with increment and decrement buttons */
  private def setupAddToMealColumn(): Unit = {
    addToMealColumn.setCellFactory { _ =>
      new TableCell[Food, Void] {
        val btnMinus = new Button("-")
        val btnPlus = new Button("+")
        val quantityLabel = new Label()

        val hbox = new HBox(5, btnMinus, quantityLabel, btnPlus)
        hbox.setAlignment(Pos.Center)

        btnMinus.setOnAction { _ =>
          handleQuantityChange(getIndex, increment = false)
        }
        btnPlus.setOnAction { _ =>
          handleQuantityChange(getIndex, increment = true)
        }

        override def updateItem(item: Void, empty: Boolean): Unit = {
          super.updateItem(item, empty)
          if (empty) {
            setGraphic(null)
          } else {
            setGraphic(hbox)
          }
        }
      }
    }
  }

  private def handleQuantityChange(index: Int, increment: Boolean): Unit = {
    if (index >= 0 && index < foodTableView.getItems.size) {
      val food = foodTableView.getItems.get(index)

      if (food != null) {
        if (increment) {
          // Show the quantity input dialog
          showQuantityInputDialog(food) match {
            case Some(quantity) =>
              food.quantityProperty.set(food.quantityProperty.get + quantity)
              mealTrackerController.foreach { controller =>
                println(s"Adding $quantity of ${food.nameProperty.get} to MealTracker.")
                controller.addFoodFromSearch(food)
              }
            case None =>
              println("User canceled quantity input.")
          }
        } else {
          // Decrement logic
          val currentQty = food.quantityProperty.get
          if (currentQty > 1) {
            food.quantityProperty.set(currentQty - 1)
          } else {
            food.quantityProperty.set(0) // or remove from mealTracker if you want
          }
        }

        // Refresh the table
        foodTableView.refresh()
      } else {
        println(s"Error: Food item at index $index is null.")
      }
    } else {
      println(s"Error: Invalid index $index for quantity change.")
    }
  }


  /** Sets up a filtered list based on the search field input */
  private def setupFilteredList(): Unit = {
    val filteredList = new FilteredList(foodList, _ => true)

    searchField.textProperty().addListener { (_, _, newValue) =>
      val lowerCaseFilter = Option(newValue).getOrElse("").trim.toLowerCase
      filteredList.setPredicate { food =>
        food.nameProperty.get.toLowerCase.contains(lowerCaseFilter)
      }
    }

    foodTableView.setItems(filteredList)
  }



  private def showQuantityInputDialog(food: Food): Option[Int] = {
    val dialog = new TextInputDialog("1")
    dialog.setTitle("Add Food Quantity")
    dialog.setHeaderText(s"Add ${food.nameProperty.get} to Meal Tracker?")
    dialog.setContentText("Enter the quantity:")

    dialog.showAndWait().toScala.flatMap { input =>
      try {
        val quantity = input.trim.toInt
        if (quantity > 0) Some(quantity)
        else {
          print("Invalid Quantity", "Quantity must be greater than 0.")
          None
        }
      } catch {
        case _: NumberFormatException =>
          print("Invalid Input", "Please enter a valid integer for the quantity.")
          None
      }
    }
  }
  }
