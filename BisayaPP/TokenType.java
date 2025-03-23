package BisayaPP;// TOKEN TYPE ENUM FOR DETERMINING WHAT TYPE OF LEXEME IS IT

public enum TokenType {
//    Single Character tokens
    LEFT_PAREN, RIGHT_PARENT, LEFT_BRACE, RIGHT_BRACE, COMMA, DOT, MINUS, PLUS, MODULO,
    SEMICOLON, SLASH, STAR, 

//    One or two character tokens
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL, NOT_EQUAL,

//    Literals - lexemes where you need to traverse to identify
    IDENTIFIER, STRING, NUMBER, CHAR,

//    Keywords
    AND, ELSE, FALSE, FOR, IF,NIL,OR, PRINT,RETURN, TRUE, WHILE, VAR, START,END,

    

    EOF,

    // BISAYA ++
    SUGOD, MUGNA,  IPAKITA, KATAPUSAN, DAWAT,KUNG, KUNGKUNG,KUNGWALA,PUNDOK,ALANGSA,
    NUMERO, LETRA,TIPIK, TINUOD,
    UG, O, DILI,

    COLON, NEWLINE,



}
