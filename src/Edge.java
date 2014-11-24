import java.awt.Color;
import java.awt.Font;

import acm.graphics.GCompound;
import acm.graphics.GLabel;
import acm.graphics.GLine;
import acm.graphics.GObject;
import acm.graphics.GPoint;

/**
 * A representation of a graph node.
 * 
 * @author Lekan Wang (lekan@lekanwang.com)
 *
 */
public class Edge implements Drawable {
	
	static GObject makeEdgeImage(double x1, double y1, double x2, double y2) {
		GCompound comp = new GCompound();
		for(int d = -LINE_THICKNESS; d <= LINE_THICKNESS; d++)
			comp.add(new GLine(x1, y1 + d, x2, y2 + d));
		comp.setColor(Color.BLACK);
		return comp;
	}
	
	private static final int OFFSET = 5;
	private Node n1, n2;
	private GPoint mid;
	private double cost;
	private static final int LINE_THICKNESS = 0; //line thickness is 2 * LINE_THICKNESS + 1
	private GObject image;
	private double slope;
	private GLabel label;
	
	// TODO You will probably want to add more stuff here.
	
	/**
	 * Creates an edge that connects nodes n1 and n2.
	 */
	public Edge(Node n1, Node n2) {
		this(n1, n2, 1);
	}
	
	public GPoint getMid() {
		return mid;
	}

	public double getSlope() {
		return slope;
	}

	public Edge(Node n1, Node n2, double cost) {
		this.n1 = n1;
		this.n2 = n2;
		n1.addEdge(this);
		n2.addEdge(this);
		image = makeEdgeImage(n1.x, n1.y, n2.x, n2.y);
		slope = (n1.y - n2.y) / (n1.x - n2.x);
		mid = new GPoint((n1.x + n2.x) / 2, (n1.y + n2.y) / 2);
		this.cost = cost;
		initLabel();
	}
	
	private void initLabel() {
		label = new GLabel(String.valueOf(cost));
		if(slope > 0)
			label.setLocation(mid.getX() - OFFSET,mid.getY() + OFFSET);
		else
			label.setLocation(mid.getX() + OFFSET, mid.getY() - OFFSET);
		label.setFont(new Font("Britannic Bold", Font.BOLD, 14));
	}
	
	/**
	 * Gets both nodes as a Pair<Node> object. You can access
	 * the individual Nodes by saying pair.left, and pair.right
	 * once you have the pair.
	 * 
	 * This method does not guarantee any ordering on the nodes.
	 * Hence, this could be useful when working with undirected
	 * graphs. (*hint hint*)
	 * 
	 * @return
	 */
	public Pair<Node> getNodes() {
		return new Pair<Node>(n1, n2);
	}
	
	/**
	 * Returns the first node. Because this guarantees an order,
	 * it could be useful when working with directed graphs.
	 * 
	 * @return
	 */
	public Node getNode1() {
		return this.n1;
	}
	
	/**
	 * Returns the second node. Because this guarantees an order,
	 * it could be useful when working with directed graphs.
	 * 
	 * @return
	 */
	public Node getNode2() {
		return this.n2;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public double getCost() {
		return this.cost;
	}

	public GObject getImage() {
		return image;
	}
	
	public GLabel getLabel() {
		return label;
	}
}
