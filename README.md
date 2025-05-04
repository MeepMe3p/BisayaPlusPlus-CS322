
# 🇵🇭 Bisaya++ (BPP)

**Bisaya++ (BPP)** is a programming language that lets you write code in Bisaya! It works similarly to other interpreted languages like Python, with support for expressions, conditionals, loops, and more.

---

## 🔰 How BPP Executes Code

You can run BPP code in two ways:
1. **Prompt Mode** – Type and run code line by line (interactive, like a terminal).
2. **File Mode** – Run code from a `.bpp` file.

---

## 🧠 Core Components

### 📄 Source Code
- Code is stored as a plain string before being processed.

---

### 🔍 LEXER — `BisayaPP.Scanner`
The **Lexer** (Lexical Analyzer) reads your code and breaks it into tokens:
- `advance()` – Reads and consumes characters.
- `peek()` – Looks at the next character without consuming it.
- `match()` – Checks if the next character matches a target (e.g., `<` vs `<=`).
- `scanToken()` – Generates tokens from the input.
- Handles **string literals** and **literals in general**.

---

### 🧱 PARSER — `Expr.java`, `GenerateAST.java`
The **Parser** turns tokens into an **Abstract Syntax Tree (AST)**:
- `GenerateAST.java` automates class generation for different expressions.
- Uses the **Visitor Pattern** for efficient interpretation.
- Expression types include: `Literal`, `Unary`, `Binary`, `Grouping`, etc.

#### Parser Logic:
- Walks through token list and maps each to its structure (constructor).
- Implements logic like `mugna x = 1, y = 2;` by parsing identifiers and values.

---

### 🧮 INTERPRETER — `BisayaPlusPlus`
- Reads tokens → builds AST → interprets → prints output.
- Converts values to strings for display.
- Handles type operations (e.g., auto-casting to float if needed).

---

### 🌐 ENVIRONMENT — `Environment.java`
- Maintains variable scope using a map.
- Supports local (`{ ... }`) and global scopes.

---

## 🧪 Expression Grammar

```text
expression     → literal | unary | binary | grouping ;
literal        → NUMBER | STRING | true | false | nil ;
grouping       → "(" expression ")" ;
unary          → ( "-" | "!" ) expression ;
binary         → expression operator expression ;
operator       → "==" | "!=" | "<" | "<=" | ">" | ">=" | "+" | "-" | "*" | "/" ;
```

## 📜 Statement Grammar

```text
program        → statement* EOF ;
statement      → exprStmt | printStmt | block | ifStmt | whileStmt ;
exprStmt       → expression ";" ;
printStmt      → "print" expression ";" ;
block          → "{" declaration* "}" ;
ifStmt         → "if" "(" expression ")" "PUNDOK" statement ("else" statement)? ;
whileStmt      → "while" "(" expression ")" statement ;
assignment     → IDENTIFIER "=" assignment | equality ;
```

---

## 🔧 Syntax Rules (BPP Keywords)

| English   | Bisaya++ Example                          |
|-----------|-------------------------------------------|
| `print`   | `IPAKITA: "Hello World"`                  |
| `input`   | `DAWAT x,y`                               |
| `if`      | `KUNG (x < 10) PUNDOK {}`                 |
| `else if` | `KUNG DILI (x < 10) PUNDOK {}`            |
| `else`    | `KUNG WALA PUNDOK {}`                     |
| `while`   | `MINTRAS (condition) PUNDOK {}`           |
| `for`     | `ALANG SA (i = 0; i < 10; i++) PUNDOK {}` |

---

## 🛠️ Compile and Run

```bash
# Compile Java files
javac -d . BisayaPP/*.java Tool/*.java

# Generate AST Classes (once)
java Tool.GenerateAST BisayaPP

# Run in prompt mode
java BisayaPP.BisayaPlusPlus

# Run from file
java BisayaPP.BisayaPlusPlus Test/Test2/1.bpp
```

---

## ⚠️ Error Handling

| Mode    | Description                     |
|---------|---------------------------------|
| `[1.1]` | Exits immediately on error     |
| `[1.2]` | Continues running on error     |
| `[1.3]` | Runs in REPL (looped prompt)   |

> 🧠 **N1 Note**: Error reporting is separated from the rest of the logic for clean design.

---

## 💡 Tips & Notes

- **T1:** Press `Ctrl + D` to exit REPL (line-by-line mode).
- **[EXTRA TODOs]**: Build an `ErrorReporter` to improve user feedback.

---

## ✅ Completed Features

- ✅ `&` for string concat
- ✅ Static type checking
- ✅ `$` for newline
- ✅ `OO` (true), `DILI` (false)
- ✅ `[]` for character literals
- ✅ Working `for`, `while`, `if`, etc.

---

## 👨‍💻 Le Programmer Notes

> The scanner's job is to break down code like:

```java
var language = "string";
```

...into **tokens** called **lexemes**, each representing a meaningful unit.
