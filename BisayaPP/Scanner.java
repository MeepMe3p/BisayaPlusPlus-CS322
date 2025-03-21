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
//            case '&':addToken(BisayaPP.TokenType.CONCAT);break;
            case '-':
                if(match('-')){
                    // for comments 
                    while(peek() != '\n' && isAtEnd()) advance();
                } else{
                    addToken(MINUS);
                }
                break;

            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL : NOT_EQUAL);
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
                break;
            case '"':
                string();
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
        TokenType type = keywords.get(text);
        System.out.println(text);
        if(type == null) 
            type = IDENTIFIER;
        addToken(type);
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
    private void number(){
        while(isDigit(peek()))
            advance();
        if(peek() == '.' && isDigit(peekNext())){
            advance();
            while(isDigit(peek()))
                advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start,current)));

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
        keywords.put("SUGOD",  SUGOD);
        keywords.put("MUGNA",  MUGNA);
        keywords.put("IPAKITA",  IPAKITA);
        keywords.put("KATAPUSAN",  KATAPUSAN);
        keywords.put("DAWAT",  DAWAT);
        keywords.put("KUNG",  KUNG);
        keywords.put("KUNGKUNG",  KUNGKUNG);
        keywords.put("KUNGWALA", KUNGWALA);
        keywords.put("PUNDOK",  PUNDOK);
        keywords.put("ALANGSA",  ALANGSA);
        keywords.put("NUMERO",  NUMERO);
        keywords.put("LETRA",  TIPIK);
        keywords.put("TINUOD",  TINUOD);
        keywords.put("UG",  UG);
        keywords.put("O",  O);
        keywords.put("DILI",  DILI);
        keywords.put("TIPIK",  TIPIK);
    //     SUGOD, MUGNA,  IPAKITA, KATAPUSAN, DAWAT,KUNG, KUNGKUNG,KUNGWALA,PUNDOK,ALANGSA,
    // NUMERO, LETRA,TIPIK, TINUOD,
    // UG, O, DILI,

    }

}
