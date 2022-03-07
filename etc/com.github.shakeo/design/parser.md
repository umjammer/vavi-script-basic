# 構文解析器BasicParserの設計

ANTLR形式で文法を書いた．  
全ての行は`statement`から始まる．
思いついたら書き足す．

```
statement
    : assignmentStatement
    | commandStatement
    ;

assignmentStatement
    : NAME '=' expr
    ;

commandStatement
    : NAME
    | NAME expr ',' expr*
    ;

expr
    : expr2
    | expr2 'AND' expr
    | expr2 'OR' expr
    | expr2 'XOR' expr
    ;

expr2
    : form
    | form '==' expr2
    | form '!=' expr2
    | form '>' expr2
    | form '<' expr2
    | form '>=' expr2
    | form '<=' expr2
    ;

form
    : term
    | term '+' form
    | term '-' form
    ;

term
    : mono
    | mono '*' term
    | mono '/' term
    | mono '\' term
    | mono 'MOD' term 
    | '-' term
    | '(' expr ')'
    ;

mono
    : NUMBER
    | STRING
    | NAME
    | NAME '(' expr ')'
    ;
```