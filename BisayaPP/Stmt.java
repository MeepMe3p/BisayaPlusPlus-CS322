package BisayaPP;

import java.util.List;

// [16]

abstract class Stmt {
    interface Visitor<R> {
    R visitExpressionStmt(Expression stmt);
    R visitPrintStmt(Print stmt);
    R visitIpakitaStmt(Ipakita stmt);
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
  static class Ipakita extends Stmt {
    Ipakita(Token operator, Expr expression) {
      this.operator = operator;
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitIpakitaStmt(this);
    }

    final Token operator;
    final Expr expression;
  }

    abstract <R> R accept(Visitor<R> visitor);
}
