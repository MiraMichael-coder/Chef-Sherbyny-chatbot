import scala.io.Source
import scala.util.{Try, Success, Failure}
import java.nio.file.{Paths, Files}
import java.io.File

case class Dish(
  name: String,
  isVegetarian: Boolean,
  ingredients: List[String],
  recipeSteps: List[String],
  recipeLink: String
)
case class CuisineCategory(name: String, filePath: String)

object FoodDB {
  // Use relative paths for better portability
  private val basePath = "C:\\Users\\Mira\\Desktop\\uni\\year 2\\Semster 2\\Advanced Prog\\Project\\chatbot\\src\\main\\scala\\data\\"

  val foodTrivia: Map[String, List[String]] = Map(
  "🇪🇬 Egyptian Cuisine" -> List(
    "Ancient Fast Food: Taameya (Egyptian falafel) dates back to the Pharaohs!",
    "Bread as Currency: Pyramid workers were paid in bread and beer.",
    "Koshari’s Global Roots: Fusion of Italian, Indian, and Middle Eastern flavors.",
    "Molokhia Mystery: Ancient Egyptians believed it had magical healing powers.",
    "Pigeon Perfection: Stuffed pigeon is a delicacy dating back centuries."
  ),
  "🇰🇷 Korean Cuisine" -> List(
    "Kimchi in Space: Specially developed for astronauts in 2008.",
    "Bibimbap’s Lucky Leftovers: Originally a Lunar New Year cleanup dish.",
    "Tteokbokki’s Royal Upgrade: Started as a mild palace dish.",
    "Fermentation Nation: Kimchi has over 200 known varieties.",
    "Rice Cakes & Luck: Tteok is often eaten during celebrations for good fortune."
  ),
  "🇫🇷 French Cuisine" -> List(
    "Croissant Conspiracy: Actually Austrian in origin!",
    "Cheese Drama: France has over 1,000 cheeses.",
    "Soup for Rebels: Onion soup was 'soup of drunkards'.",
    "Michelin Mission: The Michelin Guide was created to sell more tires, not food.",
    "Wine & Water: Some French kids drink watered-down wine with meals!"
  ),
  "🇱🇧 Lebanese Cuisine" -> List(
    "Hummus Wars: Lebanon made a 4-ton platter to claim ownership.",
    "Za’atar Secrets: Some blends include crushed rose petals!",
    "Falafel’s Holy Roots: Possibly invented by Coptic Christians.",
    "World Record Tabouleh: Lebanon made 3.5 tons of tabouleh in one bowl.",
    "Arak Magic: Traditional anise drink turns white when water is added."
  )
)
  
  val Foodjokes: Map[String, List[String]] = Map(
  "🇪🇬 Egyptian Cuisine" -> List(
    "Why did the tomato turn red? Because it saw the salad dressing!",
    "Why did the falafel break up with the pita? It found someone a little more 'spicy'!",
    "Why did the Egyptian chef get kicked out of school? Because he kept getting 'baked' in class!"
  ),
  "🇰🇷 Korean Cuisine" -> List(
    "Why did the kimchi break up with the tofu? It found someone a little more 'fermented'!",
    "Why did the rice cake go to therapy? It had too many 'sticky' issues!",
    "Why did the Korean chef get kicked out of school? Because he couldn't stop 'stir-frying'!"
  ),
  "🇫🇷 French Cuisine" -> List(
    "Why did the croissant go to the doctor? Because it was feeling a little 'buttery'!",
    "Why did the French chef get kicked out of school? Because he couldn't stop 'whisking' around!",
    "Why did the cheese break up with the bread? It found someone a little more 'grate'!"
  ),
  "🇱🇧 Lebanese Cuisine" -> List(
    "Why did the hummus break up with the pita? It found someone a little more 'dip'-licious!",
    "Why did the Lebanese chef get kicked out of school? Because he couldn't stop 'spicing' things up!",
    "Why did the tabouleh go to therapy? It had too many 'parsley' issues!"
  )
)
  
 
  val categories: List[CuisineCategory] = List(
    CuisineCategory("egyptian", s"${basePath}egyption_foods.txt"),
    CuisineCategory("lebanese", s"${basePath}lebanese_foods.txt"),
    CuisineCategory("korean", s"${basePath}korean_foods.txt"),
    CuisineCategory("french", s"${basePath}french_foods.txt")
    //CuisineCategory("Lebaneese", s"${basePath}lebanon_foods.txt")
  )

  // Load all dishes by category with error handling
  // will be in the format (categoryName, List(Dish))
  val dishesByCategory: List[(String, List[Dish])] = categories.map { 
  category => (category.name, loadDishesFromFile(category.filePath)) 
}

  private def loadDishesFromFile(filePath: String): List[Dish] = {
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
              ingredients = parts(2).split(",").map(_.trim).toList,
              recipeSteps = parts(3).split(",").map(_.trim).toList,
              recipeLink = parts(4)
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
  def allTokensMatch(tokens: List[String], dishName: String): Boolean = tokens match {
  case Nil => true
  case head :: tail => dishName.contains(head) && allTokensMatch(tail, dishName)
}
  def getAllIngredients: List[String] = {
    val ingredients = getAllDishes.flatMap(_.ingredients).distinct.toList
    ingredients
}

  def getVegetarianDishes: List[Dish] = {
  getAllDishes.filter(_.isVegetarian)
}
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
  }
  

