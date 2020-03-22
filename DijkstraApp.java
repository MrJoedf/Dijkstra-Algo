package scratch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class DijkstraApp extends JFrame {
    private List<Node> nodes;
    private Node selectedNode;
    private int nodeCount;
    private List<Node> shortestPath;
    private JButton showShortestPathButton;
    
    public DijkstraApp() {
        nodes = new ArrayList<>();
        selectedNode = null;
        nodeCount = 0;
        shortestPath = new ArrayList<>();
        
        setTitle("Dijkstra's Algorithm");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Node node : nodes) {
                    node.draw(g);
                }
                drawShortestPath(g);
            }
        };
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Left mouse button
                    int x = e.getX();
                    int y = e.getY();
                    char label = (char) ('A' + nodeCount); // Assign a letter identifier
                    nodes.add(new Node(x, y, label));
                    nodeCount++;
                    canvas.repaint();
                } else if (e.getButton() == MouseEvent.BUTTON3) { // Right mouse button
                    selectedNode = null;
                    shortestPath.clear();
                    canvas.repaint();
                }
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedNode != null) {
                    int x = e.getX();
                    int y = e.getY();
                    selectedNode.setPosition(x, y);
                    canvas.repaint();
                }
            }
        });
        
        JButton setDistanceButton = new JButton("Set Distance");
        setDistanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nodes.size() >= 2) {
                    String[] nodeNames = new String[nodes.size()];
                    for (int i = 0; i < nodes.size(); i++) {
                        nodeNames[i] = String.valueOf(nodes.get(i).getLabel());
                    }
                    String source = (String) JOptionPane.showInputDialog(
                            DijkstraApp.this,
                            "Select source node:",
                            "Set Distance",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            nodeNames,
                            nodeNames[0]);
                    if (source != null) {
                        String destination = (String) JOptionPane.showInputDialog(
                                DijkstraApp.this,
                                "Select destination node:",
                                "Set Distance",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                nodeNames,
                                nodeNames[0]);
                        if (destination != null) {
                            int distance = Integer.parseInt(JOptionPane.showInputDialog(
                                    DijkstraApp.this,
                                    "Enter distance between nodes:",
                                    "Set Distance",
                                    JOptionPane.PLAIN_MESSAGE));
                            Node sourceNode = findNodeByLabel(source.charAt(0));
                            Node destinationNode = findNodeByLabel(destination.charAt(0));
                            if (sourceNode != null && destinationNode != null) {
                                sourceNode.addNeighbor(destinationNode, distance);
                                destinationNode.addNeighbor(sourceNode, distance);
                                canvas.repaint();
                            }
                        }
                    }
                }
            }
        });
        
        showShortestPathButton = new JButton("Show Shortest Path");
        showShortestPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nodes.size() >= 2) {
                    String[] nodeNames = new String[nodes.size()];
                    for (int i = 0; i < nodes.size(); i++) {
                        nodeNames[i] = String.valueOf(nodes.get(i).getLabel());
                    }
                    String source = (String) JOptionPane.showInputDialog(
                            DijkstraApp.this,
                            "Select source node:",
                            "Show Shortest Path",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            nodeNames,
                            nodeNames[0]);
                    if (source != null) {
                        String destination = (String) JOptionPane.showInputDialog(
                                DijkstraApp.this,
                                "Select destination node:",
                                "Show Shortest Path",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                nodeNames,
                                nodeNames[0]);
                        if (destination != null) {
                            Node sourceNode = findNodeByLabel(source.charAt(0));
                            Node destinationNode = findNodeByLabel(destination.charAt(0));
                            if (sourceNode != null && destinationNode != null) {
                                findShortestPath(sourceNode, destinationNode);
                                canvas.repaint();
                            }
                        }
                    }
                }
            }
        });
        
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(setDistanceButton);
        buttonPanel.add(showShortestPathButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private Node findNodeByLabel(char label) {
        for (Node node : nodes) {
            if (node.getLabel() == label) {
                return node;
            }
        }
        return null;
    }
    
    private void findShortestPath(Node sourceNode, Node destinationNode) {
         Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>((n1, n2) -> distances.get(n1) - distances.get(n2));
        
        // Initialize distances with infinity for all nodes except the source node
        for (Node node : nodes) {
            distances.put(node, Integer.MAX_VALUE);
            if (node == sourceNode) {
                distances.put(node, 0);
            }
        }
        
        queue.add(sourceNode);
        
        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            
            if (currentNode == destinationNode) {
                break; // Reached the destination node
            }
            
            for (Neighbor neighbor : currentNode.getNeighbors()) {
                Node adjacentNode = neighbor.getNode();
                int distance = neighbor.getDistance();
                int totalDistance = distances.get(currentNode) + distance;
                
                if (totalDistance < distances.get(adjacentNode)) {
                    queue.remove(adjacentNode);
                    distances.put(adjacentNode, totalDistance);
                    previousNodes.put(adjacentNode, currentNode);
                    queue.add(adjacentNode);
                }
            }
        }
        
        shortestPath.clear();
        Node current = destinationNode;
        while (current != null) {
            shortestPath.add(0, current);
            current = previousNodes.get(current);
        }
    }
    
    private void drawShortestPath(Graphics g) {
        g.setColor(Color.GREEN);
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            Node node1 = shortestPath.get(i);
            Node node2 = shortestPath.get(i + 1);
            g.drawLine(node1.getX(), node1.getY(), node2.getX(), node2.getY());
        }
    }
    
    private class Node {
        private int x;
        private int y;
        private char label;
        private List<Neighbor> neighbors;
        
        public Node(int x, int y, char label) {
            this.x = x;
            this.y = y;
            this.label = label;
            this.neighbors = new ArrayList<>();
        }
        
        public void addNeighbor(Node node, int distance) {
            Neighbor neighbor = new Neighbor(node, distance);
            neighbors.add(neighbor);
        }
        
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void draw(Graphics g) {
            g.setColor(Color.RED);
            g.fillOval(x - 10, y - 10, 20, 20);
            g.setColor(Color.BLACK);
            g.drawOval(x - 10, y - 10, 20, 20);
            g.drawString(String.valueOf(label), x - 3, y + 4);
            
            for (Neighbor neighbor : neighbors) {
                int neighborX = neighbor.getNode().getX();
                int neighborY = neighbor.getNode().getY();
                g.setColor(Color.BLACK);
                g.drawLine(x, y, neighborX, neighborY);
                int labelX = (x + neighborX) / 2;
                int labelY = (y + neighborY) / 2;
                g.drawString(String.valueOf(neighbor.getDistance()), labelX, labelY);
            }
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public char getLabel() {
            return label;
        }
        
        public List<Neighbor> getNeighbors() {
            return neighbors;
        }
    }
    
    private class Neighbor {
        private Node node;
        private int distance;
        
        public Neighbor(Node node, int distance) {
            this.node = node;
            this.distance = distance;
        }
        
        public Node getNode() {
            return node;
        }
        
        public int getDistance() {
            return distance;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DijkstraApp app = new DijkstraApp();
                app.setVisible(true);
            }
        });
    }
}
