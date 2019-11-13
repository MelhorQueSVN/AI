package Classes;

import java.util.Random;

public class Posicao {
	
	private float cord_x; 	
	private float cord_y; 
	
	public Posicao() { 
		// gera posição aleatória de [0..100] (depois altera-se conforme tam grelha) no (x,y)
		Random rand = new Random();
		this.cord_x = (float) rand.nextFloat()*100;; 
		this.cord_y = (float) rand.nextFloat()*100;;
	} 
	
	public Posicao(float x, float y) { 
		this.cord_x = x; 
		this.cord_y = y;
	}
	
	public float getCordX() { 
		return this.cord_x;
	}
	
	public float getCordY() { 
		return this.cord_y;
	}

	public void setCordX(float x) { 
		this.cord_x = x;
	}

	public void setCordY(float y) { 
		this.cord_y = y;
	}
}
