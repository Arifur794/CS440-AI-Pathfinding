package gui.model;
/*
 * This class implements the basic Cell for the GUI
 */
public class Cell {
	
	public enum CellType {
		UNBLOCKED, BLOCKED, HARD, HIGHWAY_UNBLOCKED, HIGHWAY_HARD, 
		START_POINT_UNBLOCKED, END_POINT_UNBLOCKED, START_POINT_HARD, END_POINT_HARD
	}
	
	public enum HighwayDirection { // highway goes horizontally or vertically
		NONE, UP, DOWN, LEFT, RIGHT
	}
	
	public int x, y;             // upper left coordinates of the cell. 0, 0 is top left and 119, 159 is bottom right
	public CellType celltype;	  // what the type of the cell is
	public HighwayDirection dir; // direction of the highway - NONE by default
	
	
	public Cell(int x, int y, CellType celltype, HighwayDirection dir) {
		this.x = x;
		this.y = y;
		this.celltype = celltype;
		this.dir = dir;
	}
	
	public Cell(int x, int y, CellType celltype) {
		this(x, y, celltype, HighwayDirection.NONE);
	}
	
	public Cell(int x, int y) {
		this(x, y, CellType.UNBLOCKED, HighwayDirection.NONE);
	}
	
	public void convertTo(CellType ct) {		
		this.celltype = ct;
		if(!isHighway()) {
			this.dir = HighwayDirection.NONE;
		}	
	}
	
	public void makeHighway(HighwayDirection hd) {
		if(this.celltype == CellType.UNBLOCKED) {
			this.celltype = CellType.HIGHWAY_UNBLOCKED;
		} else {
			this.celltype = CellType.HIGHWAY_HARD;
		}
		this.dir = hd;
	}
	
	public boolean isHighway() {
		return this.celltype == CellType.HIGHWAY_HARD || this.celltype == CellType.HIGHWAY_UNBLOCKED;
	}
	
	public String toString() {
		return celltype + ": (" + x + "," + y + ")"; 
	}
}
