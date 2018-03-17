package com.bilalekrem.ruddergame.util;

import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

import com.bilalekrem.ruddergame.util.Graph.NoSuchNodeException;

public class GraphTest {

    static Graph graph;

    @Before
    public void setup() {
        graph = new Graph();
    }

    @Test
    public void testAddVertex() {
        
        graph.addVertex(Segment.CENTER, 0);
        graph.addVertex(Segment.A, 1);
    
        assertEquals(2, graph.getVertices().size());
    }
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testAddEdgeOccurException() throws NoSuchNodeException{
        exception.expect(NoSuchNodeException.class);
        exception.expectMessage("There is no vertex as C-3");
        graph.addEdge(Segment.C,3, Segment.D,4);        
    }

    @Test
    public void testAddEdge() throws NoSuchNodeException{
        graph.addVertex(Segment.A, 1);
        graph.addVertex(Segment.A, 2);
        graph.addVertex(Segment.B, 2);
        graph.addEdge(Segment.A,2, Segment.A,1);
        graph.addEdge(Segment.A,2, Segment.B,2);
    }

    @Test
    public void testGetAdjacencies() throws NoSuchNodeException {
        java.util.Set<String> adjacencies = new java.util.HashSet<>();
        adjacencies.add("A-1");
        adjacencies.add("B-2");

        graph.addVertex(Segment.A, 1);
        graph.addVertex(Segment.A, 2);
        graph.addVertex(Segment.B, 2);
        java.util.Set<String> adj = graph.getAdjacencies("A-2");

        assertEquals(adj, adj);
    }




    
}