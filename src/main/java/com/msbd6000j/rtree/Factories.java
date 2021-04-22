package com.msbd6000j.rtree;

import com.msbd6000j.rtree.geometry.Geometry;
import com.msbd6000j.rtree.internal.FactoryDefault;

public final class Factories {

    private Factories() {
        // prevent instantiation
    }

    public static <T, S extends Geometry> Factory<T, S> defaultFactory() {
        return FactoryDefault.instance();
    }
}
