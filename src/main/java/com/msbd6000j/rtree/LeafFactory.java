package com.msbd6000j.rtree;

import java.util.List;

import com.msbd6000j.rtree.geometry.Geometry;

public interface LeafFactory<T, S extends Geometry> {
    Leaf<T, S> createLeaf(List<Entry<T, S>> entries, Context<T, S> context);
}
