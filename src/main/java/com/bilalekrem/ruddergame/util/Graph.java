package com.bilalekrem.ruddergame.util;

import com.bilalekrem.ruddergame.game.Game.Piece;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The implementation of this class is inspired of,
 * gist.github.com/smddzcy/bf8fc17dedf4d40b0a873fc44f855a58
 * stackoverflow.com/questions/1737627/java-how-to-represent-graphs
 */

/**
 * The Graph class represents an undirected Graph.
 * 
 * @author Bilal Ekrem Harmansa
 */
public class Graph {

    private static final Logger LOGGER = LogManager.getLogger(Graph.class);

    protected Set<Edge> edges;
    protected Map<Location, Node> vertices;
    protected Map<Node, Set<Node>> adjacencyList;

    protected static final int INIT_EDGE_CAPACITY=64;
    protected static final int INIT_VERTICES_CAPACITY=33;

    public Graph() {
        edges = new HashSet<>(INIT_EDGE_CAPACITY);
        vertices = new HashMap<>(INIT_VERTICES_CAPACITY);
        adjacencyList = new HashMap<>(INIT_VERTICES_CAPACITY);
    }

    public void addVertex(Location location) {
        Node node = new Node(location);
        vertices.put(node.location, node);
    }

    public void addEdge(Location source, Location target) throws NoSuchNodeException{
        Node from = getNode(source);
        Node to = getNode(target);

        addEdge(from, to);
    }

    protected void addEdge(Node from, Node to) throws NoSuchNodeException {
        Set<Node> adjacenciesFrom = getAdjacenciesAsNode(from.location);
        Set<Node> adjacenciesTo = getAdjacenciesAsNode(to.location);
       
        Edge e = new Edge(from, to);
        edges.add(e);
        
        adjacenciesFrom.add(to);
        adjacenciesTo.add(from);
    }

    /**
     * This method attach Piece's to graph Node's. Each node constructed with a Location.
     * Only thing that Piece know is location, Piece' do not have an access to Node. This method
     * is a way of connecting them together. 
     * 
     * @param previousLocation previous location of Piece. If a Piece connected a Node before this call,
     * breaks the old connection between the Node and the Type
     * 
     * @param piece is needed to get the location of new Piece. When new location of found in a Node. Connects the
     * Node and the piece by calling node.piece = piece
     */
    public boolean attachPiece(Piece piece, Location previousLocation) throws NoSuchNodeException {
        if(piece == null) return false;
    
        Node node = getNode(piece.getLocation());

        if(node.available()) {
            /** if previous location exists */
            if(previousLocation != null) {
                vertices.values().stream().filter( (n) -> n.location.equals(previousLocation) ).map( (n) -> n.piece = null);
            } 

            // attach the piece with the node that piece located.
            node.piece = piece;
            return true;
        }

        return false;
    }

    /**
     * This methods can be used after attach a Piece to Node. Basicly, returns attached Piece 
     * in @param location. 
     * 
     * If there is no Piece that attached. Just return nulls.
     * 
     * If given location is not exist or could not found throws NoSuchNodeException
     */
    public Piece getAttachedPiece(Location location) throws NoSuchNodeException {
        Node node = getNode(location);
        
        if(!node.available()) return node.piece;
        
        return null;
    }

    public boolean isNodeAvailable(Location vertex) throws NoSuchNodeException {
        return getNode(vertex).available();
    }

    /**
     * @param vertex 
     */
    protected Graph.Node getNode(Location vertex) throws NoSuchNodeException {
        Graph.Node node = vertices.get(vertex);
        if (node == null )
            throw new NoSuchNodeException("There is no vertex as " + vertex);
        return node;
    }

    public Set<Location> getAdjacencies(Location vertex) throws NoSuchNodeException {
        Set<Node> adjs = getAdjacenciesAsNode(vertex);
        return adjs.stream().map( (node) -> node.location ).collect(Collectors.toCollection(HashSet::new));
    }

    protected Set<Node> getAdjacenciesAsNode(Location vertex) throws NoSuchNodeException {
        Node node = getNode(vertex);

        /**
         * Javadoc explanation of putIfAbsent is "If the specified key is not already 
         * associated with a value (or is mapped to null) associates it with the given
         * value and returns null, else returns the current value."
         */
        Set<Node> adj = adjacencyList.putIfAbsent(node, new HashSet<>());
        if(adj == null) adj = adjacencyList.get(node);
        return adj;
    }

    public Map<Location, Node> getVertices() { 
        return Collections.unmodifiableMap(vertices); 
    } 
 
    public Set<Edge> getEdges() { 
        return Collections.unmodifiableSet(edges); 
    } 
 
    public Map<Node, Set<Node>> getAdjList() { 
        return Collections.unmodifiableMap(adjacencyList); 
    } 

    /**
     * removing methods are redundant for this graph. Because the graph will be instantinate
     * in first place and then will not be any changing on its construction. Only Node's 
     * availability would be modified.
     */

    /**
    * The Node class represents vertices of a Graph with Segment code and levels.
     */
    public static class Node {
        private final Location location;
        private Piece piece;

        private Node(Segment segment, int level) {
            this(new Location(segment, level));        
        }

        private Node(Location location) {
            this.location = location;
        }

        private boolean available() {
            return piece == null;
        }
    
        @Override
        public String toString(){
            return location.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
           
            if ( !(o instanceof Node) ) return false;

            Node n = (Node)o;
        
            if (this.location.equals(n.location)) return true;
            return false; 
        }

        @Override
        public int hashCode() {
            return this.location.hashCode();
        }
        
    }
    
    public static class NoSuchNodeException extends Exception{
        // serialVersionUID get using by serialver.
        private static final long serialVersionUID = -167362L;
       
        public NoSuchNodeException(String s) {
            super(s);
        }
    }

    /**
     * The Edge class represents connections between Nodes from one point to another 
     * in the Graph.
     * Each edge's weight is 1 for the Rudder Game so no need to
     * use weight graph in here.
     * The graph will not be a digraph.
     * Edge class is redundant for this graph implementation. While writing these words,
     * I already implemented it and dont want to delete the code. Might be usefull in 
     * future, if not removing edge class and implementation will not effect any thing.
    */

    public static class Edge {
        Node from, to;

        private Edge(Node from, Node to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String toString() {
            return from.toString() + " --> " + to.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (!(o instanceof Edge)) return false;

            Edge e = (Edge)o;

            return (this.from == e.from && this.to == e.to) || (this.from == e.to && this.to == e.from);
        }

        @Override
        public int hashCode() {
            /**
             * return 31*from.hashCode() + 37*to.hashCode(); -- not using
             * implementation this method is taken from here,
             * stackoverflow.com/a/18066516/5929406
             */
            return Objects.hash(from, to);
        }
    }
}