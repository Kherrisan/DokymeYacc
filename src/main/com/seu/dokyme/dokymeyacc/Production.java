package com.seu.dokyme.dokymeyacc;

import java.util.ArrayList;
import java.util.List;

public class Production {
    private static int sid;

    public int id;
    public Symbol left;
    public List<Symbol> rights;
    public List<String> translations;

    private Boolean canNull;

    public Boolean canNull() {
        if (canNull != null) {
            return canNull;
        } else {
            boolean productionCanNull = true;
            for (Symbol symbol : rights) {
                productionCanNull = productionCanNull && symbol.canDeduceToNull();
            }
            canNull = productionCanNull;
            return canNull;
        }
    }

    public Production() {
        id = sid++;
        rights = new ArrayList<>();
        translations = new ArrayList<>();
    }

    @Override
    public String toString() {
        String rightSide = "";
        for (Symbol right : rights) {
            rightSide += right.toString() + " ";
        }
        return left.toString() + ":" + rightSide;
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
