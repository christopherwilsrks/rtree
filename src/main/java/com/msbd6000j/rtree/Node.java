package com.msbd6000j.rtree;

import java.util.List;

import com.msbd6000j.rtree.geometry.Geometry;
import com.msbd6000j.rtree.geometry.HasGeometry;
import com.msbd6000j.rtree.internal.NodeAndEntries;

public interface Node<T, S extends Geometry> extends HasGeometry {

    List<Node<T, S>> add(Entry<? extends T, ? extends S> entry);

    NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all);

    int count();

    Context<T, S> context();

}
