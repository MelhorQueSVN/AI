package Agents;

import java.util.Random;

import Classes.Posicao;
import jade.core.Agent;

public class FirefightersAgent extends Agent{
	
	private static final long serialVersionUID = 1L;
	
	// posi��o do agente
	private Posicao p;  
	// capacidade m�xima de �gua
	private int cap_max_agua;  
	// capacidade m�xima de combust�vel 
	private float cap_max_comb;  
	// capacidade atual de �gua 
	private int cap_atual_agua; 
	// capacidade atual de combust�vel
	private float cap_atual_comb; 
	// velocidade do agente 
	private int velocidade; 
	// est� dispon�vel para apagar incendios ou n�o? 
	private boolean disponivel;
	
	public void setup() { 
		/* 
		 * 	Gera coordenas aleator�as que variam de [0..100] no x e y
		 */
		this.p = new Posicao(); 
		Random rand = new Random();
		int cord_x = (int) rand.nextInt(101); 
		int cord_y = (int) rand.nextInt(101);
		p.setCordX(cord_x); 
		p.setCordY(cord_y);
		// coloca-se como dispon�vel 
		this.disponivel = true; 
	} 
	
	/* 
	 * 	GETS E SETS
	 */
	public void setCapMaxAgua(int cap) { this.cap_max_agua = cap; } 
	public void setCapMaxComb(float cap) {this.cap_atual_comb = cap;} 
	public void setCapAtAgua(int cap) {this.cap_atual_agua = cap;} 
	public void setCapAtComb(float cap) {this.cap_atual_comb = cap;} 
	public void setCapVel(int vel) {this.velocidade = vel;}
	
	public int getCapMaxAgua() {return this.cap_max_agua;} 
	public float getCapMaxComb() {return this.cap_max_comb;} 
	public int getCapAtAgua() {return this.cap_atual_agua;} 
	public float getCapAtComb() {return this.cap_atual_comb;} 
	public void setDisponivel() {this.disponivel = false;}
}
