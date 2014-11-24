import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import acm.graphics.*;
import acm.program.*;

/**
 * Who needs Google Maps when you have Pathfinder?
 * 
 * @author Darshan Patil, Raymond Chee
 * TODO: Multiple edges between two nodes
 * TODO: Line Thickness
 */
public class Pathfinder extends GraphicsProgram {
	public static void main(String[] args) {
		new Pathfinder().start(args);
	}
	
	// Constants
	public static final int APPLICATION_WIDTH = 1150;
	public static final int APPLICATION_HEIGHT = 620;
	public static final String USA_MAP_FILE = "USAmap-1000x618.png";
	public static final String STANFORD_MAP_FILE = "Stanfordmap-1000x618.png";
	private static final String PROJECT_FILE_PATH = "C:\\Users\\scamper\\workspace\\Pathfinder-Starter";
	private static final JButton MODE_BUTTON = new JButton("MODE");
	private static final JButton FIND_SPATH_BUTTON = new JButton("Find Shortest Path");
	private static final JButton FIND_CPATH_BUTTON = new JButton("Find Cheapest Path");
	private static final JButton ADD_DEST_BUTTON = new JButton("Add a Destination");
	private static final JButton ADD_WAYP_BUTTON = new JButton("Add a Waypoint");
	private static final JButton REMOVE_NODE_BUTTON = new JButton("Remove a Node");
	private static final JButton REMOVE_EDGE_BUTTON = new JButton("Remove an Edge");
	private static final JButton SAVE_MAP_BUTTON = new JButton("Save Map");
	private static final JButton SAVE_MAP_AS_BUTTON = new JButton("Save Map As");
	private static final JButton OPEN_MAP_BUTTON = new JButton("Open Map");
	private static final int REMOVE_EDGE_CODE = -2;
	private static final int REMOVE_NODE_CODE = -1;
	private static final int NO_NODE_CODE = 0;
	private static final int DEST_CODE = 1;
	private static final int WAYP_CODE = 2;
	private static final double OFFSET = 5;
	private String mapFileName = USA_MAP_FILE;
	private GImage mapImage;
	private int nextAction = NO_NODE_CODE;	// Destination: 1, no Node: 0, Waypoint: -1
	private boolean editMode = false;
	private int destCount = 0;
	private double startX = -1, startY = -1;
	private GObject curObject;
	private boolean searchOn = false;
	private Destination d1;
	private GLabel searchLabel = new GLabel("Search Mode is on!");
	private Path highlighted;
	private boolean cPath;
	
	
	// [YOUR DATA STRUCTURES WILL GO HERE]
	private HashMap<GObject, Drawable> imageMap = new HashMap<GObject, Drawable>();
	private Set<Node> nodes = new HashSet<Node>();
	private Set<Edge> edges = new HashSet<Edge>();
	
	@Override
	public void init() {
		// Setup code goes here
		this.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		setTitle("Pathfinder BETA!");
		initMap();
		addMouseListeners();
		initButton(MODE_BUTTON);
		initButton(FIND_SPATH_BUTTON);
		initButton(FIND_CPATH_BUTTON);
		initButton(ADD_DEST_BUTTON);
		initButton(ADD_WAYP_BUTTON);
		initButton(REMOVE_NODE_BUTTON);
		initButton(REMOVE_EDGE_BUTTON);
		initButton(SAVE_MAP_BUTTON);
		initButton(SAVE_MAP_AS_BUTTON);
		initButton(OPEN_MAP_BUTTON);
		MODE_BUTTON.setText("EDIT");
		FIND_SPATH_BUTTON.setEnabled(false);
		FIND_CPATH_BUTTON.setEnabled(false);
		ADD_DEST_BUTTON.setEnabled(false);
		ADD_WAYP_BUTTON.setEnabled(false);
		REMOVE_NODE_BUTTON.setEnabled(false);
		REMOVE_EDGE_BUTTON.setEnabled(false);
	}
	
	private void initButton(JButton button) {
		button.setActionCommand(button.getText());
		button.addActionListener(this);
		add(button, WEST);
	}
	
