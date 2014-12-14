package menon.cs6100.program2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//Class for storing scores associated with point on terrain
public class PointOnTerrain {
	
	private double gScore;
	private double fScore;
	private boolean isAccessible;
	private boolean insideDepot;
	private int xCoordinate;
	private int yCoordinate;
	private ForagingAgent agentOnPoint;
	private boolean hasFood;
	private Map<ForagingAgent, Long> trailMarker;
	
	private static final int maximumScent = 50;
	
	//Constructor
	public PointOnTerrain(int xCoordinate, int yCoordinate) {
		this.gScore = 0.0;
		this.fScore = 0.0;
		this.isAccessible = true;
		this.insideDepot = false;
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.agentOnPoint = null;
		this.hasFood = false;
		this.trailMarker = new HashMap<ForagingAgent, Long>();
	}
	
	//Get distance between point locations
	public double getDistanceToPoint(PointOnTerrain destinationPoint) {
		
		return Math.sqrt(Math.pow(this.getxCoordinate() - destinationPoint.getxCoordinate(), 2) + Math.pow(this.getyCoordinate() - destinationPoint.getyCoordinate(), 2));
		
	}
	
	//Move the agent from this point to another point on the terrain
	public synchronized boolean moveAgentIn(PointOnTerrain fromPoint)  {
		
		if (fromPoint.getAgentOnPoint() != null && this.isAccessible && this.agentOnPoint == null) {
			this.agentOnPoint = fromPoint.getAgentOnPoint();
			this.agentOnPoint.setPreviousLocation(fromPoint);
			this.agentOnPoint.setCurrentLocation(this);
			this.agentOnPoint.decrementShelfLife();
			fromPoint.setAgentOnPoint(null);
			
			return true;
		} else {
			return false;
		}
	}
	
	//Called by an agent to mark the trail 
	public synchronized void markTrail(ForagingAgent agent) {
		
		//If trailmarker is too strong, the agent may be stuck in a terrain loop. In that case, erase the marker.
		if (this.trailMarker.containsKey(agent)) {
			if (this.trailMarker.get(agent).longValue() > maximumScent) {
				this.trailMarker.remove(agent);
			} else {
				//Make scent stronger 
				this.trailMarker.put(agent, Long.valueOf(this.trailMarker.get(agent).longValue() + 1));
				incrementMarkerWritten(agent);
			}
		} else {
			//Leave a scent
			this.trailMarker.put(agent, Long.valueOf(1));
			incrementMarkerWritten(agent);
		}
	}
	
	private void incrementMarkerWritten(ForagingAgent agent) {
		
		AgentStatistics statistics = agent.getStatistics();
		statistics.setNumbersOfMarkersWritten(statistics.getNumbersOfMarkersWritten() + 1);
		statistics.setNumbersOfMarkersRead(statistics.getNumbersOfMarkersRead() + 1);
		agent.setStatistics(statistics);
		
	}
	
	//Return the magnitude of the scent left by the agent
	public long getAgentScent(ForagingAgent agent) {
		
		if (this.trailMarker.containsKey(agent)) {
			return this.trailMarker.get(agent).longValue();
		} else {
			return 0;
		}
	}
	
	//Return a read-only map of trailmarkers
	Map<ForagingAgent, Long> getTrailMarker() {
		return Collections.unmodifiableMap(this.trailMarker);
	}
	
	//Getters and setters
	public double getgScore() {
		return gScore;
	}
	public void setgScore(double gScore) {
		this.gScore = gScore;
	}
	public double getfScore() {
		return fScore;
	}
	public void setfScore(double fScore) {
		this.fScore = fScore;
	}
	public boolean isAccessible() {
		return isAccessible;
	}
	public void setAccessible(boolean isAccessible) {
		this.isAccessible = isAccessible;
	}

	boolean isInsideDepot() {
		return insideDepot;
	}

	void setInsideDepot(boolean insideDepot) {
		this.insideDepot = insideDepot;
	}

	public int getxCoordinate() {
		return xCoordinate;
	}

	public int getyCoordinate() {
		return yCoordinate;
	}

	ForagingAgent getAgentOnPoint() {
		return agentOnPoint;
	}

	void setAgentOnPoint(ForagingAgent agentOnPoint) {
		this.agentOnPoint = agentOnPoint;
	}

	boolean isHasFood() {
		return hasFood;
	}

	void setHasFood(boolean hasFood) {
		this.hasFood = hasFood;
	}

}
