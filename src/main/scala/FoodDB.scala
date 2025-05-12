import scala.io.Source
import scala.util.{Try, Success, Failure}
import java.nio.file.{Paths, Files}
import java.io.File
import scala.util.Random

case class Dish(
  name: String,
  isVegetarian: Boolean,
  Dish_Type: String,
  shortDescription: String,
  ingredients: List[String],
  recipeSteps: List[String],
  recipeLink: String
)
case class CuisineCategory(name: String, filePath: String)
// This object is responsible for loading food data from files
// and providing functionality to get dishes by category, ingredient, and trivia. 
// It uses a list of categories, each with a name and a file path to the dishes.
// The food trivia is hardcoded in a map, and the object provides methods
// to get trivia, random dish suggestions, and check if a cuisine or dish is valid.
object FoodDB {

  //change file path to your own
  private val basePath = "C:\\Users\\Mira\\Desktop\\uni\\year 2\\Semster 2\\Advanced Prog\\Project\\chatbot\\src\\main\\scala\\data\\"

  val foodTrivia: Map[String, List[String]] = Map(
  "🇪🇬 Egyptian Cuisine" → List(
"Ancient Fast Food: Taameya (Egyptian falafel) dates back to the Pharaohs!",
 "Bread as Currency: Pyramid workers were paid in bread and beer.",
 "Koshari's Global Roots: Fusion of Italian, Indian, and Middle Eastern flavors.",
 "Molokhia Mystery: Ancient Egyptians believed it had magical healing powers.",
"Pigeon Perfection: Stuffed pigeon is a delicacy dating back centuries.",
"Feseekh Tradition: Fermented fish eaten during Sham El-Nessim dates to ancient times.",
"Bread Obsession: Egyptians consume one of the highest per capita amounts of bread globally."
),

"🇰🇷 Korean Cuisine" → List(
 "Kimchi in Space: Specially developed for astronauts in 2008.",
 "Bibimbap's Lucky Leftovers: Originally a Lunar New Year cleanup dish.",
"Tteokbokki's Royal Upgrade: Started as a mild palace dish.",
"Fermentation Nation: Kimchi has over 200 known varieties.",
"Rice Cakes & Luck: Tteok is often eaten during celebrations for good fortune.",
"Banchan Bonanza: A traditional Korean meal can include 10+ small side dishes.",
"Black Day Noodles: Koreans eat black bean noodles (jajangmyeon) on April 14 to mourn being single."
),

"🇫🇷 French Cuisine "→ List(
"Croissant Conspiracy: Actually Austrian in origin!",
"Cheese Drama: France has over 1,000 cheeses.",
 "Soup for Rebels: Onion soup was 'soup of drunkards'.",
"Michelin Mission: The Michelin Guide was created to sell more tires, not food.",
"Wine & Water: Some French kids drink watered-down wine with meals!",
"Baguette Law: By law, a French baguette can only contain flour, water, salt, and yeast.",
 "Escargot Craze: France eats over 16,000 tons of snails per year."
),

"🇱🇧 Lebanese Cuisine" → List(
 "Hummus Wars: Lebanon made a 4-ton platter to claim ownership.",
"Za'atar Secrets: Some blends include crushed rose petals!",
"Falafel's Holy Roots: Possibly invented by Coptic Christians.",
 "World Record Tabouleh: Lebanon made 3.5 tons of tabouleh in one bowl.",
"Arak Magic: Traditional anise drink turns white when water is added.",
"Kibbeh Variety: Kibbeh has over 30 variations across Lebanon.",
"Manakish Mornings: Flatbread with za'atar is a common breakfast street food."
),

"🇮🇹 Italian Cuisine" → List(
"Tomato Surprise: Tomatoes weren't used in Italian cooking until the 16th century.",
"Pizza's Humble Origins: Pizza was originally street food for Naples' poor.",
"Pasta Shapes Galore: Italy has over 350 types of pasta!",
"Espresso Etiquette: Italians rarely drink cappuccino after 11 a.m.",
 "Gelato vs Ice Cream: Gelato has less fat but more flavor intensity.",
"Cheese Crown: Parmigiano Reggiano wheels are legally stamped and aged up to 36 months.",
 "Tiramisu Translation: 'Tiramisu' literally means 'pick me up'."
)


)
  
