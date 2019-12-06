package Classes;

import java.util.Random;

public class Posicao implements java.io.Serializable, Cloneable {
	
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
	
	public boolean equals(Posicao p) {
		if (p == null) return false;
		if (p == this) return true;
		if (!(p instanceof Posicao)) return false;
		Posicao o = (Posicao) p;
		return o.getCordX() == this.getCordX() && o.getCordY() == this.getCordY();
	}
	
	public Object clone() throws CloneNotSupportedException { 
		return super.clone();
	}
}
