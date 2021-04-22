package com.msbd6000j.rtree;

import static com.github.davidmoten.guavamini.Preconditions.checkNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.msbd6000j.rtree.geometry.Geometries;
import com.msbd6000j.rtree.geometry.Geometry;
import com.msbd6000j.rtree.internal.TreeHelper;

@DisplayName("rtree-test")
class RTreeTest {

    RTree<Integer, Geometry> tree;

    public void initTree(int d, int n) throws IOException {
        long start = System.currentTimeMillis();
        tree = RTree.maxChildren(d).bucketSize(n).create();
        checkNotNull(tree);
        InputStream resource = RTreeTest.class.getClassLoader()
                .getResourceAsStream("nodes-prod.csv");
        if (resource == null) {
            System.exit(0);
        }
        InputStreamReader read = new InputStreamReader(resource, UTF_8);
        BufferedReader reader = new BufferedReader(read);
        int idx = 0;
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            String[] split = line.split(",");
            float x = Float.parseFloat(split[0]);
            float y = Float.parseFloat(split[1]);
            tree = tree.add(idx++, Geometries.point(x, y));
        }
        System.out.printf(
                "========================maxChildren: %d\tbucketSize: " +
                        "%d=========================\n",
                d, n);
        System.out.println("init " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("tree height:\t" + tree.calculateDepth());
        tree.root().ifPresent(root -> {
            TreeHelper.TreeStatics statics = TreeHelper.calculateNonLeafSize(tree.root().get());
            System.out.printf("NonLeaf: %d\tLeaf: %d\tUtility: %f\tOverlap: %d\n",
                              statics.getCntNonLeaf(), statics.getCntLeaf(), statics.getUtility(),
                              statics.getCntOverlap());
        });
    }

    @Test
    public void search() throws IOException {
        initTree(2, 10);
        initTree(2, 100);
        initTree(6, 10);
        initTree(6, 100);
    }

}