  // Define the categories and their corresponding file paths
  val categories: List[CuisineCategory] = List(
    CuisineCategory("egyptian", s"${basePath}egyption_foods.txt"),
    CuisineCategory("lebanese", s"${basePath}lebanese_foods.txt"),
    CuisineCategory("korean", s"${basePath}korean_foods.txt"),
    CuisineCategory("french", s"${basePath}french_foods.txt"),
    CuisineCategory("Italian", s"${basePath}italian_foods.txt")
  )

  // Load all dishes by category with error handling
  // will be in the format (categoryName, List(Dish))
  val dishesByCategory: List[(String, List[Dish])] = categories.map { 
  category => (category.name, loadDishesFromFile(category.filePath)) 
}
  // This function loads dishes from a file and returns a list of Dish objects
  private def loadDishesFromFile(filePath: String): List[Dish] = 
    {
    Try {
      val source = Source.fromFile(filePath)
      try {
        source.getLines()
          .filterNot(_.trim.isEmpty)
          .filterNot(_.startsWith("#"))
          .filter(_.contains("|"))
          .drop(1) 
          .map { line =>
            val parts = line.split("\\|").map(_.trim)
            Dish(
              name = parts(0),
              isVegetarian = parts(1).toBoolean,
              Dish_Type = parts(2),
              shortDescription = parts(3), 
              ingredients = parts(4).split(",").map(_.trim).toList,
              recipeSteps = parts(5).split(",").map(_.trim).toList,
              recipeLink = parts(6)
            )
          }.toList
      } finally {
        source.close()
      }
    }.recover {
      case e: Exception =>
        println(s"Error loading dishes from $filePath: ${e.getMessage}")
        List.empty
    }.get
  }
  def getAllDishes: List[Dish] = {
    dishesByCategory.foldLeft(List.empty[Dish]) {  case (acc, (_,dishes)) =>
      acc ++ dishes
    }
  }
  // Function to get all dishes by category
  def getDishesByCategory(category: String): List[Dish] = {
  dishesByCategory.find {
    case (cat, _) => cat.equalsIgnoreCase(category)
  } match {
    case Some((_, dishes)) => dishes
    case None =>
      println(s"Warning: Category '$category' not found")
      List.empty
  }
}
  // Function to get all dishes by ingredient
  // This function checks if the ingredient exists in any dish's ingredients
  def findDishesByIngredient(ingredient: String): List[Dish] = {
    val dishes=getAllDishes.filter(_.ingredients.exists(_.equalsIgnoreCase(ingredient))).toList
     dishes
  }
  def getDish(tokens: List[String]): Option[Dish] = {
  // 1. First try exact match by combining all tokens
  val exactMatch = getAllDishes.find(_.name.toLowerCase == tokens.mkString(" ").toLowerCase)
  
  if (exactMatch.isDefined) {
    exactMatch
  } else {
    // 2. If no exact match, look for dishes that contain all the tokens
    getAllDishes.find { dish => allTokensMatch(tokens, dish.name.toLowerCase)
}
  }
}
  // Helper function to check if all tokens are present in the dish name
  // This function checks if all tokens are present in the dish name
  // It uses recursion to check each token against the dish name
  def allTokensMatch(tokens: List[String], dishName: String): Boolean = tokens match {
  case Nil => true
  case head :: tail => dishName.contains(head) && allTokensMatch(tail, dishName)
}
  //Return all ingredients from all dishes
  def getAllIngredients: List[String] = {
    getAllDishes.foldLeft(List.empty[String]) { (acc, dish) =>
      acc ++ dish.ingredients
    }.distinct
  }
  // Return all dishes that are vegetarian
  def getVegetarianDishes: List[Dish] = {
  getAllDishes.filter(_.isVegetarian)
}
  //function to return trivia based on cuisine
  def getTrivia(cuisine: String): List[String] = {
    foodTrivia.collectFirst {
      case (key, trivia) if Typos.handleTypos(key.toLowerCase).contains(Typos.handleTypos(cuisine.toLowerCase)) => trivia
    }.getOrElse(Nil)
  }
  //Helper function to get a random trivia
  def getRandomTrivia(cuisine: String): Option[String] = {
    val triviaList = getTrivia(cuisine)
    if (triviaList.nonEmpty)
      Some(scala.util.Random.shuffle(triviaList).head)
    else
      None
  }
  // Function to get a random selection of dishes
  def getRandomDishSuggestions(count: Int = 2): List[Dish] = {
    Random.shuffle(getAllDishes).take(count)
  }
  // Function to check if a cuisine is valid
   def isValidCuisine(cuisine: String): Boolean = {
    categories.exists(_.name.equalsIgnoreCase(Typos.handleTypos(cuisine)))
  }
  // Function to check if a dish name is valid
  def isValidDish(dishName: String): Boolean = {
    getAllDishes.exists(d => d.name == Typos.handleTypos(dishName.toLowerCase))
  }
// Function to retrive the dish by name
  // This function checks if the dish name matches any dish in the database
  def getDishByName(dishName: String): Option[Dish] = {
    getAllDishes.find(d => Typos.handleTypos(d.name.toLowerCase) == Typos.handleTypos(dishName.toLowerCase))
  }

