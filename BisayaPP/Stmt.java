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
        R visitKundiStmt(Kundi stmt);
        R visitSugodStmt(Sugod stmt);
        R visitDawatStmt(Dawat stmt);
        R visitMintrasStmt(Mintras stmt);
        R visitHangtudStmt(Hangtud stmt);
    }

    static class Block extends Stmt {
        final List<Stmt> statements;

        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    static class Expression extends Stmt {
        final Expr expression;

        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    static class Print extends Stmt {
        final Expr expression;

        Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    static class Var extends Stmt {
        final Token name;
        final Expr initializer;

        Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    static class If extends Stmt {
        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;

        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    static class While extends Stmt {
        final Expr condition;
        final Stmt body;

        While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    static class Ipakita extends Stmt {
        final Expr expression;

        Ipakita(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIpakitaStmt(this);
        }
    }

    static class Mugna extends Stmt {
        final Token type;
        final List<Token> names;
        final List<Expr> initializers;

        Mugna(Token type, List<Token> names, List<Expr> initializers) {
            this.type = type;
            this.names = names;
            this.initializers = initializers;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitMugnaStmt(this);
        }
    }

    static class Kung extends Stmt {
        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;

        Kung(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitKungStmt(this);
        }
    }

    static class Sugod extends Stmt {
        final List<Stmt> statements;

        Sugod(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSugodStmt(this);
        }
    }

    static class Dawat extends Stmt {
        final List<Token> names;

        Dawat(List<Token> names) {
            this.names = names;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDawatStmt(this);
        }
    }

    static class Mintras extends Stmt {
        final Expr condition;
        final Stmt body;

        Mintras(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitMintrasStmt(this);
        }
    }

    static class Hangtud extends Stmt {
        final Expr condition;
        final Stmt body;

        Hangtud(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitHangtudStmt(this);
        }
    }

    static class Kundi extends Stmt {
        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;

        Kundi(Expr condition, Stmt thenBranch, Stmt elseBranch) {
          this.condition = condition;
          this.thenBranch = thenBranch;
          this.elseBranch = elseBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitKundiStmt(this);
        }
    }

    abstract <R> R accept(Visitor<R> visitor);
}