	private void initMap() {
		removeAll();
		mapImage = new GImage(mapFileName);
		add(mapImage);
		searchLabel.setLocation(getGCanvas().getWidth() - searchLabel.getWidth() - OFFSET, OFFSET + searchLabel.getHeight());
		add(searchLabel);
		searchLabel.setVisible(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		searchOn = false;
		searchLabel.setVisible(false);
		if(highlighted != null){
			highlighted.deHighlight();
			highlighted = null;
		}
		if(cmd.equals(MODE_BUTTON.getActionCommand())) {
			nextAction = NO_NODE_CODE;
			editMode = !editMode;
			MODE_BUTTON.setText(editMode ? "SEARCH" : "EDIT");
			FIND_SPATH_BUTTON.setEnabled(!editMode && destCount >= 2);
			FIND_CPATH_BUTTON.setEnabled(!editMode && destCount >= 2);
			ADD_DEST_BUTTON.setEnabled(editMode);
			ADD_WAYP_BUTTON.setEnabled(editMode);
		}
		else if(cmd.equals(FIND_SPATH_BUTTON.getActionCommand())) {
			JOptionPane.showMessageDialog(this, "Choose two destinations");
			searchOn = true;
			searchLabel.setVisible(true);
			d1 = null;
			cPath = false;
		}
		else if(cmd.equals(FIND_CPATH_BUTTON.getActionCommand())){
			JOptionPane.showMessageDialog(this, "Choose two destinations");
			searchOn = true;
			searchLabel.setVisible(true);
			d1 = null;
			cPath = true;
		}
		else if(cmd.equals(ADD_DEST_BUTTON.getActionCommand()))
			nextAction = DEST_CODE;
		else if(cmd.equals(ADD_WAYP_BUTTON.getActionCommand()))
			nextAction = WAYP_CODE;
		else if(cmd.equals(REMOVE_NODE_BUTTON.getActionCommand()))
			nextAction = REMOVE_NODE_CODE;
		else if(cmd.equals(REMOVE_EDGE_BUTTON.getActionCommand()))
			nextAction = REMOVE_EDGE_CODE;
		else if(cmd.equals(SAVE_MAP_BUTTON.getActionCommand())) {
			JFileChooser chooser = new JFileChooser(PROJECT_FILE_PATH);
			chooser.setFileFilter(new FileNameExtensionFilter("MAP file", "map"));
			if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
				saveMap(chooser.getSelectedFile());
		}
		else if(cmd.equals(SAVE_MAP_AS_BUTTON.getActionCommand())) {
			String fileName = JOptionPane.showInputDialog(this, "What is the name of the file?");
			if(fileName != null)
				saveMap(new File(fileName + ".map"));
		}
		else if(cmd.equals(OPEN_MAP_BUTTON.getActionCommand())) {
			JFileChooser chooser = new JFileChooser(PROJECT_FILE_PATH);
			chooser.setFileFilter(new FileNameExtensionFilter("MAP file", "map", "png"));
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				openMap(chooser.getSelectedFile());
		}
		
		REMOVE_NODE_BUTTON.setEnabled(editMode && !nodes.isEmpty());
		REMOVE_EDGE_BUTTON.setEnabled(editMode && !edges.isEmpty());
	}
	
	private void openMap(File selectedFile) {
		nodes.clear();
		edges.clear();
		imageMap.clear();
		if(selectedFile.getName().endsWith(".png")) {
			mapFileName = selectedFile.getName();
			initMap();
			destCount = 0;
		}
		else
			try {
				Scanner input = new Scanner(selectedFile);
				mapFileName = input.nextLine();
				initMap();
				int nodeCount = input.nextInt();
				input.nextLine();
				Node[] nodeArr = new Node[nodeCount];
				destCount = 0;
				for(int i = 0; i < nodeCount; i++) {
					String[] data = input.nextLine().split(" ");
					String type = data[0];
					Node n;
					if(type.equals("d")) {
						String name = data[1];
						for(int j = 2; j <data.length - 2; j++)
							name += " " + data[j];
						n = new Destination(name, Double.parseDouble(data[data.length - 2]), Double.parseDouble(data[data.length - 1]));
						destCount++;
					}
					else
						n = new WayPoint(Double.parseDouble(data[1]), Double.parseDouble(data[2]));
					nodeArr[i] = n;
					addNode(n);
				}
				int edgeCount = input.nextInt();
				for(int i = 0; i < edgeCount; i++) {
					Edge e = addEdge(nodeArr[input.nextInt()], nodeArr[input.nextInt()]);
					e.setCost(input.nextDouble());
				}
				input.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
	}

	private void saveMap(File f) {
		try {
			PrintStream out = new PrintStream(f);
			out.println(mapFileName);
			out.println(nodes.size());
			ArrayList<Node> nodeList = new ArrayList<Node>(nodes);
			for(Node n: nodes)
				out.println((n instanceof Destination ? "d " + ((Destination)n).getName(): "w") + " " + n.x + " " + n.y);
			out.println(edges.size());
			for(Edge e: edges)
				out.println(nodeList.indexOf(e.getNode1()) + " " + nodeList.indexOf(e.getNode2()) + " " + e.getCost());
			out.close();
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(editMode) {
			int x, y;
			GObject image = this.getElementAt(x = e.getX(), y = e.getY());
			if(image != null && imageMap.containsKey(image) && !(image instanceof GCompound)) {
				startX = x;
				startY = y;
			}
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(editMode && startX >= 0 && startY >= 0) {
			if(curObject != null)
				remove(curObject);
			add(curObject = Edge.makeEdgeImage(startX, startY, e.getX(), e.getY()));
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(editMode && startX >= 0 && startY >= 0) {
			if(curObject != null)
				remove(curObject);
			curObject = null;
			GObject image = this.getElementAt(e.getX(), e.getY());
			GObject image2 = this.getElementAt(startX, startY);
			startX = -1;
			startY = -1;
			if(image != null && imageMap.containsKey(image) && !(image instanceof GCompound) && image != image2)
				addEdge((Node)imageMap.get(image),(Node)imageMap.get(image2));
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		double x = e.getX(), y = e.getY();
		if(editMode) {
			GObject image = this.getElementAt(x, y);
			if(image != null && imageMap.containsKey(image) && image instanceof GCompound && nextAction != REMOVE_EDGE_CODE) {
				String dist = JOptionPane.showInputDialog(this, "What is the cost of this edge?");
				Edge edge = (Edge)imageMap.get(image);
				if(dist != null)
					try {
						double d = Double.parseDouble(dist);
						edge.setCost(d);
					} catch(NumberFormatException ex) {}
			}
			else
				switch(nextAction) {
				case REMOVE_NODE_CODE:
					if(imageMap.containsKey(image) && !(image instanceof GCompound))
						removeNode((Node)imageMap.get(image));
					break;
				case REMOVE_EDGE_CODE:
					if(imageMap.containsKey(image) && image instanceof GCompound)
						removeEdge((Edge)imageMap.get(image));
					break;
				case WAYP_CODE:
					addNode(new WayPoint(x,y));
					break;
				case DEST_CODE:
					String name = JOptionPane.showInputDialog(this, "What is the name of the destination?");
					if(name == null)
						break;
					Destination dest = new Destination(name, x, y);
					addNode(dest);
					destCount++;
					break;
				}
		}
		else if(searchOn){
			GObject imageClicked = this.getElementAt(e.getX(), e.getY());
			if(imageClicked instanceof GOval){
				Destination d = (Destination)imageMap.get(imageClicked);
				if(d1 == null)
					d1 = d;
				else if(d1 != d) {
					Path p = cPath ? Djikstra(d1, d) : BFS(d1, d);
					if(p != null) {
						highlighted = p;
						p.highlight();
						d1 = null;
					}
					searchOn = false;
				}
			}
		}
//		Random r = new Random();
//		GLabel sillyLabel = new GLabel(":D", r.nextDouble() * getWidth(), r.nextDouble() * getHeight());
//		add(sillyLabel);
	}
	

	private void addNode(Node node) {
		if(node instanceof Destination) {
			Destination dest = (Destination) node;
			final GLabel LABEL = dest.getLabel();
			dest.getImage().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					add(LABEL);
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					remove(LABEL);
				}
			});
		}
		GObject obj = node.getImage();
		imageMap.put(obj, node);
		nodes.add(node);
		add(obj);
		REMOVE_NODE_BUTTON.setEnabled(true);
	}
	
	private void removeNode(Node node) {
		if(node instanceof Destination) {
			Destination dest = (Destination)node;
			remove(dest.getLabel());
		}
		GObject obj = node.getImage();
		imageMap.remove(obj);
		nodes.remove(node);
		remove(obj);
		for(Edge e: node.getEdges())
			removeEdge(e);
		REMOVE_NODE_BUTTON.setEnabled(!nodes.isEmpty());
	}
	
	private Edge addEdge(Node n1, Node n2) {
		if(n1.isDirectlyConnectedTo(n2))
			return null;
		final Edge edge = new Edge(n1, n2);
		GObject edgeImage = edge.getImage();
		final GLabel LABEL = edge.getLabel();
		edgeImage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				LABEL.setLabel("" + edge.getCost());
				add(LABEL);
				LABEL.sendBackward();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				remove(LABEL);
			}
		});
		imageMap.put(edgeImage, edge);
		add(edgeImage);
		edges.add(edge);
		edgeImage.sendBackward();
		REMOVE_EDGE_BUTTON.setEnabled(true);
		return edge;
	}
	
