package menon.cs6100.program2;

import java.util.Random;
import java.util.concurrent.Callable;

public class ForagingAgent implements Callable<Void> {
	
	private TerrainGrid foragingArea;
	private int currentmode;
	private PointOnTerrain currentLocation;
	private PointOnTerrain previousLocation;
	private Random nextPointSelector;
	private ReactiveAgentsPanel.ObstaclesTerrain uiPanel;
	private int stepCounter;
	private int directionCameFrom;
	private boolean carryingFood;
	private int agentNumber;
	private boolean leftDepot;
	private int shelfLifeCounter;
	private AgentStatistics statistics;

	private static final int FORAGING_MODE = 0;
	private static final int TRANSPORTING_MODE = 1;
	private static final int RETURNING_TO_FOOD_MODE = 2;
	
	private static final int NORTH_DIRECTION = 0;
	private static final int NORTH_EAST_DIRECTION = 1;
	private static final int EAST_DIRECTION = 2;
	private static final int SOUTH_EAST_DIRECTION = 3;
	private static final int SOUTH_DIRECTION = 4;
	private static final int SOUTH_WEST_DIRECTION = 5;
	private static final int WEST_DIRECTION = 6;
	private static final int NORTH_WEST_DIRECTION = 7;
	private static final int[] ALL_DIRECTIONS = {NORTH_DIRECTION, NORTH_EAST_DIRECTION, EAST_DIRECTION, SOUTH_EAST_DIRECTION,  
		                                         SOUTH_DIRECTION, SOUTH_WEST_DIRECTION, WEST_DIRECTION, NORTH_WEST_DIRECTION};
	private static final int MAX_NUMBER_OF_STEPS = 65;
	
	private static final int DEFAULT_SHELF_LIFE = ReactiveAgentsPanel.TERRAIN_WIDTH;
	private static final int SLEEP_TIME = 100;
	
	public ForagingAgent(TerrainGrid foragingArea, PointOnTerrain origin, long seed, int agentNumber, ReactiveAgentsPanel.ObstaclesTerrain uiPanel) {
		this.foragingArea = foragingArea;
		this.currentmode = FORAGING_MODE;
		this.currentLocation = origin;
		this.previousLocation = null;
		this.nextPointSelector = new Random(seed);
		this.uiPanel = uiPanel;
		this.stepCounter = 0;
		this.directionCameFrom = EAST_DIRECTION;
		this.carryingFood = false;
		this.agentNumber = agentNumber;
		this.leftDepot = false;
		this.shelfLifeCounter = 0;
		this.statistics = new AgentStatistics();
	}
	
	@Override
	public Void call() throws Exception {
		
		int nextDirection = 0;
		PointOnTerrain nextPoint = null;
		
		while (true) {
			if (this.currentmode == FORAGING_MODE) {
				forage(nextDirection, nextPoint);
			} else if (this.currentmode == TRANSPORTING_MODE) {
				bringBackFood(nextDirection, nextPoint);
			} else if (this.currentmode == RETURNING_TO_FOOD_MODE) {
				returnToFood(nextDirection, nextPoint);
			}
			uiPanel.updateDisplay();
			Thread.sleep(SLEEP_TIME);
		}
	}
	
	//Look for food
	private void forage(int nextDirection, PointOnTerrain nextPoint) {
		
		//Move to the next available point on the terrain
		while (true) {
			//Pick up food if present
			if (!this.currentLocation.isInsideDepot()) {
				this.leftDepot = true;
			}
			if (this.currentLocation.isHasFood()) {
				pickUpFood();
				this.currentmode = TRANSPORTING_MODE;
				this.currentLocation.markTrail(this);
				turnAround();
				break;
			}
			this.directionCameFrom = getDirectionCameFrom();
			nextDirection = getNextDirection(this.directionCameFrom);
			nextPoint = getNextPoint(nextDirection);
			if (nextPoint != null && nextPoint.moveAgentIn(this.currentLocation)) {
				//Leave trail marker scent
				nextPoint.markTrail(this);
				break;
			}
		}
		
	}
	
