package route;

import common.Node;
import common.Road;
import common.Segment;

import java.util.Collections;
import java.util.List;

/**
 * Represents a route between two nodes.
 *
 * @author Matthew Corfiatis
 */
public class Route {
    private List<Segment> segments;
    private List<Node> nodes;

    public Route(List<Segment> segments, List<Node> nodes) {
        this.segments = segments;
        this.nodes = nodes;
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }
}
