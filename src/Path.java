import java.awt.Color;
import java.util.ArrayList;


public class Path implements Comparable<Path> {
	private ArrayList<Edge> path = new ArrayList<Edge>();
	private Node start, end;
	private double cost;
	public Path(Node n) {
		this.start = n;
		this.end = n;
		this.cost = 0;
	}
	
	public Path(Path p, Node n) {
		assert p.end.isDirectlyConnectedTo(n);
		this.path.addAll(p.path);
		Edge edge = n.getEdge(p.end);
		this.path.add(edge);
		this.start = p.start;
		this.end = n;
		this.cost = p.cost + edge.getCost();
	}
	
	public Node getStart() {
		return start;
	}
	
	public Node getEnd() {
		return end;
	}
	
	public double getCost() {
		return this.cost;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof Path) {
			Path p = (Path)obj;
			return this.path.equals(p.path);
		}
		return false;
	}
	
	@Override
	public int compareTo(Path p) {
		if(this.cost == p.cost)
			return this.path.size() - p.path.size();
		return (int)Math.signum(this.cost - p.cost);
	}

	public void highlight() {
		for(Edge e : path){
			e.getImage().setColor(Color.MAGENTA);
		}	
	}
	public void deHighlight() {
		for(Edge e : path){
			e.getImage().setColor(Color.BLACK);
		}	
	}
	@Override
	public String toString() {
		String s = "";
		Node n = start;
		for(Edge e : path) {
			s += n + " - " + e.getCost() + " -> ";
			n = e.getNode1() == n ? e.getNode2() : e.getNode1();
		}
		return s + end;
	}
}
