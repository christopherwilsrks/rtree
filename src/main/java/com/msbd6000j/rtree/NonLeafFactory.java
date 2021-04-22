package com.msbd6000j.rtree;

import java.util.List;

import com.msbd6000j.rtree.geometry.Geometry;

public interface NonLeafFactory<T, S extends Geometry> {

    NonLeaf<T, S> createNonLeaf(List<? extends Node<T, S>> children, Context<T, S> context);
}
