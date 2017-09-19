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
    protected ArrayList<Vertex> vertices;
    protected HashSet<Edge> edges;
    private Color titleColor, backgroundColor;
    protected Color vertexColor, selectedVertexColor;
    protected Color edgeColor, selectedEdgeColor;
    private final static int MAX_X = Visualizer.DRAW_WIDTH;
    // subtract 100 for drawing of title
    private final static int MAX_Y = Visualizer.DRAW_HEIGHT - 100;


    public EuclideanGraph(String title) {
        this.title = title;

        // Set default colors
        backgroundColor = Color.BLACK;
        titleColor = Color.WHITE;
        vertexColor = Color.WHITE;
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
        int consecutiveFailures = 0;
        while (!verticeSet.isEmpty() && edges.size() < vertices.size() * 2 && consecutiveFailures < 400) {
            // System.out.println("Num Edges: " +  edges.size());
            // System.out.println("Vertices Set size:" + verticeSet.size());
        //for (int i = 0; i < 1000; i++) {
            Vertex vertexA = vertices.get(generator.nextInt(vertices.size()));
            Vertex vertexB = vertices.get(generator.nextInt(vertices.size()));
            // Vertex vertexA = (Vertex)verticeSet.toArray()[generator.nextInt(verticeSet.size())];
            // Vertex vertexB = (Vertex)verticeSet.toArray()[generator.nextInt(verticeSet.size())];

            Edge e = new Edge(vertexA, vertexB);

            boolean intersects = false;
            for (Edge edgeTwo : edges) {
                if (e.intersects(edgeTwo)) {
                    intersects = true;
                    break;
                }
            }
            if (intersects) {
                consecutiveFailures++;
                continue;
            }

            // arbitrarily set the max weight(length) to 60
            if (!edges.contains(e) && e.getWeight() < 200 && !vertexA.equals(vertexB)) {
                edges.add(e);
                // Todo: consider making Edge() {constructor} call Vertex.addEdge
                vertexA.addEdge(e);
                vertexB.addEdge(e);
                if (verticeSet.contains(vertexA)) {
                    verticeSet.remove(vertexA);
                }
                if (verticeSet.contains(vertexB)) {
                    verticeSet.remove(vertexB);
                }
                consecutiveFailures = 0;
            } else {
                consecutiveFailures++;
            }

        }

        // If a Vertex couldn't be removed from the set, remove it from the graph
        // because it's too far away or was just unlucky. I'll need to revisit this.
        for (Vertex v: verticeSet) {
            vertices.remove(v);
        }
        
    }

    @Override
    public void paintVisualization(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw Background
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, Visualizer.DRAW_WIDTH, Visualizer.DRAW_HEIGHT);

        //draw font
        Font font = new Font("Helvetica", Font.BOLD, 60);
        g2d.setFont(font);
        g2d.setColor(titleColor);
        FontMetrics metr = this.getFontMetrics(font);
        g2d.drawString(title, (Visualizer.DRAW_WIDTH - metr.stringWidth(title)) / 2, 100);

        // Paint vertices
        for (Vertex v : vertices) {
            v.paint(g2d);
        }

        for (Edge e : edges) {
            e.paint(g2d);
        }
    }


    protected class Vertex {
        private ArrayList<Edge> edges;
        private int x, y;
        private Color color;
        private int diameter;

        public Vertex(int x, int y) {
            this.x = x;
            this.y = y;
            diameter = 8;
            edges = new ArrayList<Edge>();
        }

        public int getX() {
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

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public int getDiameter() {
            return diameter;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Vertex)) return false;
            Vertex v = (Vertex) obj;
            return (x == v.getX() && y ==v.getY());
        }

        public void paint(Graphics2D g2d) {
            g2d.setColor(color);
            g2d.fillOval(x, y, 8, 8);
        }

    }

    protected class Edge {
        double weight;
        Vertex a, b;
        Color color;

        // Todo: constructor for disabling weight
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

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Edge)) return false;
            Edge e = (Edge) obj;
            return ((a.equals(e.getVertexA()) || a.equals(e.getVertexB()) ) && b.equals(e.getVertexA()) || b.equals(e.getVertexB()));
        }

        public void paint(Graphics2D g2d) {
            g2d.setColor(color);
            int offset = a.getDiameter() /2;
            g2d.drawLine(a.getX() + offset, a.getY() + offset, b.getX() + offset, b.getY() + offset);
            // Todo: print weight
        }

        public boolean intersects(Edge e) {
            if (Math.max(a.getX(), b.getX()) > Math.min(e.getVertexA().getX(), e.getVertexB().getX()) 
                && Math.min(a.getX(), b.getX()) < Math.max(e.getVertexA().getX(), e.getVertexB().getX())) {
                if (Math.max(a.getY(), b.getY()) > Math.min(e.getVertexA().getY(), e.getVertexB().getY()) 
                    && Math.min(a.getY(), b.getY()) < Math.max(e.getVertexA().getY(), e.getVertexB().getY())) {
                    return true;
                }
            }
            // if (Math.max(a.getY(), b.getY()) > Math.max(e.getVertexA().getY(), e.getVertexB().getY()) && Math.min(a.getY(), b.getY()) < Math.max(e.getVertexA().getY(), e.getVertexB().getY())) {
            //     if (Math.max(a.getX(), b.getX()) > Math.max(e.getVertexA().getX(), e.getVertexB().getX()) && Math.min(a.getX(), b.getX()) < Math.max(e.getVertexA().getX(), e.getVertexB().getX())) {
            //         return true;
            //     }
            // }
            
            return false;
        }
    }
}