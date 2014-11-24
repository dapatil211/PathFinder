import java.awt.Color;

import acm.graphics.GObject;
import acm.graphics.GRect;


public class WayPoint extends Node{
	public static final int SIDE = 6;
	private GObject image;
	public WayPoint(double x, double y) {
		super(x, y);
		GRect rect = new GRect(x - SIDE/2, y - SIDE/2, SIDE, SIDE);
		rect.setFillColor(Color.BLUE);
		rect.setFilled(true);
		image = rect;
	}
	public GObject getImage(){
		return image;
	}
}
