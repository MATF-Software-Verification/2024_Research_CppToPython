# 2024_Research_CppToPython

## Authors
- **Milos Obrenovic** (1041/2024)
- **Lazar Kracunovic** (1038/2024)

## Project Overview
This project implements a **translator from C++ to Python** based on **Abstract Syntax Trees (ASTs)**.  
The goal is to demonstrate how formal grammars and AST-based approaches can be applied to **automatic program translation** between programming languages.

### Key Components
- **ANTLR4 Grammar** for parsing **C++14** source code.
- **AST Hierarchy in Java**: nodes for expressions, statements, declarations, loops, and functions.
- **AST Builder**: a visitor that walks the ANTLR parse tree and constructs the internal AST model.
- **Code Generation Module**: traverses the AST and produces equivalent **Python code**.
- **Testing Framework**: compares the output of compiled C++ programs with the output of translated Python code, ensuring correctness.

---

## How to Build and Run

### Prerequisites
- **Java 21+**
- **Gradle** (build tool, included via `build.gradle`)
- **ANTLR4** (plugin included in the build)
- **Python 3.10+** (to run translated programs)
- **G++** (optional, for compiling original C++ tests)

### Build
```bash
./gradlew build
```

### Tests
Run all tests:
```bash
./gradlew testTranslator
```
To run on individual file:
####  Steps:
1. Add correct .cpp file in **tests/input**
2. In **example.java** modify line ```CharStream charStream = CharStreams.fromFileName("src/main/tests/input/example.cpp");```
3. Run ``` ./gradlew example ```
