package BisayaPP;

import BisayaPP.Expr.Assign;
import BisayaPP.Expr.Logical;
import BisayaPP.Expr.Variable;
import BisayaPP.Stmt.Block;
import BisayaPP.Stmt.Dawat;
import BisayaPP.Stmt.Expression;
import BisayaPP.Stmt.If;
import BisayaPP.Stmt.Ipakita;
import BisayaPP.Stmt.Kung;
import BisayaPP.Stmt.Mugna;
import BisayaPP.Stmt.Print;
import BisayaPP.Stmt.Sugod;
import BisayaPP.Stmt.Var;
import BisayaPP.Stmt.While;
import java.util.List;
import java.util.Scanner;

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
            // return left.toString() + right.toString();
            return stringify(left) + stringify(right);
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
                if(right instanceof Integer){
                    return -(int) right;
                }else{

                    return -(double) right;
                }


        }
        return null;
    }
    
    private void checkNumberOperand(Token operator, Object operand){
        if(operand instanceof Double || operand instanceof Integer) return;
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
        // System.out.println("oof");
        for(int i =0; i < stmt.names.size();i++){
            Token name = stmt.names.get(i);
            Expr initializer = stmt.initializers.get(i);
            Token dataType = stmt.type;
            // System.out.println(name + " " + initializer);
            
            Object value;
            if(initializer!=null){
                value = evaluate(initializer);
                // if (dataType.lexeme.equals("TINUOD")) {
                //     if (value instanceof String) {
                //         String strVal = (String) value;
                //         if (strVal.equals("OO")) {
                //             value = true;
                //         } else if (strVal.equals("DILI")) {
                //             value = false;
                //         } else {
                //             throw new RuntimeError(name, "Ang TINUOD dapat OO or DILI");
                //         }
                //     }
                //     else if (!(value instanceof Boolean)) {
                //         throw new RuntimeError(name, "Ang TINUOD dapat OO or DILI");
                //     }
                // }
                if (dataType.lexeme.equals("TINUOD")) {
                    if (value.equals("OO")) {
                        value = true;    
                        // System.out.println("aaaaaaaaaaaaa");
                        // environment.define(name, dataType.lexeme , true);

                    } else if (value.equals("DILI")) {
                        // environment.define(name, dataType.lexeme , false);
                        value = false;
                    } else {
                        throw new RuntimeError(name, "Ang TINUOD dapat OO or DILI");
                    }
                }

            }else{
                switch(stmt.type.lexeme){
                    case "NUMERO": 
                    value = 0 ; 
                        break;
                        case "TIPIK":
                        value = 0.0;
                        break;
                        case "TINUOD":
                        value = false;
                        break;
                        case "LETRA":
                        value = "";
                        break;
                        default:
                        throw new RuntimeError(name, "Dili ka type niya: "+ stmt.type.lexeme);
                    }
                }
                // System.out.println("foo");
                environment.define(name, dataType.lexeme , value);
                // environment.define(name.lexeme, value);

            }
            // System.out.println("went here");
            // System.out.println("Type:"+stmt.type);
            // System.out.println("Names:"+stmt.names);
            
            // Object value = null;
        // Token dataType = stmt.type;
        // if(stmt.initializer != null){
        //     value = evaluate(stmt.initializer);
        // }
        // for(Token name: stmt.names){
        //     if(value.equals("OO")){
        //         environment.define(name, dataType.lexeme , true);
        //     }else if (value.equals("DILI")) {
        //         environment.define(name, dataType.lexeme , false);
                
        //     }else{

        //         environment.define(name, dataType.lexeme , value);
        //     }
        //     // System.out.println("Type: "+ dataType.lexeme+ " "+ name.lexeme + " = " +value);
        // }
        // System.out.println("Initi:"+value);


        



        return null;
    }
    // =====================

    // [17]
    private String stringify(Object object){
        if(object == null) return "nil";
        if(object instanceof Double){
            
            // String text = object.toString();
            // if(text.endsWith(".0")){
            //     text = text.substring(0, text.length() - 2);
            // }
            // return text;
            double number = (Double) object;
            if(number == (int) number){
                return Integer.toString((int) number);
            }else{
                return Double.toString(number);
            }
        }
        if(object instanceof Boolean){
            return (Boolean) object ? "OO" : "DILI";
        }
        if(object instanceof String){
            String str = (String) object;
            if (str.equals("OO") || str.equals("DILI")) {
                // Treat string "OO"/"DILI" as boolean display
                return str;
            }
            return str.replace("$", "\n");
        }
        return object.toString();
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        Object value = null;
        // System.out.println("asdadassd");
        if(stmt.initializer != null){
            value = evaluate(stmt.initializer);
        }
        System.out.println("Statement is" + stmt.name.lexeme+" ----" + value);
        
        // environment.define(stmt.name.lexeme, value);
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
        // System.out.println("tung tung tung sahur");
        executeBlock(stmt.statements, environment);
        return null;
    }

    @Override
    public Object visitLogicalExpr(Logical expr) {
        Object left = evaluate(expr.left);
        if(expr.operator.type == TokenType.OR){
            if(isTruthy(left)) return left;
        }else{
            if(!isTruthy(left)) return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        while(isTruthy(evaluate(stmt.condition))){
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitDawatStmt(Dawat stmt) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Input values separated by comma: ");
        String inputLine = sc.nextLine();

        String [] values = inputLine.split(",");
        if (values.length != stmt.names.size()) {
            throw new RuntimeError(stmt.names.get(0), "Kuwang or sobra imo gibutang nga mga value.");
        }

        for(int i=0;i <stmt.names.size(); i++){
            Token varToken = stmt.names.get(i);
            String varName = varToken.lexeme;
            String rawValue = values[i].trim();
            // String dataType = environment.getType(stmt.names.get(i), rawValue);

            Object existing = environment.get(varToken);
            // Object existing = environment.get(stmt.names.get(i));
            // System.out.println(existing+ "this is exisitng");
            // if(existing instanceof Double){
            //     environment.assign(stmt.names.get(i), Double.parseDouble(rawValue));
            // } else if(existing instanceof Boolean) {
            //     if(rawValue.equals("OO")){
            //         environment.assign(stmt.names.get(i), true);
            //     }else if(rawValue.equals("DILI")){
            //         environment.assign(stmt.names.get(i), false);
            //     }else{
            //         throw new RuntimeError(stmt.names.get(i), "Ang TINUOD dapat OO or DILI");
            //     }
            // } else if(existing instanceof String){
            //     environment.assign(stmt.names.get(i), rawValue);
            // } else if(existing instanceof Integer){
            //     environment.assign(stmt.names.get(i), Integer.parseInt(rawValue));
            // }
            // else{
            //     throw new RuntimeError(stmt.names.get(i), "Walay ingana nga variable");
            // }
            if(existing instanceof Double){
                environment.assign(varToken, "TIPIK",Double.parseDouble(rawValue));
            } else if(existing instanceof Boolean) {
                if(rawValue.equals("OO")){
                    environment.assign(varToken,"TINUOD", true);
                }else if(rawValue.equals("DILI")){
                    environment.assign(varToken,"TINUOD", false);
                }else{
                    throw new RuntimeError(varToken, "Ang TINUOD dapat OO or DILI");
                }
            } else if(existing instanceof String){
                environment.assign(varToken,"LETRA", rawValue);
            } else if(existing instanceof Integer){
                environment.assign(varToken,"NUMERO", Integer.parseInt(rawValue));
            }
            else{
                throw new RuntimeError(stmt.names.get(i), "Walay ingana nga variable");
            }
        }
        return null;
    }




}
