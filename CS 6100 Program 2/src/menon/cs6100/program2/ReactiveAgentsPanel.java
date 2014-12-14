package menon.cs6100.program2;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.*;

public class ReactiveAgentsPanel {

	private static final int VERTICAL_RECTANGLE_MODE = 0;
	private static final int HORIZONTAL_RECTANGLE_MODE = 1;
	private static final int PLACE_FOOD_MODE = 4;
	private static final int PLACE_DEPOT_MODE = 5;
	private static final int NARROW_DIMENSION = 10;
	private static final int WIDE_DIMENSION = 75;
	private static final int FOOD_SIZE = 10;
	private static final int DEPOT_HEIGHT = 75;
	private static final int DEPOT_WIDTH = 75;
	private static final int DEPOT_OPENING = 20;
	private static final int DEPOT_OPENING_FROM_TOP = 20;
	private static final int DEPOT_WALL_WIDTH = 10;
	static final int TOP_OFFSET = 45;	
	static final int TERRAIN_WIDTH = 640;
	private static final int TERRAIN_HEIGHT = 480;
	
	private static int selectedMode = 0;

	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Reactive Agents and Stigmergic Communication");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        //Create the menu bar.  Make it have a green background.
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);        
        menuBar.setPreferredSize(new Dimension(TERRAIN_WIDTH, 20));
        
        //Create user menus
        JMenu fileMenu, obstaclesMenu, forageMenu;
        JMenuItem exitMenuItem, addVerticalRectangleMenuItem, placeFoodMenuItem, placeDepotMenuItem, addHorizontalRectangleMenuItem, spawnAgentsMenuItem, startForagingMenuItem, showMetricsMenuItem;
        
        //File menu
        fileMenu = new JMenu("File");
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {System.exit(0);} });
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        
        //Obstacles menu
        obstaclesMenu = new JMenu("Features");
        addVerticalRectangleMenuItem = new JMenuItem("Vertical Barrier");
        addVerticalRectangleMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {selectedMode = VERTICAL_RECTANGLE_MODE;} });
        addHorizontalRectangleMenuItem = new JMenuItem("Horizontal Barrier");
        addHorizontalRectangleMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {selectedMode = HORIZONTAL_RECTANGLE_MODE;} });
        placeFoodMenuItem = new JMenuItem("Food");
        placeFoodMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {selectedMode = PLACE_FOOD_MODE;} });
        placeDepotMenuItem = new JMenuItem("Depot");
        placeDepotMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {selectedMode = PLACE_DEPOT_MODE;} });
        
        obstaclesMenu.add(addVerticalRectangleMenuItem);
        obstaclesMenu.add(addHorizontalRectangleMenuItem);
        obstaclesMenu.add(placeFoodMenuItem);
        obstaclesMenu.add(placeDepotMenuItem);
        menuBar.add(obstaclesMenu);
        
        //Route menu
        forageMenu = new JMenu("Forage");
        menuBar.add(forageMenu);
        
        //Create the obstacles terrain
        final ObstaclesTerrain obstaclesTerrain = new ObstaclesTerrain();
        obstaclesTerrain.setPreferredSize(new Dimension(TERRAIN_WIDTH, TERRAIN_HEIGHT));
        
        spawnAgentsMenuItem = new JMenuItem("Spawn Agents");
        spawnAgentsMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {obstaclesTerrain.spawnAgents();} });
        forageMenu.add(spawnAgentsMenuItem);
        startForagingMenuItem = new JMenuItem("Gather Food");
        startForagingMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {obstaclesTerrain.startAgents();} });
        forageMenu.add(startForagingMenuItem);
        showMetricsMenuItem = new JMenuItem("Show Metrics");
        showMetricsMenuItem.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {JOptionPane.showMessageDialog(obstaclesTerrain, obstaclesTerrain.getMetricsMessage());} });
        forageMenu.add(showMetricsMenuItem);
        
        //Set the menu bar and add the label to the content pane.
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(obstaclesTerrain, BorderLayout.CENTER);
        frame.addMouseListener(obstaclesTerrain);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    //Define the terrain that will contain the obstacles, start an d end points
    @SuppressWarnings("serial")
	static class ObstaclesTerrain extends JPanel implements MouseListener {
    	
    	private static List<Point> horizontalRectangles = Collections.synchronizedList(new ArrayList<Point>());
    	private static List<Point> verticalRectangles = Collections.synchronizedList(new ArrayList<Point>());
    	private static List<Point> foodPiles = Collections.synchronizedList(new ArrayList<Point>());
    	private static List<ForagingAgent> foragingAgents = Collections.synchronizedList(new ArrayList<ForagingAgent>());
    	private static List<Future<Void>> agentResults = Collections.synchronizedList(new ArrayList<Future<Void>>());
    	private static Point depot = null;
    	private static TerrainGrid terrainGrid = new TerrainGrid(TERRAIN_WIDTH, TERRAIN_HEIGHT);
    	private static boolean agentsAlreadySpawned = false, agentsAlreadyStarted = false;
    	private static int numberOfFoodItemsPlaced = 0;
    	private static int numberOfFoodItemsCollected = 0;
    	private static final int NUMBER_OF_AGENTS = 10;
    	private static final String NEW_LINE = "\n";
    	
    	public synchronized void paintComponent(Graphics g) {
        	
            super.paintComponent(g);

            //Draw vertical rectangles
            g.setColor(Color.BLACK);
            for (Point point : verticalRectangles) {
            	g.fillRect(point.x, point.y, NARROW_DIMENSION, WIDE_DIMENSION);
            }

            //Draw horizontal rectangles
            g.setColor(Color.BLACK);
            for (Point point : horizontalRectangles) {
            	g.fillRect(point.x, point.y, WIDE_DIMENSION, NARROW_DIMENSION);
            }
            
            //Draw food piles
            g.setColor(Color.GREEN);
            synchronized(foodPiles) {
	            for (Point point : foodPiles) {
	            	g.drawOval(point.x, point.y, 1, 1);
	            }
	         }
            
            //Draw depot
            g.setColor(Color.BLACK);
            if (depot != null) {
        		//Draw the top.
            	g.fillRect(depot.x, depot.y, DEPOT_WIDTH, DEPOT_WALL_WIDTH);
        		//Draw the bottom
            	g.fillRect(depot.x, depot.y + DEPOT_HEIGHT - DEPOT_WALL_WIDTH, DEPOT_WIDTH, DEPOT_WALL_WIDTH);
        		//Draw the right side
            	g.fillRect(depot.x + DEPOT_WIDTH - DEPOT_WALL_WIDTH, depot.y + DEPOT_WALL_WIDTH, DEPOT_WALL_WIDTH, DEPOT_HEIGHT - DEPOT_WALL_WIDTH - DEPOT_WALL_WIDTH);
        		//Draw the left side above the opening
            	g.fillRect(depot.x, depot.y + DEPOT_WALL_WIDTH, DEPOT_WALL_WIDTH, DEPOT_OPENING_FROM_TOP - DEPOT_WALL_WIDTH);
        		//Draw the left side below the opening
            	g.fillRect(depot.x, depot.y + DEPOT_OPENING_FROM_TOP + DEPOT_OPENING, DEPOT_WALL_WIDTH, DEPOT_HEIGHT - DEPOT_OPENING_FROM_TOP - DEPOT_OPENING - DEPOT_WALL_WIDTH);

            }
            
            //Draw agents as dots
            synchronized(foragingAgents) {
	            for (ForagingAgent agent : foragingAgents) {
	            	if (agent.isCarryingFood()){
	            		g.setColor(Color.ORANGE);
	            	} else {
	            		g.setColor(Color.BLUE);
	            	}
	            	g.drawOval(agent.getCurrentLocation().getxCoordinate()-2, agent.getCurrentLocation().getyCoordinate()-2, 4, 4);
	            }
            }
            
    	}

		@Override
		public void mouseClicked(MouseEvent e) {
			
			switch (selectedMode) {
			
			case VERTICAL_RECTANGLE_MODE:
				if (!agentsAlreadyStarted) {
					verticalRectangles.add(new Point(e.getX(), e.getY() - TOP_OFFSET));
					terrainGrid.setRectangularBarrier(e.getX(), e.getY() - TOP_OFFSET, NARROW_DIMENSION, WIDE_DIMENSION);
				}
				break;
				
			case HORIZONTAL_RECTANGLE_MODE:
				if (!agentsAlreadyStarted) {
					horizontalRectangles.add(new Point(e.getX(), e.getY() - TOP_OFFSET));
					terrainGrid.setRectangularBarrier(e.getX(), e.getY() - TOP_OFFSET, WIDE_DIMENSION, NARROW_DIMENSION);
				}
				break;
				
			case PLACE_FOOD_MODE:
				if (!agentsAlreadyStarted) {
					placeFood(e.getX(), e.getY() - TOP_OFFSET);
				}
				break;
				
			case PLACE_DEPOT_MODE:
				if (!agentsAlreadyStarted && depot == null) {
					if (terrainGrid.ableToSetDepot(e.getX(), e.getY() - TOP_OFFSET, DEPOT_WIDTH, DEPOT_HEIGHT, DEPOT_OPENING, DEPOT_OPENING_FROM_TOP, DEPOT_WALL_WIDTH)) {
						depot = new Point(e.getX(), e.getY() - TOP_OFFSET);
					}
				}
				break;
				
			}
			
			repaint();
		}

		public String getMetricsMessage() {
			
			if (agentsAlreadyStarted) {
				StringBuffer metricsMessage = new StringBuffer();
				synchronized(foragingAgents) {
					for(ForagingAgent agent : foragingAgents) {
						metricsMessage.append("Agent #").append(agent.getAgentNumber()).append(" Metrics: ");
						metricsMessage.append("Neighboring cells examined: ").append(agent.getStatistics().getNeighboringCellsExamined());
						metricsMessage.append(", Markers read: ").append(agent.getStatistics().getNumbersOfMarkersRead());
						metricsMessage.append(", Markers written: ").append(agent.getStatistics().getNumbersOfMarkersWritten());
						metricsMessage.append(NEW_LINE).append(NEW_LINE);
					}
					BigDecimal foodCollected = BigDecimal.valueOf(numberOfFoodItemsCollected * 100).divide(BigDecimal.valueOf(numberOfFoodItemsPlaced), BigDecimal.ROUND_HALF_UP);
					metricsMessage.append("Food collected: ").append(numberOfFoodItemsCollected).append(" of ").append(numberOfFoodItemsPlaced).append(", ").append(foodCollected.toString()).append("%");
				}
				return metricsMessage.toString();
			} else {
				return "Foraging has not started yet.";
			}

		}
		
		//Place a pile of food at the point clicked
		private void placeFood(int topLeftXCoordinate, int topLeftYCoordinate) {
			
			for (int xCoordinate = topLeftXCoordinate; xCoordinate < topLeftXCoordinate + FOOD_SIZE; ++xCoordinate) {
				for (int yCoordinate = topLeftYCoordinate; yCoordinate < topLeftYCoordinate + FOOD_SIZE; ++yCoordinate) {
					foodPiles.add(new Point(xCoordinate, yCoordinate));
					if (!terrainGrid.getPoint(xCoordinate, yCoordinate).isHasFood()) {
						++numberOfFoodItemsPlaced;
					}
					terrainGrid.getPoint(xCoordinate, yCoordinate).setHasFood(true);
				}
			}
		}
		
		//Remove food from a specific location
		public void removeFoodFrom(int xCoordinate, int yCoordinate) {
			Point point = new Point(xCoordinate, yCoordinate);
			if (foodPiles.contains(point)) {
				foodPiles.remove(point);
			}
		}
	    
		//Drop food at location
		public void dropFoodAt(int xCoordinate, int yCoordinate) {
			foodPiles.add(new Point(xCoordinate, yCoordinate));
		}

		//Create agents
	    private void spawnAgents() {
	    	
	    	if (depot == null || agentsAlreadySpawned) {
	    		return;
	    	}
	    	
	    	Random terrainGridOffsetGenerator = new Random();
	    	PointOnTerrain agentLocation = null;
	    	int depotGridOffset = 0;
	    	int depotInsidexCoordinate = 0, depotInsideyCoordinate = 0;
	    	int depotInsideWidth = DEPOT_HEIGHT - DEPOT_WALL_WIDTH - DEPOT_WALL_WIDTH;
	    	int depotInsideHeight = DEPOT_WIDTH - DEPOT_WALL_WIDTH - DEPOT_WALL_WIDTH;
	    	int depotArea = depotInsideWidth * depotInsideHeight;
	    	ForagingAgent foragingAgent = null;
	    	
	    	//Create the agents
	    	for (int agentsCounter = 0; agentsCounter < NUMBER_OF_AGENTS; ++agentsCounter) {
	    		//Create the next agent inside the depot
	    		while (true) {
	    			
	    			//Random location inside depot for agent
	    			depotGridOffset = terrainGridOffsetGenerator.nextInt(depotArea);
	    			depotInsidexCoordinate = depot.x + DEPOT_WALL_WIDTH + depotGridOffset % depotInsideWidth;
	    			depotInsideyCoordinate = depot.y + DEPOT_WALL_WIDTH + depotGridOffset / depotInsideWidth;
	    			agentLocation = terrainGrid.getPoint(depotInsidexCoordinate, depotInsideyCoordinate);
	    			
	    			//Create the agent if the location is accessible and does not already have an agent in it
	    			if (agentLocation != null) {
	    				if (agentLocation.isAccessible() && agentLocation.getAgentOnPoint() == null) {
	    					foragingAgent = new ForagingAgent(terrainGrid, agentLocation, depotGridOffset, agentsCounter, this);
	    					agentLocation.setAgentOnPoint(foragingAgent);
	    					foragingAgents.add(foragingAgent);
	    					break;
	    				}
	    			}
	    		}
	    		
	    	}
	    	
	    	agentsAlreadySpawned = true;
	    	repaint();
	    }
	    
	    //Start the agents on foraging
	    private void startAgents() {
	    	
	    	//Agents can only be stated once and they have to have been already spawned
	    	if (agentsAlreadySpawned && !agentsAlreadyStarted) {
	    		
	    		//Set up an agent pool
		    	ExecutorService agentsPool = Executors.newFixedThreadPool(NUMBER_OF_AGENTS);
		    	
		    	for (ForagingAgent agent : foragingAgents) {
		    		
		    		//Start up each agent that was spawned
		    		
		    		Future<Void> result = agentsPool.submit(agent);
		    		//Save the future result objects for the agents
		    		agentResults.add(result);
		    	}
		    	
		    	agentsAlreadyStarted = true;
		    	
		    }
	    }
	    
	    public synchronized void incrementCollectedFoodAmount() {
	    	
	    	++numberOfFoodItemsCollected;
	    }

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}    	
		
		public synchronized void updateDisplay() {
			repaint();
		}
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
