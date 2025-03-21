package BisayaPP;

import java.util.List;

// [16]

abstract class Stmt {
    interface Visitor<R> {
    R visitExpressionStmt(Expression stmt);
    R visitPrintStmt(Print stmt);
    R visitVarStmt(Var stmt);
    R visitIpakitaStmt(Ipakita stmt);
    R visitMugnaStmt(Mugna stmt);
    }
  static class Expression extends Stmt {
    Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitExpressionStmt(this);
    }

    final Expr expression;
  }
  static class Print extends Stmt {
    Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitPrintStmt(this);
    }

    final Expr expression;
  }
  static class Var extends Stmt {
    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitVarStmt(this);
    }

    final Token name;
    final Expr initializer;
  }
  static class Ipakita extends Stmt {
    Ipakita(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitIpakitaStmt(this);
    }

    final Expr expression;
  }
  static class Mugna extends Stmt {
    Mugna(Token name, List<Token> names, Expr initializer) {
      this.name = name;
      this.names = names;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitMugnaStmt(this);
    }

    final Token name;
    final List<Token> names;
    final Expr initializer;
  }

    abstract <R> R accept(Visitor<R> visitor);
}
