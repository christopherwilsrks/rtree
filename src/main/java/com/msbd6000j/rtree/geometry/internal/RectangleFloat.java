package com.msbd6000j.rtree.geometry.internal;

import static com.msbd6000j.rtree.geometry.internal.GeometryUtil.max;
import static com.msbd6000j.rtree.geometry.internal.GeometryUtil.min;

import com.github.davidmoten.guavamini.Objects;
import com.github.davidmoten.guavamini.Preconditions;
import com.msbd6000j.rtree.geometry.Geometry;
import com.msbd6000j.rtree.geometry.Rectangle;
import com.msbd6000j.rtree.internal.util.ObjectsHelper;

public final class RectangleFloat implements Rectangle {
    public final float x1, y1, x2, y2;

    private RectangleFloat(float x1, float y1, float x2, float y2) {
        Preconditions.checkArgument(x2 >= x1);
        Preconditions.checkArgument(y2 >= y1);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public static Rectangle create(float x1, float y1, float x2, float y2) {
        return new RectangleFloat(x1, y1, x2, y2);
    }

    @Override
    public double x1() {
        return x1;
    }

    @Override
    public double y1() {
        return y1;
    }

    @Override
    public double x2() {
        return x2;
    }

    @Override
    public double y2() {
        return y2;
    }

    @Override
    public double area() {
        return (x2 - x1) * (y2 - y1);
    }

    @Override
    public Rectangle add(Rectangle r) {
        if (r.isDoublePrecision()) {
            return RectangleDouble.create(min(x1, r.x1()), min(y1, r.y1()), max(x2, r.x2()),
                    max(y2, r.y2()));
        } else if (r instanceof RectangleFloat) {
            RectangleFloat rf = (RectangleFloat) r;
            return RectangleFloat.create(min(x1, rf.x1), min(y1, rf.y1), max(x2, rf.x2),
                    max(y2, rf.y2));
        } else {
            PointFloat rf = (PointFloat) r;
            return RectangleFloat.create(min(x1, rf.xFloat()), min(y1, rf.yFloat()),
                    max(x2, rf.xFloat()), max(y2, rf.yFloat()));
        }
    }

    @Override
    public boolean contains(double x, double y) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    @Override
    public boolean intersects(Rectangle r) {
        return GeometryUtil.intersects(x1, y1, x2, y2, r.x1(), r.y1(), r.x2(), r.y2());
    }

    @Override
    public double distance(Rectangle r) {
        return GeometryUtil.distance(x1, y1, x2, y2, r.x1(), r.y1(), r.x2(), r.y2());
    }

    @Override
    public Rectangle mbr() {
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x1, y1, x2, y2);
    }

    @Override
    public boolean equals(Object obj) {
        RectangleFloat other = ObjectsHelper.asClass(obj, RectangleFloat.class);
        if (other != null) {
            return Objects.equal(x1, other.x1) && Objects.equal(x2, other.x2)
                    && Objects.equal(y1, other.y1) && Objects.equal(y2, other.y2);
        } else
            return false;
    }

    @Override
    public double intersectionArea(Rectangle r) {
        if (!intersects(r))
            return 0;
        else
            return RectangleDouble
                    .create(max(x1, r.x1()), max(y1, r.y1()), min(x2, r.x2()), min(y2, r.y2()))
                    .area();
    }

    @Override
    public double perimeter() {
        return 2 * (x2 - x1) + 2 * (y2 - y1);
    }

    @Override
    public Geometry geometry() {
        return this;
    }

    @Override
    public boolean isDoublePrecision() {
        return false;
    }

    @Override
    public String toString() {
        return "Rectangle [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + "]";
    }

}
