package com.bilalekrem.ruddergame.util;

import com.bilalekrem.ruddergame.game.*;

import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

import com.bilalekrem.ruddergame.util.Graph.NoSuchNodeException;

public class GraphTest {

    Graph graph;
    Location loc1;
    Location loc2;
    Location loc3;
    Location loc4;
    Location loc5;

    @Before
    public void setup() {
        graph = new Graph();
        
        loc1 = new Location(Segment.CENTER, 0);
        loc2 = new Location(Segment.A, 1);
        loc3 = new Location(Segment.A, 2);
        loc4 = new Location(Segment.B, 1);
        loc5 = new Location(Segment.B, 2);
    }

    @Test
    public void testAddVertex() {
        
        graph.addVertex(loc1);
        graph.addVertex(loc2);
    
        assertEquals(2, graph.getVertices().size());
    }
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testAddEdgeOccurException() throws NoSuchNodeException{
        exception.expect(NoSuchNodeException.class);
        exception.expectMessage("There is no vertex as C-3");

        Location loc = new Location(Segment.C,3);
        graph.addVertex(loc1);
        
        graph.addEdge(loc1, loc);        
    }

    @Test
    public void testAddEdge() throws NoSuchNodeException{
        graph.addVertex(loc2);
        graph.addVertex(loc3);


        graph.addEdge(loc2, loc3);
    }

    @Test
    public void testGetAdjacencies() throws NoSuchNodeException {
        java.util.Set<Location> adjacencies = new java.util.HashSet<>();
        adjacencies.add(new Location(Segment.A, 2));

        graph.addVertex(loc3);
        graph.addVertex(loc1);
        graph.addEdge(loc1, loc3);
        
        java.util.Set<Location> adj = graph.getAdjacencies(loc1);

        assertEquals(adj, adjacencies);
    }
    
    @Test
    public void testAvailableNode() throws NoSuchNodeException {
        graph.addVertex(loc2);
        boolean isAvailable = graph.isNodeAvailable(loc2);

        assertEquals(true, isAvailable);

        // to make a piece we need a Game
        Game game = new RudderGame();
        Game.Piece piece = game.new Piece(loc2, Game.PieceType.DARK); 
        graph.attachPiece(piece, null);
        isAvailable = graph.isNodeAvailable(loc2);
        assertEquals(false, isAvailable);
    }
}