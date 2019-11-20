package Agents;

import java.io.IOException;
import java.util.Random;

import Classes.Informacao;
import Classes.Posicao;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class FirefightersAgent extends Agent{
	
	private static final long serialVersionUID = 1L;
	
	// posição do agente
	private Posicao p;  
	// capacidade máxima de água
	private int cap_max_agua;  
	// capacidade máxima de combustível 
	private float cap_max_comb;  
	// capacidade atual de água 
	private int cap_atual_agua; 
	// capacidade atual de combustível
	private float cap_atual_comb; 
	// velocidade do agente 
	private int velocidade; 
	// está disponível para apagar incendios ou não? 
	private boolean disponivel;
	
	public void setup() { 
		/* 
		 * 	Gera coordenas aleatorías que variam de [0..100] no x e y
		 */
		this.p = new Posicao(); 
		Random rand = new Random();
		int cord_x = (int) rand.nextInt(101); 
		int cord_y = (int) rand.nextInt(101);
		p.setCordX(cord_x); 
		p.setCordY(cord_y);
		// coloca-se como disponível  
		this.disponivel = true;  
		
		/* 
		 * 	Regista os agentes firefighters nas páginas amarelas 
		 */ 		
		DFAgentDescription dfd = new DFAgentDescription(); 
		dfd.setName(getAID()); 
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType("firefighter");  
		sd.setName(this.getLocalName());
		dfd.addServices(sd); 
		try {
			DFService.register(this,dfd);
		} catch (FIPAException e) {

			e.printStackTrace();
		}
		
		// cyclic behaviour para processar pedidos.
		addBehaviour(new RecebePedidos(this));
	} 
	
	/* 
	 * 	GETS E SETS
	 */
	public void setCapMaxAgua(int cap) { this.cap_max_agua = cap; } 
	public void setCapMaxComb(float cap) {this.cap_atual_comb = cap;} 
	public void setCapAtAgua(int cap) {this.cap_atual_agua = cap;} 
	public void setCapAtComb(float cap) {this.cap_atual_comb = cap;} 
	public void setCapVel(int vel) {this.velocidade = vel;}
	public void setDisponivel(boolean disp) {this.disponivel = disp;} 
	
	public int getCapMaxAgua() {return this.cap_max_agua;} 
	public float getCapMaxComb() {return this.cap_max_comb;} 
	public int getCapAtAgua() {return this.cap_atual_agua;} 
	public float getCapAtComb() {return this.cap_atual_comb;} 
	public int getVelocidade() {return this.velocidade;} 
	public Posicao getPosicao() {return this.p;} 

	
	public class RecebePedidos extends CyclicBehaviour{
		
		private FirefightersAgent fire;
		
		public RecebePedidos(FirefightersAgent f) { 
			this.fire = f;
		}
		
		@Override
		public void action() {
			ACLMessage msg = receive(); 
			if (msg != null) { 
				if (msg.getPerformative() == ACLMessage.REQUEST) { 
					// Cria objeto informação e preenche com posição e velocidade
					Informacao i = new Informacao();  
					i.setPosicao(fire.getPosicao()); 
					i.setVelocidade(fire.getVelocidade()); 
					ACLMessage reply = msg.createReply(); 
					try {
						reply.setContentObject(i);
					} catch (IOException e) {
						e.printStackTrace();
					}
					reply.setPerformative(ACLMessage.PROPOSE);
					System.out.println("Agente " + fire.getLocalName() + " enviou informação " + i.getVelocidade());
					send(reply);
				// caso seja escolhido para apagar um incêndio
				} else if (msg.getPerformative() == ACLMessage.PROPOSE) { 
					fire.setDisponivel(false);
					System.out.println("FUI ESCOLHIDO " + fire.getName());
				}
			}
			
		} 
		
	}
}


