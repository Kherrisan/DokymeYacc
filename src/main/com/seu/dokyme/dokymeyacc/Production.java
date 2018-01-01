package com.seu.dokyme.dokymeyacc;

import java.util.ArrayList;
import java.util.List;

public class Production {
    private static int sid;

    public int id;
    public Symbol left;
    public List<Symbol> rights;
    public List<String> translations;

    public Production() {
        id = sid++;
        this.rights = new ArrayList<>();
    }

    @Override
    public String toString() {
        String rightSide = "";
        for (Symbol right : rights) {
            rightSide += right.toString() + " ";
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
