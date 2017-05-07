package Checkboard;

import java.awt.*;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


import Checkboard.SlidePuzzleModel.CarSpots;

class SlidePuzzleGUI extends JPanel {
	//=============================================== instance variables
	private GraphicsPanel    _puzzleGraphics;
	public SlidePuzzleModel _puzzleModel = new SlidePuzzleModel();
	String infoDumpText = ("-");
	String infoDumpTextShutdown = (infoDumpText + "\nAVCPP is currently shut down. Press RESUME OPERATIONS"
			+ " to resume normal operations.");
	JTextArea infoDump = new JTextArea(infoDumpText, 44, 60);
	JButton openSpots= new JButton("There are "+emptyCount()+ " spots open");
	JButton getInfo= new JButton("Call Your Car");
	JButton moveNorth= new JButton("Move North");
	JButton moveSouth= new JButton("Move South");
	JButton moveWest= new JButton("Move West");
	JButton moveEast= new JButton("Move East");
	JButton emergencyShutdown= new JButton("EMERGENCY SHUTDOWN");
	private boolean stillRunning = true;
	//end instance variables
	
	void setCarSpots(int row, int col, String name, int timeNumber, String timeTagged, String dateInit) {
		_puzzleModel.setName(row, col, name);
		_puzzleModel.setSpotTaken(row, col);
		_puzzleModel.setTime(row, col, timeNumber, timeTagged);
		_puzzleModel.setTimeSince(row, col, dateInit);
	}//end getName
	
	boolean isEmptyTile(int row, int col) {
		return _puzzleModel.isEmptyTile(row, col);
	}
	
	public int[] findMyCar(String name) {
		int[] rowCol = new int[3];
		boolean found = false;
		for (int i = 0; i < 5; i++) {
			if (found == false) {
				for (int j = 0; j < 5; j++) {
					if ((_puzzleModel.getName(i, j)).equals(name) == true) {
						rowCol[0] = i;
						rowCol[1] = j;
						rowCol[2] = Integer.parseInt(_puzzleModel.getStorageNumber(i, j));
						found = true;
					}
				}
			}
		}
		if (found == true) {
			return rowCol;
		} else {
			return null;
		}
	}
	//====================================================== constructor
	public SlidePuzzleGUI() {
		//--- Create buttons and text fields
		getInfo.addActionListener(new gettingInfo());
		moveNorth.addActionListener(new movingNorth());
		moveNorth.setVisible(false);
		moveSouth.addActionListener(new movingSouth());
		moveSouth.setVisible(false);
		moveWest.addActionListener(new movingWest());
		moveWest.setVisible(false);
		moveEast.addActionListener(new movingEast());
		moveEast.setVisible(false);
		emergencyShutdown.addActionListener(new shuttingDown());
		infoDump.setEditable(false);
		
		//--- Create control panel
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		
		controlPanel.add(getInfo);
		controlPanel.add(moveNorth);
		controlPanel.add(moveSouth);
		controlPanel.add(moveWest);
		controlPanel.add(moveEast);
		controlPanel.add(emergencyShutdown);
		
		//--- Create graphics panel
		_puzzleGraphics = new GraphicsPanel();
		
		//--- Create info panel
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new FlowLayout());
		infoPanel.add(infoDump);
		
