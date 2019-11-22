package Classes;

import jade.core.AID;

/* 
 * 	Classe utilizada para encapsular a informação do agente que é escolhido para apagar incêndio
 */
public class InfoEscolhido implements java.io.Serializable {
	
	private AID agente; 
	private double tempo;  
	// posição do incêndio
	private Posicao p;
	
	public InfoEscolhido() { 
		this.agente = null; 
		this.tempo = 0; 
		this.p = new Posicao();
	}

	public InfoEscolhido(AID a, double t, Posicao p) { 
		this.agente = a; 
		this.tempo = t; 
		this.p = p;
	}
	
	public AID getAgente() { return this.agente; }
	public double getTempo() { return this.tempo; } 
	public Posicao getPosicao() {return this.p;}
	
	public void setAgente(AID a) { this.agente = a; } 
	public void setTempo(double t) { this.tempo = t; } 
	public void setPosicao(Posicao p) {this.p = p;}
}
