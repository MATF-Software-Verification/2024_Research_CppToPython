# System Description

## Authors
- **Milos Obrenovic** (1041/2024)
- **Lazar Kracunovic** (1038/2024)

---

## Problem Description
The problem addressed in this project is **automatic program translation** between two different programming languages:
- **Source Language:** C++14
- **Target Language:** Python 3

The main challenge is that these languages have **different syntax and semantics**, yet many constructs (loops, functions, expressions) can be represented in both.  
By using **formal grammars** and **AST-based representations**, we can parse source code into an abstract model and then generate equivalent code in another language.

---

## System Architecture

The system is divided into the following modules:

1. **ANTLR4 Grammar (C++14.g4)**
    - Defines the lexical and syntactic structure of C++14.
    - Produces a parse tree from input source code.

2. **AST Hierarchy (Java Classes)**
    - Custom classes for different node types:
        - `ExpressionNode`
        - `VariableDeclarationNode`
        - `FunctionNode`
        - `ForNode`, `WhileNode`, `IfNode`
    - These nodes form a simplified, language-independent model.

3. **AST Builder**
    - Implements an ANTLR visitor.
    - Walks the parse tree and constructs the internal AST.
    - Discards unnecessary C++ syntax (like semicolons, type specifiers where not needed in Python).

4. **Code Generation Module**
    - Traverses the AST.
    - Produces equivalent **Python code**.
    - Handles indentation, function definitions, loops, and expressions.

5. **Testing Framework (TestSuitsRunner)**
    - Automates correctness verification.
    - Steps:
        1. Compile and run the original C++ program.
        2. Translate it into Python.
        3. Run the Python program.
        4. Compare outputs.
    - Reports mismatches for debugging.

---

## Solution Approach

### Key Ideas
- **Language Independence**: By using an AST, translation does not depend on surface syntax.
- **Node Abstraction**: Each AST node represents a fundamental programming construct, not specific C++ syntax.
- **Visitor Pattern**: Used to build and traverse the AST efficiently.
- **Incremental Development**: Start with expressions and loops, extend to more complex constructs.

### Example Algorithm (Translation Pipeline)
1. Input: `program.cpp`
2. Parse with ANTLR → Parse Tree
3. AST Builder → Custom AST representation
4. Codegen → Python code (`program.py`)
5. (Optional) Run tests to check correctness

---

## Design Decisions
- **ANTLR4 chosen** because it provides a well-tested C++14 grammar and tools for generating parse trees.
- **Java** chosen for AST implementation and tooling (Gradle, ANTLR integration).
- **Python** chosen as target because of its simple syntax and popularity.
- **Testing by comparing outputs** ensures functional equivalence, not just syntactic correctness.

---

## Conclusion
This project shows how **compiler techniques** (parsing, ASTs, code generation) can be applied to the practical task of **translating between programming languages**.  
The modular design makes it possible to extend the translator with more C++ features or even adapt it to support other target languages.