  def detectMultiWordIngredients(tokens: List[String]): List[String] = {
  val ingredientSet = getAllIngredients.map(_.toLowerCase)  

  // Tail-recursive helper with pattern matching
  @annotation.tailrec
  def extractPhrases( n: Int,remaining: List[String],acc: List[String] = Nil ): List[String] = 
  remaining match {
    case Nil => acc.reverse  // Base case: no more words
    case _ if(remaining.length < n ) => acc.reverse  // Base case: not enough words left
    case head :: tail =>
      val phrase = (head :: tail.take(n - 1)).mkString(" ").toLowerCase // Combine n words

      val newAcc = if (ingredientSet.contains(phrase)) phrase :: acc else acc

      extractPhrases(n, tail, newAcc)  // Move window by 1
  }

  // Combine 2-word and 3-word matches, then deduplicate
  (extractPhrases(2, tokens) ++ extractPhrases(3, tokens)).distinct
}
def detectMultiWordDishes(tokens: List[String]): List[String] = {
  val dishSet = FoodDB.getAllDishes.map(_.name) // Preload dish names

  // Tail-recursive helper to find n-word phrases
  @annotation.tailrec
  def extractPhrases(n: Int, remaining: List[String], acc: List[String] = Nil): List[String] =
    remaining match {
      case Nil => acc.reverse  // Base case: no more words
      case _ if( remaining.length < n )=> acc.reverse  // Not enough words left
      case _ =>
        val phrase = remaining.take(n).mkString(" ").toLowerCase
        val newAcc = if (dishSet.contains(phrase)) phrase :: acc else acc
        extractPhrases(n, remaining.tail, newAcc)  // Slide window by 1 word
    }

  // Check for 1-word, 2-word, and 3-word dish names (prioritizing longer matches)
  val maxPhraseLength = 3  // Adjust based on your longest dish name (e.g., "beef wellington")
  
  (extractPhrases(2, tokens) ++ extractPhrases(3, tokens)).distinct

}  
def findBestDishMatch(tokens: List[String]): Option[Dish] = {
  detectMultiWordDishes(tokens).foldLeft(Option.empty[Dish]) {
    case (None, dishName) => FoodDB.getAllDishes.find(_.name.equalsIgnoreCase(dishName))
    case (found, _) => found  // Keep first found result
  }.orElse {
    // Fall back to partial matching
    FoodDB.getAllDishes.find { dish =>
      val dishWords = dish.name.split(" ")
      tokens.exists { token =>
        dishWords.exists(dw => dw.contains(token) || token.contains(dw))
      }
    }
  }
}
  
  
}


