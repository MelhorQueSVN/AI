package Agents;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import Classes.InfoEscolhido;
import Classes.Informacao;
import Classes.Posicao;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

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
		// coloca-se como disponível todos os agentes 
		this.disponivel = false; 
		
		/* 
		 * 	Regista os agentes firefighters nas p�ginas amarelas 
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
	public boolean getDisponivel() {return this.disponivel;}
	
	// função com o intuito de abastecer o combustível.
	public void abasteceCombustivel() throws InterruptedException { 
		// obtem a lista de combustiveis do quartelagent.
		List<Posicao> lista_combustiveis = QuartelAgent.lista_combustiveis; 
		for(Posicao p : lista_combustiveis) { 
			if (p.getCordX() == this.p.getCordX() && p.getCordY() == this.p.getCordY()) {
				this.cap_atual_comb = this.cap_max_comb; 
				Thread.sleep(1000);
			}
		}
	} 
	
	// função com o intuito de abastecer a água.
	public void abasteceAgua() throws InterruptedException { 
		// obtem a lista de abastecimento de agua do quartelagent
		List<Posicao> lista_agua = QuartelAgent.lista_aguas; 
		for(Posicao p : lista_agua) { 
			if (p.getCordX() == this.p.getCordX() && p.getCordY() == this.p.getCordY()) { 
				this.cap_atual_comb = this.cap_max_comb; 
				Thread.sleep(1000);
			}
		}
	}  
	
	public class RecebePedidos extends CyclicBehaviour{
		
		private FirefightersAgent fire;
		
		public RecebePedidos(FirefightersAgent f) { 
			this.fire = f;
		}
		
		// função que dada a posição do incêndio verifica se possui o combustível necessário para chegar
		// a essa dada posição.
		public boolean verificaCombustivel(Posicao p) { 
			Posicao pos_agent = fire.getPosicao();
			// gasta 0.1 de combustível por cada movimento
			float gasto_comb = (float) 0.1;
			double dist =  Math.sqrt(Math.pow((p.getCordX() - pos_agent.getCordX()),2) 
						   + Math.pow((p.getCordY() - pos_agent.getCordY()),2));   
			double comb_necessario = dist * gasto_comb; 
			if (comb_necessario > fire.getCapAtComb()) { 
				return false;
			} else { 
				return true;
			}
		} 
		
		// retorna posição do posto de abastecimento + próximo
		public Posicao postoAbastecimentoMaisPerto(List<Posicao> abastecimentos) { 
			Posicao pos_agent = fire.getPosicao(); 
			Posicao pos_min = new Posicao(); 
			double dist_min = 1000;
			for (Posicao p : abastecimentos) { 
				double dist = Math.sqrt(Math.pow((p.getCordX() - pos_agent.getCordX()),2) 
						   + Math.pow((p.getCordY() - pos_agent.getCordY()),2));   
				if (dist < dist_min) { 
					dist_min = dist; 
					pos_min = p;
				}
			}
			return pos_min;
		}
		
		// calcula o tempo que demora a abastecer e a chegar ao incêndio
		public double calculaTempo( Posicao p, Posicao inc) { 
			Posicao pos_agent = fire.getPosicao();
			double dist_ag_abastecer = Math.sqrt(Math.pow((p.getCordX() - pos_agent.getCordX()),2) 
					   + Math.pow((p.getCordY() - pos_agent.getCordY()),2));  
			double dist_ab_inc = Math.sqrt(Math.pow((p.getCordX() - inc.getCordX()),2) 
					   + Math.pow((p.getCordY() - inc.getCordY()),2));  
			double dist_total = dist_ag_abastecer + dist_ab_inc; 
			return (dist_total)/(fire.getVelocidade());
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
					System.out.println("Agente " + fire.getLocalName() + " enviou informacao " + i.getVelocidade());
					send(reply);
				// caso seja escolhido para apagar um incêndio
				} else if (msg.getPerformative() == ACLMessage.PROPOSE) { 
					// Desencapsula a posição do incêndio e o tempo que demora a chegar ao incêndio
					try {
						InfoEscolhido i = (InfoEscolhido) msg.getContentObject();
						Posicao inc = i.getPosicao(); 
						double tempo_a_chegar = i.getTempo(); 
						boolean possui = verificaCombustivel(inc); 
						// caso possua recuros suficientes para apagar o incêndio move-se para esse incêndio
						if (possui == true && fire.getCapAtAgua() > 0 && fire.getDisponivel() == true) { 
							ACLMessage reply = msg.createReply(); 
							reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL); 
							System.out.println("Tenho combustivel e água suficiente, e aceitei a proposta\n");
							try {
								reply.setContentObject(i);
							} catch (IOException e) {
								e.printStackTrace();
							} 
							fire.setDisponivel(false); 
							/* 
							 * 	FALTA METER FUNÇÃO PARA MOVER
							 */
							send(reply);
						// caso contrário se o agente se encontra indesponível refusa a proposta 
						} else if (fire.getDisponivel() == false) { 
							ACLMessage reply = msg.createReply(); 
							reply.setPerformative(ACLMessage.REFUSE);  
							try {
								reply.setContentObject(i);
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.out.println("Não estou disponivel!\n");
							send(reply);
						
						// não possui recursos suficientes mas mesmo assim calcula o tempo que demora a abastecer+apagar 
						// e manda para o quartelagent avaliar a proposta
						} else {  
							// retorna posto abastecimento mais perto do agente
							Posicao posto_mais_perto = postoAbastecimentoMaisPerto(QuartelAgent.lista_combustiveis);
							// calcula o tempo de ir abastecer + tempo de se mover para incêndio
							double t = calculaTempo(posto_mais_perto,i.getPosicao()); 
							i.setTempo(t);
							ACLMessage reply = msg.createReply(); 
							try {
								reply.setContentObject(i);
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.out.println("Enviei counter proposal para quartel: " + t);
							send(reply);
						}
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
			}
			
		} 
		
	}
}


