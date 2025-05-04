package BisayaPP;

import static BisayaPP.TokenType.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Parser {
    private static class ParseError extends RuntimeException{}

    private final List <Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Entry point of the parser
    List<Stmt> parse(){
        List<Stmt> statements = new ArrayList<>();
        try {
            if(!peek().lexeme.equals("SUGOD")){
                throw error(peek(),"Kailangan sugod sugod");
            }

            statements.add(sugodStatement());
        } catch (ParseError e) {
            System.err.println("No error");

        }
        return statements;
    }


    // ======================== EXPRESSIONS ========================

    private Expr expression(){
        return assignment();
    }

    private Expr assignment() {
        // Expr expr = equality();
        Expr expr = or();
        if(match(EQUAL)){
            Token equals = previous();
            Expr value = assignment();

            if(expr instanceof Expr.Variable){
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name,value);
            }
            error(equals,"Invalid assignment target.");
        }
        return expr;
    }

    private Expr or() {
        Expr expr = and();
        while(match(O)){
            Token operator=  previous();
            Expr right = and();
            expr = new Expr.Logical(expr,operator,right);
        }
        return expr;
    }

    private Expr and() {
        Expr expr = equality();
        while(match(UG)){
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr,operator,right);
        }
        return expr;
    }

    private Expr equality(){
        Expr expr = comparison();

        // while (match(BANG_EQUAL, EQUAL_EQUAL, NOT_EQUAL)) { // TODO: REMOVE BANGEQUAL
        while (match( EQUAL_EQUAL, NOT_EQUAL)) { // TODO: REMOVE BANGEQUAL
            // System.out.println("dapat  ka musud diri cuz == rmaan sha");
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
        while(match(SLASH,STAR,MODULO)){
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary(){
        if(match(BANG, MINUS,DILI)){
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator,right);
        }
        // return primary();
        return postfix();
    }

    private Expr postfix(){
        Expr expr = primary();
        while(match(PLUSPLUS, MINUSMINUS)){
            Token operator = previous();
            expr = new Expr.Postfix(expr,operator);
        }
        return expr;
    }

    private Expr primary(){
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NIL)) return new Expr.Literal(null);

        if(match(NUMBER,STRING,CHAR)){
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


    // ======================== STATEMENTS ========================

    private Stmt declaration(){
        try {
            if(match(VAR))
                return varDeclaration();
            if(match(MUGNA))
                return mugnaDeclaration();
            return statement();
        } catch (ParseError e) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration(){
        Token name = consume(IDENTIFIER, "Kailangan naay variable name");

        Expr initializer = null;
        if(match(EQUAL)){
            initializer = expression();
        }
        consume(SEMICOLON, "Expect ';' after variable declarations");
        return new Stmt.Var(name, initializer);
    }

    private Stmt whileStatement(){
        consume(LEFT_PAREN,"Expect '(' after 'while'");
        Expr condition = expression();
        consume(RIGHT_PARENT,"Expect ')' after condition");
        consume(PUNDOK, "Kulangan ug pundok");
        Stmt body = statement();
        return new Stmt.While(condition,body);
    }

    private Stmt statement(){
        if(match(ALANG)){
            consume(SA, "Kailangan naay SA inig human sa ALANG");
            return forStatement();
        }

        if(match(IF)) return ifStatement();
        if(match(PRINT)) return printStatement();
        if(match(LEFT_BRACE)) return new Stmt.Block(block());
        if(match(WHILE)) return whileStatement();

        // BISAYA++
        if(match(IPAKITA)) return ipakitaStatement();
        if(match(KUNG)) return kungStatement();
        if(match(DAWAT)) return dawatStatement();
        if(match(MINTRAS)) return mintrasStatement();
        if(match(HANGTUD)) return hangtudStatement();

        return expressionStatement();
    }

    private Stmt mintrasStatement(){
        consume(LEFT_PAREN, "Dapat naay '(' human sa MINTRAS");
        Expr condition = expression();
        consume(RIGHT_PARENT, "Dapat naay ')' human sa kundisyon");

        consume(PUNDOK, "Kailangan naay 'PUNDOK' inig human sa expression sa MINTRAS");

        Stmt body = statement();
        return new Stmt.Mintras(condition,body);
    }

    private Stmt hangtudStatement(){
        consume(LEFT_PAREN, "Dapat naay '(' human sa HANGTUD");
        Expr condition = expression();
        consume(RIGHT_PARENT, "Dapat naay ')' human sa kundisyon");

        consume(PUNDOK, "Kailangan naay 'PUNDOK' inig human sa expression sa HANGTUD");

        Stmt body = statement();
        return new Stmt.Hangtud(condition,body);
    }

    private Stmt forStatement(){
        consume(LEFT_PAREN, "Dapat naay '(' human sa ALANG SA");

        // Initialization Part: for (NUMERO i = 0, ...)
        Stmt initializer = null;
        if (match(MUGNA)) {
            initializer = forLoopVar();
        } else if (!check(COMMA)) {
            initializer = expressionStatement();
        }
        consume(COMMA, "Dapat naay ',' human sa initializer");

        // Condition Part: for (..., i<n, ...)
        Expr condition = null;
        if(!check(COMMA)){
            condition = expression();
        }
        consume(COMMA, "Kailangan naay comma inig human sa condition");

        // Increment Part: for (..., i++)
        Expr increment = null;
        if (!check(RIGHT_PARENT)) {
            increment = expression();
        }
        consume(RIGHT_PARENT, "Kailangan naay ')' inig human ");
        consume(PUNDOK, "pundok napud kulang");
        Stmt body = statement();
    
        if(increment != null){
            body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
        }
        if(condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);
    
        if(initializer != null){
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }
    
        return body;
    }

    private Stmt forLoopVar(){
        Token type = consume(NUMERO, "Dapat naay data type human sa MUGNA");
        Token name = consume(IDENTIFIER, "Dapat nay variable name");
        Expr init = null;
        if(match(EQUAL)) init = expression();

        return new Stmt.Mugna(type,Collections.singletonList(name), Collections.singletonList(init));
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

    private Stmt kungStatement() {
        consume(LEFT_PAREN, "Kailangan adunay '(' inig human sa 'KUNG'");
        Expr condition = expression();
        consume(RIGHT_PARENT, "Kailangan isira gamit ang ')' inig human sa expresyon");
        consume(PUNDOK, "Kailangan naay 'PUNDOK' inig human sa KUNG");
    
        Stmt thenBranch = statement();
        Stmt elseBranch = null;

        if (match(KUNGDILI)) {
            elseBranch = kungStatement();  
        } 
        else if (match(KUNGWALA)) {
            consume(PUNDOK, "Kailangan naay 'PUNDOK' inig human sa KUNG WALA");
            elseBranch = statement();
        }
    
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt sugodStatement(){
        consume(SUGOD, "Dapat magsugod ang program gamit ang 'SUGOD'");
        List<Stmt> statements = new ArrayList<>();
        try {
            while(!peek().lexeme.equals("KATAPUSAN") && !isAtEnd()){
                statements.add(declaration());
            }
        } catch (ParseError e) {
            synchronize();
        }
        consume(KATAPUSAN, "Kailangan naay 'KATAPUSAN' sa katapusan sa program");
        return new Stmt.Sugod(statements);
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
    
    // BISAYA++
    private Stmt ipakitaStatement(){
        Expr expr = expression();
        return new Stmt.Ipakita(expr);
    }

    private Stmt dawatStatement(){
        List <Token> variableNames = new ArrayList<>();
        do { 
            variableNames.add(consume(IDENTIFIER, "Kailangan ngalan sa variable"));
            // System.out.println(variableNames);
        } while (match(COMMA));
        return new Stmt.Dawat(variableNames);
    }

    // [21]
    private Stmt mugnaDeclaration(){
        // System.out.println("pasokkk herererera");
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
        List<Expr> initializers = new ArrayList<>();
        do { 
            Token name = consume(IDENTIFIER, "Dapat nay variable name");
            names.add(name);
            if(match(EQUAL)){
                initializers.add(expression());
            } else{
                initializers.add(null);
            }

        } while (match(COMMA));


        return new Stmt.Mugna(type,names,initializers);
    }


    // ======================== HELPER METHODS ========================

    // [20]
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
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:

                // BISAYA++
                case MUGNA:
                case KATAPUSAN:
                case SUGOD:
                case MINTRAS:
                case HANGTUD:
                case KUNG:
                case PUNDOK:
                case ALANG:
                case IPAKITA:
                return;
            }
            advance();
        }
    }
}
