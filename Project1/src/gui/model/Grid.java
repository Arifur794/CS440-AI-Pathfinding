package gui.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import gui.model.Cell.CellType;
import gui.model.Cell.HighwayDirection;


/*The grid itself. It goes from 0 to width and height - 1. GridController gives the rows and columns.
 * Columns is the width and grid is the height 
 * The Top left is 0, 0 and the bottom right is 119, 159
 */
public class Grid {
	private final int width;
	private final int height;
	final Cell[][] grid;
	private GridProperties GP = new GridProperties();
	private static Random rand = new Random();
	final int MAX_HIGHWAY_TRIES = 10;
	public int[][] hardCellsCenters = new int[GP.NUM_HARD_CELL_CTR][2];
	public int[] startCell = new int[2];
	public int[] endCell = new int[2];
	
	public Grid(int width, int height) {
		this.width = width;
		this.height = height;
		this.grid = new Cell[height][width];
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				this.grid[i][j] = new Cell(j, i);
			}
		}
	}
	
	public Cell[][] getGrid() {
		return this.grid;
	}
	
	public Cell getCell(int x, int y) {
		return this.grid[y - 1][x - 1];
	}
	
	public void createGrid() {
		this.createHardCells();
		while(!this.createHighways()) { //makes it so we run create highways again until we do so successfully and get true.
			this.removeHighwayCells(); //clears the highway cells from a failed attempt to make the highways
		}
		this.createBlockedCells();
		this.createStartAndEnd();
	}
	
	private void createHardCells() {
		// create hard cells
		for(int i = 0; i < GP.NUM_HARD_CELL_CTR; i++) {
			//gets the random center of the hard cell area
			int center_x = random(0, this.getWidth() - 1);
			int center_y = random(0, this.getHeight() - 1);
			
			this.hardCellsCenters[i][0] = center_x;
			this.hardCellsCenters[i][1] = center_y;
			
			// gets the starting and ending x and y for the hard cell area
			int start_x = center_x - GP.HARD_CELL_REGION/2;
			int end_x = start_x + GP.HARD_CELL_REGION;
			int start_y = center_y - GP.HARD_CELL_REGION/2;
			int end_y = start_y + GP.HARD_CELL_REGION;
			
			if(start_x < 0) {
				start_x = 0;
			}
			
			if(start_y < 0) {
				start_y = 0;
			}
			
			if(end_x > getWidth()) {
				end_x = getWidth();
			}
			
			if(end_y > getHeight()) {
				end_y = getHeight();
			}
			
			//goes through hard cell area and rolls a random number which determines if it is a hard cell or not
			for(int j = start_x; j < end_x; j++) {
				for(int k = start_y; k < end_y; k++) {
					float hardCellChance = random(0, 10)/10.0f;
					if(hardCellChance >= GP.HARD_CELL_PRB) {
						this.grid[k][j].convertTo(CellType.HARD);
					}
				}
			}
		}
	}
	
	/*
	 * Tries to create the number of highways specified in Grid properties
	 * Returns true if successful and false if not
	 */
	private boolean createHighways() {
		for(int i = 0; i < GP.NUM_HIGHWAYS; i++) {
			int tries = 0;
			while(!createHighway() && tries < this.MAX_HIGHWAY_TRIES) { 
				tries++;
			}
			if(tries >= this.MAX_HIGHWAY_TRIES) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Creates a single highway
	 * returns true if it was created and false if it was not created
	 */
	private boolean createHighway() {
		int[] boundaryCell = this.getRandomBoundary();
		int b_x = boundaryCell[0], b_y = boundaryCell[1];

		HighwayDirection hdir = HighwayDirection.NONE;
		if(b_x == 0) {
			hdir = HighwayDirection.RIGHT;
		} else if(b_x == getWidth() - 1) {
			hdir = HighwayDirection.LEFT;
		} else if(b_y == 0) {
			hdir = HighwayDirection.DOWN;
		} else if(b_y == getHeight() - 1) {
			hdir = HighwayDirection.UP;
		}
		
		Cell c = this.grid[b_y][b_x];
		if(c.isHighway()) { // if try to go somewhere already on highway return false
			return false;
		}
		
		LinkedList<Integer> addedItems = new LinkedList<Integer>(); // store cells that have been made into highways for removal if invalid
		c.makeHighway(hdir); // make the starting cell a highway
		addedItems.add(b_x);
		addedItems.add(b_y);

		int numCells = 0; // number of highway cells
		while(true) { // pave highway till you hit a boundary	
			
			for(int i = 0; i < GP.NUM_HIGHWAY_CELLS; i++) {
				int[] newCell = translate(b_x, b_y, hdir, 1);
				if(newCell == null) { // happens if highway hits the outside bounds
					if(numCells >= GP.MIN_HIGHWAY_LEN){
						return true;
					} else {
						removeHighwayCells(addedItems);
						return false;
					}
				}
				b_x = newCell[0]; b_y = newCell[1];
				
				c = this.grid[b_y][b_x];
				if(c.isHighway()) { // happens if you run into another highway
					removeHighwayCells(addedItems);
					return false;
				}
				c.makeHighway(hdir);
				addedItems.add(b_x);
				addedItems.add(b_y);
				numCells++;
				if(isBoundaryCell(b_x, b_y)) {
					if(numCells >= GP.MIN_HIGHWAY_LEN){
						return true;
					} else {
						removeHighwayCells(addedItems);
						return false;
					}
				}
			}
			
			float changeDir = random(0, 10)/10f; // rolls a random number to see if direction changes
			if(changeDir <= GP.HIGHWAY_CHG_DIR1_PRB) { // perpendicular direction 1
				hdir = (hdir == HighwayDirection.UP || hdir == HighwayDirection.DOWN) 
						? HighwayDirection.LEFT : HighwayDirection.UP;
			} else if(changeDir <= GP.HIGHWAY_CHG_DIR1_PRB + GP.HIGHWAY_CHG_DIR2_PRB) { // perpendicular direction 2
				hdir = (hdir == HighwayDirection.UP || hdir == HighwayDirection.DOWN) 
						? HighwayDirection.RIGHT : HighwayDirection.DOWN;
			}
			
		}
	}
	
	private void removeHighwayCells(LinkedList<Integer> list) {
		ListIterator<Integer> iter = list.listIterator();
		while(iter.hasNext()) {
			int x = iter.next();
			int y = iter.next();
			grid[y][x].convertTo(CellType.UNBLOCKED);
		}
		list.clear();
	}
	
	private void removeHighwayCells() {
		Cell c;
		for(int i = 0; i < getHeight(); i++) {
			for(int j = 0; j < getWidth(); j++) {
				c = this.grid[i][j];
				if(c.isHighway()) {
					c.convertTo(CellType.UNBLOCKED);
				}
			}
		}
	}
	
	/*
	 * Generates random coordinates of a boundary cell w/o corner cells
	 */
	private int[] getRandomBoundary() {
		int chooseBoundary = random(0, 4), x = 0, y = 0;
		if(chooseBoundary < 1) { // upper boundary
			x = random(1, getWidth() - 2);
		} else if(chooseBoundary < 2) { // right boundary
			x = getWidth() - 1; y = random(1, getHeight() - 2);
		} else if(chooseBoundary < 3) { // bottom boundary
			y = getHeight() - 1; x = random(1, getWidth() - 2);
		} else { // left boundary
			y = random(1, getHeight() - 2);
		}
		return new int[]{x, y};
	}
	
	/*
	 * Translates a cell in the direction by some units 
	 * @return null if cell is outside bounds or 
	 * {x, y} the coordinates of translated cell
	 */
	private int[] translate(int x, int y, HighwayDirection dir, int units) {

		int new_x = x, new_y = y;
		switch(dir) {
		case UP:
			new_y-=units;
			break;
		case DOWN:
			new_y+=units;
			break;
		case LEFT:
			new_x-=units;
			break;
		case RIGHT:
			new_x+=units;
			break;
		default:
			break;
		}
		return (!inBounds(new_x, new_y)) ? null : new int[]{new_x, new_y};
	}
	
	private boolean isBoundaryCell(int x, int y) {
		return (x == 0 || x == getWidth() - 1 || y == 0 || y == getHeight() - 1) ? true : false;
	}
	
	private boolean inBounds(int x, int y) {
		return (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) ? false : true;
	}
	
	private void createBlockedCells() {
		int numHardCells = (int)(GP.BLOCK_CELL_PER * getWidth() * getHeight());
		int count = 0, x, y;
		Cell c;
		while(count < numHardCells) {
			x = random(0, getWidth() - 1);
			y = random(0, getHeight() - 1);
			c = this.grid[y][x];
			if(!c.isHighway()) {
				c.convertTo(CellType.BLOCKED);
				count++;
			}
		}
	}
	
	private enum SectionType {
		TOP_ROW, BOT_ROW, LEFT_COL, RIGHT_COL
	}
	
	private void createStartAndEnd() {
		SectionType startRow, endRow, startCol, endCol;
		Cell start, end;
		while(true) {
			//chooses randomly where start and end will be
			if(random(0, 1) < 0.5) {
				startRow = SectionType.TOP_ROW;
			} else {
				startRow = SectionType.BOT_ROW;
			}
			
			if(random(0, 1) < 0.5) {
				startCol = SectionType.LEFT_COL;
			} else {
				startCol = SectionType.RIGHT_COL;
			}
			
			if(random(0, 1) < 0.5) {
				endRow = SectionType.BOT_ROW;
			} else {
				endRow = SectionType.TOP_ROW;
			}
			
			if(random(0, 1) < 0.5) {
				endCol = SectionType.RIGHT_COL;
			} else {
				endCol = SectionType.LEFT_COL;
			}
			int startY = this.pickRandomCellInSection(startRow);
			int startX = this.pickRandomCellInSection(startCol);
			int endY = this.pickRandomCellInSection(endRow);
			int endX = this.pickRandomCellInSection(endCol);
			
			start = this.grid[startY][startX]; 
			end = this.grid[endY][endX];
			
			if( (start.celltype == CellType.UNBLOCKED || 
					start.celltype == CellType.HARD) &&
					(end.celltype == CellType.UNBLOCKED ||
					end.celltype == CellType.HARD) &&
					this.distance(start, end) >= GP.MIN_DIST) {
				
				if(start.celltype == CellType.UNBLOCKED) {
					this.grid[startY][startX].convertTo(CellType.START_POINT_UNBLOCKED);
				} else {
					this.grid[startY][startX].convertTo(CellType.START_POINT_HARD);
				}
				
				if(end.celltype == CellType.UNBLOCKED) {
					this.grid[endY][endX].convertTo(CellType.END_POINT_UNBLOCKED);
				} else {
					this.grid[endY][endX].convertTo(CellType.END_POINT_HARD);
				}
				
				this.startCell[1] = startX; this.startCell[0] = startY;
				this.endCell[1] = endX; this.endCell[0] = endY;
				break;
				
			}		
		}
		
	}
	
	private int distance(Cell start, Cell end) {
		return (int)(Math.sqrt((start.x-end.x)*(start.x-end.x) + (start.y-end.y)*(start.y-end.y)));
	}
	
	private int pickRandomCellInSection(SectionType st) {
		switch(st) {
		case BOT_ROW:
			return random(getHeight() - 1 - GP.POINT_AREA, getHeight() - 1);
		case TOP_ROW:
		case LEFT_COL:
			return random(0, GP.POINT_AREA - 1);
		case RIGHT_COL:
			return random(getWidth() - 1 - GP.POINT_AREA, getWidth() - 1);
		default:
			return 0;		
		}
	}
	private static int random(int begin, int end) {
		return rand.nextInt((end - begin) + 1) + begin;
	}
	
	/*
	 * costTo returns the cost between two neighbor cells a, b
	 * @return cost: -1 if cells are not neighbors or blocked, 0 if same cell
	 */
	public float costTo(Cell a, Cell b) {
		return this.costTo(a.x, a.y, b.x, b.y, getTypeInt(a), getTypeInt(b));
	}
	
	private float costTo(int ax, int ay, int bx, int by, int atype, int btype) {
		if(ax == bx && ay == by) { // same cell
			return 0.0f;	
		} else if( (ax == bx && Math.abs(ay - by) == 1) 
				|| (ay == by && Math.abs(ax - bx) == 1)) { // horizontal neighbor
			return GP.HOR_VER_COSTS[atype][btype];
		} else if( Math.abs(ax - bx) == 1 && Math.abs(ay - by) == 1) { // Diagonal neighbor
			return GP.DIAG_COSTS[atype][btype];
		} 
		return -1;
	}
	
	
	public boolean changeStart(int[] newStart) {
		if ( (newStart[0] <= getHeight() -1  && newStart[0] >= getHeight() - 1 - GP.POINT_AREA) ||
				(newStart[0] <= GP.POINT_AREA - 1 && newStart[0] >= 0) &&
				(newStart[1] <= getWidth() - 1  && newStart[1] >= getWidth() - 1 - GP.POINT_AREA) ||
				(newStart[1] <= GP.POINT_AREA - 1 && newStart[1] >= 0)) {
			Cell newStartCell = this.grid[newStart[0]][newStart[1]];
			Cell oldStart = this.grid[startCell[0]][startCell[1]];
			if (newStartCell.celltype == CellType.BLOCKED || newStartCell.celltype == CellType.HIGHWAY_UNBLOCKED
					|| newStartCell.celltype == CellType.HIGHWAY_HARD) {
				return false;
			}
			if(newStartCell.celltype == CellType.UNBLOCKED) {
				this.grid[newStart[0]][newStart[1]].convertTo(CellType.START_POINT_UNBLOCKED);
			} else {
				this.grid[newStart[0]][newStart[1]].convertTo(CellType.START_POINT_HARD);
			}
			if(oldStart.celltype == CellType.START_POINT_UNBLOCKED) {
				this.grid[startCell[0]][startCell[1]].convertTo(CellType.UNBLOCKED);
			} else {
				this.grid[startCell[0]][startCell[1]].convertTo(CellType.HARD);
			}
			startCell[0]=newStart[0];
			startCell[1]=newStart[1];
			return true;
		}
		return false;
	}
	
	public boolean changeEnd(int[] newEnd) {
		if ( (newEnd[0] <= getHeight() -1  && newEnd[0] >= getHeight() - 1 - GP.POINT_AREA) ||
				(newEnd[0] <= GP.POINT_AREA - 1 && newEnd[0] >= 0) &&
				(newEnd[1] <= getWidth() - 1  && newEnd[1] >= getWidth() - 1 - GP.POINT_AREA) ||
				(newEnd[1] <= GP.POINT_AREA - 1 && newEnd[1] >= 0)) {
			Cell newEndCell = this.grid[newEnd[0]][newEnd[1]];
			Cell oldEnd = this.grid[endCell[0]][endCell[1]];
			if (newEndCell.celltype == CellType.BLOCKED || newEndCell.celltype == CellType.HIGHWAY_UNBLOCKED
					|| newEndCell.celltype == CellType.HIGHWAY_HARD) {
				return false;
			}
			if(newEndCell.celltype == CellType.UNBLOCKED) {
				this.grid[newEnd[0]][newEnd[1]].convertTo(CellType.END_POINT_UNBLOCKED);
			} else {
				this.grid[newEnd[0]][newEnd[1]].convertTo(CellType.END_POINT_HARD);
			}
			if(oldEnd.celltype == CellType.END_POINT_UNBLOCKED) {
				this.grid[endCell[0]][endCell[1]].convertTo(CellType.UNBLOCKED);
			} else {
				this.grid[endCell[0]][endCell[1]].convertTo(CellType.HARD);
			}
			endCell[0]=newEnd[0];
			endCell[1]=newEnd[1];
			return true;
		}
		return false;
	}
	
	private static int getTypeInt(Cell a) {
		switch(a.celltype) {
		case HIGHWAY_HARD:
			return 4;
		case HIGHWAY_UNBLOCKED:
			return 3;
		case BLOCKED:
			return 2;
		case END_POINT_HARD:
		case START_POINT_HARD:
		case HARD:
			return 1;
		case END_POINT_UNBLOCKED:
		case START_POINT_UNBLOCKED:
		case UNBLOCKED:
		default:
			return 0;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	private boolean neighborBounds(int x, int y) {
		return (x < 0 || x > width - 1 || y < 0 || y > height - 1) ? false : true;
 	}
	public Cell[] getNeighbors(int x, int y) {
		ArrayList<Cell> neighbors = new ArrayList<Cell>(8);
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				if(neighborBounds(x + i, y + j) && !(i == 0 && j == 0)) {
					neighbors.add(this.getCell(x + i + 1, y + j + 1));
				}
			}
		}
		
		Cell[] nArr = new Cell[neighbors.size()];
		return neighbors.toArray(nArr);
	}
}
