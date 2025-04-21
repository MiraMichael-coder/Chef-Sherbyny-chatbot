import scala.io.Source
import scala.util.{Try, Success, Failure}
import java.nio.file.{Paths, Files}
import java.io.File
import scala.util.Random
case class QuizQuestion( question: String,choices: List[String], correctAnswer: String)
case class CusineQuestions(name: String, filePath: String)
object Quizez
{
  private val basePath = "C:\\Users\\Mira\\Desktop\\uni\\year 2\\Semster 2\\Advanced Prog\\Project\\chatbot\\src\\main\\scala\\data\\"
  
  val categories: List[CusineQuestions] = List(
    CusineQuestions("egyptian", s"${basePath}egyption_questions.txt"),
    CusineQuestions("lebanese", s"${basePath}lebanese_foods.txt"),
    CusineQuestions("korean", s"${basePath}korean_foods.txt"),
    CusineQuestions("french", s"${basePath}french_foods.txt"),
    CusineQuestions("general", s"${basePath}general_questions.txt")
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
    def getRandomQuestions(questions: List[QuizQuestion]): List[QuizQuestion] = {
        scala.util.Random.shuffle(questions).take(5)
    }
    def checkAnswer(question: QuizQuestion, userAnswer: String): Boolean = { // lo 3ayez yanswer by index 
        question.correctAnswer== userAnswer.toLowerCase 
    }

def startquiz(cuisine: String ="general", handleTypos: String => String): Unit = {
  val questions = Quizez.getQuizByCategory(cuisine)
  if (questions.isEmpty) {
    println(s"No questions available for $cuisine cuisine.")
  } else {
    println(s"\nStarting $cuisine quiz (${questions.size} questions)...")
    println("Type your answer (A/B/C/D) or the full answer. Type 'quit' to exit.\n")
    
    // Get random questions
    val randomQuestions = Quizez.getRandomQuestions(questions)

    var score = 0
    randomQuestions.foreach{ question =>
      println(Quizez.formatQuestion(question))
      val userAnswer = scala.io.StdIn.readLine("Your answer: ").toLowerCase
      val normalizedAnswer = userAnswer.toLowerCase match {
        case "a" if question.choices.size > 0 => question.choices(0).toLowerCase
        case "b" if question.choices.size > 1 => question.choices(1).toLowerCase
        case "c" if question.choices.size > 2 => question.choices(2).toLowerCase
        case "d" if question.choices.size > 3 => question.choices(3).toLowerCase
        case _ => handleTypos(userAnswer) 
      
      }
      if (normalizedAnswer == "quit") {
        println("Exiting the quiz.")
        println(s"Your partial score: $score/${randomQuestions.size}")
      }
      if (Quizez.checkAnswer(question, normalizedAnswer)) {
        println("Correct!")
        score += 1
      } else {
        println(s"Wrong! The correct answer is: ${question.correctAnswer}")
      }
    }
    println(s"\nYour final score: $score/${randomQuestions.size}")
  }
}
}
