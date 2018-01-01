package com.seu.dokyme.dokymeyacc;

import java.util.Stack;

public class Monitor {
    private Stack<Integer> stateStack;
    private Stack<Symbol> symbolStack;

    public Monitor() {
        this.stateStack = new Stack<>();
        this.symbolStack = new Stack<>();
    }
}
