import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;


public class Destination extends Node{
	private String name;
	private GObject image;
	private GLabel label;
	public static final int RADIUS = 5;
	public Destination(String name, double x, double y){
		super(x, y);
		this.name = name;
		GOval oval = new GOval(x - RADIUS, y - RADIUS, 2 * RADIUS, 2 * RADIUS);
		oval.setFillColor(Color.RED);
		oval.setFilled(true);
		image = oval;
		label = new GLabel(name, x - Destination.RADIUS, y - Destination.RADIUS);
		label.setFont(new Font("Britannic Bold", Font.BOLD, 14));
	}
	
	public String getName() {
		return name;
	}
	
	public GObject getImage(){
		return image;
	}
	
	public GLabel getLabel() {
		return label;
	}
}
