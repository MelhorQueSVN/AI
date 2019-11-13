package Agents;

import Classes.Posicao;
import jade.core.Agent;

public class FirefightersAgent extends Agent{
	
	private static final long serialVersionUID = 1L;
	
	// posi��o do agente
	private Posicao p;  
	// capacidade m�xima de �gua
	private float cap_max_agua;  
	// capacidade m�xima de combust�vel 
	private float cap_max_comb;  
	// capacidade atual de �gua 
	private float cap_atual_agua; 
	// capacidade atual de combust�vel
	private float cap_atual_comb; 
	// velocidade do agente 
	private int velocidade; 
	// est� dispon�vel para apagar incendios ou n�o? 
	private boolean disponivel;
	
	public void setup() { 
		// gera posi��o aleat�ria no mapa
		this.p = new Posicao(); 
		// coloca-se como dispon�vel 
		this.disponivel = true; 
		// os restantes cap �gua e combust�vel deveria ser em cada um dos agentes n�o? pq variam de tipo agente para agente
	} 
	
}
