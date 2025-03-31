package BisayaPP;

import java.util.List;

// [16]

abstract class Stmt {
    interface Visitor<R> {
    R visitBlockStmt(Block stmt);
    R visitExpressionStmt(Expression stmt);
    R visitPrintStmt(Print stmt);
    R visitVarStmt(Var stmt);
    R visitIfStmt(If stmt);
    R visitWhileStmt(While stmt);
    R visitIpakitaStmt(Ipakita stmt);
    R visitMugnaStmt(Mugna stmt);
    R visitKungStmt(Kung stmt);
    R visitSugodStmt(Sugod stmt);
    }
  static class Block extends Stmt {
    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitBlockStmt(this);
    }

    final List<Stmt> statements;
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
  static class If extends Stmt {
    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitIfStmt(this);
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
  }
  static class While extends Stmt {
    While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitWhileStmt(this);
    }

    final Expr condition;
    final Stmt body;
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
    Mugna(Token type, List<Token> names, Expr initializer) {
      this.type = type;
      this.names = names;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitMugnaStmt(this);
    }

    final Token type;
    final List<Token> names;
    final Expr initializer;
  }
  static class Kung extends Stmt {
    Kung(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitKungStmt(this);
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
  }
  static class Sugod extends Stmt {
    Sugod(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
    return visitor.visitSugodStmt(this);
    }

    final List<Stmt> statements;
  }

    abstract <R> R accept(Visitor<R> visitor);
}
