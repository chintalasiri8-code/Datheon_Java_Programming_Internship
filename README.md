# Datheon_Java_Programming_Internship

This repository contains the completed Java programming internship projects developed for the **Datheon Java Programming Internship**. Each project is self-contained within its own directory and built in pure core Java, demonstrating Object-Oriented Programming (OOP) principles, clean software architecture, robust input validation, exception handling, data structures, and algorithmic problem-solving.

---

## 📂 Repository Structure

```text
Datheon_Java_Programming_Internship/
├── .gitignore
├── README.md                                                    # Main repository documentation
│
├── STUDENT GRADE MANAGEMENT SYSTEM/                             # Task 1
│   ├── Student.java                                             # Student domain model & GPA logic
│   ├── GradeManager.java                                        # Grade processing & class statistics
│   ├── Main.java                                                # Interactive console user interface
│   ├── SystemVerifier.java                                      # Automated test suite (39 test cases)
│   ├── .gitignore                                               # Project-specific git ignore rules
│   └── README.md                                                # Detailed project documentation
│
├── WORD COUNTER/                                                # Task 2
│   ├── WordAnalyzer.java                                        # Tokenizer, frequency counter & analyzer
│   ├── sample.txt                                               # Primary test corpus
│   ├── few_words.txt                                            # Sample with frequency ties
│   ├── mixed_test.txt                                           # Sample with punctuation & symbols
│   ├── no_punctuation.txt                                       # Edge-case test input
│   ├── empty.txt                                                # Empty file edge-case test
│   └── README.md                                                # Detailed project documentation
│
└── DIGITAL LIBRARY INVENTORY & CHECKOUT MANAGEMENT SYSTEM/      # Task 3
    ├── Book.java                                                # Book entity & inventory status
    ├── Member.java                                              # Library member entity
    ├── Loan.java                                                # Checkout transaction & date tracking
    ├── LibrarySystem.java                                       # Main application & interactive menu
    ├── LibrarySystemTest.java                                   # Automated test suite
    └── README.md                                                # Detailed project documentation
```

---

## 🚀 Projects Overview

### 1. Student Grade Management System (`STUDENT GRADE MANAGEMENT SYSTEM/`)
A modular console application implementing OOP principles and the Java Collections Framework to manage student records and calculate academic performance metrics.
- **Key Features:**
  - Add, search, update, and remove student records.
  - Grade computation: numerical scores (0–100), GPA conversion (4.0 scale), and letter grades (A, B, C, D, F).
  - Class statistics calculation: highest, lowest, and average class grades.
  - Automated verification test suite (`SystemVerifier.java`) covering 39 edge-case tests.
- **How to Run:**
  ```bash
  cd "STUDENT GRADE MANAGEMENT SYSTEM"
  javac *.java
  java Main
  ```
  To run automated verification tests:
  ```bash
  java SystemVerifier
  ```

---

### 2. Word Counter & Text Corpus Analyzer (`WORD COUNTER/`)
A high-performance text analysis utility that parses text documents to calculate corpus metrics and analyze word frequency distributions.
- **Key Features:**
  - Tokenization and normalization with regular expressions (`java.util.regex`).
  - Total word count, unique words, sentences, and average sentence length.
  - Word frequency mapping with `HashMap<String, Integer>`.
  - Top 10 frequent words extraction sorted by count descending with alphabetical tie-breaking.
  - Robust handling for empty files, edge cases, and missing paths.
- **How to Run:**
  ```bash
  cd "WORD COUNTER"
  javac *.java
  java WordAnalyzer
  ```
  Or pass a file path directly as an argument:
  ```bash
  java WordAnalyzer sample.txt
  ```

---

### 3. Digital Library Inventory & Checkout Management System (`DIGITAL LIBRARY INVENTORY & CHECKOUT MANAGEMENT SYSTEM/`)
An enterprise-style library inventory and circulation management application managing books, members, checkout loans, and return operations.
- **Key Features:**
  - Book catalog inventory management (Title, Author, ISBN, availability status).
  - Multi-attribute catalog search (by Title, Author, or ISBN substring).
  - Member management and registration.
  - Checkout loans with automatic 14-day due date calculation using `java.time.LocalDate`.
  - Book return workflow with automated overdue fine calculation (₹5.00/day).
  - Automated validation test suite (`LibrarySystemTest.java`).
- **How to Run:**
  ```bash
  cd "DIGITAL LIBRARY INVENTORY & CHECKOUT MANAGEMENT SYSTEM"
  javac *.java
  java LibrarySystem
  ```
  To run test verification:
  ```bash
  java LibrarySystemTest
  ```

---

## 🛠️ Technology Stack & Requirements

- **Language:** Java (JDK 17 or higher recommended; fully compatible up to Java 25)
- **Dependencies:** Standard Java SE Library (`java.util.*`, `java.io.*`, `java.time.*`, `java.lang.*`) — zero external dependencies.
- **Operating System:** Platform independent (Windows, macOS, Linux).

---

## 👤 Author

- **Intern:** Chintala Siri
- **Internship:** Datheon Java Programming Internship
- **Repository:** [Datheon_Java_Programming_Internship](https://github.com/chintalasiri8-code/Datheon_Java_Programming_Internship)