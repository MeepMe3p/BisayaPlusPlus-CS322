package Tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * [12] This tool auto-generates the abstract syntax tree (AST) classes used
 * in the Bisaya++ interpreter/compiler. It outputs Java files like Expr.java and Stmt.java
 * with nested classes representing various language constructs.
 */
public class GenerateAST {

    public static void main(String[] args) throws IOException {
        // Ensure the user supplies exactly one command-line argument (output directory).
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }

        String outputDir = args[0];

        // Generate the Expr.java AST class with all expression types.
        defineAST(outputDir, "Expr", Arrays.asList(
                "Assign     : Token name, Expr value",
                "Binary     : Expr left, Token operator, Expr right",
                "Grouping   : Expr expression",
                "Literal    : Object value",
                "Logical    : Expr left, Token operator, Expr right",
                "Unary      : Token operator, Expr right",
                "Variable   : Token name",

                // BISAYA++ extensions
                "AssignBis  : Token type, Token name, Expr value",
                "Postfix    : Expr left, Token operator"
        ));

        // Generate the Stmt.java AST class with all statement types.
        defineAST(outputDir, "Stmt", Arrays.asList(
                "Block      : List<Stmt> statements",
                "Expression : Expr expression",
                "Print      : Expr expression",
                "Var        : Token name, Expr initializer",
                "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "While      : Expr condition, Stmt body",

                // BISAYA++ extensions
                "Ipakita    : Expr expression",
                "Mugna      : Token type, List<Token> names, List<Expr> initializers",
                "Kung       : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "Sugod      : List<Stmt> statements",
                "Dawat      : List<Token> names",
                "Mintras    : Expr condition, Stmt body",
                "Hangtud    : Expr condition, Stmt body",
                "Kundi      : Expr condition, Stmt thenBranch, Stmt elseBranch"

        ));
    }

    /**
     * [15] Generates the source code for a single AST base class (e.g., Expr or Stmt),
     * along with all its concrete subclasses.
     *
     * @param outputDir Directory to save the generated Java file.
     * @param baseName  Name of the base abstract class (Expr or Stmt).
     * @param types     List of subclass declarations in the form "ClassName : fieldType fieldName, ..."
     */
    private static void defineAST(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        // Write package and imports
        writer.println("package BisayaPP;\n");
        writer.println("import java.util.List;\n");
        writer.println("// [16]\n");

        // Declare abstract base class
        writer.println("abstract class " + baseName + " {");

        // Generate the Visitor interface inside the base class
        defineVisitor(writer, baseName, types);

        // Generate each subclass as a static nested class
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        // Declare the abstract accept() method for visitors
        writer.println("\n    abstract <R> R accept(Visitor<R> visitor);");

        // End base class
        writer.println("}");
        writer.close();
    }

    /**
     * [14] Defines the Visitor<R> interface inside the AST base class.
     * Each visit method corresponds to a concrete subclass of the AST.
     */
    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("    }\n");
    }

    /**
     * [13] Defines a concrete AST subclass inside the base class.
     * Example: class Binary extends Expr { ... }
     *
     * @param writer     Writer to output the code.
     * @param baseName   The name of the base class (Expr or Stmt).
     * @param className  The name of the subclass.
     * @param fieldList  Comma-separated list of fields (e.g., "Expr left, Token operator").
     */
    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("    static class " + className + " extends " + baseName + " {");

        // Define fields
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            writer.println("        final " + field + ";");
        }
        writer.println();

        // Define constructor
        writer.println("        " + className + "(" + fieldList + ") {");
        for (String field : fields) {
            String name = field.split(" ")[1]; // Extract variable name
            writer.println("            this." + name + " = " + name + ";");
        }
        writer.println("        }\n");

        // Implement accept() method for the visitor
        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");

        // End class
        writer.println("    }\n");
    }
}
