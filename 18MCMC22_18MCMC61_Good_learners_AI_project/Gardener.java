import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JComboBox;
public class Gardener 
{
	JFrame frame;
	public int cells = 30;
	private int delay = 30;
	private int startx = -1;
	private int starty = -1;
	private int finishx = -1;
	private int finishy = -1;
	private int tool = 0;
	private int checks = 0;
	private int length = 0;
	private int WIDTH = 850;
	private final int HEIGHT = 650;
	private final int MSIZE = 600;
	private int CSIZE = MSIZE/cells;
	private String[] tools = {"Gardener "," Thirsty Plants ","Make wall ", "Erase wall"};
	private boolean solving = false;

	Node[][] map;

	Random r = new Random();

	Map canvas;

	
	JLabel toolL = new JLabel("Select cell for : - ");

	JLabel checkL = new JLabel("Checks: "+checks);
	JLabel lengthL = new JLabel("Path Length: "+length);
	JButton searchB = new JButton("Start search");
	JButton resetB = new JButton("Reset");
	JButton genMapB = new JButton("Create Garden ");
	JButton clearMapB = new JButton("Clear ALL");
	JButton devlopIntroB = new JButton(" Devloped By ");
	JComboBox toolBx = new JComboBox(tools);
	
	public Gardener() 
	{	//CONSTRUCTOR 
		clearMap();
		initialize();
	}

	public static void main(String[] args) 
	{	//MAIN METHOD
		new Gardener();
	}

	class Node 
	{	
		// 0 = Gardener, 1 = plant, 2 = wall, 3 = empty, 4 = explored, 5 = shortest path
		private int cellType = 0;
		private int hops;
		private int x;
		private int y;
		private int lastX;
		private int lastY;
		public Node(int type, int x, int y) 
		{	//CONSTRUCTOR
			cellType = type;
			this.x = x;
			this.y = y;
			hops = -1;

		}
		public int getX() 
		{

			return x;
		}		//GET METHODS
		public int getY()
		{

			return y;
		}
		public int getLastX() 
		{

			return lastX;
		}
		public int getLastY() 
		{
			return lastY;
		}
		public int getType() 
		{
			return cellType;
		}
		public int getHops() 
		{
			return hops;
		}

		public void setType(int type) 
		{
			cellType = type;
		}		//SET METHODS
		public void setLastNode(int x, int y) 
		{
			lastX = x; 
			lastY = y;
		}
		public void setHops(int hops) 
		{
			this.hops = hops;
		}
	}

	
	
	public void generateMap() 
	{	//GENERATE MAP
		clearMap();	//CREATE CLEAR MAP TO START
		for(int i = 0; i < 400; i++) 
		{
			Node current;
			do 
			{
				int x = r.nextInt(cells);
				int y = r.nextInt(cells);
				current = map[x][y];	//FIND A RANDOM NODE IN THE GRID
			} while(current.getType()==2);	//IF IT IS ALREADY A WALL, FIND A NEW ONE
			current.setType(2);	//SET NODE TO BE A WALL
		}
	}
	
	public void clearMap() 
	{	//CLEAR MAP
		finishx = -1;	//RESET THE START AND FINISH
		finishy = -1;
		startx = -1;
		starty = -1;
		map = new Node[30][30];	//CREATE NEW MAP OF NODES
		for(int x = 0; x < 30; x++) 
		{
			for(int y = 0; y < 30; y++) 
			{
				map[x][y] = new Node(3,x,y);	//SET ALL NODES TO EMPTY
			}
		}
		reset();	//RESET SOME VARIABLES
	}
	
	public void resetMap() 
	{	//RESET MAP
		for(int x = 0; x < cells; x++) 
		{
			for(int y = 0; y < cells; y++) 
			{
				Node current = map[x][y];
				if(current.getType() == 4 || current.getType() == 5)	//CHECK TO SEE IF CURRENT NODE IS EITHER CHECKED OR FINAL PATH
					map[x][y] = new Node(3,x,y);	//RESET IT TO AN EMPTY NODE
			}
		}
		if(startx > -1 && starty > -1) 
		{	//RESET THE START AND FINISH
			map[startx][starty] = new Node(0,startx,starty);
			map[startx][starty].setHops(0);
		}
		if(finishx > -1 && finishy > -1)
		{
			map[finishx][finishy] = new Node(1,finishx,finishy);
		}
		reset();	//RESET SOME VARIABLES
	}

