package com.seu.dokyme.dokymeyacc;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Dokyme
 */
public class Symbol {
    String name;
    String raw;
    List<Production> productions;
    List<Production> in;
    List<String> attributes;

    private Boolean canNull;

    public static final Symbol Null = new Symbol("~");

    public static final Symbol DollarR = new Symbol("DollarR_000");

    public static final Symbol PlaceHolder = new Symbol("$");

    /**
     * 判断一个符号能否推出Null。
     *
     * @return
     */
    public boolean canDeduceToNull() {
//        if (canNull != null) {
//            return canNull;
//        }
//        if (productions.size() == 0 && !equals(Null)) {
//            canNull = false;
//            return false;
//        } else if (productions.size() == 0 && equals(Null)) {
//            canNull = true;
//            return true;
//        } else {
//            boolean symbolCanNull = false;
//            for (Production p : productions) {
//                symbolCanNull = symbolCanNull || p.canNull();
//            }
//            canNull = symbolCanNull;
//            return symbolCanNull;
//        }
        return false;
    }

    public boolean isTerminal() {
        return productions.size() == 0;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Symbol)) {
            return false;
        } else {
            return ((Symbol) obj).name.equals(name);
        }
    }

    public String getClassName() {
        return this.name.toUpperCase();
    }

    public Symbol(String name) {
        this.name = name;
        this.productions = new ArrayList<>();
        this.in = new ArrayList<>();
        this.attributes = new ArrayList<>();
    }

    public Symbol() {
        this("");
    }
}
