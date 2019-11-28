package Agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

public class FirefightersAgent extends Agent implements Cloneable{
	
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
		this.disponivel = true; 
		
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
	public boolean abasteceCombustivel() throws InterruptedException { 
		// obtem a lista de combustiveis do quartelagent.
		List<Posicao> lista_combustiveis = QuartelAgent.lista_combustiveis; 
		boolean verificar = false;
		for(Posicao p : lista_combustiveis) { 
			if (p.getCordX() == this.p.getCordX() && p.getCordY() == this.p.getCordY()) {
				this.cap_atual_comb = this.cap_max_comb; 
				verificar = true;
				// simula o tempo de abastecimento
				Thread.sleep(1000);
			}
		}
		return verificar;
	} 
	
	// função com o intuito de abastecer a água.
	public boolean abasteceAgua() throws InterruptedException { 
		// obtem a lista de abastecimento de agua do quartelagent
		List<Posicao> lista_agua = QuartelAgent.lista_aguas;  
		boolean verificar = false;
		for(Posicao p : lista_agua) { 
			if (p.getCordX() == this.p.getCordX() && p.getCordY() == this.p.getCordY()) { 
				this.cap_atual_comb = this.cap_max_comb; 
				verificar = true;
				// simula o tempo de abastecimento
				Thread.sleep(1000); 
			}
		}
		return verificar;
	}   
		
	/* 
	 * 	Retorna o index corresponde ao caminho mais curto, testando as 4 opções diferentes de modo 
	 * 	a chegar ao destino, o index irá ter os seguintes significados: 
	 * 		0 -> cima 
	 * 		1 -> baixo 
	 * 		2 -> direita 
	 * 		3 -> esquerda 
	 * 	Estas transformações são de seguidas aplicadas no firefightersagents.
	 */
	public ArrayList<Integer> calculaProximo(Posicao destino,Posicao inicio) { 
		ArrayList<Double> distancias = new ArrayList<>();
		ArrayList<Integer> indexes = new ArrayList<>();

		Posicao posicao_fire = new Posicao(); 
		try {
			posicao_fire = (Posicao) inicio.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		int i = 0;
		while (posicao_fire.getCordX() != destino.getCordX() && posicao_fire.getCordY() != destino.getCordY()) {
			// para cima 
			double dist_cima = Math.sqrt(Math.pow(( posicao_fire.getCordX() - destino.getCordX()),2) 
						+ Math.pow(( (posicao_fire.getCordY()+1) - destino.getCordY()),2));  
			
			// para baixo
			double dist_baixo = Math.sqrt(Math.pow(( posicao_fire.getCordX() - destino.getCordX()),2) 
		   				+ Math.pow(( (posicao_fire.getCordY()-1) - destino.getCordY()),2));  
			
			// para direita 
			double dist_direita = Math.sqrt(Math.pow(( (posicao_fire.getCordX()+1) - destino.getCordX()),2) 
		   				+ Math.pow((posicao_fire.getCordY() - destino.getCordY()),2));   
			
			// para esquerda 
			double dist_esquerda = Math.sqrt(Math.pow(( (posicao_fire.getCordX()-1) - destino.getCordX()),2) 
		   				+ Math.pow((posicao_fire.getCordY() - destino.getCordY()),2));   
			
			distancias.add(dist_cima); 
			distancias.add(dist_baixo); 
			distancias.add(dist_direita); 
		    distancias.add(dist_esquerda); 				
			
		    Collections.sort(distancias);

			if (distancias.get(0) == dist_cima) { 
				i = 0; 
				System.out.println("i cima " + i); 
				posicao_fire.setCordY(posicao_fire.getCordY()+1);
				distancias.clear();
				indexes.add(i);
			} 
			else if (distancias.get(0) == dist_baixo) {
				i = 1;   
				System.out.println("i baixo " + i);  
				posicao_fire.setCordY(posicao_fire.getCordY()-1);
				// faz clear
				distancias.clear();
				indexes.add(i);
			}
			else if (distancias.get(0) == dist_direita) { 
				i = 2;  
				System.out.println("i dir " + i);  
				posicao_fire.setCordX(posicao_fire.getCordX()+1);
				// faz clear
				distancias.clear();;
				indexes.add(i);
			}
			else if (distancias.get(0) == dist_esquerda) { 
				i = 3;  
				System.out.println("i esq " + i);  
				posicao_fire.setCordX(posicao_fire.getCordX()-1);
				// faz clear
				distancias.clear();;
				indexes.add(i);
			}
		}  
		
		return indexes;
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
			ArrayList<Integer> cam = calculaProximo(p,fire.getPosicao()); 
			double comb_necessario = cam.size() * gasto_comb; 	// multiplica o numero de casas pelo gasto
			if (comb_necessario > fire.getCapAtComb()) { 
				return false;
			} else { 
				return true;
			}
		} 
		
		// retorna posição do posto de abastecimento + próximo
		public Posicao postoAbastecimentoMaisPerto(List<Posicao> abastecimentos) { 
			Posicao pos_min = new Posicao(); 
			int size_min = 1000;
			int size_at = 0;
			ArrayList<Integer> cam = new ArrayList<>();
			for (Posicao p : abastecimentos) { 
				cam = calculaProximo(p,fire.getPosicao()); 
				size_at = cam.size(); 
				if (size_at < size_min) { 
					size_min = size_at; 
					pos_min = p;
				}
			}
			return pos_min;
		}
		
		// calcula o tempo que demora a abastecer e a chegar ao incêndio
		public double calculaTempo( Posicao p, Posicao inc) { 
			double dist_ag_abastecer = calculaProximo(p,fire.getPosicao()).size();
			double dist_ab_inc = calculaProximo(p,inc).size();
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
							send(reply);
			
							fire.setDisponivel(false); 
							System.out.println("Cheguei aqui, e tenho posicao " + fire.getPosicao().getCordX() + " " + fire.getPosicao().getCordY());
							ArrayList<Integer> caminho_ind = calculaProximo(inc,fire.getPosicao()); 
							System.out.println("Firefigheer escolhido after cam: " 
											 + fire.getPosicao().getCordX() + " " + fire.getPosicao().getCordY());
							for (int indice : caminho_ind) { 
								switch (indice) { 
									case 0 : fire.getPosicao().setCordY(fire.getPosicao().getCordY()+1);  
											 break; 
									case 1 : fire.getPosicao().setCordY(fire.getPosicao().getCordY()-1);  
											 break; 
									case 2 : fire.getPosicao().setCordX(fire.getPosicao().getCordX()+1); 
											 break; 
									case 3 : fire.getPosicao().setCordX(fire.getPosicao().getCordX()-1);  
											 break; 
									
									default : break;
								}
								
								System.out.println(" " + fire.getLocalName() + " ( " + fire.getPosicao().getCordX() 
												 + " , " + fire.getPosicao().getCordY() + " )");
								
								try {
									int velocidade_agente = fire.getVelocidade();
									Thread.sleep(500/velocidade_agente);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								
								fire.setCapAtComb((float) (fire.getCapAtComb()-0.1));
								System.out.println("Combustivel agente: " + fire.getCapAtComb());
							}
							
							
							// demora 1 segundo a apagar o incêndio
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							System.out.println("Apaguei incêndio!\n");
							fire.setDisponivel(true);
						
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
							// envia uma CALL FOR PROPOSAL para quartelagent.
							reply.setPerformative(ACLMessage.CFP);
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


