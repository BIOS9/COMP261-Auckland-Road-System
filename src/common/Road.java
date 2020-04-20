package common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * common.Road represents ... a road ... in our graph, which is some metadata and a
 * collection of Segments. We have lots of information about Roads, but don't
 * use much of it.
 * 
 * @author tony
 */
public class Road {
	public final int roadID, speed, roadClass;
	public final String name, city;
	public final boolean oneWay, noCars, noBikes, noPedestrians;
	public final Collection<Segment> components;
	public double trafficLightValue = 0;

	public Road(int roadID, int type, String label, String city, int oneway,
			int speed, int roadclass, int notforcar, int notforpede,
			int notforbicy) {
		this.roadID = roadID;
		this.city = city;
		this.speed = speed;
		this.roadClass = roadclass;
		this.name = label;
		this.oneWay = (oneway == 1);
		this.noCars = (notforcar == 1);
		this.noBikes = (notforbicy == 1);
		this.noPedestrians = (notforpede == 1);
		this.components = new HashSet<Segment>();
	}

	public void addSegment(Segment seg) {
		components.add(seg);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Road road = (Road) o;
		return roadID == road.roadID &&
				Objects.equals(name, road.name) &&
				Objects.equals(city, road.city);
	}

	@Override
	public int hashCode() {
		return Objects.hash(roadID, name, city);
	}
}

// code for COMP261 assignments