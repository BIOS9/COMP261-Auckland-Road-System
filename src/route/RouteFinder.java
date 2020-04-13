package route;

import common.Graph;
import common.Node;
import common.Segment;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Finds routes between points using A* search.
 *
 * @author Matthew Corfiatis
 */
public class RouteFinder {

    public static Route findRoute(Graph graph, Node start, Node end) {
        System.out.printf("Finding route between %s and %s.%n", start, end);

        PriorityQueue<FringeElement> fringe = new PriorityQueue<>();
        Set<Node> visited = new HashSet<>();

        fringe.offer(new FringeElement(start, null, 0, getHeuristicCost(start, end)));

        while (!fringe.isEmpty()) {
            FringeElement currentElement = fringe.poll();

            if(currentElement.node.equals(end))
                return generateRoute(currentElement);

            if(visited.contains(currentElement.node))
                continue;
            visited.add(currentElement.node);

            for (Segment segment : currentElement.node.segments) {
                Node connectedNode = segment.getOtherNode(currentElement.node);

                fringe.offer(new FringeElement(
                        connectedNode, // Node
                        currentElement, // Previous element.
                        segment.length + currentElement.realCost, // Real cost.
                        getHeuristicCost(connectedNode, end) // Heuristic cost.
                ));
            }
        }

        return null;
    }

    /**
     * Generates a route using the final fringe element of an A* search result.
     * @param element Final fringe element containing goal node.
     * @return Route from start node to end node.
     */
    private static Route generateRoute(FringeElement element) {
        List<Node> nodes = new ArrayList<>();
        List<Segment> segments = new ArrayList<>();

        FringeElement currentElement = element;
        while (true) {
            nodes.add(currentElement.node);

            if(currentElement.previousElement != null)
                currentElement = currentElement.previousElement;
            else
                break;
        }

        Collections.reverse(nodes);
        Collections.reverse(segments);

        return new Route(segments, nodes);
    }

    /**
     * Gets the heuristic cost from the current node to the end node using
     * Euclidean distances.
     * @param current Current working node.
     * @param end Goal node.
     * @return Estimated cost value.
     */
    private static double getHeuristicCost(Node current, Node end) {
        return current.location.distance(end.location);
    }
}
