package menon.cs6100.program2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TerrainGrid {

	private List<PointOnTerrain> grid;
	private int terrainWidth;
	private int terrainHeight;
	
	//Constructor takes dimensions of the terrain
	public TerrainGrid(int width, int height) {
		
		this.terrainWidth = width;
		this.terrainHeight = height;
		
		grid = Collections.synchronizedList(new ArrayList<PointOnTerrain>(width * height));
		PointOnTerrain pointOnTerrain = null;
		for (int gridOffset = 0; gridOffset < this.terrainWidth * this.terrainHeight; ++gridOffset) {
			pointOnTerrain = new PointOnTerrain(getXCoordinate(gridOffset), getYCoordinate(gridOffset));
			this.grid.add(pointOnTerrain);
		}
	}
	
	//Get the point corresponding to the neighbor to the north
	public PointOnTerrain getNeighborToNorth(PointOnTerrain pointOnTerrain) {
		
		//Check if valid values have been passed in
		if (!isValidCoordinate(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate())) {
			return null;
		}

		//Check if current point is on the border of the terrain
		if (pointOnTerrain.getyCoordinate() == 0) {
			return null;
		}
		
		int currentPointOffset = getTerrainOffset(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate());
		
		PointOnTerrain neighbor = grid.get(currentPointOffset - this.terrainWidth);
		
		//Check if point is inside a barrier
		if (neighbor.isAccessible()) {
			return neighbor;
		} else {
			return null;
		}
		
	}
	
	//Get the point corresponding to the neighbor to the south
	public PointOnTerrain getNeighborToSouth(PointOnTerrain pointOnTerrain) {
		
		//Check if valid values have been passed in
		if (!isValidCoordinate(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate())) {
			return null;
		}
		
		//Check if current point is on the border of the terrain
		if (pointOnTerrain.getyCoordinate() == this.terrainHeight - 1) {
			return null;
		}
		
		int currentPointOffset = getTerrainOffset(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate());
		
		PointOnTerrain neighbor = grid.get(currentPointOffset + this.terrainWidth);
		
		//Check if point is inside a barrier
		if (neighbor.isAccessible()) {
			return neighbor;
		} else {
			return null;
		}
		
	}
	
	//Get the point corresponding to the neighbor to the East
	public PointOnTerrain getNeighborToEast(PointOnTerrain pointOnTerrain) {
		
		//Check if valid values have been passed in
		if (!isValidCoordinate(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate())) {
			return null;
		}
		
		//Check if current point is on the border of the terrain
		if (pointOnTerrain.getxCoordinate() == this.terrainWidth - 1) {
			return null;
		}
		
		int currentPointOffset = getTerrainOffset(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate());
		
		PointOnTerrain neighbor = grid.get(currentPointOffset + 1);
		
		//Check if point is inside a barrier
		if (neighbor.isAccessible()) {
			return neighbor;
		} else {
			return null;
		}
		
	}
	
	//Get the point corresponding to the neighbor to the West
	public PointOnTerrain getNeighborToWest(PointOnTerrain pointOnTerrain) {
		
		//Check if valid values have been passed in
		if (!isValidCoordinate(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate())) {
			return null;
		}
		
		//Check if current point is on the border of the terrain
		if (pointOnTerrain.getxCoordinate() == 0) {
			return null;
		}
		
		int currentPointOffset = getTerrainOffset(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate());
		
		PointOnTerrain neighbor = grid.get(currentPointOffset - 1);
		
		//Check if point is inside a barrier
		if (neighbor.isAccessible()) {
			return neighbor;
		} else {
			return null;
		}
		
	}
	
	//Get the point corresponding to the neighbor to the north-west
	public PointOnTerrain getNeighborToNorthWest(PointOnTerrain pointOnTerrain) {
		
		//Check if valid values have been passed in
		if (!isValidCoordinate(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate())) {
			return null;
		}
		
		//Check if current point is on the left or top border of the terrain
		if (pointOnTerrain.getxCoordinate() == 0 || pointOnTerrain.getyCoordinate() == 0) {
			return null;
		}
		
		int currentPointOffset = getTerrainOffset(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate());
		
		PointOnTerrain neighbor = grid.get(currentPointOffset - this.terrainWidth - 1);
		
		//Check if point is inside a barrier
		if (neighbor.isAccessible()) {
			return neighbor;
		} else {
			return null;
			
		}
	}
	
	//Get the point corresponding to the neighbor to the north-east
	public PointOnTerrain getNeighborToNorthEast(PointOnTerrain pointOnTerrain) {
		
		//Check if valid values have been passed in
		if (!isValidCoordinate(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate())) {
			return null;
		}
		
		//Check if current point is on the top or right border of the terrain
		if (pointOnTerrain.getyCoordinate() == 0 || pointOnTerrain.getxCoordinate() == this.terrainWidth - 1) {
			return null;
		}
		
		int currentPointOffset = getTerrainOffset(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate());
		
		PointOnTerrain neighbor = grid.get(currentPointOffset - this.terrainWidth + 1);
		
		//Check if point is inside a barrier
		if (neighbor.isAccessible()) {
			return neighbor;
		} else {
			return null;
			
		}
	}
	
	//Get the point corresponding to the neighbor to the south-west
	public PointOnTerrain getNeighborToSouthWest(PointOnTerrain pointOnTerrain) {
		
		//Check if valid values have been passed in
		if (!isValidCoordinate(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate())) {
			return null;
		}
		
		//Check if current point is on the left or bottom border of the terrain
		if (pointOnTerrain.getxCoordinate() == 0 || pointOnTerrain.getyCoordinate() == this.terrainHeight - 1) {
			return null;
		}
		
		int currentPointOffset = getTerrainOffset(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate());
		
		PointOnTerrain neighbor = grid.get(currentPointOffset + this.terrainWidth - 1);
		
		//Check if point is inside a barrier
		if (neighbor.isAccessible()) {
			return neighbor;
		} else {
			return null;
			
		}
	}
	
	//Get the point corresponding to the neighbor to the south-east
	public PointOnTerrain getNeighborToSouthEast(PointOnTerrain pointOnTerrain) {
		
		//Check if valid values have been passed in
		if (!isValidCoordinate(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate())) {
			return null;
		}
		
		//Check if current point is on the right or bottom border of the terrain
		if (pointOnTerrain.getxCoordinate() == this.terrainWidth - 1 || pointOnTerrain.getyCoordinate() == this.terrainHeight - 1) {
			return null;
		}
		
		int currentPointOffset = getTerrainOffset(pointOnTerrain.getxCoordinate(), pointOnTerrain.getyCoordinate());
		
		PointOnTerrain neighbor = grid.get(currentPointOffset + this.terrainWidth + 1);
		
		//Check if point is inside a barrier
		if (neighbor.isAccessible()) {
			return neighbor;
		} else {
			return null;
			
		}
	}
	
	//Get next point on the terrain with strongest scent from the same agent
	public PointOnTerrain getNextPointWithScent(ForagingAgent agent) {
		
		long maximumScentFound = 0;
		PointOnTerrain nextPoint = null, candidatePoint = null;
		
		candidatePoint = getNeighborToNorth(agent.getCurrentLocation());
		if (candidatePoint != null && (candidatePoint.getxCoordinate() != agent.getPreviousLocation().getxCoordinate() ||
			candidatePoint.getyCoordinate() != agent.getPreviousLocation().getyCoordinate())) {
			if (candidatePoint.getAgentScent(agent) > maximumScentFound) {
				maximumScentFound = candidatePoint.getAgentScent(agent);
				nextPoint = candidatePoint;
			}
		}
				
		candidatePoint = getNeighborToNorthEast(agent.getCurrentLocation());
		if (candidatePoint != null && (candidatePoint.getxCoordinate() != agent.getPreviousLocation().getxCoordinate() ||
			candidatePoint.getyCoordinate() != agent.getPreviousLocation().getyCoordinate())) {
			if (candidatePoint.getAgentScent(agent) > maximumScentFound) {
				maximumScentFound = candidatePoint.getAgentScent(agent);
				nextPoint = candidatePoint;
			}
		}
		
		candidatePoint = getNeighborToEast(agent.getCurrentLocation());
		if (candidatePoint != null && (candidatePoint.getxCoordinate() != agent.getPreviousLocation().getxCoordinate() ||
			candidatePoint.getyCoordinate() != agent.getPreviousLocation().getyCoordinate())) {
			if (candidatePoint.getAgentScent(agent) > maximumScentFound) {
				maximumScentFound = candidatePoint.getAgentScent(agent);
				nextPoint = candidatePoint;
			}
		}
		
		candidatePoint = getNeighborToSouthEast(agent.getCurrentLocation());
		if (candidatePoint != null && (candidatePoint.getxCoordinate() != agent.getPreviousLocation().getxCoordinate() ||
			candidatePoint.getyCoordinate() != agent.getPreviousLocation().getyCoordinate())) {
			if (candidatePoint.getAgentScent(agent) > maximumScentFound) {
				maximumScentFound = candidatePoint.getAgentScent(agent);
				nextPoint = candidatePoint;
			}
		}
		
		candidatePoint = getNeighborToSouth(agent.getCurrentLocation());
		if (candidatePoint != null && (candidatePoint.getxCoordinate() != agent.getPreviousLocation().getxCoordinate() ||
			candidatePoint.getyCoordinate() != agent.getPreviousLocation().getyCoordinate())) {
			if (candidatePoint.getAgentScent(agent) > maximumScentFound) {
				maximumScentFound = candidatePoint.getAgentScent(agent);
				nextPoint = candidatePoint;
			}
		}
		
		candidatePoint = getNeighborToSouthWest(agent.getCurrentLocation());
		if (candidatePoint != null && (candidatePoint.getxCoordinate() != agent.getPreviousLocation().getxCoordinate() ||
			candidatePoint.getyCoordinate() != agent.getPreviousLocation().getyCoordinate())) {
			if (candidatePoint.getAgentScent(agent) > maximumScentFound) {
				maximumScentFound = candidatePoint.getAgentScent(agent);
				nextPoint = candidatePoint;
			}
		}
		
		candidatePoint = getNeighborToWest(agent.getCurrentLocation());
		if (candidatePoint != null && (candidatePoint.getxCoordinate() != agent.getPreviousLocation().getxCoordinate() ||
			candidatePoint.getyCoordinate() != agent.getPreviousLocation().getyCoordinate())) {
			if (candidatePoint.getAgentScent(agent) > maximumScentFound) {
				maximumScentFound = candidatePoint.getAgentScent(agent);
				nextPoint = candidatePoint;
			}
		}
		
		candidatePoint = getNeighborToNorthWest(agent.getCurrentLocation());
		if (candidatePoint != null && (candidatePoint.getxCoordinate() != agent.getPreviousLocation().getxCoordinate() ||
			candidatePoint.getyCoordinate() != agent.getPreviousLocation().getyCoordinate())) {
			if (candidatePoint.getAgentScent(agent) > maximumScentFound) {
				maximumScentFound = candidatePoint.getAgentScent(agent);
				nextPoint = candidatePoint;
			}
		}
		
		return nextPoint;
	}
	
	private AgentStatistics getIncrementedStatistics(int howMuchToIncrement, ForagingAgent agent) {
		
		AgentStatistics statistics = agent.getStatistics();
		int currentCount = statistics.getNeighboringCellsExamined() + howMuchToIncrement;
		statistics.setNeighboringCellsExamined(currentCount);
		return statistics;
		
	}
	
	//Get next point on the terrain with food
	public PointOnTerrain getNextPointWithFood(ForagingAgent agent) {
		
		PointOnTerrain candidatePoint = null;
		int numberOfNeighborsExamined = 0;
		
		candidatePoint = getNeighborToNorth(agent.getCurrentLocation());
		++numberOfNeighborsExamined;
		if (candidatePoint.isHasFood() && !candidatePoint.isInsideDepot()) {
			agent.setStatistics(getIncrementedStatistics(numberOfNeighborsExamined, agent));
			return candidatePoint;
		}
				
		candidatePoint = getNeighborToNorthEast(agent.getCurrentLocation());
		++numberOfNeighborsExamined;
		if (candidatePoint.isHasFood() && !candidatePoint.isInsideDepot()) {
			agent.setStatistics(getIncrementedStatistics(numberOfNeighborsExamined, agent));
			return candidatePoint;
		}
		
		candidatePoint = getNeighborToEast(agent.getCurrentLocation());
		++numberOfNeighborsExamined;
		if (candidatePoint.isHasFood() && !candidatePoint.isInsideDepot()) {
			agent.setStatistics(getIncrementedStatistics(numberOfNeighborsExamined, agent));
			return candidatePoint;
		}
		
		candidatePoint = getNeighborToSouthEast(agent.getCurrentLocation());
		++numberOfNeighborsExamined;
		if (candidatePoint.isHasFood() && !candidatePoint.isInsideDepot()) {
			agent.setStatistics(getIncrementedStatistics(numberOfNeighborsExamined, agent));
			return candidatePoint;
		}
		
		candidatePoint = getNeighborToSouth(agent.getCurrentLocation());
		++numberOfNeighborsExamined;
		if (candidatePoint.isHasFood() && !candidatePoint.isInsideDepot()) {
			agent.setStatistics(getIncrementedStatistics(numberOfNeighborsExamined, agent));
			return candidatePoint;
		}
		
		candidatePoint = getNeighborToSouthWest(agent.getCurrentLocation());
		++numberOfNeighborsExamined;
		if (candidatePoint.isHasFood() && !candidatePoint.isInsideDepot()) {
			agent.setStatistics(getIncrementedStatistics(numberOfNeighborsExamined, agent));
			return candidatePoint;
		}
		
		candidatePoint = getNeighborToWest(agent.getCurrentLocation());
		++numberOfNeighborsExamined;
		if (candidatePoint.isHasFood() && !candidatePoint.isInsideDepot()) {
			agent.setStatistics(getIncrementedStatistics(numberOfNeighborsExamined, agent));
			return candidatePoint;
		}
		
		candidatePoint = getNeighborToNorthWest(agent.getCurrentLocation());
		++numberOfNeighborsExamined;
		if (candidatePoint.isHasFood() && !candidatePoint.isInsideDepot()) {
			agent.setStatistics(getIncrementedStatistics(numberOfNeighborsExamined, agent));
			return candidatePoint;
		}
		
		return candidatePoint;
	}
	
	//Get next point on the terrain with strongest scent from another agent
	public PointOnTerrain getNextPointWithOtherScent(ForagingAgent agent) {
		return null;
	}
	
	//Set up rectangular barrier on terrain
	public void setRectangularBarrier(int topLeftXCoordinate, int topLeftYCoordinate, int rectangleWidth, int rectangleHeight) {
		
		if (!isValidCoordinate(topLeftXCoordinate, topLeftYCoordinate)) {
			return;
		}
		
		//Compute maximum X and Y allowed coordinates as the rectangle may not fit in the terrain
		int maximumXCoordinate = topLeftXCoordinate + rectangleWidth - 1 < this.terrainWidth ? topLeftXCoordinate + rectangleWidth - 1 : this.terrainWidth -1 ;
		int maximumYCoordinate = topLeftYCoordinate + rectangleHeight - 1 < this.terrainHeight ? topLeftYCoordinate + rectangleHeight - 1 : this.terrainHeight -1 ;
				
		//Mark area covered by the rectangle as inaccessible, i.e. has a barrier
		for (int rectangleXCoordinate = topLeftXCoordinate; rectangleXCoordinate <= maximumXCoordinate; ++rectangleXCoordinate) {
			for (int rectangleYCoordinate = topLeftYCoordinate; rectangleYCoordinate <= maximumYCoordinate; ++rectangleYCoordinate) {
				grid.get(getTerrainOffset(rectangleXCoordinate, rectangleYCoordinate)).setAccessible(false);
			}
		}
	}
	
	//Set up depot on the terrain if it will fit
	public boolean ableToSetDepot(int topLeftXCoordinate, int topLeftYCoordinate, int depotWidth, int depotHeight, int depotOpening, int depotOpeningFromTop, int depotWallWidth) {
		
		//Check if valid coordinates are passed in
		if (!isValidCoordinate(topLeftXCoordinate, topLeftYCoordinate)) {
			return false;
		}
		
		//Check if the depot can be placed at the selected point
		if (topLeftXCoordinate + depotWidth - 1 >= this.terrainWidth || topLeftYCoordinate + depotHeight - 1 >= this.terrainHeight) {
			return false;
		}
		
		//Construct the depot and mark the points it sits on as inaccessible. First construct the top.
		setRectangularBarrier(topLeftXCoordinate, topLeftYCoordinate, depotWidth, depotWallWidth);
		//Construct the bottom
		setRectangularBarrier(topLeftXCoordinate, topLeftYCoordinate + depotHeight - depotWallWidth, depotWidth, depotWallWidth);
		//Construct the right side
		setRectangularBarrier(topLeftXCoordinate + depotWidth - depotWallWidth, topLeftYCoordinate + depotWallWidth, depotWallWidth, depotHeight - depotWallWidth - depotWallWidth);
		//Construct the left side above the opening
		setRectangularBarrier(topLeftXCoordinate, topLeftYCoordinate + depotWallWidth, depotWallWidth, depotOpeningFromTop - depotWallWidth);
		//Construct the left side below the opening
		setRectangularBarrier(topLeftXCoordinate, topLeftYCoordinate + depotOpeningFromTop + depotOpening, depotWallWidth, depotHeight - depotOpeningFromTop - depotOpening - depotWallWidth);
		//Mark area inside depot
		markAreaInsideDepot(topLeftXCoordinate, topLeftYCoordinate, depotWidth, depotHeight, depotWallWidth);
		return true;
	}
	
	//Mark area inside depot so that agents can tell when they are inside the depot
	private void markAreaInsideDepot(int topLeftXCoordinate, int topLeftYCoordinate, int depotWidth, int depotHeight, int depotWallWidth) {
		
		int topLeftXCoordinateInside = topLeftXCoordinate + depotWallWidth;
		int topLeftYCoordinateInside = topLeftYCoordinate + depotWallWidth;
		int depotInsideWidth = depotWidth - depotWallWidth - depotWallWidth;
		int depotInsideHeight = depotHeight - depotWallWidth - depotWallWidth;
		for (int xCoordinate = topLeftXCoordinateInside; xCoordinate < topLeftXCoordinateInside + depotInsideWidth; ++ xCoordinate) {
			for (int yCoordinate = topLeftYCoordinateInside; yCoordinate < topLeftYCoordinateInside + depotInsideHeight; ++ yCoordinate) {
				grid.get(getTerrainOffset(xCoordinate, yCoordinate)).setInsideDepot(true);
			}
		}
	}
	
	//Return the point corresponding to the offset
	public PointOnTerrain getPoint(int xCoordinate, int yCoordinate) {
		
		//Check if valid coordinates are passed in
		if (!isValidCoordinate(xCoordinate, yCoordinate)) {
			return null;
		}
		
		return this.grid.get(this.getTerrainOffset(xCoordinate, yCoordinate));
		
	}
	
	public int getWidth() {
		return this.terrainWidth;
	}
	
	public int getHeight() {
		return this.terrainHeight;
	}
	
	//Return x coordinate given an offset
	private int getXCoordinate(int gridOffset) {
		return gridOffset % this.terrainWidth;
	}
	
	//Return y coordinate given an offset
	private int getYCoordinate(int gridOffset) {
		return gridOffset / this.terrainWidth;
	}
	
	//Check if the coordinates passed in are valid for the terrain
	private boolean isValidCoordinate(int xCoordinate, int yCoordinate) {
		
		if (xCoordinate > this.terrainWidth - 1 || yCoordinate > this.terrainHeight - 1 || xCoordinate < 0 || yCoordinate < 0) {
			return false;
		} else {
			return true;
		}
				
	}
	
	
	//Return terrain offset in array list given x and y coordinates 
	private int getTerrainOffset(int xCoordinate, int yCoordinate) {
		
		return yCoordinate * this.terrainWidth + xCoordinate;
		
	}
	
}
