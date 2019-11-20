package Classes;

import java.util.Random;

public class Posicao implements java.io.Serializable {
	
	private int cord_x; 	
	private int cord_y; 
	
	public Posicao() { 
		this.cord_x = 0; 
		this.cord_y = 0;
	} 
	
	public Posicao(int x, int y) { 
		this.cord_x = x; 
		this.cord_y = y;
	}
	
	public int getCordX() { 
		return this.cord_x;
	}
	
	public int getCordY() { 
		return this.cord_y;
	}

	public void setCordX(int x) { 
		this.cord_x = x;
	}

	public void setCordY(int y) { 
		this.cord_y = y;
	}
}
