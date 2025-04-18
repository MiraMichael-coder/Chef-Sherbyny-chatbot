error id: 659A2122FF680800D4337F8991F6EF8D
file:///C:/Users/Mira/Desktop/uni/year%202/Semster%202/Advanced%20Prog/Project/chatbot/src/main/scala/Quizgenerator.scala
### java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/Mira/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.10/scala-library-2.13.10-sources.jar!/scala/collection/immutable/List.scala

occurred in the presentation compiler.



action parameters:
offset: 2107
uri: file:///C:/Users/Mira/Desktop/uni/year%202/Semster%202/Advanced%20Prog/Project/chatbot/src/main/scala/Quizgenerator.scala
text:
```scala
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
  )
 
  // Load all dishes by category with error handling
  val quizezbyCategory: Map[String, List[Dish]] = categories.map { category =>
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
            correctAnswer = parts(2)
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
  val choiceLetters = List('a', 'b', 'c', 'd')
  
  // Step 2: Combine each choice with its letter
  val pairedChoices = question.choices.m@@(choiceLetters)
  
  // Step 3: Format each pair
  var formattedLines = List[String]()
  for ((choice, letter) <- pairedChoices) {
    formattedLines = formattedLines :+ s"$letter. $choice"
  }
  
  // Step 4: Combine everything
  val questionPart = s"Question: ${question.question}"
  val choicesPart = formattedLines.mkString("\n")
  
  s"$questionPart\n\n$choicesPart"
}
  def checkAnswer(question: QuizQuestion, userAnswer: String): Boolean = {
    question.correctAnswer== userAnswer.toLowerCase 
  }
  
 










}
```


presentation compiler configuration:
Scala version: 3.3.1
Classpath:
<WORKSPACE>\.bloop\chatbot\bloop-bsp-clients-classes\classes-Metals-sbAePoemQn296LobfCuFMw== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.10.4\semanticdb-javac-0.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala3-library_3\3.3.1\scala3-library_3-3.3.1.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\modules\scala-parser-combinators_3\2.3.0\scala-parser-combinators_3-2.3.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.10\scala-library-2.13.10.jar [exists ]
Options:
-Xsemanticdb -sourceroot <WORKSPACE>




#### Error stacktrace:

```
java.base/sun.nio.fs.WindowsPathParser.normalize(WindowsPathParser.java:182)
	java.base/sun.nio.fs.WindowsPathParser.parse(WindowsPathParser.java:153)
	java.base/sun.nio.fs.WindowsPathParser.parse(WindowsPathParser.java:77)
	java.base/sun.nio.fs.WindowsPath.parse(WindowsPath.java:92)
	java.base/sun.nio.fs.WindowsFileSystem.getPath(WindowsFileSystem.java:232)
	java.base/java.nio.file.Path.of(Path.java:147)
	java.base/java.nio.file.Paths.get(Paths.java:69)
	scala.meta.io.AbsolutePath$.apply(AbsolutePath.scala:58)
	scala.meta.internal.metals.MetalsSymbolSearch.$anonfun$definitionSourceToplevels$2(MetalsSymbolSearch.scala:70)
	scala.Option.map(Option.scala:242)
	scala.meta.internal.metals.MetalsSymbolSearch.definitionSourceToplevels(MetalsSymbolSearch.scala:69)
	scala.meta.internal.pc.completions.CaseKeywordCompletion$.scala$meta$internal$pc$completions$CaseKeywordCompletion$$$sortSubclasses(MatchCaseCompletions.scala:331)
	scala.meta.internal.pc.completions.CaseKeywordCompletion$.matchContribute(MatchCaseCompletions.scala:279)
	scala.meta.internal.pc.completions.Completions.advancedCompletions(Completions.scala:393)
	scala.meta.internal.pc.completions.Completions.completions(Completions.scala:186)
	scala.meta.internal.pc.completions.CompletionProvider.completions(CompletionProvider.scala:91)
	scala.meta.internal.pc.ScalaPresentationCompiler.complete$$anonfun$1(ScalaPresentationCompiler.scala:147)
```
#### Short summary: 

java.nio.file.InvalidPathException: Illegal char <:> at index 3: jar:file:///C:/Users/Mira/AppData/Local/Coursier/cache/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.10/scala-library-2.13.10-sources.jar!/scala/collection/immutable/List.scala