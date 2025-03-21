package BisayaPP;

import java.util.HashMap;
import java.util.Map;

// [19]
class Environment {
    private final Map <String, Object> values = new HashMap();

    Object get (Token name){
        if(values.containsKey(name.lexeme)){
            System.out.println("naa ka here? 1");
            return values.get(name.lexeme);
        }
        throw new RuntimeError(name, "Undefined variable '"+ name.lexeme+"''.");
    }

    void define (String name, Object value){
        values.put(name,value);
    }
}
