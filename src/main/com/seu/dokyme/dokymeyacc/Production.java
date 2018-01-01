package com.seu.dokyme.dokymeyacc;

import java.util.List;

public class Production {
    public Symbol left;
    public List<Symbol> rights;
    public List<String> translations;

    @Override
    public String toString() {
        String rightSide = "";
        for (Symbol right : rights) {
            rightSide += right.toString();
        }
        return left.toString() + "->" + rightSide;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Production)) {
            return false;
        } else {
            return ((Production) obj).left.equals(left) && ((Production) obj).rights.equals(rights);
        }
    }
}
