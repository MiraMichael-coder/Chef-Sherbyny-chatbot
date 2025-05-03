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
    CusineQuestions("lebanese", s"${basePath}lebanese_questions.txt"),
    CusineQuestions("korean", s"${basePath}korean_questions.txt"),
    CusineQuestions("french", s"${basePath}french_questions.txt"),
    CusineQuestions("general", s"${basePath}general_questions.txt"),
    CusineQuestions("italian", s"${basePath}italian_questions.txt")
  ) 
  val quizezbyCategory: Map[String, List[QuizQuestion]] = categories.map { category =>
    category.name -> loadQuizFromFile(category.filePath)
  }.toMap
    private def loadQuizFromFile(filePath: String): List[QuizQuestion] = {
  Try {
    val source = Source.fromFile(filePath, "UTF-8")
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
            correctAnswer = parts(2).trim.toLowerCase
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
    val questionPart =  question.question
    val choicesPart = formattedChoices.mkString("\n")

    s"$questionPart\n\n$choicesPart"
}
    def getRandomQuestions(questions: List[QuizQuestion]): List[QuizQuestion] = {
        scala.util.Random.shuffle(questions).take(5)
    }
    def checkAnswer(question: QuizQuestion, userAnswer: String): Boolean = { // lo 3ayez yanswer by index 
        question.correctAnswer== userAnswer.toLowerCase 
    }

  def startquiz(cuisine: String = "general", handleTypos: String => String): Unit = {
    
  val questions = Quizez.getQuizByCategory(cuisine)
  if (questions.isEmpty){
    Analytics.logInteraction(
        s"No quiz found for:: ${cuisine.capitalize} cuisine",
        "ended quiz",
        UserState.getName
      )
    
    println(s"No questions available for $cuisine cuisine.")
  } else {
    Analytics.logInteraction(
        s"User requested quiz for: ${cuisine.capitalize} ",
        "Starting quiz",
        UserState.getName
      )
    println(s"\nStarting $cuisine quiz...")
    println("Type your answer (A/B/C/D) or the full answer. Type 'quit' to exit.\n")

    val randomQuestions = Quizez.getRandomQuestions(questions)
    val totalQuestions = randomQuestions.size

    // Process questions and collect results immutably
    val (answers, finalScore) = randomQuestions.foldLeft((List.empty[Boolean], 0, 1)) {
  case ((accAnswers, score, currentIndex), question) =>
    println(s"Question $currentIndex: " + Quizez.formatQuestion(question))
    val userAnswer = scala.io.StdIn.readLine("Your answer: ").toLowerCase

    if (userAnswer == "quit") {
      println(summarizeQuizResults(accAnswers, randomQuestions.take(currentIndex - 1)))
      return
    }

        val normalizedAnswer = handleTypos(userAnswer).toLowerCase.trim match {
          case "a" | "first" | "1" | "one" if question.choices.size > 0 => question.choices(0).toLowerCase
          case "b" | "second" | "2" | "two" if question.choices.size > 1 => question.choices(1).toLowerCase
          case "c" | "third" | "3" | "three" if question.choices.size > 2 => question.choices(2).toLowerCase
          case "d" | "fourth" | "4" | "four" if question.choices.size > 3 => question.choices(3).toLowerCase
          case _ => handleTypos(userAnswer)
      }
      val isCorrect = Quizez.checkAnswer(question, normalizedAnswer)
      val userName= UserState.getName
    Analytics.logQuizInteraction(
      question.question,
      normalizedAnswer,
      question.correctAnswer,
      isCorrect,
      userName
    )
 
    if (isCorrect) {
      println("Deliciously correct! 🥙 Let’s keep going")
      (accAnswers :+ true, score + 1, currentIndex + 1)
    } else {
      println(s"❌ Wrong! Correct answer: ${question.correctAnswer}")
      (accAnswers :+ false, score, currentIndex + 1)
    }
} match { case (ans, sc, _) => (ans, sc) }

    println(summarizeQuizResults(answers, randomQuestions))

   Analytics.analyzeQuizPerformance(UserState.getName,handleTypos)
}
  }
  def summarizeQuizResults(answers: List[Boolean], questions: List[QuizQuestion]): String = {
  val totalQuestions = answers.length
  val correctCount = answers.count(_ == true)
  val percentage = (correctCount.toDouble / totalQuestions * 100).round
  val correctRatio = s"$correctCount/$totalQuestions"
  val percentageStr = s"$percentage%"

  val performanceFeedback = percentage match {
    case p if p >= 80 => "🌟 Excellent! You're a culinary expert!"
    case p if p >= 60 => "👍 Good job! You know your food well."
    case p if p >= 40 => "🤔 Not bad! Keep exploring different cuisines."
    case _ => "🍳 Beginner's luck! Try the quiz again to improve."
  }

  val missed = answers.zipWithIndex.filter(!_._1).map(_._2)
  val missedDetails = missed.map { i =>
    val q = questions(i)
    s"❌ Q: ${q.question}\n   Correct answer: ${q.correctAnswer.capitalize}"
  }

  val missedSummary = 
    if (missedDetails.nonEmpty) "\nMissed Questions:\n" + missedDetails.mkString("\n\n")
    else "\n✅ You got everything right! No missed questions."

  s"""|Quiz Results:
      |
      |-----------------------------
      |Total questions: $totalQuestions
      |Correct answers: $correctRatio
      |Percentage: $percentageStr
      |
      |$performanceFeedback
      |$missedSummary
      |""".stripMargin




  }


}
