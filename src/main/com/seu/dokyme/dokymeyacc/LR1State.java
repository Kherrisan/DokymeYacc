package com.seu.dokyme.dokymeyacc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LR1State {

    private static int sid = 0;

    protected List<LR1Item> items;
    private int id;

    public LR1State(List<LR1Item> items) {
        this.items = items;
        this.id = sid++;
    }

    public LR1State() {
        this(new ArrayList<>());
    }

    public Set<Symbol> getAllShiftable() {
        Set<Symbol> allShiftable = new HashSet<>();
        for (LR1Item item : items) {
            if (item.nextNonTerminal()) {
                allShiftable.add(item.next());
            }
        }
        return allShiftable;
    }

    public LR1State shift(Symbol shiftSymbol) {
        Set<LR1Item> newItems = new HashSet<>();
        for (LR1Item item : items) {
            if (item.next().equals(shiftSymbol)) {
                LR1Item newItem = item.clone();
                newItem.shift();
                newItems.add(newItem);
            }
        }
        LR1State state = new LR1State(newItems);
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
            if (existed.equals(item)) {
                existed.predicts.addAll(item.predicts);
                return false;
            }
        }
        items.add(item);
        return true;
    }

    /**
     * 状态内扩展。
     */
    public void extend(DokymeYaccFile yaccFile) {
        boolean add = true;
        while (add) {
            add = false;
            for (LR1Item item : items) {
                for (LR1Item newItem : item.inStateExtension(yaccFile)) {
                    add = addLR1Item(newItem);
                }
            }
        }
    }

    /**
     * 判断两个状态是否相等，用于确认shift之后是否回到了之前的某个状态中。
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