	private void removeEdge(Edge edge) {
		GObject image = edge.getImage();
		imageMap.remove(image);
		remove(image);
		edges.remove(edge);
		remove(edge.getLabel());
		edge.getNode1().removeEdge(edge);
		edge.getNode2().removeEdge(edge);
		REMOVE_EDGE_BUTTON.setEnabled(!edges.isEmpty());
	}
	
	private Path Djikstra(Node start, Node goal){
		Queue<Path> frontier = new PriorityQueue<Path>();
		frontier.add(new Path(start));
		Set<Path> explored = new HashSet<Path>();
		while(!frontier.isEmpty()) {
			Path p = frontier.poll();
			if(p.getEnd() == goal)
				return p;
			explored.add(p);
			Node end = p.getEnd();
			for(Edge e: p.getEnd().getEdges()) {
				Node newNode = e.getNode1() == end ? e.getNode2() : e.getNode1();
				Path newPath = new Path(p, newNode);
				if(!explored.contains(newPath))
					frontier.add(newPath);
			}
		}
		return null;
	}
	private Path BFS(Node start, Node goal) {
		Queue<Path> frontier = new LinkedList<Path>();
		frontier.add(new Path(start));
		Set<Path> explored = new HashSet<Path>();
		while(!frontier.isEmpty()) {
			Path p = frontier.poll();
			if(p.getEnd() == goal)
				return p;
			explored.add(p);
			Node end = p.getEnd();
			for(Edge e: p.getEnd().getEdges()) {
				Node newNode = e.getNode1() == end ? e.getNode2() : e.getNode1();
				Path newPath = new Path(p, newNode);
				if(!explored.contains(newPath))
					frontier.add(newPath);
			}
		}
		return null;
	}
}
