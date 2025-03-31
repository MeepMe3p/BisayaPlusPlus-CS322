package BisayaPP;

import BisayaPP.Expr.Assign;
import BisayaPP.Expr.Variable;
import BisayaPP.Stmt.Block;
import BisayaPP.Stmt.Expression;
import BisayaPP.Stmt.If;
import BisayaPP.Stmt.Ipakita;
import BisayaPP.Stmt.Kung;
import BisayaPP.Stmt.Mugna;
import BisayaPP.Stmt.Print;
import BisayaPP.Stmt.Sugod;
import BisayaPP.Stmt.Var;
import java.util.List;

public class Interpreter implements Expr.Visitor <Object>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();
    // [18]
    public void interpret(List<Stmt> statements){
        try {
            for(Stmt statement: statements){
                execute(statement);
            }
        } catch (RuntimeError e) {
            BisayaPlusPlus.runTimeError(e);
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        // System.out.println("binary");
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        if (expr.operator.type == TokenType.PLUS) {
            if (left instanceof String || right instanceof String) {
                return left.toString() + right.toString();
            }
        }

        if(left instanceof Number && right instanceof Number){
            Number numLeft = toNumber(left);
            Number numRight = toNumber(right);

            switch(expr.operator.type){
                case GREATER: return numLeft.doubleValue() > numRight.doubleValue();
                case GREATER_EQUAL: return numLeft.doubleValue() >= numRight.doubleValue();
                case LESS: return numLeft.doubleValue() < numRight.doubleValue();
                case LESS_EQUAL: return numLeft.doubleValue() <= numRight.doubleValue();
                case BANG_EQUAL: 
                case NOT_EQUAL: return !numLeft.equals(numRight);
                case EQUAL: return numLeft.equals(numRight);
                case MINUS: return performOperation(numLeft, numRight, '-');
                case PLUS: return performOperation(numLeft, numRight, '+');
                case SLASH: 
                    if (numRight.doubleValue() == 0) {
                        throw new RuntimeError(expr.operator, "Dili kadivide ug zero.");
                    }
                    return numLeft.doubleValue() / numRight.doubleValue();
                case STAR: return performOperation(numLeft, numRight, '*');
            }
        }
        if(expr.operator.type == TokenType.CONCAT){
            return left.toString() + right.toString();
        }
        throw new RuntimeError(expr.operator, "Kailangan duha ka string or duha ka numero.");
    }
    private Number toNumber(Object obj) {
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Double) return (Double) obj;
        throw new RuntimeError(null, "Operand must be a number.");
    }

    private Number performOperation(Number left, Number right, char operation){
        boolean isDouble = (left instanceof Double) || (right instanceof Double);
        switch(operation){
            case '+': return isDouble ? left.doubleValue() + right.doubleValue() : left.intValue() + right.intValue();
            case '-': return isDouble ? left.doubleValue() - right.doubleValue() : left.intValue() - right.intValue();
            case '*': return isDouble ? left.doubleValue() * right.doubleValue() : left.intValue() * right.intValue();
            default: throw new RuntimeError(null, "Unknown operator");
        }
    }
    private void checkNumberOperands(Token operator, Object left, Object right){
        // if(left instanceof Double && right instanceof Double) return;
        if(left instanceof Number && right instanceof Number) return;
        throw new RuntimeError(operator, "Operands must be a number");
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        // System.out.println("grouping");
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        // System.out.println("literal");
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        // System.out.println("unary");
        Object right = evaluate(expr.right);

        switch(expr.operator.type){
            case BANG:
                return !isTruthy(right);
            case MINUS: 
                checkNumberOperand(expr.operator, right);
                return -(double) right;


        }
        return null;
    }
    
    private void checkNumberOperand(Token operator, Object operand){
        if(operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number");
    }
    private boolean isTruthy(Object object){
        if(object == null) return false;
        if(object instanceof Boolean) return (boolean) object;
        return true;
    }
    private boolean isEqual(Object a, Object b){
        if(a == null && b == null) return false;
        if(a == null) return false;
        return a.equals(b);
    }
    private Object evaluate (Expr expr){
        return expr.accept(this);
    }
    private void execute(Stmt stmt){
        stmt.accept(this);
    }
    void executeBlock(List<Stmt> statements, Environment environment){
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for(Stmt statement: statements){
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }
    

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println();
        // System.out.println(stringify(value));
        return null;
    }
    
    // BISAYA ++ STUFF
    @Override
    public Void visitIpakitaStmt(Ipakita stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;   
    }
    @Override
    public Void visitMugnaStmt(Mugna stmt) {
        // System.out.println("went here");
        // System.out.println("Type:"+stmt.type);
        // System.out.println("Names:"+stmt.names);
        
        Object value = null;
        Token dataType = stmt.type;
        if(stmt.initializer != null){
            value = evaluate(stmt.initializer);
        }
        for(Token name: stmt.names){
            // System.out.println("Type: "+ dataType.lexeme+ " "+ name.lexeme + " = " +value);
            environment.define(name, dataType.lexeme , value);
        }
        // System.out.println("Initi:"+value);


        



        return null;
    }
    // =====================

    // [17]
    private String stringify(Object object){
        if(object == null) return "nil";
        if(object instanceof Double){
            String text = object.toString();
            if(text.endsWith(".0")){
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        Object value = null;

        if(stmt.initializer != null){
            value = evaluate(stmt.initializer);
        }
        environment.define(stmt.name.lexeme, value);
        return null;
    }



    @Override
    public Object visitVariableExpr(Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Object visitAssignExpr(Assign expr) {
        Object value = evaluate(expr.value);
        Object tk = environment.get(expr.name);
        // System.out.println("The ttoken is: "+tk);
        String dataType = environment.getType(expr.name,value);
        environment.assign(expr.name, dataType, value);
        return value;
    }

    @Override
    public Object visitAssignBisExpr(Expr.AssignBis expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, expr.type.lexeme, value);
        return value;
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        executeBlock(stmt.statements,new Environment(environment));
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        if(isTruthy(evaluate(stmt.condition))){
            execute(stmt.thenBranch);
        }else if(stmt.elseBranch != null){
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitKungStmt(Kung stmt) {
        if(isTruthy(evaluate(stmt.condition))){
            execute(stmt.thenBranch);
        }else if(stmt.elseBranch != null){
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitSugodStmt(Sugod stmt) {
        execute(stmt.statement);
        return null;
    }




}
