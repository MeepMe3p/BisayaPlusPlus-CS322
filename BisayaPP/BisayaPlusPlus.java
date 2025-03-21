package BisayaPP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

//import java.util.BisayaPP.Scanner;

// MAIN EYYY
public class BisayaPlusPlus {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError =false;
//    [1]
    public static void main(String[] args) {
        if(args.length > 1){
            System.out.println("Usage: Bisaya++ [script]");
            System.exit(64);
        }else if(args.length ==1){
            try {
                runFile(args[0]);
            } catch (IOException ex) {
            }
        }else{
            try {
                System.out.println("aaaa");
                runPrompt();
            } catch (IOException ex) {
            }
        }
    }

// [2]
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

//  [1.1]
        if(hadError) System.exit(65);
        if(hadRuntimeError) System.exit(70);

    }

//  [3]
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader=  new BufferedReader(input);

//  [1.3]
//  [T1]
        for(;;){
            System.out.println("> ");
            String line = reader.readLine();
            if(line == null) break;
            run(line);

//          [1.2]
            hadError = false;
        }
    }

    private static void run(String source){
        Scanner scanner = new Scanner(source);
        List <Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        if(hadError) return;

        // System.out.println(new AstPrinter().print(expression));
        // for(Token token: tokens){
        //     System.out.println(token);
        // }

        interpreter.interpret(statements);
    }

    

//    FOR ERROR HANDLING
//    [R1]
    static void error(int line, String message){
        report(line,"", message);
    }
    private static void report(int line, String where,String message){
        System.err.println("[Sa linya nga "+ line + "] Adunay Error"+where +": "+message);
        hadError = true;
    }
    static void error(Token token, String message){
        if(token.type == TokenType.EOF){
            report(token.line, " at end",message);
        }else{
            report(token.line, " at '" + token.lexeme + "'",message);
        }
    }
    static void runTimeError(RuntimeError error){
        System.err.println(error.getMessage() + "\n [line "+ error.token.line + "]");
        hadRuntimeError = true;
    }
}
