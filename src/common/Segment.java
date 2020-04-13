package common;

import java.awt.*;
import java.util.Objects;

/**
 * A common.Segment is the most interesting class making up our graph, and represents
 * an edge between two Nodes. It knows the common.Road it belongs to as well as the
 * Nodes it joins, and contains a series of Locations that make up the length of
 * the common.Segment and can be used to render it.
 * 
 * @author tony
 */
public class Segment {

	public final Road road;
	public final Node start, end;
	public final double length;
	public final Location[] points;

	public Segment(Graph graph, int roadID, double length, int node1ID,
			int node2ID, double[] coords) {

		this.road = graph.roads.get(roadID);
		this.start = graph.nodes.get(node1ID);
		this.end = graph.nodes.get(node2ID);
		this.length = length;

		points = new Location[coords.length / 2];
		for (int i = 0; i < points.length; i++) {
			points[i] = Location
					.newFromLatLon(coords[2 * i], coords[2 * i + 1]);
		}

		this.road.addSegment(this);
		this.start.addSegment(this);
		this.end.addSegment(this);
	}

	public void draw(Graphics g, Dimension area, Location origin, double scale) {
		for (int i = 1; i < points.length; i++) {
			Point p = points[i - 1].asPoint(origin, scale);
			p.translate(area.width / 2, area.height / 2);

			Point q = points[i].asPoint(origin, scale);
			q.translate(area.width / 2, area.height / 2);

			g.drawLine(p.x, p.y, q.x, q.y);
		}
	}

	/**
	 * Gets the node that is not the specified node.
	 * @param node Node to not return.
	 * @return Node at the other end of the segment.
	 */
	public Node getOtherNode(Node node) {
		if(start.equals(node))
			return end;
		return start;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Segment segment = (Segment) o;
		return Double.compare(segment.length, length) == 0 &&
				Objects.equals(road, segment.road) &&
				Objects.equals(start, segment.start) &&
				Objects.equals(end, segment.end);
	}

	@Override
	public int hashCode() {
		return Objects.hash(road, start, end, length);
	}
}

// code for COMP261 assignments