package com.seu.dokyme.dokymeyacc;

import java.util.ArrayList;
import java.util.List;

public class Symbol {
    String name;
    String raw;
    List<Production> productions;
    List<Production> in;
    List<String> attributes;

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public Symbol(String name) {
        this.name = name;
        this.productions = new ArrayList<>();
        this.in = new ArrayList<>();
        this.attributes = new ArrayList<>();
    }
}
