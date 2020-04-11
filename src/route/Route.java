package route;

import common.Node;
import common.Road;

import java.util.Collections;
import java.util.List;

/**
 * Represents a route between two nodes.
 *
 * @author Matthew Corfiatis
 */
public class Route {
    private List<Road> roads;
    private List<Node> nodes;

    public Route(List<Road> roads, List<Node> nodes) {
        this.roads = roads;
        this.nodes = nodes;
    }

    public List<Road> getRoads() {
        return Collections.unmodifiableList(roads);
    }

    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }
}
