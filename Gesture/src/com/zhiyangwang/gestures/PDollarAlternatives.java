package com.zhiyangwang.gestures;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.zhiyangwang.geometry.Measurements;
import com.zhiyangwang.geometry.Point;

public class PDollarAlternatives {
	/**
     * Optimal alignment of two clouds of points using the Hungarian weighted matching algorithm for bipartite graphs.
     * Returns the matching between the two clouds.
     */
    public static int[] hungarian(Point[] points1, Point[] points2, int[] matching)
    {
        int n = points1.length;
        double[][] weights = new double[n][n];
        for (int i = 0; i < n; i++)
        {
            weights[i] = new double[n];
            for (int j = 0; j < n; j++)
                weights[i][j] = -Measurements.sqrEuclideanDistance(points1[i], points2[j]);
        }

        matching = performHungarianMatching(weights);
        double cost = 0;
        for (int i = 0; i < n; i++)
            cost += -weights[i][matching[i]];
//        return cost;
        return matching;
    }

    /**
     * Implements the Hungarian algorithm for finding the maximum weighted matching for bipartite graphs
     */
    private static int[] performHungarianMatching(double[][] weights)
    {
        // constants
        int NOT_FOUND = -1;
        int NOT_MATCHED = -1;

        // number of vertexes for the two sets of a bipartite graph
        // (the left and right sets)
        /*********************May Cause FAULT*************************************/
        int n = weights[0].length;
        
        // initialize vertex labeling for the left and right sets
        double[] labelsLeft = new double[n];
        double[] labelsRight = new double[n];
        for (int i = 0; i < n; i++)
        {
            labelsLeft[i] = weights[i][0];
            for (int j = 1; j < n; j++)
                labelsLeft[i] = Math.max(labelsLeft[i], weights[i][j]);
            labelsRight[i] = 0;
        }

        // initalize matching
        int matchingCount = 0;              // the size of the current matching (stop when reach n)
        int[] matchingLeft = new int[n];
        int[] matchingRight = new int[n];
        for (int i = 0; i < n; i++)
        {
            matchingLeft[i] = NOT_MATCHED;  // the vertex from the right set to which vertex i from the left set is matched to
            matchingRight[i] = NOT_MATCHED; // the vertex from the left set to which vertex i from the right set is matched to
        }

        Boolean[] visitedLeft = new Boolean[n];   // visitedLeft[i] = true if vertex i from the left set has been processed
        Boolean[] visitedRight = new Boolean[n];  // visitedRight[i] = true if vertex i from the right set has been processed
        int[] parent = new int[n];          // stores the edges of the Hungarian tree, parent[i] = the vertex from the left set that led to the discovery of vertex i from the right set
        while (matchingCount < n)
        {
            // pick free vertex from the left set as the root of the Hungarian tree
            int u = 0;
            while (matchingLeft[u] != NOT_MATCHED) u++;

            // update the labels until a non-matched vertex y from the right set is found
            // which will increase the cardinality of the matching
            int y = NOT_FOUND;
            while (y == NOT_FOUND)
            {
                // reset processed vertexes
            	Arrays.fill(visitedLeft, false);
            	Arrays.fill(visitedRight, false);

                // traverse the equality graph using the breadth-first approach
                // we stop traversal when 
                // 1) no more vertexes are available for exploring or 
                // 2) we found a free vertex in the right set (denoted by y)
                Queue<Integer> queue = new LinkedList<Integer>();
                queue.add(u);
                visitedLeft[u] = true;
                while (queue.size() > 0 && y == NOT_FOUND)
                {
                    int vertex = queue.poll();
                    for (int j = 0; j < n; j++)
                        if (!visitedRight[j])
                        {
                            double diff = weights[vertex][j] - (labelsLeft[vertex] + labelsRight[j]);
                            if (diff < 0)
                                diff = -diff;
                            if (diff < 10e-4f)
                            {
                                parent[j] = vertex;
                                visitedRight[j] = true;

                                // check whether j from the right set is already matched to a vertex (z) from the left set
                                int z = matchingRight[j];
                                if (z == NOT_MATCHED)
                                {
                                    // we found a non-matched vertex, stop traversal
                                    y = j;
                                    break;
                                }
                                else
                                {
                                    // add z to the BFS queue and continue exploration
                                    queue.add(z);
                                    visitedLeft[z] = true;
                                }
                            }
                        }
                }

                if (y == NOT_FOUND)
                {
                    // update vertex lables in order to enlarge the equality graph
                    double alpha = Double.MAX_VALUE;
                    for (int i = 0; i < n; i++)
                        if (visitedLeft[i])
                            for (int j = 0; j < n; j++)
                                if (!visitedRight[j])
                                {
                                    double diff = labelsLeft[i] + labelsRight[j] - weights[i][j];
                                    if (alpha > diff)
                                        alpha = diff;
                                }

                    for (int i = 0; i < n; i++)
                    {
                        if (visitedLeft[i]) labelsLeft[i] -= alpha;
                        if (visitedRight[i]) labelsRight[i] += alpha;
                    }
                }
                else
                {
                    // the path from root u (left set) to y (right set) is an augmenting path
                    // and therefore we can increase the cardinality of the current matching by reversing the edges of the path
                    int index = y;
                    while (index != NOT_MATCHED)
                    {
                        int t = matchingLeft[parent[index]];
                        matchingLeft[parent[index]] = index;
                        matchingRight[index] = parent[index];
                        index = t;
                    }
                    matchingCount++;
                }
            }
        }

        // return the optimal alignment of the two sets of vertexes
        return matchingLeft;
    }
}
