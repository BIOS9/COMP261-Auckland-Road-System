package route;

public class TurnRestriction {
    public final int nodeID;
    public final int previousNodeID, previousRoadID;
    public final int nextNodeID, nextRoadID;

    public TurnRestriction(int nodeID, int previousNodeID, int previousRoadID, int nextNodeID, int nextRoadID) {
        this.nodeID = nodeID;
        this.previousNodeID = previousNodeID;
        this.previousRoadID = previousRoadID;
        this.nextNodeID = nextNodeID;
        this.nextRoadID = nextRoadID;
    }

    public TurnRestriction(String nodeID, String previousNodeID, String previousRoadID, String nextNodeID, String nextRoadID) {
        this.nodeID = Integer.parseInt(nodeID);
        this.previousNodeID = Integer.parseInt(previousNodeID);
        this.previousRoadID = Integer.parseInt(previousRoadID);
        this.nextNodeID = Integer.parseInt(nextNodeID);
        this.nextRoadID = Integer.parseInt(nextRoadID);
    }
}
