error id: 
file:///C:/Users/Mira/Desktop/uni/year%202/Semster%202/Advanced%20Prog/Project/chatbot/src/main/scala/Main.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 157
uri: file:///C:/Users/Mira/Desktop/uni/year%202/Semster%202/Advanced%20Prog/Project/chatbot/src/main/scala/Main.scala
text:
```scala
object Main {
def greetUser(): String = {
    val availableCuisines = FoodDB.categories.map(_.name.capitalize).mkString(", ")
    s"""Hi I'm Chef Sherbyny@@ Chatbot!
I know about these cuisines: $availableCuisines
Ask me about specific dishes or type 'quiz'!""".stripMargin
  }

def showDishes(category: String): Unit = {
    val dishes = FoodDB.getDishesByCategory(category)
    if (dishes.isEmpty) {
      println(s"No dishes found for $category cuisine")
    } else {
      println(s"\n=== ${category.capitalize} Dishes ===")
      dishes.foreach { dish =>
        val vegStatus = if (dish.isVegetarian) "Vegetarian" else "Non-vegetarian"
        println(s"◎ ${dish.name} ($vegStatus)")
        println(s"   Ingredients: ${dish.ingredients.mkString(", ")}")
      }
    }
  }
def parseInput(input: String): List[String] = {
  input.toLowerCase.split("\\s+").toList
}

def main(): Unit = {
  println(greetUser())
  
  // Example usage:
  showDishes("egyptian")
  
  // Uncomment to test other features:
  // println("\n=== Vegetarian Dishes ===")
  // FoodDB.dishesByCategory("italian").filter(_.isVegetarian).foreach(println)
  
  // println("\n=== Dishes with Rice ===")
  // FoodDB.findDishesByIngredient("rice").foreach(println)
}  
}  
```


#### Short summary: 

empty definition using pc, found symbol in pc: 