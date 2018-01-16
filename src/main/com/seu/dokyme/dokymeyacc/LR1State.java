package com.seu.dokyme.dokymeyacc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LR1State {

    private static int sid = 0;

    public List<LR1Item> items;
    public int id;

    public static LR1State build(DokymeYaccFile yaccFile, List<LR1Item> items) {
        LR1State newState = new LR1State(items);
        newState.closure(yaccFile);
        return newState;
    }

    public static LR1State build(DokymeYaccFile yaccFile, LR1Item item) {
        List<LR1Item> items = new ArrayList<>();
        items.add(item);
        return build(yaccFile, items);
    }


    public LR1State(List<LR1Item> items) {
        this.items = items;
        this.id = sid++;
    }

    public LR1State() {
        this(new ArrayList<>());
    }

    public Set<LR1Item> getAllReducable() {
        Set<LR1Item> reducable = new HashSet<>();
        for (LR1Item item : items) {
            if (item.isReducable()) {
                reducable.add(item);
            }
        }
        return reducable;
    }

    public List<Symbol> getAllShiftable() {
        Set<Symbol> allShiftable = new HashSet<>();
        for (LR1Item item : items) {
            Symbol next = item.next();
            if (next != null) {
                allShiftable.add(next);
            }
        }
        return new ArrayList<>(allShiftable);
    }

    public LR1State shift(Symbol shiftSymbol, DokymeYaccFile yaccFile) {
        List<LR1Item> newItems = new ArrayList<>();
        for (LR1Item item : items) {
            if (item.next() != null && item.next().equals(shiftSymbol)) {
                LR1Item newItem = item.clone();
                newItem.shift();
                newItems.add(newItem);
            }
        }
        LR1State state = new LR1State(newItems);
        state.closure(yaccFile);
        return state;
    }

    /**
     * 向一个LR1State中添加一个LR1Item。
     *
     * @param item
     * @return 如果添加了新的item，则返回true。
     */
    public boolean addLR1Item(LR1Item item) {
        for (LR1Item existed : items) {
            if (existed.productionEquals(item)) {
                //在该状态已有的item找到了产生式相同的item。
                return existed.predicts.addAll(item.predicts);
            }
        }
        items.add(item);
        return true;
    }

    /**
     * 迭代的做状态内扩展，直到该状态无法继续扩展。
     *
     * @param yaccFile
     */
    public void closure(DokymeYaccFile yaccFile) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < items.size(); i++) {
                //对每一个item，判断dot之后是不是非终结符，如果是的话，就把那个非终结符为右部的产生式加入到该状态中，预测符为first(BetaA)。
                LR1Item item = items.get(i);
                for (LR1Item newItem : item.inStateExtension(yaccFile)) {
                    if (addLR1Item(newItem)) {
                        changed = true;
                    }
                }
            }
        }
        return;
    }

    /**
     * 判断两个状态是否相等，用于确认shift之后是否回到了之前的某个状态中。
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LR1State)) {
            return false;
        } else {
            return ((LR1State) obj).items.equals(items);
        }
    }
}
