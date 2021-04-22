package com.msbd6000j.rtree.internal;

import java.util.List;

import com.msbd6000j.rtree.Context;
import com.msbd6000j.rtree.Entries;
import com.msbd6000j.rtree.Entry;
import com.msbd6000j.rtree.Factory;
import com.msbd6000j.rtree.Leaf;
import com.msbd6000j.rtree.Node;
import com.msbd6000j.rtree.NonLeaf;
import com.msbd6000j.rtree.geometry.Geometry;

public final class FactoryDefault<T, S extends Geometry> implements Factory<T, S> {

    private static class Holder {
        private static final Factory<Object, Geometry> INSTANCE = new FactoryDefault<Object, Geometry>();
    }

    @SuppressWarnings("unchecked")
    public static <T, S extends Geometry> Factory<T, S> instance() {
        return (Factory<T, S>) Holder.INSTANCE;
    }

    @Override
    public Leaf<T, S> createLeaf(List<Entry<T, S>> entries, Context<T, S> context) {
        return new LeafDefault<T, S>(entries, context);
    }

    @Override
    public NonLeaf<T, S> createNonLeaf(List<? extends Node<T, S>> children, Context<T, S> context) {
        return new NonLeafDefault<T, S>(children, context);
    }

    @Override
    public Entry<T, S> createEntry(T value, S geometry) {
        return Entries.entry(value, geometry);
    }

}
