# Simple expressions evaluator

The application can parse and evaluate simple arithmetic expressions, such as:

@pow(2, 1 + x * 2) + (9 - y) * 3

It supports:
* Arithmetic operations
* Parentheses
* Named variables
* Several functions from java.lang.Math: sqrt, log, abs, acos, cos, cosh, pow, addExact

# Getting Started

### REST API
#### Save
To save an expression:
* Start the app using the gradle-command: ./gradlew clean bootRun
* Make the POST-request: curl -d '{ "name": "formula", "expression": "4*var1 + var2 - 3 + (1*0 - 3)" }' -X POST http://localhost:8080/api/v1/expressions

#### Evaluate
To evaluate a previously saved expression:
* Make the POST-request: curl -d '{ "var1": "1", "var2": "4" }' -X POST http://localhost:8080/api/v1/expressions/formula/calculations


