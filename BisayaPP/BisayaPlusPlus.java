package BisayaPP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

//import java.util.BisayaPP.Scanner;

/**
 * The main entry point for the Bisaya++ programming language interpreter.
 *
 * <p>This class supports two modes of execution:
 * <ul>
 *   <li><b>Script mode</b> - Executes a full Bisaya++ source file passed as a command-line argument.
 *   <li><b>REPL mode</b> - Starts an interactive prompt where users can type and evaluate code line by line.
 *
 * <p>The class handles high-level control flow for interpreting code:
 * <ol>
 *   <li>Reading the source (from file or user input)
 *   <li>Scanning the source into tokens
 *   <li>Parsing tokens into an abstract syntax tree (AST)
 *   <li>Executing the AST using the {@link Interpreter}
 * </ol>
 *
 * <p> It also manages error reporting, using {@code hadError} and {@code hadRuntimeError}
 * flags to track syntax and runtime issues.
 */
public class BisayaPlusPlus {
    private static final Interpreter interpreter = new Interpreter();

    // These flags track whether any syntax or runtime errors occurred
    static boolean hadRuntimeError =false;
    static boolean hadError = false;

    // [1]
    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Usage: Bisaya++ [script]");
            System.exit(64); // Exit code 64 = command line usage error
        } else if (args.length == 1) {
            try {
                runFile(args[0]); // Run code from a file
            } catch (IOException ignored) {}
        } else {
            try {
                System.out.println();
                runPrompt(); // Run REPL (interactive prompt)
            } catch (IOException ignored) {}
        }
        System.out.println("No error");
    }

    /**
     * [2] Runs the program from a script file.
     * @param path the file path to the Bisaya++ script
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // [1.1] Exit with error codes depending on the error type
        if (hadError) System.exit(65);         // Syntax error
        if (hadRuntimeError) System.exit(70);  // Runtime error
    }

    /**
     * [3] Runs the interpreter in REPL (interactive) mode.
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader=  new BufferedReader(input);

        // [1.3]
        // [T1]
        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break; // End on Ctrl+D or Ctrl+Z
            run(line);
            hadError = false; // Reset error flag for next input
        }
    }

    /**
     * Core function that handles:
     * 1. Scanning (tokenizing) source code
     * 2. Parsing tokens into AST
     * 3. Interpreting the AST
     * @param source The source code string
     */
    private static void run(String source) {
        // Lexical analysis: convert source code to tokens
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // Parsing: convert tokens to syntax tree (statements)
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if syntax errors occurred
        if (hadError) return;

        // Interpret the program
        interpreter.interpret(statements);
    }

    // Error Reporting Utilities ------------------------

    /**
     * Reports a compile-time error on a specific line.
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Helper method to format error messages.
     */
    private static void report(int line, String where, String message) {
        System.err.println("[Sa linya nga " + line + "] Adunay Error" + where + ": " + message);
        hadError = true;
    }

    /**
     * Reports a compile-time error associated with a specific token.
     */
    static void error(Token token, String message){
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    /**
     * Reports a runtime error (e.g. division by zero).
     */
    static void runTimeError(RuntimeError error){
        System.err.println(error.getMessage() + "\n [line "+ error.token.line + "]");
        hadRuntimeError = true;
    }
}
