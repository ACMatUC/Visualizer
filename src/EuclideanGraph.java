/***
 @author Kurt Lewis
 ***/

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class EuclideanGraph extends Visualizer {
    private String title;
    private ArrayList<Vertex> vertices;
    private Color titleColor, backgroundColor;
    private Color VertexColor, selectedVertexColor;
    private Color edgeColor, selectedEdgeColor;
    private final static int MAX_X = Visualizer.DRAW_WIDTH;
    // subtract 100 for drawing of title
    private final static int MAX_Y = Visualizer.DRAW_HEIGHT - 100;
    private HashSet<Edge> edges;

    public EuclideanGraph(String title) {
        this.title = title;

        // Set default colors
        backgroundColor = Color.BLACK;
        titleColor = Color.WHITE;
        VertexColor = Color.WHITE;
        selectedVertexColor = Color.CYAN;
        edgeColor = Color.GRAY;
        selectedEdgeColor = Color.MAGENTA;

        vertices = new ArrayList<Vertex>();
        edges = new HashSet<Edge>();
        buildGraph();
    }


    private void buildGraph() {
        Random generator  = new Random();
        int numVertices = generator.nextInt(50) + 50;
        HashSet<Vertex> verticeSet = new HashSet<Vertex>();

        // Generate random Vertices and add them to both the member variable list
        // and the scope-local set. The set will be used for removing Vertices too far away from the rest of the graph.
        for (int i = 0; i < numVertices; i++) {
            // Add DRAW_HEIGHT  - MAX_Y to the Y coordinate because the top pixels
            // are dedicated to the title - offset the y coordinate accordingly
            Vertex v = new Vertex(generator.nextInt(MAX_X), generator.nextInt(MAX_Y) + Visualizer.DRAW_HEIGHT - MAX_Y);

            // Don't allow for Vertices on top of each other
            if (!verticeSet.contains(v))
            {
                verticeSet.add(v);
                vertices.add(v);
            } else {
                i--;
            }           
        }
        
        // Add edges until either a connected graph is found or there are a 
        // set amount of Vertices
        while (!verticeSet.isEmpty() || edges.size() < vertices.size() * 3) {
            Vertex vertexA = vertices.get(generator.nextInt(vertices.size()));
            Vertex vertexB = vertices.get(generator.nextInt(vertices.size()));

            Edge e = new Edge(vertexA, vertexB);
            // arbitrarily set the max weight(length) to 60
            if (!edges.contains(e) && e.getWeight() < 60 && !vertexA.equals(vertexB)) {
                edges.add(e);
                vertexA.addEdge(e);
                vertexB.addEdge(e);
                if (verticeSet.contains(vertexA)) {
                    verticeSet.remove(vertexA);
                }
                if (verticeSet.contains(vertexB)) {
                    verticeSet.remove(vertexB);
                }
            }
        }

        // If a Vertex couldn't be removed from the set, remove it from the graph
        // because it's too far away or was just unlucky. I'll need to revisit this.
        for (Vertex v: verticeSet) {
            vertices.remove(v);
        }
        
    }


    protected class Vertex {
        private ArrayList<Edge> edges;
        private int x, y;

        public Vertex(int x, int y) {
            this.x = x;
            this.y = y;
            edges = new ArrayList<Edge>();
        }

        public int getX(){
            return x;
        }

        public int getY() {
            return y;
        }

        public void addEdge(Edge e) {
            edges.add(e);
        }

        public ArrayList<Edge> getEdges() {
            return edges;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Vertex)) return false;
            Vertex v = (Vertex) obj;
            return (x == v.getX() && y ==v.getY());
        }

    }

    protected class Edge {
        double weight;
        Vertex a, b;

        public Edge(Vertex a, Vertex b) {
            this.a = a;
            this.b = b;
            weight = Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
        }

        public Edge(Vertex a, Vertex b, double weight) {
            this.a = a;
            this.b = b;
            this.weight = weight;
        }

        public double getWeight() {
            return weight;
        }

        public Vertex getVertexA() {
            return a;
        }

        public Vertex getVertexB() {
            return  b;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Edge)) return false;
            Edge e = (Edge) obj;
            return ((a.equals(e.getVertexA()) || a.equals(e.getVertexB()) ) && b.equals(e.getVertexA()) || b.equals(e.getVertexB()));
        }
    }
}