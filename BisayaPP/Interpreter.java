package BisayaPP;

import BisayaPP.Stmt.Expression;
import BisayaPP.Stmt.Ipakita;
import BisayaPP.Stmt.Print;
import java.util.List;

public class Interpreter implements Expr.Visitor <Object>, Stmt.Visitor<Void> {

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        // System.out.println("binary");
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch(expr.operator.type){
            case GREATER: 
                checkNumberOperands(expr.operator, left,right);
                return (double) left > (double) right;
            case GREATER_EQUAL: 

                checkNumberOperands(expr.operator, left,right);
                return (double) left >= (double) right;
            case LESS: 
                checkNumberOperands(expr.operator, left,right);
                return (double) left < (double) right;
            case LESS_EQUAL: 
                checkNumberOperands(expr.operator, left,right);
                return (double) left <= (double) right;
            // TODO: REMOVE LATER=====================v
            case BANG_EQUAL:
                checkNumberOperands(expr.operator, left,right);
                return !isEqual(left,right);
                // TODO: REMOVE LATER=====================^
                
            case NOT_EQUAL:
                checkNumberOperands(expr.operator, left,right);
                return !isEqual(left,right);
            case EQUAL:
                checkNumberOperands(expr.operator, left,right);
                return isEqual(left,right);
            
            case MINUS:
                checkNumberOperands(expr.operator, left,right);
                return (double) left - (double) right;
            case PLUS:
                if(left instanceof Double && right instanceof Double){
                    return (double) left + (double) right;
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

        }

        return null;
                
    }
    private void checkNumberOperands(Token operator, Object left, Object right){
        if(left instanceof Double && right instanceof Double) return;
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
        System.out.println(stringify(value));
        return null;
    }
    
    // BISAYA ++ STUFF
    @Override
    public Void visitIpakitaStmt(Ipakita stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;   
    }
    // =====================
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






}
