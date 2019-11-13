package Classes;

public class Informacao {
	
	// posição do agente
	private Posicao p;  
	// velocidade do agente
	private int vel; 
	
	public Informacao() {
		this.p = new Posicao(); 
		this.vel = 0;
	}
	
	public Informacao(Posicao pos, int veloc) { 
		this.p = pos; 
		this.vel = veloc;
	}

	public Posicao getPos() { 
		return this.p;
	}

	public int getVelocidade() { 
		return this.vel;
	}

	public void setPosicao(Posicao pos) { 
		this.p = pos;
	}

	public void setVelocidade(int velo) { 
		this.vel = velo;
	}
}
