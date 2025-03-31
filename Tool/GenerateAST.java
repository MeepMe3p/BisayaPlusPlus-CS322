package Tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

// [12]
public class GenerateAST {
    public static void main(String[] args) throws IOException{
        if(args.length != 1){
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];

        defineAST(outputDir,"Expr",Arrays.asList(
            "Assign  : Token name, Expr value",
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Object value",
            "Logical  : Expr left, Token operator, Expr right",
            "Unary    : Token operator, Expr right",
            "Variable : Token name" 
            // BISAYA++
            , "AssignBis: Token type, Token name, Expr value"
        ));

        defineAST(outputDir,"Stmt",Arrays.asList(
            "Block      : List<Stmt> statements",
            "Expression : Expr expression"
            ,"Print      : Expr expression"
            ,"Var        : Token name, Expr initializer"
            ,"If         : Expr condition, Stmt thenBranch," +
            " Stmt elseBranch",
            "While       : Expr condition, Stmt body"

            // BISAYA++
            ,"Ipakita    : Expr expression"
            ,"Mugna      : Token type, List<Token> names, Expr initializer"
            ,"Kung       : Expr condition, Stmt thenBranch, Stmt elseBranch"
            ,"Sugod       : List<Stmt> statements"
            
        ));
    }
    // [15]
    private static void defineAST(String outputDir, String baseName, List<String> types) throws IOException{
        String path = outputDir + "/"+baseName+".java";
        PrintWriter writer = new PrintWriter(path,"UTF-8");

        writer.println("package BisayaPP;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("// [16]");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim(); 
            defineType(writer, baseName, className, fields);
        }

        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
        
    }
    // [14]
    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types){
        writer.println("    interface Visitor<R> {");
        for(String type : types){
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit"+typeName+baseName+"("+ typeName+ " "+ baseName.toLowerCase()+");");
        }
        writer.println("    }");
    }
    // [13]
    private static void defineType(
        PrintWriter writer, String baseName,
        String className, String fieldList) {
        writer.println("  static class " + className + " extends " +  baseName + " {");
  
      // Constructor.
        writer.println("    " + className + "(" + fieldList + ") {");
  
      // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("      this." + name + " = " + name + ";");
        }
  
        writer.println("    }");

        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("    return visitor.visit"+className+baseName+"(this);");
        writer.println("    }");
  
        // Fields.
        writer.println();
        for (String field : fields) {
            writer.println("    final " + field + ";");
        }
        writer.println("  }");
    }
}
