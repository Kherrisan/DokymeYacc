package com.seu.dokyme.dokymeyacc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LR1Item {
    public Production production;
    public Set<Symbol> predicts;
    public int dot;

    public LR1Item(Production production, Set<Symbol> predicts, int dot) {
        this.production = production;
        this.predicts = predicts;
        this.dot = dot;
    }

    public LR1Item(Production production, Set<Symbol> predicts) {
        this(production, predicts, 0);
    }

    public LR1Item(Production production) {
        this(production, new HashSet<>());
    }

    public boolean nextNonTerminal() {
        return production.rights.get(dot).productions.size() != 0;
    }

    public Symbol next() {
        return production.rights.get(dot);
    }

    public void shift() {
        dot++;
    }

    public List<LR1Item> inStateExtension(DokymeYaccFile yaccFile) {
        if (!nextNonTerminal()) {
            //如果是终结符，就不要拓展。
            return new ArrayList<>();
        }
        List<Symbol> betaA = production.rights.subList(dot + 1, production.rights.size());
        Symbol placeholder = new Symbol("$");
        betaA.add(placeholder);//向betaA尾部加一个占位符，表示预测符集合。如果返回的first集合中有该占位符，则把预测符都并入first集中。
        Set<Symbol> firstBetaA = yaccFile.first(betaA);
        if (firstBetaA.contains(placeholder)) {
            firstBetaA.addAll(predicts);
        }
        firstBetaA.remove(placeholder);
        List<LR1Item> itemsToBeAdded = new ArrayList<>();
        for (Production production : production.rights.get(dot).productions) {
            itemsToBeAdded.add(new LR1Item(production, firstBetaA));
        }
        return itemsToBeAdded;
    }

    @Override
    protected LR1Item clone() {
        return new LR1Item(production, new HashSet<>(predicts), dot);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LR1Item)) {
            return false;
        } else {
            return ((LR1Item) obj).predicts.equals(predicts) && ((LR1Item) obj).production.equals(production) && ((LR1Item) obj).dot == dot;
        }
    }
}
