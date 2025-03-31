package BisayaPP;

import java.util.HashMap;
import java.util.Map;

// [19]
class Environment {
    private final Map <String, Object> values = new HashMap();
    // [23]
    final Environment enclosing;

    Environment(){
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }
    

    Object get (Token name){
        if(values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }
        if(enclosing != null) return enclosing.get(name);
        throw new RuntimeError(name, "Undefined variable '"+ name.lexeme+"''.");
    }

    void define (String name, Object value){
        values.put(name,value);
    }
    void assign(Token name, Object value){
        if(values.containsKey(name.lexeme)){
            values.put(name.lexeme,value);
            return;
        }
        if(enclosing != null){
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "Undefined variable '"+ name.lexeme+"'.");
    }
    void assign(Token name, String dataType, Object value){
        if(typeCheck(dataType, value)){
            if(values.containsKey(name.lexeme)){
                values.put(name.lexeme, value);
                return;
            }
        }
        throw new RuntimeError(name, "Variable '"+ name.lexeme+" not declared. ");
    }
    void define(Token name, String dataType, Object value){
        if(typeCheck(dataType, value)){
            // System.out.println("Type: "+ dataType+ "  Name = "+ value);
            values.put(name.lexeme,value);
        } else{
            // System.out.println("DATA TYPE OF VALUE IS: "+ value.getClass());
            throw new RuntimeError(name,"Type mismatch: Expected "+dataType+ "but is "+ value);
        }
    }

    private boolean typeCheck(String dataType, Object value){
        switch(dataType){
            case "NUMERO": return value instanceof Integer;
            case "TIPIK": return value instanceof Double;
            case "LETRA": return value instanceof Character;
            case "TINUOD": return value instanceof Boolean;
            
        }
        return false;
    }
    String getType(Token name,Object value){
        // System.out.println("VALUE CLASS IS: "+value.getClass() + "Value is an instance of NUMERO"+(value instanceof Integer));
        // return value.getClass();
        if(value instanceof Integer) return "NUMERO";
        if(value instanceof Double) return "TIPIK";
        if(value instanceof Character) return "LETRA";
        if(value instanceof Boolean) return "TINUOD";

        throw new RuntimeError(name,"Type mismatch: Expected "+get(name).getClass().getSimpleName()+ "but value is "+ value.getClass().getSimpleName());
        // return null;
    }
}
