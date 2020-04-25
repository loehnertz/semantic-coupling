[![](https://jitpack.io/v/loehnertz/semantic-coupling.svg)](https://jitpack.io/#loehnertz/semantic-coupling)

# semantic-coupling
A library to compute the semantic coupling between source code files (e.g. classes)

## Participation
**The library currently only supports source code written in Java and the English language.**
I am very happy to pull other sets of programming and natural languages into this library, just open a PR for them.

## Usage
- Add this library to your own project: https://jitpack.io/#loehnertz/semantic-coupling/rc4
- If you are using Kotlin as well, just go ahead and instantiate the main class:
   ```
   val files: List<File> = retrieveFiles()
   val fileContentsMap: Map<String, String> = files.map { convertFileNameToIdentifier(it.absolutePath) to it.readText() }.toMap()  // A map of file name to raw file contents
   val semanticCouplingCalculator = SemanticCouplingCalculator(
       files = fileContentsMap,
       programmingLanguage = ProgrammingLanguage.JAVA,
       naturalLanguage = NaturalLanguage.EN
   )
   val semanticCouplings: List<SemanticCoupling> = semanticCouplingCalculator.calculate()
   ```
- If you are using Java, you can do pretty much the same:
    ```
    SemanticCouplingCalculator semanticCouplingCalculator = new SemanticCouplingCalculator(fileContentsMap, ProgrammingLanguage.JAVA, NaturalLanguage.EN);
    List<SemanticCoupling> semanticCouplings = semanticCouplingCalculator.calculate();
    ```

If you have any further questions, just open an issue or send me an e-mail.
