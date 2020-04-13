package route;

import common.Node;

public class FringeElement implements Comparable<FringeElement> {
    public final Node node;
    public final FringeElement previousElement; // Previous fringe node
    public final double realCost, // Real cost from start node to this node.
            estimatedCost; // Real cost to this node + estimated cost to end node

    public FringeElement(Node node, FringeElement previousElement, double realCost, double heuristicCost) {
        this.node = node;
        this.previousElement = previousElement;
        this.realCost = realCost;
        this.estimatedCost = realCost + heuristicCost;
    }

    @Override
    public int compareTo(FringeElement o) {
        if(o == null)
            return -1;

        if(estimatedCost < o.estimatedCost)
            return -1;
        else if(estimatedCost > o.estimatedCost)
            return 1;

        return 0;
    }
}
