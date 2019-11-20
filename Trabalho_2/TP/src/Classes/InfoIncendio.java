package Classes;

public class InfoIncendio implements java.io.Serializable{
	
	// posição do incêndio
	private Posicao p; 
	// gravidade do incêndio
	private int gravidade; 
	
	public InfoIncendio() { 
		p = new Posicao(); 
		gravidade = 0;
	} 
	
	public InfoIncendio(InfoIncendio i) { 
		this.p = i.getPos(); 
		this.gravidade = i.getGravidade();
	}
	
	public InfoIncendio(Posicao pos,int grav) { 
		this.p = pos; 
		this.gravidade = grav;
	}
	
	public Posicao getPos() { 
		return this.p;
	}

	public int getGravidade() { 
		return this.gravidade;
	}

	public void setPosicao(Posicao pos) { 
		this.p = pos;
	}

	public void setGravidade(int grav) { 
		this.gravidade = grav;
	}
}
