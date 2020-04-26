package io;

import common.Graph;
import common.Node;
import common.Road;
import common.Segment;
import route.TurnRestriction;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * This is the Stream version of the io.Parser.java file.
 * The two files are essentially the same, so feel free to use either of them.
 * This file is an alternative of io.Parser.java.
 * It implements the methods using the Stream functionality.
 * It is faster, and help you learn a bit of recent and fancy Java functionalities.
 */
public class Parser {

	public static int maxSpeed = 0, maxClass = 0;

    private Parser(){
        //empty private constructor
    }

	private static final Function<String, String[]> splitByTab =
			line -> line.split("[\t]+");

	public static Map<Integer, Node> parseNodes(
			File nodes,
			Graph graph)
	{
		try {
			final Function<Node, Integer> key = node -> node.nodeID;
			final Function<Node, Node> value = node -> node;
			final Function<String[], Node> nodeFrom =
                    tokens -> {
                        int nodeID = asInt(tokens[0]);
                        double lat = asDouble(tokens[1]),
                               lon = asDouble(tokens[2]);
                        return new Node(nodeID, lat, lon);
                    };

			return Files.lines(nodes.toPath())
						.parallel()
						.map(splitByTab)
						.map(nodeFrom)
                        .collect(Collectors.toMap(key, value));
		} catch (IOException | NumberFormatException e) {
			throw new RuntimeException("file reading failed.");
		}
	}

	public static Map<Integer, Road> parseRoads(
			File roads,
			Graph graph)
	{
		try{
			maxSpeed = 0;
			final Function<Road, Integer> key = road -> road.roadID;
			final Function<Road, Road> value = road -> road;
			final Function<String[], Road> toRoad =
                    tokens -> {
                        int roadID = asInt(tokens[0]),
                            type = asInt(tokens[1]);
                        String label = tokens[2],
                               city = tokens[3];
                        int oneway = asInt(tokens[4]),
                            speed = asInt(tokens[5]),
                            road_class = asInt(tokens[6]),
                            not_for_car = asInt(tokens[7]),
                            not_for_pedestrian = asInt(tokens[8]),
                            not_for_bicycle = asInt(tokens[9]);

                        if(speed > maxSpeed) // Find max speed of all roads in this file
                        	maxSpeed = speed;

						if(road_class > maxClass) // Find max class of all roads in this file
							maxClass = road_class;

                        return new Road(
                                roadID, type, label,
                                city, oneway, speed,
                                road_class, not_for_car,
                                not_for_pedestrian, not_for_bicycle
                        );
                    };

			return Files.lines(roads.toPath())
						.skip(1)	//skip header
						.parallel()
						.map(splitByTab)
						.map(toRoad)
                        .collect(Collectors.toMap(key,value));
		}catch (IOException | NumberFormatException e){
			throw new RuntimeException("file reading failed.");
		}
	}

	public static Map<String, Integer> parseTrafficLights(File file) {
		try {
			return Files.lines(file.toPath())
					.parallel()
					.map(splitByTab)
					.collect(Collectors.toMap(
							(String[] arr) -> arr[0],
							(String[] arr) -> Integer.parseInt(arr[1])
					));
		} catch (IOException ex) {
			throw new RuntimeException("Failed to parse traffic light data: " + ex.getMessage());
		}
	}

	public static Map<Integer, List<TurnRestriction>> parseRestrictions(File file) {
		try {
			return Files.lines(file.toPath())
					.skip(1)
					.parallel()
					.map(splitByTab)
					.collect(Collectors.toMap(
							(String[] arr) -> Integer.parseInt(arr[2]),
							(String[] arr) -> {
								ArrayList<TurnRestriction> restrictions = new ArrayList<>();
								restrictions.add(new TurnRestriction(arr[2], arr[0], arr[1], arr[4], arr[3]));
								return restrictions;
							}, (a, b) -> {
								a.addAll(b);
								return a;
							}));
		} catch (IOException ex) {
			throw new RuntimeException("Failed to parse restriction data: " + ex.getMessage());
		}
	}

	public static Collection<Segment> parseSegments(
			File segments,
			Graph graph)
	{
		try{
		    final Function<String[], Segment> toSegment =
                    tokens -> {
                        int roadID = asInt(tokens[0]);
                        double length = asDouble(tokens[1]);
                        int node1ID = asInt(tokens[2]),
                            node2ID = asInt(tokens[3]);
                        double[] coords = asDouble(tokens,4);

                        return new Segment(
                                graph, roadID,
                                length, node1ID,
                                node2ID, coords
                        );
                    };

			return Files.lines(segments.toPath())
                        .skip(1)	//skip header
						.parallel()
                        .map(splitByTab)
                        .map(toSegment)
                        .collect(Collectors.toSet());
		}catch (IOException | NumberFormatException e){
			throw new RuntimeException("file reading failed.");
		}
	}

	private static int asInt(String str)
	{
		return Integer.parseInt(str);
	}

	private static double asDouble(String str)
	{
		return Double.parseDouble(str);
	}

    private static double[] asDouble(
            String[] tokens,
            int skip)
    {
        if(skip < 0) skip = 0;
        return Stream.of(tokens)
                     .skip(skip)	//skip to read the coordinates
                     .mapToDouble(Parser::asDouble)
                     .toArray();
    }
}
// code for COMP261 assignments