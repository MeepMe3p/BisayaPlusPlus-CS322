package BisayaPP;

import static BisayaPP.TokenType.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code Scanner} class serves as the LEXICAL ANALYZER for the Bisaya++ language.
 * <p> It takes raw source code as input and breaks it down into a sequence of {@link Token} objects
 * that represent the smallest units of meaning (lexemes) such as keywords, identifiers, literals,
 * and operators.
 *
 * <p> The scanner handles:
 * <ul>
 *   <li>Recognizing Bisaya++ keywords and symbols</li>
 *   <li>Parsing literals (numbers, strings, characters)</li>
 *   <li>Tracking line numbers for error reporting</li>
 *   <li>Skipping whitespace and comments</li>
 * </ul>
 *
 * <p> This class is typically the first step in a compiler or interpreter pipeline and provides the
 * foundation for syntax analysis (parsing).
 *
 * @see Token
 * @see TokenType
 */
public class Scanner {
    // [5]
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    // Mapping of reserved keywords (e.g., "if", "while", "IPAKITA") to their corresponding TokenType
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();

        // Standard Lox-style keywords
        keywords.put("and",    AND);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);

        // Bisaya++ custom keywords
        keywords.put("SUGOD",     SUGOD);
        keywords.put("MUGNA",     MUGNA);
        keywords.put("IPAKITA",   IPAKITA);
        keywords.put("KATAPUSAN", KATAPUSAN);
        keywords.put("DAWAT",     DAWAT);
        keywords.put("KUNG",      KUNG);
        keywords.put("WALA",      KUNGWALA);
        keywords.put("PUNDOK",    PUNDOK);
        keywords.put("ALANG",     ALANG);
        keywords.put("SA",        SA);
        keywords.put("MINTRAS",   MINTRAS);
        keywords.put("NUMERO",    NUMERO);
        keywords.put("LETRA",     LETRA); // Optional: future usage
        keywords.put("TINUOD",    TINUOD);
        keywords.put("UG",        UG);
        keywords.put("O",         O);
        keywords.put("DILI",      DILI);
        keywords.put("TIPIK",     TIPIK);
    }

    // Constructor takes source code as input
    Scanner(String source) {
        this.source = source;
    }

    // The main scanner method: loops through the source and scans tokens until the end
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current; // Update start of the next lexeme
            scanToken();     // Scan one token
        }

        // Append an EOF token at the end
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    // Returns true if the end of the source is reached
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // Scans a single token based on the current character
    private void scanToken() {
        char c = advance(); // Consume current character

        switch (c) {
            // Single-character tokens
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PARENT); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case ':': addToken(COLON); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '%': addToken(MODULO); break;
            case '&': addToken(CONCAT); break;

            // Handle '+' and '++'
            case '+':
                if (match('+')) {
                    addToken(PLUSPLUS);
                } else {
                    addToken(PLUS);
                }
                break;

            // Handle '-', '--', and comments
            case '-':
                if (match('-')) {
                    if (peek() == ' ' || peek() == '\n' || isAtEnd()) {
                        // Skip inline comment
                        while (peek() != '\n' && !isAtEnd()) advance();
                    } else {
                        addToken(MINUSMINUS);
                    }
                } else {
                    addToken(MINUS);
                }
                break;

            // Two-character operators
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<':
                if (match('=')) addToken(LESS_EQUAL);
                else if (match('>')) addToken(NOT_EQUAL);
                else addToken(LESS);
                break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;

            // Division or line comment
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance(); // Skip comment
                } else {
                    addToken(SLASH);
                }
                break;

            // Whitespace characters: skip
            case ' ':
            case '\r':
            case '\t':
                break;

            // New line: increment line counter
            case '\n':
                line++;
                break;

            // String literal
            case '"':
                string();
                break;

            // Character literal (e.g., 'a')
            case '\'':
                character();
                break;

            // Custom Bisaya++ character syntax: [a]
            case '[':
                letra();
                break;

            // Shortcut string token (e.g., `$`)
            case '$':
                addToken(STRING, "\n");
                break;

            // Catch unrecognized characters or identifiers
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    BisayaPlusPlus.error(line, "Di mao nga character.");
                }
                break;
        }
    }

    // Recognize identifiers and keywords
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);

        // Handle compound keywords like "KUNG DILI"
        if (text.equals("KUNG") && match(' ')) {
            String nextWord = readNextWord();

            if (nextWord.equals("DILI")) {
                addToken(KUNGDILI);
                return;
            } else if (nextWord.equals("WALA")) {
                addToken(KUNGWALA);
                return;
            }
            current -= nextWord.length(); // Roll back if not compound keyword
        }

        // Use keyword map or default to IDENTIFIER
        TokenType type = keywords.getOrDefault(text, IDENTIFIER);

        // Ensure certain keywords are followed by a colon
        if ((type == IPAKITA || type == DAWAT) && !match(':')) {
            BisayaPlusPlus.error(line, "Expected ':' after " + type);
        }

        addToken(type);
    }

    // Reads the next word for compound keyword detection
    private String readNextWord() {
        while (peek() == ' ') advance();  // Skip spaces
        int wordStart = current;

        while (isAlphaNumeric(peek())) advance();

        return source.substring(wordStart, current);
    }

    // Handle string literals enclosed in "
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            BisayaPlusPlus.error(line, "Unterminated string,");
            return;
        }

        advance(); // Consume closing "

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    // Handle character literal: 'a'
    private void character() {
        if (isAtEnd()) {
            BisayaPlusPlus.error(line, "Walay sulod ang LETRA");
        }
        char value = advance();
        if (peek() != '\'') {
            BisayaPlusPlus.error(line, "Kailangan isira gamit ang ']'");
        }
        advance();
        addToken(CHAR, value);
    }

    // Handle Bisaya++ custom char syntax: [a]
    private void letra() {
        if (isAtEnd()) {
            BisayaPlusPlus.error(line, "Walay sulod ang LETRA");
        }
        char value = advance();
        if (peek() != ']') {
            BisayaPlusPlus.error(line, "Kailangan isira gamit ang ']'");
        }
        advance();
        addToken(CHAR, value);
    }

    // Parse integer and float numbers
    private void number() {
        while (isDigit(peek())) advance();

        boolean isFloat = false;

        if (peek() == '.' && isDigit(peekNext())) {
            isFloat = true;
            advance(); // Consume '.'
            while (isDigit(peek())) advance();
        }

        String numberLiteral = source.substring(start, current);

        if (isFloat)
            addToken(NUMBER, Double.parseDouble(numberLiteral));
        else
            addToken(NUMBER, Integer.parseInt(numberLiteral));
    }

    // Helpers for matching, peeking, and character classification

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(current);
    }

    private char peekNext() {
        return (current + 1 >= source.length()) ? '\0' : source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private char advance() {
        return source.charAt(current++);
    }

    // Adds token with no literal value
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    // Adds token with optional literal value (like 123, "hello")
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
