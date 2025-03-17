import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

//import java.util.Scanner;

// MAIN EYYY
public class BisayaPlusPlus {
    static boolean hadError = false;
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

        for(Token token: tokens){
            System.out.println(token);
        }
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
}
