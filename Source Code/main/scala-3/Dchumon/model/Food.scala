package Dchumon.model

import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty, SimpleStringProperty}
import scalafx.beans.property.{DoubleProperty, IntegerProperty, StringProperty}

import java.time.LocalDate

class Food(
            name: String,
            calories: Int,
            protein: Double,
            fat: Double,
            carbohydrates: Double
          ) {
  val nameProperty: StringProperty = new StringProperty(this, "name", name)
  val caloriesProperty: IntegerProperty = new IntegerProperty(this, "calories", calories)
  val proteinProperty: DoubleProperty = new DoubleProperty(this, "protein", protein)
  val fatProperty: DoubleProperty = new DoubleProperty(this, "fat", fat)
  val carbsProperty: DoubleProperty = new DoubleProperty(this, "carbohydrates", carbohydrates)
  val selected = new SimpleBooleanProperty(false)
  val dateAdded = new SimpleStringProperty(LocalDate.now.toString)

  // New: quantity property
  val quantityProperty: IntegerProperty = new IntegerProperty(this, "quantity", 0)

  def dateAddedProperty: SimpleStringProperty = dateAdded
  def getDateAdded: String = dateAdded.get()
  def setDateAdded(value: String): Unit = dateAdded.set(value)

  // New: increment & decrement quantity
  def incrementQuantity(): Unit =
    quantityProperty.set(quantityProperty.get + 1)

  def decrementQuantity(): Unit = {
    if (quantityProperty.get > 0) { // avoid going below 0
      quantityProperty.set(quantityProperty.get - 1)
    }
  }

  // override toString for easier debugging
  override def toString: String =
    s"Food(name=$name, calories=$calories, protein=$protein, fat=$fat, carbohydrates=$carbohydrates, quantity=${quantityProperty.get})"
}

object Food {
  def apply(name: String, calories: Int, protein: Double, fat: Double, carbohydrates: Double): Food =
    new Food(name, calories, protein, fat, carbohydrates)
}
