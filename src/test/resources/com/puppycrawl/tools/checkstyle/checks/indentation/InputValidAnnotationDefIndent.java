package com.puppycrawl.tools.checkstyle.checks.indentation; //indent:0 exp:0

@interface InputInvalidAnnotationDefIndent1 {} //indent:0 exp:0
@interface InputInvalidAnnotationDefIndent2 //indent:0 exp:0
{ //indent:0 exp:0
} //indent:0 exp:0

@interface //indent:0 exp:0
InputInvalidAnnotationDefIndent3 //indent:0 exp:4 warn
{} //indent:0 exp:0

public //indent:0 exp:0
@interface InputInvalidAnnotationDefIndent4 {} //indent:0 exp:4 warn