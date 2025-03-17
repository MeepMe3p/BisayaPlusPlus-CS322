package BisayaPP;

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
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PARENT); break;
            case '{':addToken(TokenType.LEFT_BRACE);break;
            case '}':addToken(TokenType.RIGHT_BRACE);break;
            case ',':addToken(TokenType.COMMA);break;
            case '.':addToken(TokenType.DOT);break;
            case '+':addToken(TokenType.PLUS);break;
            // case '-':addToken(BisayaPP.TokenType.MINUS);break;
            case ';':addToken(TokenType.SEMICOLON);break;
            case '*':addToken(TokenType.STAR);break;

            // case '/':addToken(BisayaPP.TokenType.SLASH);break;
            case '%':addToken(TokenType.MODULO);break;
//            case '&':addToken(BisayaPP.TokenType.CONCAT);break;
            case '-':
                if(match('-')){
                    // for comments 
                    while(peek() != '\n' && isAtEnd()) advance();
                } else{
                    addToken(TokenType.MINUS);
                }
                break;

            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL : TokenType.NOT_EQUAL);
                break;

            case '<':
                // addToken(match('=') ? BisayaPP.TokenType.LESS_EQUAL : BisayaPP.TokenType.LESS);
                if(match('=')){
                    addToken(TokenType.LESS_EQUAL);
                } else if(match('>')){
                    addToken(TokenType.NOT_EQUAL);
                } else{
                    addToken(TokenType.LESS);
                }
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            
            case '/':
                if(match('/')){
                    // TODO: REMOVE LATER
                    while(peek() != '\n' && isAtEnd()) advance();
                }else{
                    addToken(TokenType.SLASH);
                }
                break;
            case 'o':
                if(match('r')){
                    addToken(TokenType.OR);
                }
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
                string(); break;
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
        if(type == null) 
            type = TokenType.IDENTIFIER;
        addToken(type);
    }
    // [11]
    private void string(){
        while(peek() != '"' && isAtEnd()){
            if(peek() == 'n'){
                line++;
            }
            advance();
        }
        if(isAtEnd()){
            BisayaPlusPlus.error(line,"Unterminated string,");
            return;
        }
        advance();
        String value = source.substring(start+1,current-1);
        addToken(TokenType.STRING,value);
    }
    private void number(){
        while(isDigit(peek()))
            advance();
        if(peek() == '.' && isDigit(peekNext())){
            advance();
            while(isDigit(peek()))
                advance();
        }
        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start,current)));

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
        keywords.put("and",    TokenType.AND);
        // keywords.put("class",  CLASS);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("for",    TokenType.FOR);
        // keywords.put("fun",    FUN);
        keywords.put("if",    TokenType.IF);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("or",     TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        // keywords.put("super",  SUPER);
        // keywords.put("this",   THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);


        // FROM BISAYA++
        keywords.put("SUGOD",  TokenType.SUGOD);
        keywords.put("MUGNA",  TokenType.MUGNA);
        keywords.put("IPAKITA",  TokenType.IPAKITA);
        keywords.put("KATAPUSAN",  TokenType.KATAPUSAN);
        keywords.put("DAWAT",  TokenType.DAWAT);
        keywords.put("KUNG",  TokenType.KUNG);
        keywords.put("KUNGKUNG",  TokenType.KUNGKUNG);
        keywords.put("KUNGWALA",  TokenType.KUNGWALA);
        keywords.put("PUNDOK",  TokenType.PUNDOK);
        keywords.put("ALANGSA",  TokenType.ALANGSA);
        keywords.put("NUMERO",  TokenType.NUMERO);
        keywords.put("LETRA",  TokenType.TIPIK);
        keywords.put("TINUOD",  TokenType.TINUOD);
        keywords.put("UG",  TokenType.UG);
        keywords.put("O",  TokenType.O);
        keywords.put("DILI",  TokenType.DILI);
        keywords.put("TIPIK",  TokenType.TIPIK);
    //     SUGOD, MUGNA,  IPAKITA, KATAPUSAN, DAWAT,KUNG, KUNGKUNG,KUNGWALA,PUNDOK,ALANGSA,
    // NUMERO, LETRA,TIPIK, TINUOD,
    // UG, O, DILI,

    }

}
