# Student Result Management System (Core Java, no MySQL)

## Requirements
Java 17+ (uses switch expressions, text blocks, `Stream.toList()`). No external libraries.

## Run (VS Code terminal, from project root)
```
javac -d out src/Main.java src/model/*.java src/service/*.java src/storage/*.java src/util/*.java
java -cp out Main
```
Data is saved to `data/students.dat` automatically after every change. CSV export goes to `data/results.csv`.

## Rules
- Subject pass: >= 33% of that subject's max marks. Overall pass: every subject passed AND overall >= 33%.
- Grades: A+ 90+, A 80-89, B 70-79, C 60-69, D 50-59, E 33-49, F fail.
- Grade points: A+ 10, A 9, B 8, C 7, D 6, E 5, F 0. GPA = sum(points x credits) / total credits.
- Rank: dense ranking by percentage (equal % = same rank); display tie-break by total marks, then name.

Change grading rules in `model/Grade.java` and pass marks in `Subject` / `Result`.
