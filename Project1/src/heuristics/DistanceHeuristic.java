package heuristics;

import gui.model.Grid;
import searches.Node;

public class DistanceHeuristic {
	private int ex, ey, sx, sy;
	private float startToGoalDist;
	
	
	@SuppressWarnings("unused")
	private Grid g;
	public DistanceHeuristic(Grid g) {
		this.g = g;
		ex = g.endCell[1];
		ey = g.endCell[0];
		sx = g.startCell[1];
		sy = g.endCell[0];
		
		//the diagonal line distance from start to goal from Pythagorean theorem.
		startToGoalDist = (float) Math.sqrt( ((sx - ex)*(sx- ex)) + ((sy-ey)*(sy-ey)) );
	}
	
	public float getH(Node a) {
		//get the diagonal line distance from current place to goal and get 1/4th of it since in a best case scenario we always travel with an actual cost of .25.
		//Divide that by the startToGoalDistance on the diagonal.
		return ( 0.25f * (float) Math.sqrt( ((a.x - ex)*(a.x- ex)) + ((a.y-ey)*(a.y-ey)) ) ) / startToGoalDist;
		
		//same as above but get half of it instead. Not admissable because not a best case scenario for traveling along the diagonal
		//return ( 0.50f * (float) Math.sqrt( ((a.x - bx)*(a.x- bx)) + ((a.y-by)*(a.y-by)) ) ) / startToGoalDist; 
		
		//See above
		//return (0.75f * (float) Math.sqrt( ((a.x - bx)*(a.x- bx)) + ((a.y-by)*(a.y-by)) ) ) / startToGoalDist;
		
		//Not admissable because it assumes all travel times on the diagonal are equal.
		//return ((float) Math.sqrt( ((a.x - bx)*(a.x- bx)) + ((a.y-by)*(a.y-by)) ) ) / startToGoalDist;
	}
}
