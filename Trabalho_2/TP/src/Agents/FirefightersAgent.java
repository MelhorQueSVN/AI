package Agents;

import Classes.Posicao;
import jade.core.Agent;

public class FirefightersAgent extends Agent{
	
	private static final long serialVersionUID = 1L;
	
	// posição do agente
	private Posicao p;  
	// capacidade máxima de água
	private float cap_max_agua;  
	// capacidade máxima de combustível 
	private float cap_max_comb;  
	// capacidade atual de água 
	private float cap_atual_agua; 
	// capacidade atual de combustível
	private float cap_atual_comb; 
	// velocidade do agente 
	private int velocidade; 
	// está disponível para apagar incendios ou não? 
	private boolean disponivel;
	
	public void setup() { 
		// gera posição aleatória no mapa
		this.p = new Posicao(); 
		// coloca-se como disponível 
		this.disponivel = true; 
		// os restantes cap água e combustível deveria ser em cada um dos agentes não? pq variam de tipo agente para agente
	} 
	
}
