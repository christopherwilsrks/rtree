package com.msbd6000j.rtree.internal;

import java.util.List;

import com.msbd6000j.rtree.Context;
import com.msbd6000j.rtree.Entry;
import com.msbd6000j.rtree.Leaf;
import com.msbd6000j.rtree.Node;
import com.msbd6000j.rtree.geometry.Geometry;
import com.msbd6000j.rtree.geometry.Rectangle;

public final class LeafDefault<T, S extends Geometry> implements Leaf<T, S> {

    private final List<Entry<T, S>> entries;
    private final Rectangle mbr;
    private final Context<T, S> context;

    public LeafDefault(List<Entry<T, S>> entries, Context<T, S> context) {
        this.entries = entries;
        this.context = context;
        this.mbr = Util.mbr(entries);
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    @Override
    public List<Entry<T, S>> entries() {
        return entries;
    }

    @Override
    public int count() {
        return entries.size();
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        return LeafHelper.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return LeafHelper.delete(entry, all, this);
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public Entry<T, S> entry(int i) {
        return entries.get(i);
    }

}
