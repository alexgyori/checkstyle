package com.puppycrawl.tools.checkstyle.checks.indentation; //indent:0 exp:0

@interface InputValidAnnotationMethodIndent1 { //indent:0 exp:0
} //indent:0 exp:0
@interface InputValidAnnotationMethodIndent2 { //indent:0 exp:0
    int f1 = 0; //indent:4 exp:4
} //indent:0 exp:0
@interface InputValidAnnotationMethodIndent3 { //indent:0 exp:0
    String[] m1(); //indent:4 exp:4
    String[] //indent:4 exp:4
        m2(); //indent:8 exp:8
} //indent:0 exp:0
@interface InputValidAnnotationMethodIndent4 { //indent:0 exp:0
    String[] m1() default {}; //indent:4 exp:4
    String[] //indent:4 exp:4
        m2() default {}; //indent:8 exp:8
    String[] m2() //indent:4 exp:4
        default {}; //indent:8 exp:8
    String[] //indent:4 exp:4
        m2() //indent:8 exp:8
        default {}; //indent:8 exp:8
    String[] //indent:4 exp:4
        m2() //indent:8 exp:8
        default //indent:8 exp:8
        {}; //indent:8 exp:8
} //indent:0 exp:0