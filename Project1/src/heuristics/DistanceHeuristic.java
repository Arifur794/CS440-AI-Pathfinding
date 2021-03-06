package heuristics;

import gui.model.Grid;
import searches.Node;

public class DistanceHeuristic extends Heuristic {
	private int ex, ey;
	
	
	@SuppressWarnings("unused")
	private Grid g;
	public DistanceHeuristic(Grid g) {
		super(g);
		ex = g.endCell[1];
		ey = g.endCell[0];
	}
	
	public float getH(Node a) {
		//The Euclidean distance from currNode to goal multiplied by 0.25 because of best case scenario
		return ( 0.25f * (float) Math.sqrt( ((a.x - ex)*(a.x- ex)) + ((a.y-ey)*(a.y-ey)) ) );
	}
}
