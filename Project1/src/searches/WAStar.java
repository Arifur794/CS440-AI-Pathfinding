package searches;

import gui.model.Grid;
import heuristics.Heuristic;

public class WAStar extends SearchAlgo {
	private Heuristic h;
	private float weight;
	
	public WAStar(Node start, Node goal, Grid grid, Heuristic h, float weight) {
		super(start, goal, grid);
		this.h = h;
		this.weight = weight;
	}
	
	@Override
	public float hcostFunc(Node n) {
		return h.getH(n) * weight;
	}
}
