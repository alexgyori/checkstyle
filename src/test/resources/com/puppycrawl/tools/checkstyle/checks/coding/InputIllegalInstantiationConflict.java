package com.puppycrawl.tools.checkstyle.checks.coding;

import InputSemantic.Boolean;

class InputIllegalInstantiationConflict {
    public class Boolean {
        static {
            Boolean b = new Boolean();
        }
    }
}