		//--- Set the layout and add the components
		this.setLayout(new BorderLayout());
		this.add(controlPanel, BorderLayout.NORTH);
		this.add(_puzzleGraphics, BorderLayout.CENTER);
		this.add(infoPanel, BorderLayout.EAST);
	}//end constructor
	
	
	//////////////////////////////////////////////// class GraphicsPanel
	// This is defined inside the outer class so that
	// it can use the outer class instance variables.
	class GraphicsPanel extends JPanel implements MouseListener {
		private static final int ROWS = 6;
		private static final int COLS = 5;
		private boolean canGetInfo = false;
		private boolean canForceCarLeave = false;
		
		private static final int CELL_SIZE = 90; // Pixels
		private Font _biggerFont;
		
		
		//================================================== constructor
		public GraphicsPanel() {
			_biggerFont = new Font("SansSerif", Font.BOLD, CELL_SIZE/4);
			this.setPreferredSize(new Dimension(CELL_SIZE * COLS, CELL_SIZE*ROWS));
			this.setBackground(Color.black);
			this.addMouseListener(this);  // Listen own mouse events.
		}//end constructor
		
		//=======================================x method paintComponent
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			for (int r=0; r<ROWS-1; r++) {
				for (int c=0; c<COLS; c++) {
					int x = c * CELL_SIZE;
					int y = r * CELL_SIZE;
					String text = _puzzleModel.getStorageNumber(r, c);
					if (text != null) {
						if(_puzzleModel.getSpotTaken(r, c) == true){
							
							g.setColor(Color.red);
							g.fillRect(x+2, y+2, CELL_SIZE-4, CELL_SIZE-4);
							g.setColor(Color.black);
							g.setFont(_biggerFont);
							g.drawString(text, x+20, y+(3*CELL_SIZE)/4);
							
						}else{
						g.setColor(Color.gray);
						g.fillRect(x+2, y+2, CELL_SIZE-4, CELL_SIZE-4);
						g.setColor(Color.black);
						g.setFont(_biggerFont);
						g.drawString(text, x+20, y+(3*CELL_SIZE)/4);
						}
					}
				}
			}
			// paints ENTRY square
			g.setColor(Color.cyan);
			g.fillRect(2, (5*CELL_SIZE)+2, CELL_SIZE-4, CELL_SIZE-4);
			g.setColor(Color.black);
			g.setFont(_biggerFont);
			g.drawString(_puzzleModel.getStorageNumber(5, 0), 20, (5*CELL_SIZE)+(3*CELL_SIZE)/4);
			// paints walls in bottom row
			for (int c=1; c<COLS-1; c++) {
				int x = c * CELL_SIZE;
				String text = _puzzleModel.getStorageNumber(5, c);
				if (text != null) {
					g.setColor(Color.black);
					g.fillRect(x+2, (5*CELL_SIZE)+2, CELL_SIZE-4, CELL_SIZE-4);
				}
			}
			// paints EXIT square
			g.setColor(Color.cyan);
			g.fillRect(4*CELL_SIZE+2, (5*CELL_SIZE)+2, CELL_SIZE-4, CELL_SIZE-4);
			g.setColor(Color.black);
			g.setFont(_biggerFont);
			g.drawString(_puzzleModel.getStorageNumber(5, 4), (4*CELL_SIZE)+20, (5*CELL_SIZE)+(3*CELL_SIZE)/4);
		}//end paintComponent
		//toggles whether or not clicking a given platform gives you information
		public void toggleGetInfo() {
			if (canGetInfo == false) {
				canGetInfo = true;
			} else {
				canGetInfo = false;
			}
		}//end toggleGetInfo
		public void toggleCarForceLeave() {
			if (canForceCarLeave == false) {
				canForceCarLeave = true;
			} else {
				canForceCarLeave = false;
			}
		}//end toggleGetInfo
		public int[] findMyCar(String name) {
			int[] rowCol = new int[2];
			boolean found = false;
			for (int i = 0; i < ROWS - 1; i++) {
				for (int j = 0; j < COLS - 1; j++) {
					if ((_puzzleModel.getName(i, j)).equals(name) == true) {
						rowCol[0] = i;
						rowCol[1] = j;
						found = true;
					}
				}
			}
			if (found == true) {
				return rowCol;
			} else {
				return null;
			}
		}
		//======================================== listener mousePressed
		public void mousePressed(MouseEvent e) {
			int col = e.getX()/CELL_SIZE;
          int row = e.getY()/CELL_SIZE;
			if (_puzzleModel.getSpotTaken(row, col)==true) {
				
	            String setAsInfoDumpText = "Platform No: " + _puzzleModel.getStorageNumber(row, col) +
	            		"\nPosition: " + (col+1) + ", " + (row+1) + "\nOccupied: " + _puzzleModel.getSpotTaken(row, col) + "\nCar Owner: " +
	            		_puzzleModel.getName(row, col) + "\nTime Due: " + _puzzleModel.getTime(row, col) +
	            		"\nSpot Taken Since: " + _puzzleModel.getTimeSince(row, col);
	            infoDumpText = setAsInfoDumpText;
	            if (stillRunning == true) {
	            	infoDump.setText(infoDumpText);
	            } else {
	            	String infoDumpTextShutdown = (infoDumpText + "\nAVCPP is currently shut down. "
	            			+ "Press RESUME OPERATIONS to resume normal operations.");
	            	infoDump.setText(infoDumpTextShutdown);
	            }
	            toggleGetInfo();
	            if (stillRunning == false) {
	            }
			} else {
				if(col == 4 && row == 4){
					// done
				}else if(_puzzleModel._emptyTile.getCol()>col){
					for (int i = _puzzleModel._emptyTile.getCol();i>col;i--){
						_puzzleModel.moveTileWest();
						_puzzleGraphics.repaint();
						
						}
						for (int i = _puzzleModel._emptyTile.getRow();i>row;i--){
							_puzzleModel.moveTileNorth();
							_puzzleGraphics.repaint();
						}
						for (int i = _puzzleModel._emptyTile.getRow();i<3;i++){
						_puzzleModel.moveTileEast();
						_puzzleGraphics.repaint();
						_puzzleModel.moveTileSouth();
						_puzzleGraphics.repaint();
						_puzzleModel.moveTileSouth();
						_puzzleGraphics.repaint();
						_puzzleModel.moveTileWest();
						_puzzleGraphics.repaint();
						_puzzleModel.moveTileNorth();
						_puzzleGraphics.repaint();
						}
						_puzzleModel.moveTileEast();
						_puzzleGraphics.repaint();
						_puzzleModel.moveTileSouth();
						_puzzleGraphics.repaint();
						_puzzleModel.moveTileWest();
						_puzzleGraphics.repaint();
						for (int i = 0;i<3-col;i++){
							_puzzleModel.moveTileNorth();
							_puzzleGraphics.repaint();
							_puzzleModel.moveTileEast();
							_puzzleGraphics.repaint();
							_puzzleModel.moveTileEast();
							_puzzleGraphics.repaint();
							_puzzleModel.moveTileSouth();
							_puzzleGraphics.repaint();
							_puzzleModel.moveTileWest();
							_puzzleGraphics.repaint();
							}
				} else if (col == 4) {
					for (int i = _puzzleModel._emptyTile.getCol();i<col;i++){
						_puzzleModel.moveTileEast();
						_puzzleGraphics.repaint();
						}
					for (int i = _puzzleModel._emptyTile.getRow();i>row;i--){
						_puzzleModel.moveTileNorth();
						_puzzleGraphics.repaint();
					}
					for (int i = _puzzleModel._emptyTile.getRow();i<3;i++){
						_puzzleModel.moveTileWest();
						_puzzleGraphics.repaint();
						_puzzleModel.moveTileSouth();
						_puzzleGraphics.repaint();
						_puzzleModel.moveTileSouth();
						_puzzleGraphics.repaint();
						_puzzleModel.moveTileEast();
						_puzzleGraphics.repaint();
						_puzzleModel.moveTileNorth();
						_puzzleGraphics.repaint();
						}
//				} else if (col == 0) {
//					for (int i = _puzzleModel._emptyTile.getCol();i>=col;i--){
//						_puzzleModel.moveTileWest();
//						_puzzleGraphics.repaint();
//						}
//					for (int i = _puzzleModel._emptyTile.getRow();i>row;i--){
//						_puzzleModel.moveTileNorth();
//						_puzzleGraphics.repaint();
//					}
//					for (int i = _puzzleModel._emptyTile.getRow();i<3;i++){
//						_puzzleModel.moveTileEast();
//						_puzzleGraphics.repaint();
//						_puzzleModel.moveTileSouth();
//						_puzzleGraphics.repaint();
//						_puzzleModel.moveTileSouth();
//						_puzzleGraphics.repaint();
//						_puzzleModel.moveTileWest();
//						_puzzleGraphics.repaint();
//						_puzzleModel.moveTileNorth();
//						_puzzleGraphics.repaint();
//						}
//					for (int i = 0;i<3-col;i++){
//						_puzzleModel.moveTileNorth();
//						_puzzleGraphics.repaint();
//						_puzzleModel.moveTileEast();
//						_puzzleGraphics.repaint();
//						_puzzleModel.moveTileEast();
//						_puzzleGraphics.repaint();
//						_puzzleModel.moveTileSouth();
//						_puzzleGraphics.repaint();
//						_puzzleModel.moveTileWest();
//						_puzzleGraphics.repaint();
//						}
					
				} else {
				for (int i = _puzzleModel._emptyTile.getCol();i<col;i++){
					
				_puzzleModel.moveTileEast();
				_puzzleGraphics.repaint();
				}
				for (int i = _puzzleModel._emptyTile.getRow();i>row;i--){
					_puzzleModel.moveTileNorth();
					_puzzleGraphics.repaint();
				}
				for (int i = _puzzleModel._emptyTile.getRow();i<3;i++){
				_puzzleModel.moveTileEast();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileSouth();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileSouth();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileWest();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileNorth();
				_puzzleGraphics.repaint();
				}
				_puzzleModel.moveTileEast();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileSouth();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileWest();
				_puzzleGraphics.repaint();
				for (int i = 0;i<3-col;i++){
					_puzzleModel.moveTileNorth();
					_puzzleGraphics.repaint();
					_puzzleModel.moveTileEast();
					_puzzleGraphics.repaint();
					_puzzleModel.moveTileEast();
					_puzzleGraphics.repaint();
					_puzzleModel.moveTileSouth();
					_puzzleGraphics.repaint();
					_puzzleModel.moveTileWest();
					_puzzleGraphics.repaint();
					}
				}
//			_puzzleModel.getCarSpots(row, col).setColor(Color.blue);
			
			GUI EntranceGUI = new GUI(MetaGUI.slidePuzzle);
			EntranceGUI.showTextFieldDemo();
				
			}
			if (canForceCarLeave == true) {
				
	            String setAsInfoDumpText = "Platform No: " + _puzzleModel.getStorageNumber(row, col) +
	            		"\nPosition: " + (col+1) + ", " + (row+1) + "\nOccupied: " + _puzzleModel.getSpotTaken(row, col) + "\nCar Owner: " +
	            		_puzzleModel.getName(row, col) + "\nTime Due: " + _puzzleModel.getTime(row, col) +
	            		"\nSpot Taken Since: " + _puzzleModel.getTimeSince(row, col);
	            infoDumpText = setAsInfoDumpText;
	            if (stillRunning == true) {
	            	infoDump.setText(infoDumpText);
	            } else {
	            	String infoDumpTextShutdown = (infoDumpText + "\nAVCPP is currently shut down. "
	            			+ "Press RESUME OPERATIONS to resume normal operations.");
	            	infoDump.setText(infoDumpTextShutdown);
	            }
	            toggleGetInfo();
	            if (stillRunning == false) {
	            	getInfo.setVisible(true);
	            }
			}
		}//end mousePressed	
		
		//========================================== ignore these events
		public void mouseClicked (MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered (MouseEvent e) {}
		public void mouseExited  (MouseEvent e) {}
	}//end class GraphicsPanel
	
	////////////////////////////////////////// inner class gettingInfo
	public class gettingInfo implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GUI ExitGUI = new GUI(MetaGUI.slidePuzzle);
			ExitGUI.showTextFieldExit();
		}
	}//end inner class gettingInfo
	public class movingNorth implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_puzzleModel.moveTileNorth();
			_puzzleGraphics.repaint();
		}
	}//end inner class movingNorth
	public class movingSouth implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_puzzleModel.moveTileSouth();
			_puzzleGraphics.repaint();
		}
	}//end inner class movingSouth
	public class movingWest implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_puzzleModel.moveTileWest();
			_puzzleGraphics.repaint();
		}
	}//end inner class movingWest
	public class movingEast implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_puzzleModel.moveTileEast();
			_puzzleGraphics.repaint();
		}
	}//end inner class movingEast
	public class carLeaving implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String entryName = _puzzleModel.getName(4, 0);
			if (entryName.equals("N/A") == false && entryName.equals("Unoccupied") == false) {
				_puzzleModel.restoreDefaults(4, 0);
				_puzzleGraphics.repaint();
			}
		}
	}//end inner class carLeaving
	public class carForceLeaving implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			_puzzleGraphics.toggleCarForceLeave();
			getInfo.setVisible(false);
		}
	}//end inner class carForceLeaving
	public class shuttingDown implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (stillRunning == true) {
				emergencyShutdown.setText("RESUME OPERATIONS");
				String infoDumpTextShutdown = (infoDumpText + "\nAVCPP is currently shut down. Press RESUME"
						+ " OPERATIONS to resume normal operations.");
				infoDump.setText(infoDumpTextShutdown);
				infoDump.setBackground(Color.ORANGE);
				stillRunning = false;
				moveNorth.setVisible(true);
				moveSouth.setVisible(true);
				moveWest.setVisible(true);
				moveEast.setVisible(true);
			} else {
				emergencyShutdown.setText("EMERGENCY SHUTDOWN");
				infoDump.setText(infoDumpText);
				infoDump.setBackground(Color.WHITE);
				stillRunning = true;
				getInfo.setVisible(true);
				moveNorth.setVisible(false);
				moveSouth.setVisible(false);
				moveWest.setVisible(false);
				moveEast.setVisible(false);
			}
		}
	}//end inner class shuttingDown
	
	public void removeCar(int row, int col){
		
		_puzzleModel.moveTileSouth();
		_puzzleModel.moveTileSouth();
			
		if(row == 4 && col == 0){
			
		}else if(_puzzleModel._emptyTile.getCol()>col){
			for (int i = _puzzleModel._emptyTile.getCol();i>col;i--){
				_puzzleModel.moveTileWest();
				_puzzleGraphics.repaint();
				
				}
				for (int i = _puzzleModel._emptyTile.getRow();i>row;i--){
					_puzzleModel.moveTileNorth();
					_puzzleGraphics.repaint();
				}
				for (int i = _puzzleModel._emptyTile.getRow();i<3;i++){
				_puzzleModel.moveTileEast();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileSouth();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileSouth();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileWest();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileNorth();
				_puzzleGraphics.repaint();
				}
				_puzzleModel.moveTileWest();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileSouth();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileEast();
				_puzzleGraphics.repaint();
//				for (int i = 0;i<3-col;i++){
//					_puzzleModel.moveTileNorth();
//					_puzzleGraphics.repaint();
//					_puzzleModel.moveTileWest();
//					_puzzleGraphics.repaint();
//					_puzzleModel.moveTileWest();
//					_puzzleGraphics.repaint();
//					_puzzleModel.moveTileSouth();
//					_puzzleGraphics.repaint();
//					_puzzleModel.moveTileEast();
//					_puzzleGraphics.repaint();
//					}
//		} else if (col == 0) {
//			for (int i = _puzzleModel._emptyTile.getCol();i<col;i++){
//				_puzzleModel.moveTileEast();
//				_puzzleGraphics.repaint();
//				}
//			for (int i = _puzzleModel._emptyTile.getRow();i>row;i--){
//				_puzzleModel.moveTileNorth();
//				_puzzleGraphics.repaint();
//			}
//			for (int i = _puzzleModel._emptyTile.getRow();i<3;i++){
//				_puzzleModel.moveTileEast();
//				_puzzleGraphics.repaint();
//				_puzzleModel.moveTileSouth();
//				_puzzleGraphics.repaint();
//				_puzzleModel.moveTileSouth();
//				_puzzleGraphics.repaint();
//				_puzzleModel.moveTileWest();
//				_puzzleGraphics.repaint();
//				_puzzleModel.moveTileNorth();
//				_puzzleGraphics.repaint();
//				}
//
//			
		} else {
		for (int i = _puzzleModel._emptyTile.getCol();i<col;i++){
			
		_puzzleModel.moveTileEast();
		_puzzleGraphics.repaint();
		}
		for (int i = _puzzleModel._emptyTile.getRow();i>row;i--){
			_puzzleModel.moveTileNorth();
			_puzzleGraphics.repaint();
		}
		if(row == 4 && col == 4){
			// on right
			for (int i = 0;i<3;i++){
			_puzzleModel.moveTileNorth();
			_puzzleGraphics.repaint();
			_puzzleModel.moveTileWest();
			_puzzleGraphics.repaint();
			_puzzleModel.moveTileWest();
			_puzzleGraphics.repaint();
			_puzzleModel.moveTileSouth();
			_puzzleGraphics.repaint();
			_puzzleModel.moveTileEast();
			_puzzleGraphics.repaint();
			}
		} else if(row == 4 && col !=4){
			_puzzleModel.moveTileWest();
			_puzzleGraphics.repaint();
			_puzzleModel.moveTileSouth();
			_puzzleGraphics.repaint();
			_puzzleModel.moveTileEast();
			_puzzleGraphics.repaint();
			for (int i = _puzzleModel._emptyTile.getCol();i>1;i--){
				_puzzleModel.moveTileNorth();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileWest();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileWest();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileSouth();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileEast();
				_puzzleGraphics.repaint();
				}
			
		}
			// to the top
			_puzzleModel.moveTileWest();
			_puzzleGraphics.repaint();
			_puzzleModel.moveTileSouth();
			_puzzleGraphics.repaint();
			_puzzleModel.moveTileSouth();
			_puzzleGraphics.repaint();
			_puzzleModel.moveTileEast();
			_puzzleGraphics.repaint();
			_puzzleModel.moveTileNorth();
			_puzzleGraphics.repaint();
			for (int i = 0;i<col-1;i++){
				_puzzleModel.moveTileNorth();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileWest();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileWest();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileSouth();
				_puzzleGraphics.repaint();
				_puzzleModel.moveTileEast();
				_puzzleGraphics.repaint();
				}
			
		
//		for (int i = _puzzleModel._emptyTile.getRow();i<3;i++){
//		_puzzleModel.moveTileWest();
//		_puzzleGraphics.repaint();
//		_puzzleModel.moveTileSouth();
//		_puzzleGraphics.repaint();
//		_puzzleModel.moveTileSouth();
//		_puzzleGraphics.repaint();
//		_puzzleModel.moveTileEast();
//		_puzzleGraphics.repaint();
//		_puzzleModel.moveTileNorth();
//		_puzzleGraphics.repaint();
//		}
//		_puzzleModel.moveTileWest();
//		_puzzleGraphics.repaint();
//		_puzzleModel.moveTileSouth();
//		_puzzleGraphics.repaint();
//		_puzzleModel.moveTileEast();
//		_puzzleGraphics.repaint();
//		for (int i = 0;i<3-col;i++){
//			_puzzleModel.moveTileNorth();
//			_puzzleGraphics.repaint();
//			_puzzleModel.moveTileWest();
//			_puzzleGraphics.repaint();
//			_puzzleModel.moveTileWest();
//			_puzzleGraphics.repaint();
//			_puzzleModel.moveTileSouth();
//			_puzzleGraphics.repaint();
//			_puzzleModel.moveTileEast();
//			_puzzleGraphics.repaint();
//			}
		}
		String entryName = _puzzleModel.getName(4, 0);
		if (entryName.equals("N/A") == false && entryName.equals("Unoccupied") == false) {
			_puzzleModel.restoreDefaults(4, 0);
			_puzzleGraphics.repaint();
		}
	}
	
	public int emptyCount(){
		
		int count = 0;
		for(int i = 0;i<5;i++){
			for(int j = 0;j<5;j++){
				CarSpots temp = _puzzleModel.getCarSpots(i,j);
				if(!temp.getSpotTaken()){
					count = count + 1;
				}
			}
		}
		return count-2;
		
	}
	
}//end class SlidePuzzleGUI