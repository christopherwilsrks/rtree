package com.msbd6000j.rtree;

import com.msbd6000j.rtree.geometry.Geometry;
import com.msbd6000j.rtree.geometry.HasGeometry;

public interface Entry<T, S extends Geometry> extends HasGeometry {

    T value();

    @Override
    S geometry();

}