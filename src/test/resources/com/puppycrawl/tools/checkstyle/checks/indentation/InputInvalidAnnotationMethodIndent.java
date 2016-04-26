package com.puppycrawl.tools.checkstyle.checks.indentation; //indent:0 exp:0

@interface InputValidAnnotationMethodIndent1 { //indent:0 exp:0
int f1 = 0; //indent:0 exp:4 warn
} //indent:0 exp:0
@interface InputValidAnnotationMethodIndent2 { //indent:0 exp:0
String m1(); //indent:0 exp:4 warn
String //indent:0 exp:4 warn
m2(); //indent:0 exp:4 warn
} //indent:0 exp:0
@interface InputValidAnnotationMethodIndent3 { //indent:0 exp:0
String m1() default ""; //indent:0 exp:4 warn
String //indent:0 exp:4 warn
m2() default ""; //indent:0 exp:4 warn
String m3() //indent:0 exp:4 warn
default ""; //indent:0 exp:8 warn
String //indent:0 exp:4 warn
m4() //indent:0 exp:4 warn
default ""; //indent:0 exp:4 warn
String //indent:0 exp:4 warn
m5() //indent:0 exp:4 warn
default //indent:0 exp:4 warn
""; //indent:0 exp:4 warn
} //indent:0 exp:0