package com.msbd6000j.rtree.geometry;

public interface Rectangle extends Geometry, HasGeometry {

    double x1();

    double y1();

    double x2();

    double y2();

    double area();

    double intersectionArea(Rectangle r);

    double perimeter();

    Rectangle add(Rectangle r);

    boolean contains(double x, double y);
    
    boolean isDoublePrecision();

    @Override
    default double minDistance(Rectangle r) {
        if (!(r instanceof Point)) {
            throw new UnsupportedOperationException("this method is not implemented");
        }
        return r.minDistance(this);
    }

    @Override
    default double minMaxDistance(Rectangle r) {
        if (!(r instanceof Point)) {
            throw new UnsupportedOperationException("this method is not implemented");
        }
        return r.minMaxDistance(this);
    }

}