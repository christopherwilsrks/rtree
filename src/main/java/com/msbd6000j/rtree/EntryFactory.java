package com.msbd6000j.rtree;

import com.msbd6000j.rtree.geometry.Geometry;

public interface EntryFactory<T,S extends Geometry> {
    Entry<T,S> createEntry(T value, S geometry);
}
