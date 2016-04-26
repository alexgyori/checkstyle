package com.puppycrawl.tools.checkstyle.checks.indentation; //indent:0 exp:0

    @interface InputInvalidAnnotationDefIndent1 {} //indent:4 exp:0 warn
    @interface InputInvalidAnnotationDefIndent2 //indent:4 exp:0 warn
    { //indent:4 exp:0 warn
    } //indent:4 exp:0 warn
    @interface //indent:4 exp:0 warn
InputInvalidAnnotationDefIndent3 //indent:0 exp:8 warn
    {} //indent:4 exp:0 warn
    public //indent:4 exp:0 warn
    @interface InputInvalidAnnotationDefIndent3 {} //indent:4 exp:0 warn