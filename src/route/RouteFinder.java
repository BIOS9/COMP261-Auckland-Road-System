package route;

import common.Graph;
import common.Node;
import common.Road;
import common.Segment;
import gui.GUI;
import io.Parser;

import java.util.*;

/**
 * Finds routes between points using A* search.
 *
 * @author Matthew Corfiatis
 */
public class RouteFinder {

    public static double TRAFFIC_LIGHT_COST = 0;
    public static double CLASS_FACTOR = 5000;

    public static Route findRoute(Graph graph, Node start, Node end, boolean useTime, GUI.Vehicle vehicle, Map<Integer, List<TurnRestriction>> restrictions) {
        System.out.printf("Finding route between %s and %s.%n", start, end);

        PriorityQueue<FringeElement> fringe = new PriorityQueue<>();
        Set<Node> visited = new HashSet<>();

        fringe.offer(new FringeElement(start, null, null, 0, getHeuristicCost(start, end, useTime)));

        while (!fringe.isEmpty()) {
            FringeElement currentElement = fringe.poll();

            if(currentElement.node.equals(end))
                return generateRoute(currentElement);

            if(visited.contains(currentElement.node))
                continue;
            visited.add(currentElement.node);

            for (Segment segment : currentElement.node.segments) {
                Node connectedNode;
                if(segment.road.oneWay) {
                    if(segment.start.equals(currentElement.node)) // Road is one way, can go from start to end.
                        connectedNode = segment.end;
                    else
                        continue; // Road is one way, cannot go from end node to start node.
                } else { // Road is bi-directional, can just get the other node.
                    connectedNode = segment.getOtherNode(currentElement.node);
                }

                if(restrictions != null && restrictions.containsKey(currentElement.node.nodeID)) {
                    List<TurnRestriction> r = restrictions.get(currentElement.node.nodeID);
                    Node previousNode = currentElement.previousElement.node;
                    Node nextNode = connectedNode;
                    Road previousRoad = currentElement.previousElement.connectingSegment.road;
                    Road nextRoad = segment.road;

                    for(TurnRestriction tr : r) {
                        if(previousNode.nodeID == tr.previousNodeID
                                && previousRoad.roadID == tr.previousRoadID
                                && nextNode.nodeID == tr.nextNodeID
                                && nextRoad.roadID == tr.nextRoadID) // Is turn restricted
                            continue;
                    }
                }

                // Take vehicle restrictions into account.
                switch (vehicle) {
                    case DRIVING:
                        if(segment.road.noCars)
                            continue;
                        break;
                    case BIKING:
                        if(segment.road.noBikes)
                            continue;
                        break;
                    case WALKING:
                        if(segment.road.noPedestrians)
                            continue;
                        break;
                }


                double classCost = 1 / (((double)segment.road.roadClass + 1) / ((double)Parser.maxClass + 1));
                double realCost = useTime ? (segment.length / segment.road.speed) : segment.length; // Use time = distance / speed if time is set
                if(useTime)
                    realCost += classCost * CLASS_FACTOR;

                realCost += (segment.road.trafficLightValue * TRAFFIC_LIGHT_COST);


                fringe.offer(new FringeElement(
                        connectedNode, // Node
                        currentElement, // Previous element.
                        segment, // Connecting segment.
                        realCost + currentElement.realCost, // Real cost.
                        getHeuristicCost(connectedNode, end, useTime) // Heuristic cost.
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
            if(currentElement.connectingSegment != null)
                segments.add(currentElement.connectingSegment);

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
    private static double getHeuristicCost(Node current, Node end, boolean useTime) {
        if(useTime)
            return (current.location.distance(end.location) / Parser.maxSpeed); // use max speed and max class so we can assume the minimum cost

        return current.location.distance(end.location);
    }
}
