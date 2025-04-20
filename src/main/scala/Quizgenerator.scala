import scala.io.Source
import scala.util.{Try, Success, Failure}
import java.nio.file.{Paths, Files}
import java.io.File

case class QuizQuestion( question: String,choices: List[String], correctAnswer: String)
case class CusineQuestions(name: String, filePath: String)
object Quizez
{
  private val basePath = "C:\\Users\\Mira\\Desktop\\uni\\year 2\\Semster 2\\Advanced Prog\\Project\\chatbot\\src\\main\\scala\\data\\"
  
  val categories: List[CusineQuestions] = List(
    CusineQuestions("egyptian", s"${basePath}egyption_questions.txt"),
    //CusineQuestions("lebanese", s"${basePath}lebanese_foods.txt"),
    //CusineQuestions("korean", s"${basePath}korean_foods.txt"),
    //CusineQuestions("french", s"${basePath}french_foods.txt")
    //CusineQuestions("General ", s"${basePath}general_questions.txt")
  )
  val quizezbyCategory: Map[String, List[QuizQuestion]] = categories.map { category =>
    category.name -> loadQuizFromFile(category.filePath)
  }.toMap
    private def loadQuizFromFile(filePath: String): List[QuizQuestion] = {
  Try {
    val source = Source.fromFile(filePath)
    try {
      source.getLines()
        .filterNot(_.trim.isEmpty)
        .filterNot(_.startsWith("#"))
        .filter(_.contains("|"))
        .map { line =>
          val parts = line.split("\\|").map(_.trim)
          QuizQuestion(
            question = parts(0),
            choices = parts(1).split(",").map(_.trim).toList,
            correctAnswer = parts(2).toLowerCase
          )
        }.toList
    } finally {
      source.close()
    }
  }.recover {
    case e: Exception =>
      println(s"Error loading quiz from $filePath: ${e.getMessage}")
      List.empty
  }.get
}
    def getQuizByCategory(category: String): List[QuizQuestion] = {
        quizezbyCategory.getOrElse(category.toLowerCase, {
        println(s"Warning: Category '$category' not found")
        List.empty
        })
    }
    def formatQuestion(question: QuizQuestion): String = {
    // Step 1: Define the letters we'll use
    val choiceLetters = List('A', 'B', 'C', 'D')
    
    // Step 2: Combine each choice with its letter
    val pairedChoices = question.choices.map(c=>
        
    (c.head.toUpper + c.tail,choiceLetters(question.choices.indexOf(c))))
    // Step 3: Format each pair
    val formattedChoices = pairedChoices.map { 
    case (choice, letter) => s"$letter. $choice" 
    }
    // Step 4: Combine everything
    val questionPart = s"Question: ${question.question}"
    val choicesPart = formattedChoices.mkString("\n")

    s"$questionPart\n\n$choicesPart"
}
    def checkAnswer(question: QuizQuestion, userAnswer: String): Boolean = { // lo 3ayez yanswer by index 
        question.correctAnswer== userAnswer.toLowerCase 
    }
}

//generate random 5 question s
// save answers score
// 
/* object EgyptianQuizPrinter {
  def main(args: Array[String]): Unit = {
    // Initialize the quiz system
    val quizSystem = Quizez
    
    // Get all Egyptian cuisine questions
    val egyptianQuestions = quizSystem.getQuizByCategory("egyptian")
    
    // Print all questions with formatted choices
    egyptianQuestions.foreach { question =>
      println(quizSystem.formatQuestion(question))
      println() // Add space between questions
    }
    
    // Print total count
    println(s"\nTotal questions: ${egyptianQuestions.size}")
  }
} */