	//Bring food back to the depot
	private void bringBackFood(int nextDirection, PointOnTerrain nextPoint) {
		//Follow own trail back to depot
		nextPoint = getNextPointWithScent();
		if (nextPoint != null) {
			while (true) {
				if (nextPoint.moveAgentIn(this.currentLocation)) {
					//Leave trail marker scent
					this.currentLocation.markTrail(this);
					if (this.shelfLifeCounter > 0) {
						if (this.currentLocation.isInsideDepot()) {
							this.leftDepot = false;
							dropFood();
							this.currentmode = RETURNING_TO_FOOD_MODE;
							turnAround();
						}
					} else {
						this.carryingFood = false;
						this.currentmode = FORAGING_MODE;
					}
					break;
				}
			}
		} else {
			nextPoint = getNextPointWithOtherScent();
			if (nextPoint != null) {
				while (true) {
					if (nextPoint.moveAgentIn(this.currentLocation)) {
						//Leave trail marker scent
						nextPoint.markTrail(this);
						break;
					}
				}
			} else {
				this.currentmode = FORAGING_MODE;
			}
		}
		
	}
	
	//Return to the food
	private void returnToFood(int nextDirection, PointOnTerrain nextPoint) {
		while (true) {
			if (this.currentLocation.isHasFood() && !this.currentLocation.isInsideDepot()) {
				pickUpFood();
				this.currentLocation.markTrail(this);
				this.currentmode = TRANSPORTING_MODE;
				turnAround();
				break;
			}
			//Follow own trail back to food
			nextPoint = getNextPointWithScent();
			if (nextPoint != null) {
				if (!nextPoint.isInsideDepot()) {
					leftDepot = true;
				}
				if (leftDepot & this.currentLocation.isInsideDepot()) {
					this.currentmode = FORAGING_MODE;
					turnAround();
					break;
				}
				while (true) {
					if (nextPoint.moveAgentIn(this.currentLocation)) {
						//Leave trail marker scent
						nextPoint.markTrail(this);
						break;
					}
				}
				break;
			} else {
				nextPoint = getNextPointWithFood();
				if (nextPoint != null) {
					while (true) {
						if (nextPoint.moveAgentIn(this.currentLocation)) {
							//Leave trail marker scent
							nextPoint.markTrail(this);
							break;
						}
					}
					break;
				} else {
					this.currentmode = FORAGING_MODE;
					break;
				}
			}
		}

	}
	
	//Returns the next direction the agent should move to
	private int getNextDirection(int directionCameFrom) {
		
		++this.stepCounter;
		this.stepCounter = this.stepCounter % MAX_NUMBER_OF_STEPS;
		
		if (stepCounter != 0) {
			return getOppositeOf(directionCameFrom);
		}
		
		//This will get an integer between 1 and 7
		int nextDirection = this.nextPointSelector.nextInt(ALL_DIRECTIONS.length - 2) + 1;
		return (directionCameFrom + nextDirection) % ALL_DIRECTIONS.length;
		
	}
	
	//Return opposite of came from, so that it keeps going in the same direction
	private int getOppositeOf(int directionCameFrom) {
		
		switch (directionCameFrom) {
		
		case NORTH_DIRECTION:
			return SOUTH_DIRECTION;
		
		case NORTH_EAST_DIRECTION:
			return SOUTH_WEST_DIRECTION;
		
		case EAST_DIRECTION:
			return WEST_DIRECTION;

		case SOUTH_EAST_DIRECTION:
			return NORTH_WEST_DIRECTION;
			
		case SOUTH_DIRECTION:
			return NORTH_DIRECTION;
			
		case SOUTH_WEST_DIRECTION:
			return NORTH_EAST_DIRECTION;
			
		case WEST_DIRECTION:
			return EAST_DIRECTION;
			
		default:
			return SOUTH_EAST_DIRECTION;

		}
	}
	
	//Set the previous location for this agent
	void setPreviousLocation(PointOnTerrain previousLocation) {
		this.previousLocation = previousLocation;
	}
	
