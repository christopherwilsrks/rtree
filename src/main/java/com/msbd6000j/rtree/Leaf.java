package com.msbd6000j.rtree;

import java.util.List;

import com.msbd6000j.rtree.geometry.Geometry;

public interface Leaf<T, S extends Geometry> extends Node<T, S> {

    List<Entry<T, S>> entries();

    /**
     * Returns the ith entry (0-based). This method should be preferred for
     * performance reasons when only one entry is required (in comparison to
     * {@code entries().get(i)}).
     * 
     * @param i
     *            0-based index
     * @return ith entry
     */
    Entry<T, S> entry(int i);

}