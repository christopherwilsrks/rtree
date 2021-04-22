package com.msbd6000j.rtree.internal;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.msbd6000j.rtree.Leaf;
import com.msbd6000j.rtree.Node;
import com.msbd6000j.rtree.NonLeaf;
import com.msbd6000j.rtree.geometry.Geometry;
import com.msbd6000j.rtree.geometry.Rectangle;

import lombok.Builder;
import lombok.Data;

/**
 * @author ruankaisheng <ruankaisheng@kuaishou.com>
 * Created on 2021-04-04
 */
public final class TreeHelper {

    @Builder
    @Data
    public static final class TreeStatics {
        private final int   cntNonLeaf;
        private final int   cntLeaf;
        private final int   cntOverlap;
        private final float utility;
    }

    private TreeHelper() {
    }

    public static <T, S extends Geometry> TreeStatics calculateNonLeafSize(Node<T, S> root) {
        LinkedList<Node<T, S>> queue      = new LinkedList<>();
        final int              bucketSize = root.context().bucketSize();
        int                    cntLeaf    = 0, cntNonLeaf = 0, cntOverlap = 0;
        float                  utility    = 0f;
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

    private static <T, S extends Geometry> boolean hasOverlap(NonLeaf<T, S> node) {
        List<Node<T, S>> children = node.children();
        if (CollectionUtils.isNotEmpty(children)) {
            int size = children.size();
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    if (isOverlap(node.child(i), node.child(j)) || isOverlap(node.child(j), node.child(i))) {
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
