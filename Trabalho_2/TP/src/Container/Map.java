package Container;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Agents.FirefightersAgent;
import Agents.QuartelAgent;
import Classes.Posicao;
import jade.core.AID;
import jade.core.Agent;


public class Map extends JPanel{
	
	public static final Color CITY = new Color(0,210,0);
    public static final Color AGENT = new Color(153,102,0);
    public static final Color ABAST = new Color(0,0,153); 
    public static final Color COMB = new Color(255,228,196);
    public static final Color INCEN = new Color(255,140,0);  
	
    public static final Color[] TERRAIN = {
            CITY,
            ABAST,
            AGENT, 
            COMB,
            INCEN
        };

    public static final int NUM_ROWS = 100;
    public static final int NUM_COLS = 100;

    public static final int PREFERRED_GRID_SIZE_PIXELS = 10;  
    
    private Color[][] terrainGrid;  
    
    public List<Posicao> lista_aba = new ArrayList<>(); 
    public List<Posicao> lista_abc = new ArrayList<>();  
    
    
    private FirefightersAgent fa = new FirefightersAgent(); 
    private QuartelAgent qa = new QuartelAgent();
    
    private JFrame frame = new JFrame("AI");
     
    public Map() { 
    	this.terrainGrid = new Color[NUM_ROWS][NUM_COLS]; 	
    	
    	this.qa = qa;
    	
    	lista_aba = qa.lista_aguas; 
    	lista_abc = qa.lista_combustiveis;  
    	
        for (int i = 0; i < NUM_ROWS; i++) {
             for (int j = 0; j < NUM_COLS; j++) {
            	 terrainGrid[i][j] = this.CITY; 
             }
    	} 
    	 
        /* 
         * 	DESENHA AS POSIÇÕES ESTATICAS
         */
    	for (Posicao p : lista_aba) { 
    		terrainGrid[p.getCordX()][p.getCordY()] = this.ABAST; 
    	} 
    	for (Posicao p : lista_abc) { 
    		terrainGrid[p.getCordX()][p.getCordY()] = this.COMB;
    	}

    	/*
    	for (Posicao p : a.values()) {   
    		terrainGrid[p.getCordX()][p.getCordY()] = this.AGENT;		
    	}
    	*/ 
    	
    	int preferredWidth = NUM_COLS * PREFERRED_GRID_SIZE_PIXELS;
        int preferredHeight = NUM_ROWS * PREFERRED_GRID_SIZE_PIXELS;
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));  
        
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        //frame.setVisible(true);
         
    } 
    
    public void updateAgents(HashMap<AID,Posicao> ags) { 
    	for (Posicao p :  ags.values()) { 
    		this.terrainGrid[p.getCordX()][p.getCordY()] = this.AGENT; 		
    	}
       	
    	int preferredWidth = NUM_COLS * PREFERRED_GRID_SIZE_PIXELS;
        int preferredHeight = NUM_ROWS * PREFERRED_GRID_SIZE_PIXELS;
        setPreferredSize(new Dimension(preferredWidth, preferredHeight)); 
        
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    public void updateIncendios(List<Posicao> posicoes) { 
    	for (Posicao p : posicoes) 
    		this.terrainGrid[p.getCordX()][p.getCordY()] = this.INCEN;
    	
    	System.out.println("MAP OBTIVE NOVA POSICAO: " + posicoes.toString());
    	
    	int preferredWidth = NUM_COLS * PREFERRED_GRID_SIZE_PIXELS;
        int preferredHeight = NUM_ROWS * PREFERRED_GRID_SIZE_PIXELS;
        setPreferredSize(new Dimension(preferredWidth, preferredHeight)); 
        
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        // Important to call super class method
        super.paintComponent(g);
        // Clear the board
        g.clearRect(0, 0, getWidth(), getHeight());
        // Draw the grid
        int rectWidth = getWidth() / NUM_COLS;
        int rectHeight = getHeight() / NUM_ROWS;

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                // Upper left corner of this terrain rect
                int x = i * rectWidth;
                int y = j * rectHeight;
                Color terrainColor = terrainGrid[i][j];
                g.setColor(terrainColor);
                g.fillRect(x, y, rectWidth, rectHeight);
            }
        }
    }
}
