package BisayaPP;

import java.util.HashMap;
import java.util.Map;

// [19]
class Environment {
    private final Map <String, Object> values = new HashMap();

    Object get (Token name){
        if(values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }
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
        throw new RuntimeError(name, "Undefined variable '"+ name.lexeme+"'.");
    }
    void define(Token name, String dataType, Object value){
        if(typeCheck(dataType, value)){
            System.out.println("Type: "+ dataType+ "  Name = "+ value);
            values.put(name.lexeme,value);
        } else{
            System.out.println("DATA TYPE OF VALUE IS: "+ value.getClass());
            throw new RuntimeError(name,"Type mismatch: Expected "+dataType+ "but is "+ value);
        }
    }

    private boolean typeCheck(String dataType, Object value){
        switch(dataType){
            case "NUMERO": return value instanceof Integer;
            case "TIPIK": return value instanceof Double;
            case "LETRA": return value instanceof String;
            case "TINUOD": return value instanceof Boolean;
            
        }
        return false;
    }
}
