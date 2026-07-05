package Dchumon.view

import Dchumon.model.Food
import javafx.beans.property.{DoubleProperty, IntegerProperty, SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml.FXML
import javafx.scene.control._
import javafx.scene.layout.HBox
import scala.jdk.CollectionConverters._
import Dchumon.view.FoodSearchController
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

class MealTrackerController {

  /** Represents a MealItem with a `Food` object and its corresponding quantity */
  case class MealItem(food: Food, quantity: IntegerProperty) {
    def totalCalories: Double = food.caloriesProperty.get * quantity.get
    def totalProtein: Double = food.proteinProperty.get * quantity.get
    def totalFat: Double = food.fatProperty.get * quantity.get
    def totalCarbs: Double = food.carbsProperty.get * quantity.get
  }

  // FXML fields
  @FXML private var foodTableView: TableView[MealItem] = _
  @FXML private var foodItemsColumn: TableColumn[MealItem, String] = _
  @FXML private var dateAddedColumn: TableColumn[MealItem, String] = _
  @FXML private var quantityColumn: TableColumn[MealItem, Number] = _
  @FXML private var actionsColumn: TableColumn[MealItem, Void] = _
  @FXML private var totalCalories: TextField = _
  @FXML private var totalProtein: TextField = _
  @FXML private var totalFat: TextField = _
  @FXML private var totalCarbohydrates: TextField = _

  // ObservableList to store meal items
  private val foodItems: ObservableList[MealItem] = FXCollections.observableArrayList[MealItem]()

  // Total nutritional values
  private val totalCaloriesValue: DoubleProperty = new SimpleDoubleProperty(0.0)
  private val totalProteinValue: DoubleProperty = new SimpleDoubleProperty(0.0)
  private val totalFatValue: DoubleProperty = new SimpleDoubleProperty(0.0)
  private val totalCarbohydratesValue: DoubleProperty = new SimpleDoubleProperty(0.0)

  /** Initializes the controller when FXML is loaded */
  @FXML
  def initialize(): Unit = {
    println(s"MealTrackerController instance: ${System.identityHashCode(this)}")
    bindTableColumns()
    setupActionsColumn()
    bindTotalsToFields()
    AppContext.setMealTrackerController(this)

  }

  /** Binds the columns of the table to the correct attributes of `MealItem` */
  private def bindTableColumns(): Unit = {
    foodItemsColumn.setCellValueFactory(cd => cd.getValue.food.nameProperty)
    dateAddedColumn.setCellValueFactory(cd => cd.getValue.food.dateAddedProperty)
    quantityColumn.setCellValueFactory(cd => cd.getValue.quantity)

    // Set data source for the TableView
    foodTableView.setItems(foodItems)
  }

  /** Configures buttons for increment (+) and decrement (-) in the actionsColumn */
  private def setupActionsColumn(): Unit = {
    actionsColumn.setCellFactory { _ =>
      new TableCell[MealItem, Void] {
        val btnMinus = new Button("-")
        val btnPlus = new Button("+")
        private val hbox = new HBox(5, btnMinus, btnPlus)

        btnMinus.setOnAction { _ =>
          val mealItem = getTableView.getItems.get(getIndex)
          // Decrement the quantity and recalculate totals
          if (mealItem.quantity.get > 0) {
            mealItem.quantity.set(mealItem.quantity.get - 1)
            calculateTotals()

            // Remove item if quantity reaches 0
            if (mealItem.quantity.get == 0) {
              getTableView.getItems.remove(mealItem)
              println(s"Removed ${mealItem.food.nameProperty.get} from MealTracker because quantity is 0.")
            }
          }
        }

        btnPlus.setOnAction { _ =>
          val mealItem = getTableView.getItems.get(getIndex)
          mealItem.quantity.set(mealItem.quantity.get + 1)
          calculateTotals()
          println(s"Incremented quantity of ${mealItem.food.nameProperty.get} to ${mealItem.quantity.get}.")
        }

        override def updateItem(item: Void, empty: Boolean): Unit = {
          super.updateItem(item, empty)
          setGraphic(if (empty) null else hbox)
        }
      }
    }
  }

  /** Dynamically binds total nutritional values to TextFields */
  private def bindTotalsToFields(): Unit = {
    totalCalories.textProperty().bind(totalCaloriesValue.asString("%.1f"))
    totalProtein.textProperty().bind(totalProteinValue.asString("%.1f"))
    totalFat.textProperty().bind(totalFatValue.asString("%.1f"))
    totalCarbohydrates.textProperty().bind(totalCarbohydratesValue.asString("%.1f"))
  }

  def addFoodFromSearch(food: Food): Unit = {
    val existingItem = foodItems.asScala.find(item =>
      item.food.nameProperty.get == food.nameProperty.get
    )

    existingItem match {
      case Some(mealItem) =>
        mealItem.quantity.set(mealItem.quantity.get + food.quantityProperty.get)
        mealItem.quantity.addListener((_, oldValue, newValue) => {
          println(s"Quantity updated: $oldValue -> $newValue")
          if (newValue.intValue() == 0) {
            foodItems.remove(mealItem)
            println(s"Removed ${mealItem.food.nameProperty.get} because quantity is 0.")
          }
        })
      case None =>
        val newMealItem = MealItem(food, new SimpleIntegerProperty(food.quantityProperty.get))
        foodItems.add(newMealItem)

        // Add listener for quantity == 0
        newMealItem.quantity.addListener((_, oldValue, newValue) => {
          if (newValue.intValue() == 0) {
            foodItems.remove(newMealItem)
            println(s"Removed ${newMealItem.food.nameProperty.get} because quantity is 0.")
          }
        })
    }

    // Recalculate totals
    calculateTotals()

    // Refresh the TableView to reflect changes
    foodTableView.refresh()
  }

  /** Recalculates the total nutritional values and updates the properties */
  private def calculateTotals(): Unit = {
    val (calories, protein, fat, carbs) = foodItems.asScala.foldLeft((0.0, 0.0, 0.0, 0.0)) {
      case ((c, p, f, cb), item) =>
        println(s"Processing: ${item.food.nameProperty.get}, Quantity: ${item.quantity.get}")
        (
          c + item.totalCalories,
          p + item.totalProtein,
          f + item.totalFat,
          cb + item.totalCarbs
        )
    }

    // Update the total properties
    totalCaloriesValue.set(calories)
    totalProteinValue.set(protein)
    totalFatValue.set(fat)
    totalCarbohydratesValue.set(carbs)
  }
}

object AppContext {
  private var mealTrackerController: MealTrackerController = _

  def setMealTrackerController(controller: MealTrackerController): Unit = {
    mealTrackerController = controller
  }

  def getMealTrackerController: MealTrackerController = mealTrackerController
}