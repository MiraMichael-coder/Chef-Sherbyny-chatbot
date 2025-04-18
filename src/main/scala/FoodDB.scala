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
  
  val categories: List[CuisineCategory] = List(
    CuisineCategory("egyptian", s"${basePath}egyption_foods.txt"),
    CuisineCategory("lebanese", s"${basePath}lebanese_foods.txt"),
    CuisineCategory("korean", s"${basePath}korean_foods.txt"),
    CuisineCategory("french", s"${basePath}french_foods.txt")
    //CuisineCategory("Lebaneese", s"${basePath}lebanon_foods.txt")
  )

  // Load all dishes by category with error handling
  val dishesByCategory: Map[String, List[Dish]] = categories.map { category =>
    category.name -> loadDishesFromFile(category.filePath)
  }.toMap

  private def loadDishesFromFile(filePath: String): List[Dish] = {
    Try {
      val source = Source.fromFile(filePath)
      try {
        source.getLines()
          .filterNot(_.trim.isEmpty)
          .filterNot(_.startsWith("#"))
          .filter(_.contains("|"))
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


  def getDishesByCategory(category: String): List[Dish] = {
    dishesByCategory.getOrElse(category.toLowerCase, {
      println(s"Warning: Category '$category' not found")
      List.empty
    })
  }

  def findDishesByIngredient(ingredient: String): List[Dish] = {
    dishesByCategory.values.flatten
      .filter(_.ingredients.exists(_.equalsIgnoreCase(ingredient)))
      .toList
  }
  /* def startInteractiveQuiz(cuisine: String): Unit = {
  val questions = Quizez.getQuizByCategory(cuisine)
  if (questions.isEmpty) {
    println(s"No questions available for $cuisine cuisine.")
  } else {
    println(s"\nStarting $cuisine quiz (${questions.size} questions)...")
    println("Type your answer (A/B/C/D) or the full answer. Type 'quit' to exit.\n")
    
    var score = 0
    var shouldContinue = true
    var questionIndex = 0

    while (shouldContinue && questionIndex < questions.size) {
      val question = questions(questionIndex)
      println(s"Question ${questionIndex + 1}:")
      println(Quizez.formatQuestion(question))
      
      val userAnswer = scala.io.StdIn.readLine().trim.toLowerCase
      val correctedInput = handleTypos(userAnswer)
      if (correctedInput == "quit") {
        shouldContinue = false
      } else {
        if (Quizez.checkAnswer(question, correctedInput)) {
          println("Correct!\n")
          score += 1
        } else {
          println(s"Incorrect. The correct answer was: ${question.correctAnswer}\n")
        }
        questionIndex += 1
      }
    }

    if (shouldContinue) {
      println(s"Quiz complete! Your score: $score/${questions.size}")
    } else {
      println(s"Quiz stopped. Partial score: $score")
    }
  }
} */

}
