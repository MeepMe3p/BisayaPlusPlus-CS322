package BisayaPP;

import static BisayaPP.TokenType.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// THE LEXICAL ANALYZER FOR DETERMINING WHAT LEXEME CHARACTER BELONGS TO
public class Scanner {
//    [5]
    private final String source;
    private final List <Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source){
        this.source = source;
    }
//    [5]
    List<Token> scanTokens(){
        while(!isAtEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF,"",null,line));
        // System.out.println(tokens); // TODO: DEBUGGING REMOVE LATER
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch(c){
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PARENT); break;
            case '{':addToken(LEFT_BRACE);break;
            case '}':addToken(RIGHT_BRACE);break;
            case ',':addToken(COMMA);break;
            case '.':addToken(DOT);break;
            case '+':addToken(PLUS);break;
            // case '-':addToken(BisayaPP.TokenType.MINUS);break;
            case ';':addToken(SEMICOLON);break;
            case '*':addToken(STAR);break;

            // case '/':addToken(BisayaPP.TokenType.SLASH);break;
            case '%':addToken(MODULO);break;
           case '&':addToken(CONCAT);break;
            case '-':
                if(match('-')){
                    // for comments 

                    while(peek() != '\n' && !isAtEnd()){
                        advance();
                    } 
                    
                } else{
                    addToken(MINUS);
                }
                break;

            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;

            case '<':
                // addToken(match('=') ? BisayaPP.TokenType.LESS_EQUAL : BisayaPP.TokenType.LESS);
                if(match('=')){
                    addToken(LESS_EQUAL);
                } else if(match('>')){
                    
                    addToken(NOT_EQUAL);
                } else{
                    addToken(LESS);
                }
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            
            case '/':
                if(match('/')){
                    // TODO: REMOVE LATER
                    while(peek() != '\n' && isAtEnd()) advance();
                }else{
                    addToken(SLASH);
                }
                break;
            case 'o':
                if(match('r')){
                    addToken(OR);
                }
                break;
            case ':':
                addToken(COLON);
                break;
            // IGNORE WHITE SPACES
            
            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                line++;
                // addToken(NEWLINE);    //TODO TEST REMOVE LEATER
                break;
            case '"':
                string();
                break;
            case '\'':
                character();
                // System.out.println("character went here");
                break;
            case '[':
                letra();
                break;
            case '$':
                addToken(STRING,"\n");
                break;
        default:
                if(isDigit(c)){
                    number();
                } else if(isAlpha(c)){
                    identifier();
                }
                else{
                    BisayaPlusPlus.error(line,"Di mao nga character."); break;
                }

        }
    }
    private void identifier(){
        while(isAlphaNumeric(peek()))
            advance();
        String text = source.substring(start,current);

        if(text.equals("KUNG") && match(' ')){
            String nextWord = readNextWord();
            if(nextWord.equals("DILI")){
                addToken(KUNGDILI);
                return;
            }else if(nextWord.equals("WALA")){
                addToken(KUNGWALA);
                return;
            }
            else{
                current -= nextWord.length();
            }
        }
        TokenType type = keywords.get(text);
        // System.out.println(text);
        
        if(type == null) {
            type = IDENTIFIER;
        }
        if(type == IPAKITA && !match(':')){
            BisayaPlusPlus.error(line,"Expected ':' after IPAKITA");
        }
        if(type == DAWAT && !match(':')){
            BisayaPlusPlus.error(line,"Expected ':' after DAWAT");

        }
        
        addToken(type);
        
    }
    private String readNextWord(){
        while (peek() == ' ') advance();  

        int wordStart = current; 
    
        while (isAlphaNumeric(peek())) advance(); 
    
        return source.substring(wordStart, current);
    }
    // [11]
    private void string(){
        while(peek() != '"' && !isAtEnd()){
            if(peek() == '\n') line++;
            advance();
        }
        if(isAtEnd()){
            BisayaPlusPlus.error(line,"Unterminated string,");
            return;
        }
        advance();
        String value = source.substring(start+1,current-1);
        addToken(STRING,value);
    }
    private void character() {
        if(isAtEnd()){
            BisayaPlusPlus.error(line, "Walay sulod ang LETRA");
        }
        char value = advance();
        if(peek() != '\''){
            BisayaPlusPlus.error(line, "Kailangan isira gamit ang ']'");
        }
        advance();
        addToken(CHAR,value);
    }
    private void letra(){
        if(isAtEnd()){
            BisayaPlusPlus.error(line, "Walay sulod ang LETRA");
        }
        char value = advance();
        if(peek() != ']'){
            BisayaPlusPlus.error(line, "Kailangan isira gamit ang ']'");
        }
        advance();
        addToken(CHAR,value);
    }
    
    
    private void number(){
        // while(isDigit(peek()))
        //     advance();
        // if(peek() == '.' && isDigit(peekNext())){
        //     advance();
        //     while(isDigit(peek()))
        //         advance();
        // }
        // addToken(NUMBER, Double.parseDouble(source.substring(start,current)));
        while (isDigit(peek())) 
        advance();

        boolean isFloat = false;

        if (peek() == '.' && isDigit(peekNext())) {
            isFloat = true; 
            advance();
            
            while (isDigit(peek())) 
                advance();
        }
        String numberLiteral = source.substring(start, current);
     
        if (isFloat) {
            addToken(NUMBER, Double.parseDouble(numberLiteral));
        } else {
            addToken(NUMBER, Integer.parseInt(numberLiteral)); 
        }
    }
    // [10]
    private boolean match(char next) {
        if(isAtEnd()) return false;
        if(source.charAt(current) != next) return false;
        current++;
        return true;
    }
    // [9]
    private char peek(){
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }
    private char peekNext(){
        if(current + 1 >= source.length())
            return '\0';
        return source.charAt(current+1);
    }
    private boolean isAlpha(char c){
        return (c >= 'a' && c <='z') ||
        (c>= 'A' && c <= 'Z') ||
        (c == '_');
    }
    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c);
    }
    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }
    // [6]
    private char advance() {
        // System.out.print(source.charAt(current));
        
        return source.charAt(current++);
    }
    // [7]
    private void addToken(TokenType tokenType) {
        addToken(tokenType,null);
    }
    // [8]
    private void addToken(TokenType type, Object literal){
        String text = source.substring(start,current);
        tokens.add(new Token(type,text,literal,line));
    }

    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        // keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        // keywords.put("fun",    FUN);
        keywords.put("if",    IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        // keywords.put("super",  SUPER);
        // keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);


        // FROM BISAYA++
        keywords.put("SUGOD",  SUGOD); // done
        keywords.put("MUGNA",  MUGNA); // done
        keywords.put("IPAKITA",  IPAKITA); // done
        keywords.put("KATAPUSAN",  KATAPUSAN); //done
        keywords.put("DAWAT",  DAWAT); // done
        keywords.put("KUNG",  KUNG); //done
        // keywords.put("DILI",  KUNGDILI);
        keywords.put("WALA", KUNGWALA); //done
        keywords.put("PUNDOK",  PUNDOK); //done
        keywords.put("ALANGSA",  ALANGSA);
        keywords.put("MINTRAS",  MINTRAS);
        keywords.put("NUMERO",  NUMERO); // done
        keywords.put("LETRA",  LETRA); // TODO
        keywords.put("TINUOD",  TINUOD); // done
        keywords.put("UG",  UG); // done
        keywords.put("O",  O); // done
        keywords.put("DILI",  DILI);  //done

        // keywords.put("\"OO\"",TRUE);
        // keywords.put("\"DILI\"",FALSE);
        keywords.put("TIPIK",  TIPIK); // done

    }

}
