import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import acm.graphics.GOval;

/**
 * A representation of a graph node.
 * 
 * @author Lekan Wang (lekan@lekanwang.com)
 *
 */
public abstract class Node implements Drawable {
	private List<Edge> edges;
	double value; // An example of a value stored in the node.
	double x, y;	// coordinates
	// TODO You will need to modify or add to this to store what you need
	
	/**
	 * Creates an empty node with no edges, and a default 0 value;
	 */
	public Node() {
		this.edges = new ArrayList<Edge>();
	}
	
	/**
	 * Creates a node with no edges, and the given value.
	 * @param value
	 */
	public Node(double value) {
		this();
		this.value = value;
	}
	public Node(double x, double y){
		this();
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Adds the given edge to this node. The edge must be not null
	 * and valid.
	 * 
	 * @param edge
	 */
	public void addEdge(Edge edge) {
		assert (edge != null);
		edges.add(edge);
	}
	
	public boolean isDirectlyConnectedTo(Node other) {
		for(Edge edge: edges)
			if(edge.getNode1() == other || edge.getNode2() == other)
				return true;
		return false;
	}
	public Edge getEdge(Node n){
		for(Edge edge: edges)
			if(edge.getNode1() == n || edge.getNode2() == n)
				return edge;
		return null;
	}
	
	public void removeEdge(Edge e) {
		edges.remove(e);
	}
	
	/**
	 * Gets the list of edges as an unmodifiable list.
	 */
	public List<Edge> getEdges() {
		return Collections.unmodifiableList(edges);
	}
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	
}
