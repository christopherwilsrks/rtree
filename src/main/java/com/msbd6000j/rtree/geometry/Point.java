package com.msbd6000j.rtree.geometry;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.stream.Stream;

public interface Point extends Rectangle {

    double x();

    double y();

    @Override
    default double minDistance(Rectangle r) {
        if (r.contains(x(), y())) return 0;
        if ((r.x1() <= x() && x() <= r.x2()) || (r.y1() <= y() && y() <= r.y2())) {
            return Stream.of(abs(y() - r.y1()), abs(y() - r.y2()), abs(y() - r.y1()),
                             abs(y() - r.y2())).min(Double::compareTo).get();
        } else {
            double _x = abs(r.x1() - x()) <= abs(r.x2() - x()) ? r.x1() : r.x2();
            double _y = abs(r.y1() - y()) <= abs(r.y2() - y()) ? r.y1() : r.y2();
            return calDistance(_x, _y);
        }
    }

    @Override
    default double minMaxDistance(Rectangle r) {
        double d1 = calDistance(r.x1(), r.y1());
        double d2 = calDistance(r.x1(), r.y2());
        double d3 = calDistance(r.x2(), r.y1());
        double d4 = calDistance(r.x2(), r.y2());
        double max1 = max(d1, d2);
        double max2 = max(d1, d3);
        double max3 = max(d2, d4);
        double max4 = max(d3, d4);
        return Stream.of(max1, max2, max3, max4).min(Double::compareTo).get();
    }

    private double calDistance(double x, double y) {
        return sqrt(pow(x() - x, 2) + pow(y() - y, 2));
    }

}
