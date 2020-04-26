package common;

import gui.Mapper;
import io.Parser;
import route.TurnRestriction;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.*;

/**
 * This represents the data structure storing all the roads, nodes, and
 * segments, as well as some information on which nodes and segments should be
 * highlighted.
 *
 * @author tony
 */
public class Graph {
    // map node IDs to Nodes.
    Map<Integer, Node> nodes = new HashMap<>();
    // map road IDs to Roads.
    Map<Integer, Road> roads;
    // just some collection of Segments.
    Collection<Segment> segments;
    Map<Integer, List<TurnRestriction>> turnRestrictions;

    Collection<Node> highlightedNodes = new HashSet<>();
    Collection<Road> highlightedRoads = new HashSet<>();
    Collection<Segment> highlightedSegments = new HashSet<>();

    public Graph(File nodes, File roads, File segments, File polygons, File trafficLights, File restrictions) {
        this.nodes = Parser.parseNodes(nodes, this);
        this.roads = Parser.parseRoads(roads, this);
        this.segments = Parser.parseSegments(segments, this);


        if(trafficLights != null)
            applyTrafficLightCounts(Parser.parseTrafficLights(trafficLights));

        if(restrictions != null)
            this.turnRestrictions = Parser.parseRestrictions(restrictions);
    }

    private void applyTrafficLightCounts(Map<String, Integer> counts) {
        HashMap<String, List<Road>> roadMap = new HashMap<>();

        for(Road r : roads.values()) {
            if(roadMap.containsKey(r.name)) {
                roadMap.get(r.name).add(r);
            } else {
                ArrayList<Road> roadList = new ArrayList<>();
                roadList.add(r);
                roadMap.put(r.name, roadList);
            }
        }

        // Evenly divide the lights over all the roads sections with the same name.
        for(Map.Entry<String, Integer> entry : counts.entrySet()) {
            List<Road> roadList = roadMap.get(entry.getKey());
            double lightCost = (double)entry.getValue() / (double)roadList.size();

            for(Road r : roadList) {
                r.trafficLightValue = lightCost;
            }
        }
    }

    public void draw(Graphics g, Dimension screen, Location origin, double scale) {
        // a compatibility wart on swing is that it has to give out Graphics
        // objects, but Graphics2D objects are nicer to work with. Luckily
        // they're a subclass, and swing always gives them out anyway, so we can
        // just do this.
        Graphics2D g2 = (Graphics2D) g;

        // draw all the segments.
        g2.setColor(Mapper.SEGMENT_COLOUR);
        for (Segment s : segments)
            s.draw(g2, screen, origin, scale);

        // draw all highlighted segments
        g2.setColor(Mapper.HIGHLIGHT_COLOUR);
        g2.setStroke(new BasicStroke(3));
        for (Segment seg : highlightedSegments) {
            seg.draw(g2, screen, origin, scale);
        }

        // draw the segments of all highlighted roads.
        g2.setColor(Mapper.HIGHLIGHT_COLOUR);
        g2.setStroke(new BasicStroke(3));
        for (Road road : highlightedRoads) {
            for (Segment seg : road.components) {
                seg.draw(g2, screen, origin, scale);
            }
        }

        // draw all the nodes.
        g2.setColor(Mapper.NODE_COLOUR);
        for (Node n : nodes.values())
            n.draw(g2, screen, origin, scale);

        // draw the highlighted node, if it exists.
        g2.setColor(Mapper.HIGHLIGHT_COLOUR);
        for (Node n : highlightedNodes)
            n.draw(g2, screen, origin, scale);
    }

    public void setHighlight(Node node) {
        this.highlightedNodes.clear();
        if (node != null)
            highlightedNodes.add(node);
    }

    public void setHighlightNodes(Collection<Node> nodes) {
        this.highlightedNodes.clear();
        if (nodes != null)
            highlightedNodes.addAll(nodes);
    }

    public void setHighlightedSegments(Collection<Segment> segments) {
        this.highlightedSegments.clear();
        if (segments != null)
            highlightedSegments.addAll(segments);
    }

    public void setHighlight(Collection<Road> roads) {
        this.highlightedRoads = roads;
    }

    public Map<Integer, Node> getNodes() {
        return nodes;
    }

    public Map<Integer, List<TurnRestriction>> getTurnRestrictions() { return turnRestrictions; }
}

// code for COMP261 assignments