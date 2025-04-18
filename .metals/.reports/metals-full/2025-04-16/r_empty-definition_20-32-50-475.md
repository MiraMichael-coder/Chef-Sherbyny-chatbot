error id: scala/Predef.String#
file:///C:/Users/Mira/Desktop/uni/year%202/Semster%202/Advanced%20Prog/Project/chatbot/src/main/scala/Main.scala
empty definition using pc, found symbol in pc: scala/Predef.String#
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -String.
	 -String#
	 -String().
	 -scala/Predef.String.
	 -scala/Predef.String#
	 -scala/Predef.String().
offset: 856
uri: file:///C:/Users/Mira/Desktop/uni/year%202/Semster%202/Advanced%20Prog/Project/chatbot/src/main/scala/Main.scala
text:
```scala
import scala.io.Source
import scala.util.{Try, Success, Failure}
import java.io.File

case class Dish(name: String, country: String, isVegetarian: Boolean, ingredients: List[String])

object FoodDB {
  def loadDishes(): List[Dish] = {
    Try {
      Source.fromFile("C:\\Users\\Mira\\Desktop\\uni\\year 2\\Semster 2\\Advanced Prog\\Project\\chatbot\\src\\main\\scala\\data\\food.txt").getLines().toList
        .filterNot(_.trim.isEmpty)
        .filterNot(_.startsWith("#"))
        .map { line =>
          val parts = line.split("\\|")
          Dish(
            name = parts(0).trim,
            isVegetarian = parts(1).trim.toBoolean,
            ingredients = parts(2).trim.split(",").map(_.trim).toList
          )
        }
    }println(s"Error loading dishes: ${e.getMessage}")
        List.empty
  }
def greetUser():Strin@@g =
{
    "Hi I'm Shef Sherbyny Chatbot ,Ask me about pizza, sushi, or type 'quiz'!"
}





@main def main():Unit ={
val dishes = FoodDB.loadDishes()
  dishes.foreach { dish =>
    println(s"${dish.name.padTo(10, ' ')} from ${dish.country.padTo(10, ' ')} veg: ${dish.isVegetarian} ingredients: ${dish.ingredients.mkString(", ")}")
  
}
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/Predef.String#