	private void initialize() 
	{	//INITIALIZE THE GUI ELEMENTS
		frame = new JFrame();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setSize(620,950);
		frame.setTitle("Gardener & Thirsty Plant");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
      
		genMapB.setBounds(6,50,300,20);
		resetB.setBounds(330,80,120,100);
		clearMapB.setBounds(470,80,120,100);
		toolL.setBounds(6,85,300,25);
		toolBx.setBounds(6,120,300,20);
		searchB.setBounds(6,160,300,20);
		checkL.setBounds(6,830,100,25);
		lengthL.setBounds(6,870,100,25);
		devlopIntroB.setBounds(400,820,200,80);
		frame.getContentPane().add(searchB);
		frame.getContentPane().add(resetB);
		frame.getContentPane().add(genMapB);
		frame.getContentPane().add(clearMapB);
		frame.getContentPane().add(toolL);
		frame.getContentPane().add(toolBx);
		frame.getContentPane().add(checkL);
		frame.getContentPane().add(lengthL);
		frame.getContentPane().add(devlopIntroB);
		
		canvas = new Map();
		canvas.setBounds(6,200, MSIZE+1, MSIZE+1);
		frame.getContentPane().add(canvas);
		
		searchB.addActionListener(new ActionListener() 
		{		//ACTION LISTENERS
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				reset();
				if((startx > -1 && starty > -1) && (finishx > -1 && finishy > -1))
					solving = true;
			}
		});
		resetB.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				resetMap();
				Update();
			}
		});
		genMapB.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				generateMap();
				Update();
			}
		});
		clearMapB.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				clearMap();
				Update();
			}
		});
		
		toolBx.addItemListener(new ItemListener() 
		{
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				tool = toolBx.getSelectedIndex();
			}
		});

		
		devlopIntroB.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{ 
				JOptionPane.showMessageDialog(frame, " Project Name : Gardener & Thirsty Plant \n"
												   + " Devloper : Korbha Nagesh & Lokesh Kirad\n"
												   + " M.C.A (2017-21) ,SCIS ,Uohyd \n"
												   + " Submission Date :10 May 2020 ", "Devloped By", JOptionPane.PLAIN_MESSAGE, new ImageIcon(""));
			}
		});
		startSearch();	//START STATE
	}
	
	public void startSearch() 
	{	//START STATE
		if(solving) 
		{
			Dijkstra() ;
		}
		pause();	//PAUSE STATE
	}
	
	public void pause() 
	{	//PAUSE STATE
		int i = 0;
		while(!solving) 
		{
			i++;
			if(i > 500)
			{
				i = 0;
			}
			try 
			{
				Thread.sleep(1);
			} 
			catch(Exception e) {}
		}
		startSearch();	//START STATE
	}
	
	public void Update() 
	{
		CSIZE = MSIZE/cells;
		canvas.repaint();
		lengthL.setText("Path Length: "+length);
		checkL.setText("Checks: "+checks);
	}
	
	public void reset() 
	{	//RESET METHOD
		solving = false;
		length = 0;
		checks = 0;
	}
	
	public void delay() 
	{	//DELAY METHOD
		try 
		{
			Thread.sleep(delay);
		} catch(Exception e) {}
	}
	public void Dijkstra() 
	{
		ArrayList<Node> priority = new ArrayList<Node>();	//CREATE A PRIORITY QUE
		priority.add(map[startx][starty]);	//ADD THE START TO THE QUE
		while(solving) 
		{
			if(priority.size() <= 0) 
			{	//IF THE QUE IS 0 THEN NO PATH CAN BE FOUND
				solving = false;
				break;
			}
			
			 int hops = priority.get(0).getHops()+1;	//INCREMENT THE HOPS VARIABLE
            
			ArrayList<Node> explored = exploreNeighbors(priority.get(0), hops);	//CREATE AN ARRAYLIST OF NODES THAT WERE EXPLORED
			if(explored.size() > 0) 
			{
				priority.remove(0);	//REMOVE THE NODE FROM THE QUE
				priority.addAll(explored);	//ADD ALL THE NEW NODES TO THE QUE
				Update();
				delay();
			}
			else 
			{	//IF NO NODES WERE EXPLORED THEN JUST REMOVE THE NODE FROM THE QUE
				priority.remove(0);
			}	
		}
	}
		
	public ArrayList<Node> exploreNeighbors(Node current, int hops) 
	{	//EXPLORE NEIGHBORS
		//LIST OF NODES THAT HAVE BEEN EXPLORED
		ArrayList<Node> explored = new ArrayList<Node>();
				
		for(int a = -1; a <= 1; a++) 
		{
			for(int b = -1; b <= 1; b++) 
			{
              	int xbound = current.getX()+a;
				int ybound = current.getY()+b;

				if((xbound > -1 && xbound < cells) && (ybound > -1 && ybound < cells)) 
				{	//MAKES SURE THE NODE IS NOT OUTSIDE THE GRID
					Node neighbor = map[xbound][ybound];
					if((neighbor.getHops()==-1 || neighbor.getHops() > hops) && neighbor.getType()!=2) 
					{	//CHECKS IF THE NODE IS NOT A WALL AND THAT IT HAS NOT BEEN EXPLORED
						explore(neighbor, current.getX(), current.getY(), hops);	//EXPLORE THE NODE
						explored.add(neighbor);	//ADD THE NODE TO THE LIST
					}
				}
			}
		}
		return explored;
	}
		
	public void explore(Node current, int lastx, int lasty, int hops) 
	{	//EXPLORE A NODE
		if(current.getType()!=0 && current.getType() != 1)
		{
			//CHECK THAT THE NODE IS NOT THE START OR FINISH
			current.setType(4);	//SET IT TO EXPLORED
		}	
		current.setLastNode(lastx, lasty);	//KEEP TRACK OF THE NODE THAT THIS NODE IS EXPLORED FROM
		current.setHops(hops);	//SET THE HOPS FROM THE START
		checks++;
		if(current.getType() == 1) 
		{	//IF THE NODE IS THE FINISH THEN BACKTRACK TO GET THE PATH
			backtrack(current.getLastX(), current.getLastY(),hops);
		}
	}
		
	public void backtrack(int lx, int ly, int hops) 
	{	//BACKTRACK
		length = hops;
		while(hops > 1) 
		{	//BACKTRACK FROM THE END OF THE PATH TO THE START
        	Node current = map[lx][ly];
			current.setType(5);
			lx = current.getLastX();
			ly = current.getLastY();
			hops--;
		}
		solving = false;
	}
	class Map extends JPanel implements MouseListener, MouseMotionListener
	{	//MAP CLASS
		public Map() 
		{
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		public void paintComponent(Graphics g) 
		{	//REPAINT
			super.paintComponent(g);
			for(int x = 0; x < 30; x++) 
			{	//PAINT EACH NODE IN THE GRID
				for(int y = 0; y < 30; y++) 
				{
					switch(map[x][y].getType()) 
					{
						case 0:
							g.setColor(Color.GREEN);
							break;
						case 1:
							g.setColor(Color.RED);
							break;
						case 2:
							g.setColor(Color.BLACK);
							break;
						case 3:
							g.setColor(Color.WHITE);
							break;
						case 4:
							g.setColor(Color.CYAN);
							break;
						case 5:
							g.setColor(Color.YELLOW);
							break;
					}
					g.fillRect(x*CSIZE,y*CSIZE,CSIZE,CSIZE);
					g.setColor(Color.BLACK);
					g.drawRect(x*CSIZE,y*CSIZE,CSIZE,CSIZE);
					
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) 
		{
			try 
			{
				int x = e.getX()/CSIZE;	
				int y = e.getY()/CSIZE;
				Node current = map[x][y];
				if((tool == 2 || tool == 3) && (current.getType() != 0 && current.getType() != 1))
					current.setType(tool);
				Update();
			} 
			catch(Exception z) {}
		}

		@Override
		public void mouseMoved(MouseEvent e) {}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) 
		{
			resetMap();	//RESET THE MAP WHENEVER CLICKED
			try {
				int x = e.getX()/CSIZE;	//GET THE X AND Y OF THE MOUSE CLICK IN RELATION TO THE SIZE OF THE GRID
				int y = e.getY()/CSIZE;
				Node current = map[x][y];
				switch(tool ) 
				{
					case 0: 
					{	//START NODE
						if(current.getType()!=2) 
						{	//IF NOT WALL
							if(startx > -1 && starty > -1) 
							{	//IF START EXISTS SET IT TO EMPTY
								map[startx][starty].setType(3);
								map[startx][starty].setHops(-1);
							}
							current.setHops(0);
							startx = x;	//SET THE START X AND Y
							starty = y;
							current.setType(0);	//SET THE NODE CLICKED TO BE START
						}
						break;
					}
					case 1: 
					{//FINISH NODE
						if(current.getType()!=2) 
						{	//IF NOT WALL
							if(finishx > -1 && finishy > -1)	//IF FINISH EXISTS SET IT TO EMPTY
								map[finishx][finishy].setType(3);
							finishx = x;	//SET THE FINISH X AND Y
							finishy = y;
							current.setType(1);	//SET THE NODE CLICKED TO BE FINISH
						}
						break;
					}
					default:
					if(current.getType() != 0 && current.getType() != 1)
					{
						current.setType(tool);
					}
					break;
				}
				Update();
			} catch(Exception z) {}	//EXCEPTION HANDLER
		}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}
	
}