	//Get the relative direction the agent came from
	private int getDirectionCameFrom() {
		
		//The default is East
		if (this.previousLocation == null) {
			return EAST_DIRECTION;
		}
		
		if (this.previousLocation.getxCoordinate() == this.currentLocation.getxCoordinate()) {
			if (this.previousLocation.getyCoordinate() > this.currentLocation.getyCoordinate()) {
				return SOUTH_DIRECTION;
			} else {
				return NORTH_DIRECTION;
			}
		} else if (this.previousLocation.getyCoordinate() == this.currentLocation.getyCoordinate()) {
			if (this.previousLocation.getxCoordinate() > this.currentLocation.getxCoordinate()) {
				return EAST_DIRECTION;
			} else {
				return WEST_DIRECTION;
			}
		} else if (this.previousLocation.getxCoordinate() > this.currentLocation.getxCoordinate()) {
			if (this.previousLocation.getyCoordinate() > this.currentLocation.getyCoordinate()) {
				return SOUTH_EAST_DIRECTION;
			} else {
				return NORTH_EAST_DIRECTION;
			}
		} else {
			if (this.previousLocation.getyCoordinate() > this.currentLocation.getyCoordinate()) {
				return SOUTH_WEST_DIRECTION;
			} else {
				return NORTH_WEST_DIRECTION;
			}
		}
		
	}
	
	//Get the point on the terrain corresponding to the direction passed in
	private PointOnTerrain getNextPoint(int direction) {
		
		this.statistics.setNeighboringCellsExamined(this.statistics.getNeighboringCellsExamined() + 1);

		if (direction == NORTH_DIRECTION) {
			return this.foragingArea.getNeighborToNorth(this.currentLocation);
		} else if (direction == NORTH_EAST_DIRECTION) {
			return this.foragingArea.getNeighborToNorthEast(this.currentLocation);
		} else if (direction == EAST_DIRECTION) {
			return this.foragingArea.getNeighborToEast(this.currentLocation);
		} else if (direction == SOUTH_EAST_DIRECTION) {
			return this.foragingArea.getNeighborToSouthEast(this.currentLocation);
		} else if (direction == SOUTH_DIRECTION) {
			return this.foragingArea.getNeighborToSouth(this.currentLocation);
		} else if (direction == SOUTH_WEST_DIRECTION) {
			return this.foragingArea.getNeighborToSouthWest(this.currentLocation);
		} else if (direction == WEST_DIRECTION) {
			return this.foragingArea.getNeighborToWest(this.currentLocation);
		} else {
			return this.foragingArea.getNeighborToNorthWest(this.currentLocation);
		}	
		
	}
	
	//Pick up food
	private void pickUpFood() {
		
		this.currentLocation.setHasFood(false);
		this.carryingFood = true;
		this.uiPanel.removeFoodFrom(this.currentLocation.getxCoordinate(), this.currentLocation.getyCoordinate());
		this.shelfLifeCounter = ForagingAgent.DEFAULT_SHELF_LIFE;
	}
	
	//Drop the food inside the depot
	private void dropFood() {
		
		this.currentLocation.setHasFood(true);
		this.carryingFood = false;
		this.uiPanel.dropFoodAt(this.currentLocation.getxCoordinate(), this.currentLocation.getyCoordinate());
		this.uiPanel.incrementCollectedFoodAmount();
	}
	
	//Turn around
	private void turnAround() {
		
		while (true) {
			
			if (this.previousLocation.moveAgentIn(this.currentLocation)) {
				break;
			}
		}
	}
	
	//Get the next point with the agents own scent
	private PointOnTerrain getNextPointWithScent() {
		
		this.statistics.setNeighboringCellsExamined(this.statistics.getNeighboringCellsExamined() + 8);

		return this.foragingArea.getNextPointWithScent(this);
	}
	
	//Get the neighboring point with food
	private PointOnTerrain getNextPointWithFood() {
		return this.foragingArea.getNextPointWithFood(this);
	}
	//Get the next point with another agent's scent
	private PointOnTerrain getNextPointWithOtherScent() {
		return this.foragingArea.getNextPointWithOtherScent(this);
	}
	
	PointOnTerrain getCurrentLocation() {
		return this.currentLocation;
	}
	
	void setCurrentLocation(PointOnTerrain pointOnTerrain) {
		this.currentLocation = pointOnTerrain;
	}
	
	PointOnTerrain getPreviousLocation() {
		return this.previousLocation;
	}

	boolean isCarryingFood() {
		return carryingFood;
	}
	
	public void decrementShelfLife() {
		--this.shelfLifeCounter;
	}

	AgentStatistics getStatistics() {
		return statistics;
	}

	void setStatistics(AgentStatistics statistics) {
		this.statistics = statistics;
	}

	int getAgentNumber() {
		return agentNumber;
	}
	
}