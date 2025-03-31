package BisayaPP;

import static BisayaPP.TokenType.*;
import java.util.ArrayList;
import java.util.List;
public class Parser {

    private static class ParseError extends RuntimeException{}

    private final List <Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    List<Stmt> parse(){
        List<Stmt> statements = new ArrayList<>();
        while(!isAtEnd()){
            statements.add(declaration());
        }
        return statements;

    }
    private Expr expression(){
        return assignment();
    }
    private Stmt declaration(){
        try {
            if(match(VAR)) return varDeclaration();
            // TODO: MUGNA
            if(match(MUGNA)) return mugnaDeclaration();
            return statement();
        } catch (ParseError e) {
            synchronize();
            return null;
        }
    }
    private Stmt varDeclaration(){
        Token name = consume(IDENTIFIER, "Expect variable name");

        Expr initializer = null;
        if(match(EQUAL)){
            initializer = expression();
        }
        consume(SEMICOLON, "Expect ';' after variable declarationzz");
        return new Stmt.Var(name, initializer);
    }
    private Stmt statement(){
        if(match(IF)) return ifStatement();
        if(match(PRINT)) return printStatement();
        if(match(LEFT_BRACE)) return new Stmt.Block(block());

        // BISAYA++
        if(match(IPAKITA)) return ipakitaStatement();

        return expressionStatement();
    }
    private Stmt ifStatement(){
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PARENT, "Expect ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if(match(ELSE)){
            elseBranch = statement();
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }
    private Stmt printStatement(){
        Expr value = expression();
        consume(SEMICOLON,"Expect ';' after value");
        return new Stmt.Print(value);
    }
    private Stmt expressionStatement(){
        Expr expr = expression();
        // consume(SEMICOLON, "Expect ';' after value");
        return new Stmt.Expression(expr);
    }
    private List<Stmt> block(){
        List<Stmt> statements = new ArrayList<>();
        // TODO: USEFUL ATA FOR SUGOD AND KATAPUSAN
        while(!check(RIGHT_BRACE) && !isAtEnd()){ 
            statements.add(declaration());
        }
        consume(RIGHT_BRACE,"Dapat naay '}' ubug human sa code");
        return statements;
    }
    private Expr assignment() {
        Expr expr = equality();
        if(match(EQUAL)){
            Token equals = previous();
            Expr value = assignment();

            if(expr instanceof Expr.Variable){
                Token name = ((Expr.Variable) expr).name;
                // System.out.println("nipasok here");
                return new Expr.Assign(name,value);
            }
            error(equals,"Invalid assignment target.");
        }
        return expr;

    }
    // BISAYA++
    private Stmt ipakitaStatement(){
        Expr expr = expression();
        return new Stmt.Ipakita(expr);
    }
    // [21]
    private Stmt mugnaDeclaration(){
        Token type;
        if(match(NUMERO)){
            type = previous();
        } else if(match(TIPIK)){
            type = previous();
        } else if(match(LETRA)){
            type = previous();
        } else if(match(TINUOD)){
            type = previous();
        } else{
            throw error(peek(), "After MUGNA Expect a data type.");
        }
        List<Token> names = new ArrayList<>();

        names.add(consume(IDENTIFIER, "Expect variable name"));
        while(match(COMMA)){
            // System.out.println("went hereeee!!");
            names.add(consume(IDENTIFIER, "Expect variable name"));
        }
        Expr initializer = null;
        if(match(EQUAL)){
            initializer = expression();
        }
        // System.out.println("nipasok here squared" + peek().type);
        // consume(SEMICOLON, "Expect ';' after variable declarationzz");

        // System.out.println("After");

        // System.out.println("The names"+names+" The type "+ type+" The initializer"+initializer);
        return new Stmt.Mugna(type,names,initializer);
    
    }


    // ======================
    // [20]
    private Expr equality(){
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL, NOT_EQUAL)) { // TODO: REMOVE BANGEQUAL
            // System.out.println("dapat di ka musud diri cuz = rmaan sha");
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }
    private Expr comparison(){
        Expr expr = term();
        while (match(GREATER,GREATER_EQUAL,LESS,LESS_EQUAL)){
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }
    private Expr term(){
        Expr expr = factor();

        while(match(MINUS, PLUS,CONCAT)){
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr factor(){
        Expr expr = unary();
        while(match(SLASH,STAR)){
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr unary(){
        if(match(BANG, MINUS)){
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator,right);
        }
        return primary();
    }
    private Expr primary(){
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NIL)) return new Expr.Literal(null);

        if(match(NUMBER,STRING,CHAR)){
            // System.out.println("nipasok here: "+previous().type);
            // System.out.println("pasok ka hereeeeee??");
            // System.out.print(previous().literal);
            return new Expr.Literal(previous().literal);
        }
        if(match(IDENTIFIER)){
            return new Expr.Variable(previous());
        }
        if(match(LEFT_PAREN)){
            Expr expr = expression();
            consume(RIGHT_PARENT, "Expect ')' after expression." );
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expect expression");
    }

    private boolean match(TokenType... types){
        for(TokenType type: types){
            if(check(type)){
                advance();
                return true;
            }
        }
        return false;
    }
    private Token consume(TokenType type, String message){
        if(check(type)) return advance();
        throw error(peek(),message);
    }

    private boolean check(TokenType type){
        if(isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance(){
        if(!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd(){
        return peek().type == EOF;
    }
    private Token peek(){
        return tokens.get(current);
    }
    private Token previous(){
        return tokens.get(current -1);
    }
    private ParseError error(Token token, String message){
        BisayaPlusPlus.error(token,message);
        return new ParseError();
    }
    private void synchronize(){
        advance();

        while(!isAtEnd()){
            if(previous().type == SEMICOLON) return;

            switch(peek().type){
                // case CLASS:
                // case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:

                // BISAYA++
                case MUGNA:

                    return;
            }
            advance();
        }
    }

    
}
