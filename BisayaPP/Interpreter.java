package BisayaPP;

import BisayaPP.Expr.Assign;
import BisayaPP.Expr.Variable;
import BisayaPP.Stmt.Expression;
import BisayaPP.Stmt.Ipakita;
import BisayaPP.Stmt.Mugna;
import BisayaPP.Stmt.Print;
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
        switch(expr.operator.type){
            case GREATER: 
                if(left instanceof Number && right instanceof Number){
                    if((left instanceof Double) || (right instanceof Double)){
                        return (double) turnToDouble(left) > (double) turnToDouble(right);
                    }
                    return (int) left > (int) right;
                }
                // checkNumberOperands(expr.operator, left,right);
                // return (double) left > (double) right;
                
            case GREATER_EQUAL: 
                if(left instanceof Number && right instanceof Number){
                    if((left instanceof Double) || (right instanceof Double)){
                        return (double) turnToDouble(left) >= (double) turnToDouble(right);
                    }
                    return (int) left >= (int) right;
                }
                // checkNumberOperands(expr.operator, left,right);
                // return (double) left >= (double) right;
            case LESS: 
                if(left instanceof Number && right instanceof Number){
                    if((left instanceof Double) || (right instanceof Double)){
                        return (double) turnToDouble(left) < (double) turnToDouble(right);
                    }
                    return (int) left < (int) right;
                }
                // checkNumberOperands(expr.operator, left,right);
                // return (double) left < (double) right;
            case LESS_EQUAL: 
                if(left instanceof Number && right instanceof Number){
                    if((left instanceof Double) || (right instanceof Double)){
                        return (double) turnToDouble(left) <= (double) turnToDouble(right);
                    }
                    return (int) left <= (int) right;
                }
                // checkNumberOperands(expr.operator, left,right);
                // return (double) left <= (double) right;
            // TODO: REMOVE LATER=====================v
            case BANG_EQUAL:
                if(left instanceof Number && right instanceof Number){
                    if((left instanceof Double) || (right instanceof Double)){
                        return (double) turnToDouble(left) != (double) turnToDouble(right);
                    }
                    return (int) left != (int) right;
                }
                // checkNumberOperands(expr.operator, left,right);
                // return !isEqual(left,right);
                // TODO: REMOVE LATER=====================^
                
            case NOT_EQUAL:
                if(left instanceof Number && right instanceof Number){
                    if((left instanceof Double) || (right instanceof Double)){
                        return (double) turnToDouble(left) != (double) turnToDouble(right);
                    }
                    return (int) left != (int) right;
                }
                // checkNumberOperands(expr.operator, left,right);
                // return !isEqual(left,right);
            case EQUAL:
                if(left instanceof Number && right instanceof Number){
                    if((left instanceof Double) || (right instanceof Double)){
                        return (double) turnToDouble(left) == (double) turnToDouble(right);
                    }
                    return (int) left == (int) right;
                }
                // checkNumberOperands(expr.operator, left,right);
                // return isEqual(left,right);
            
            case MINUS:
                if(left instanceof Number && right instanceof Number){
                    if((left instanceof Double) || (right instanceof Double)){
                        return (double) turnToDouble(left) - (double) turnToDouble(right);
                    }
                    return (int) left - (int) right;
                }
                // checkNumberOperands(expr.operator, left,right);
                // return (double) left - (double) right;
            case PLUS:
                // if(left instanceof Double && right instanceof Double){
                if(left instanceof Number && right instanceof Number){
                    if((left instanceof Double) || (right instanceof Double)){
                        // System.out.println("atleast one is double");
                        return (double) turnToDouble(left) + (double) turnToDouble(right);
                    }
                    // System.out.println("both are int");
                    return (int) left + (int) right;
                    
                    // return (double) left + (double) right;
                }
                if(left instanceof String && right instanceof String){
                    return (String) left + (String) right;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH: 
                checkNumberOperands(expr.operator, left,right);
                return (double) left / (double) right;
            case STAR: 
                checkNumberOperands(expr.operator, left,right);
                return (double) left * (double) right;
            
            case CONCAT:
                if(left instanceof String && right instanceof String){
                return (String) left + (String) right;
                }
                
                return left.toString() + right.toString();
                // BisayaPlusPlus.error(1, "mali ka huhu");

        }

        return null;
                
    }
    private Double turnToDouble(Object num){
        if(num instanceof Integer){
            return (double) ((Integer) num).intValue();
        }
        return (double)num;
    }
    // private void checkOperation(Object left, Object right, Token operator){
    //     if((left instanceof Number) && (right instanceof Number)) return;
    //     throw new RuntimeError(operator, "Cannot "+operator.lexeme+ " "+ left.getClass().getSimpleName()+ " and "+  right.getClass().getSimpleName());
    // }
    // private void performOperation(Object left, Object right)
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

}
