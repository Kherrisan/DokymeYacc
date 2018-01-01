package com.seu.dokyme.dokymeyacc;

import java.util.ArrayList;
import java.util.List;

public class Symbol {
    String name;
    String raw;
    List<Production> productions;
    List<Production> in;
    List<String> attributes;

    public static Symbol Null = new Symbol("~");

    public static Symbol DollarR = new Symbol("$R");

    public static Symbol PlaceHolder = new Symbol("$");

    /**
     * 判断一个符号能否推出Null。
     *
     * @return
     */
    public boolean canDeduceToNull() {
        if (productions.size() == 0 && !equals(Null)) {
            return false;
        } else if (productions.size() == 0 && equals(Null)) {
            return true;
        } else {
            boolean symbolCanNull = false;
            for (Production p : productions) {
                boolean productionCanNull = true;
                for (Symbol symbol : p.rights) {
                    productionCanNull = productionCanNull && symbol.canDeduceToNull();
                }
                symbolCanNull = symbolCanNull || productionCanNull;
            }
            return symbolCanNull;
        }
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

    public Symbol(String name) {
        this.name = name;
        this.productions = new ArrayList<>();
        this.in = new ArrayList<>();
        this.attributes = new ArrayList<>();
    }
}
