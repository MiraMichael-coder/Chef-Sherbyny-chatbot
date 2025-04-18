error id: `<none>`.
file:///C:/Users/Mira/Desktop/uni/year%202/Semster%202/Advanced%20Prog/Project/chatbot/src/main/scala/Main.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 275
uri: file:///C:/Users/Mira/Desktop/uni/year%202/Semster%202/Advanced%20Prog/Project/chatbot/src/main/scala/Main.scala
text:
```scala
import scala.io.Source
import scala.util.{Try, Success, Failure}

case class Dish(name: String, country: String, isVegetarian: Boolean, ingredients: List[String])

object FoodDB {
  // Load dishes from file
  def loadDishes(): List[Dish] = {
    Try(Source.fromFile("@@foods.txt").getLines().toList) match {
      case Success(lines) =>
        lines.filterNot(_.startsWith("#")).map { line =>
          val parts = line.split("\\|")
          Dish(
            name = parts(0),
            country = parts(1),
            isVegetarian = parts(2).toBoolean,
            ingredients = parts(3).split(",").toList
          )
        }
      case Failure(_) => 
        println("Error: Couldn't load dishes file!")
        List.empty
    }
  }
}

def greetUser():String =
{
    "Hi I'm Shef Sherbyny Chatbot ,Ask me about pizza, sushi, or type 'quiz'!"
}





@main def main():Unit ={
 val dishes = FoodDB.loadDishes()
// Find a dish
  dishes.find(_.name == "pizza") match {
  case Some(dish) => 
    println(s"${dish.name} ingredients: ${dish.ingredients.mkString(", ")}")
  case None => 
    println("Dish not found")
}
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.