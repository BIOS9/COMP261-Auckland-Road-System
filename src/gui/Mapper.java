package gui;

import com.sun.istack.internal.Nullable;
import common.Graph;
import common.Location;
import common.Node;
import route.Route;
import route.RouteFinder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;

/**
 * This is the main class for the mapping program. It extends the GUI abstract
 * class and implements all the methods necessary, as well as having a main
 * function.
 * 
 * @author tony
 */
public class Mapper extends GUI {
	public static final Color NODE_COLOUR = new Color(77, 113, 255);
	public static final Color SEGMENT_COLOUR = new Color(130, 130, 130);
	public static final Color HIGHLIGHT_COLOUR = new Color(255, 219, 77);

	// these two constants define the size of the node squares at different zoom
	// levels; the equation used is node size = NODE_INTERCEPT + NODE_GRADIENT *
	// log(scale)
	public static final int NODE_INTERCEPT = 1;
	public static final double NODE_GRADIENT = 0.8;

	private static final double ZOOM_SCALE_CHANGE = 0.3;
	private static final double ZOOM_SCROLL_SCALE_CHANGE = 0.1;
	private static final double MIN_SCALE = 0.1;
	private static final double MAX_SCALE = 10000;
	private static final double MOVE_CHANGE = 30;

	// how far away from a node you can click before it isn't counted.
	public static final double MAX_CLICKED_DISTANCE = 0.15;

	// these two define the 'view' of the program, ie. where you're looking and
	// how zoomed in you are.
	private Location origin = new Location(0, 0);
	private double scale = 10;
	private double dragStartOriginX = 0, dragStartOriginY = 0;

	private Node lastClicked = null;

	// our data structures.
	private Graph graph;

	/**
	 * Highlights a route between nodes.
	 * @param route Route to highlight.
	 */
	private void highlightRoute(@Nullable Route route) {
		if(route == null) {
			getTextOutputArea().setText("No route found between nodes.");
			return;
		}

		graph.setHighlightNodes(route.getNodes());
	}

	@Override
	protected void redraw(Graphics g) {
		if (graph != null)
			graph.draw(g, getDrawingAreaDimension(), origin, scale);
	}

	@Override
	protected void onClick(MouseEvent e) {
		Point clickPoint = e.getPoint();
		clickPoint.translate(-getDrawingAreaDimension().width / 2, -getDrawingAreaDimension().height / 2);
		Location clicked = Location.newFromPoint(clickPoint, origin, scale);

		// find the closest node.
		double bestDist = Double.MAX_VALUE;
		Node closest = null;

		for (Node node : graph.getNodes().values()) {
			double distance = clicked.distance(node.location);
			if (distance < bestDist) {
				bestDist = distance;
				closest = node;
			}
		}

		// if it's close enough, highlight it and show some information.
		if (clicked.distance(closest.location) < MAX_CLICKED_DISTANCE) {
			getTextOutputArea().setText(closest.toString());
			graph.setHighlight(closest);

			if(lastClicked == null) {
				lastClicked = closest;
			} else {
				Route route = RouteFinder.findRoute(graph, lastClicked, closest);
				highlightRoute(route);

				lastClicked = null;
			}
		}
	}

	@Override
	protected void onSearch() {
		// Does nothing
	}

	@Override
	protected void onMove(GUI.Move m) {
		switch (m) {
			case ZOOM_IN:
				scale += ZOOM_SCALE_CHANGE * scale; // Scale multiplication normalizes zooming so the zooming speed remains constant.
				if (scale > MAX_SCALE)
					scale = MAX_SCALE;
				break;
			case ZOOM_OUT:
				scale -= ZOOM_SCALE_CHANGE * scale;
				if (scale < MIN_SCALE)
					scale = MIN_SCALE;
				break;

			case NORTH:
				origin = origin.moveBy(0, MOVE_CHANGE / scale);
				break;
			case SOUTH:
				origin = origin.moveBy(0, -MOVE_CHANGE / scale);
				break;
			case EAST:
				origin = origin.moveBy(MOVE_CHANGE / scale, 0);
				break;
			case WEST:
				origin = origin.moveBy(-MOVE_CHANGE / scale, 0);
				break;
		}
	}

	@Override
	protected void onMouseDragged(double draggedX, double draggedY) {
		origin = new Location(
				dragStartOriginX - (draggedX / scale),
				dragStartOriginY + (draggedY / scale)
		);
	}

	@Override
	protected void onMouseMoved(double x, double y) {

	}

	@Override
	protected void onMouseDragStart() {
		dragStartOriginX = origin.x;
		dragStartOriginY = origin.y;
	}

	@Override
	protected void onMouseDragStop() {

	}

	@Override
	protected void onMouseWheelMove(MouseWheelEvent e) {
		scale -= e.getWheelRotation() * ZOOM_SCROLL_SCALE_CHANGE * scale;

		if (scale > MAX_SCALE)
			scale = MAX_SCALE;
		if (scale < MIN_SCALE)
			scale = MIN_SCALE;
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {
		graph = new Graph(nodes, roads, segments, polygons);
		origin = new Location(0, 0); // close enough
		scale = 10;
	}

	public static void main(String[] args) {
		new Mapper();
	}
}

// code for COMP261 assignments