package com.msbd6000j.rtree.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.msbd6000j.rtree.Leaf;
import com.msbd6000j.rtree.Node;
import com.msbd6000j.rtree.NonLeaf;
import com.msbd6000j.rtree.geometry.Geometry;
import com.msbd6000j.rtree.geometry.Point;
import com.msbd6000j.rtree.geometry.Rectangle;

import lombok.Builder;
import lombok.Data;

/**
 * @author ruankaisheng <ruankaisheng@kuaishou.com>
 * Created on 2021-04-04
 */
public final class TreeHelper {

    /**
     * Store tree statistic information
     * */
    @Builder
    @Data
    public static final class TreeStatics {
        private final int cntNonLeaf;
        private final int cntLeaf;
        private final int cntOverlap;
        private final float utility;
    }

    /**
     * Store information for task 4 when applying knn method
     * */
    @Builder
    @Data
    public static final class NNStatics {
        private int cntVisited;
        private int cntPointCal;
        private int cntPruned;
    }

    private TreeHelper() {
    }

    public static <T, S extends Geometry> TreeStatics calculateTreeStatistics(Node<T, S> root) {
        // calculate tree information based on level traversal
        LinkedList<Node<T, S>> queue = new LinkedList<>();
        final int bucketSize = root.context().bucketSize();
        int cntLeaf = 0, cntNonLeaf = 0, cntOverlap = 0;
        float utility = 0f;
        queue.add(root);
        while (!queue.isEmpty()) {
            Node<T, S> node = queue.poll();
            if (node instanceof NonLeaf) {
                cntNonLeaf++;
                NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) node;
                if (hasOverlap(nonLeaf)) cntOverlap++;
                queue.addAll(nonLeaf.children());
            } else {
                cntLeaf++;
                Leaf<T, S> leaf = (Leaf<T, S>) node;
                utility += leaf.entries().size() / (bucketSize * 1.);
            }
        }
        utility /= cntLeaf;
        return TreeStatics.builder()
                .cntNonLeaf(cntNonLeaf)
                .cntLeaf(cntLeaf)
                .utility(utility)
                .cntOverlap(cntOverlap)
                .build();
    }

    public static <T, S extends Geometry> Geometry findNearestPoint(Point p, Node<T, S> node,
            NNStatics.NNStaticsBuilder builder) {

        builder.cntVisited(builder.cntVisited + 1);

        if (node instanceof NonLeaf) {
            // abl is a container storing either Point(p) or Rectangle(mbr)
            // map is a container storing geometry -> node, since abl stores only point and mbr,
            // map could help us to find mbr's corresponding node
            LinkedList<Geometry> abl = new LinkedList<>();
            HashMap<Geometry, Node<T, S>> map = new HashMap<>();
            NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) node;
            List<Node<T, S>> children = nonLeaf.children();

            // put all nodes into abl
            children.forEach(child -> {
                Geometry g;
                if (child instanceof NonLeaf) {
                    g = child.geometry();
                } else {
                    // if leaf node, replace it with the nearest point among the bucket
                    builder.cntPointCal(builder.cntPointCal + child.count());
                    g = calBestNNFromLeaf(p, (Leaf<T, S>) child);
                    g.mbr();
                }
                abl.add(g);
                map.put(g, child);
            });

            // ordering them by minDistance
            abl.sort((o1, o2) -> (int) (o1.minDistance(p) - o2.minDistance(p)));
            int i = 0, j = 1;
            while (abl.size() > 1) {
                Geometry g1 = abl.get(i);
                Geometry g2 = abl.get(j);

                if (!(g1 instanceof Point) && !(g2 instanceof Point)) {
                    // pruning rule 1
                    if (p.minDistance(g1.mbr()) > p.minMaxDistance(g2.mbr())) {
                        builder.cntPruned(builder.cntPruned + 1);
                        abl.remove(i);
                    } else if (p.minDistance(g2.mbr()) > p.minMaxDistance(g1.mbr())) {
                        builder.cntPruned(builder.cntPruned + 1);
                        abl.remove(j);
                    } else {
                        // replace mbr1 with its best nn points
                        abl.remove(i);
                        Geometry _g = findNearestPoint(p, map.get(g1), builder);
                        abl.add(i, _g);
                        // pruning rule 3
                        builder.cntPointCal(builder.cntPointCal + abl.size() - 1);
                        abl.removeIf(
                                geometry -> {
                                    boolean shouldRemove = _g != geometry && !(geometry instanceof Point) && p.minDistance(
                                            geometry.mbr()) > p.distance(_g.mbr());
                                    if (shouldRemove) {
                                        builder.cntPruned(builder.cntPruned + 1);
                                    }
                                    return shouldRemove;
                                });
                    }
                } else if (g1 instanceof Point && g2 instanceof Point) {
                    // both are points, surely we can remove one of them
                    builder.cntPointCal(builder.cntPointCal + 2);
                    if (p.distance(g1.mbr()) <= p.distance(g2.mbr())) {
                        abl.remove(j);
                    } else {
                        abl.remove(i);
                    }
                } else {
                    // pruning rule 2
                    Point _p;
                    Rectangle _m;
                    if (g1 instanceof Point) {
                        _p = (Point) g1;
                        _m = (Rectangle) g2;
                    } else {
                        _p = (Point) g2;
                        _m = (Rectangle) g1;
                    }
                    boolean shouldRemoveP = false;
                    for (Geometry g : abl) {
                        builder.cntPointCal(builder.cntPointCal + 1);
                        if (!(g instanceof Point) && p.distance(_p.mbr()) > p.minMaxDistance(
                                g.mbr())) {
                            shouldRemoveP = true;
                            break;
                        }
                    }
                    if (shouldRemoveP) {
                        abl.remove(_p);
                    } else {
                        int idx = abl.indexOf(_m);
                        abl.remove(idx);
                        Geometry _g = findNearestPoint(p, map.get(_m.mbr()), builder);
                        abl.add(idx, _g);
                        // pruning rule 3
                        builder.cntPointCal(builder.cntPointCal + abl.size() - 1);
                        abl.removeIf(
                                geometry -> {
                                    boolean shouldRemove = _g != geometry && !(geometry instanceof Point) && p
                                            .minDistance(geometry.mbr()) > p.distance(_g.mbr());
                                    if (shouldRemove) {
                                        builder.cntPruned(builder.cntPruned + 1);
                                    }
                                    return shouldRemove;
                                });
                    }
                }
            }
            Geometry g = abl.poll();
            if (g instanceof Point) {
                return g;
            } else {
                return findNearestPoint(p, map.get(g), builder);
            }
        } else {
            // compute actual distances for each points, find the best nn.
            builder.cntPointCal(builder.cntPointCal + node.count());
            Leaf<T, S> leaf = (Leaf<T, S>) node;
            return calBestNNFromLeaf(p, leaf);
        }

    }

    private static <T, S extends Geometry> Geometry calBestNNFromLeaf(Point p, Leaf<T, S> leaf) {
        return leaf.entries()
                .stream()
                .min((o1, o2) -> (int) (p.distance(o1.geometry().mbr()) - p.distance(
                        o2.geometry().mbr()))).get().geometry();
    }

    private static <T, S extends Geometry> boolean hasOverlap(NonLeaf<T, S> node) {
        List<Node<T, S>> children = node.children();
        if (CollectionUtils.isNotEmpty(children)) {
            int size = children.size();
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    if (isOverlap(node.child(i), node.child(j)) || isOverlap(node.child(j),
                                                                             node.child(i))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static <T, S extends Geometry> boolean isOverlap(Node<T, S> a, Node<T, S> b) {
        Rectangle mbrA = a.geometry().mbr();
        Rectangle mbrB = b.geometry().mbr();
        return (mbrA.x1() <= mbrB.x1() && mbrB.x2() <= mbrA.x2() && mbrA.y1() <= mbrB.y1() && mbrB.y2() <= mbrA
                .y2());
    }

}
