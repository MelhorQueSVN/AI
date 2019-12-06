package Agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Classes.InfoEscolhido;
import Classes.Informacao;
import Classes.Posicao;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
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
	private double cap_max_comb;
	// capacidade atual de �gua
	private int cap_atual_agua;
	// capacidade atual de combust�vel
	private double cap_atual_comb;
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
		int cord_x = (int) rand.nextInt(99);
		int cord_y = (int) rand.nextInt(99);
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

		addBehaviour(new EnviaPosicaoInicial(this));
		addBehaviour(new RecebeMSG(this));


	}

	/*
	 * 	GETS E SETS
	 */
	public void setCapMaxAgua(int cap) { this.cap_max_agua = cap; }
	public void setCapMaxComb(double cap) {this.cap_max_comb = cap;}
	public void setCapAtAgua(int cap) {this.cap_atual_agua = cap;}
	public void setCapAtComb(double cap) {this.cap_atual_comb = cap;}
	public void setCapVel(int vel) {this.velocidade = vel;}
	public void setDisponivel(boolean disp) {this.disponivel = disp;}

	public int getCapMaxAgua() {return this.cap_max_agua;}
	public double getCapMaxComb() {return this.cap_max_comb;}
	public int getCapAtAgua() {return this.cap_atual_agua;}
	public double getCapAtComb() {return this.cap_atual_comb;}
	public int getVelocidade() {return this.velocidade;}
	public Posicao getPosicao() {return this.p;}
	public boolean getDisponivel() {return this.disponivel;}


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
			double dist_cima = Math.sqrt(Math.pow((posicao_fire.getCordX() - destino.getCordX()), 2)
					+ Math.pow(((posicao_fire.getCordY() + 1) - destino.getCordY()), 2));

			// para baixo
			double dist_baixo = Math.sqrt(Math.pow((posicao_fire.getCordX() - destino.getCordX()), 2)
					+ Math.pow(((posicao_fire.getCordY() - 1) - destino.getCordY()), 2));

			// para direita
			double dist_direita = Math.sqrt(Math.pow(((posicao_fire.getCordX() + 1) - destino.getCordX()), 2)
					+ Math.pow((posicao_fire.getCordY() - destino.getCordY()), 2));

			// para esquerda
			double dist_esquerda = Math.sqrt(Math.pow(((posicao_fire.getCordX() - 1) - destino.getCordX()), 2)
					+ Math.pow((posicao_fire.getCordY() - destino.getCordY()), 2));

			distancias.add(dist_cima);
			distancias.add(dist_baixo);
			distancias.add(dist_direita);
			distancias.add(dist_esquerda);

			Collections.sort(distancias);

			if (distancias.get(0) == dist_cima) {
				i = 0;
				posicao_fire.setCordY(posicao_fire.getCordY() + 1);
				distancias.clear();
				indexes.add(i);
			} else if (distancias.get(0) == dist_baixo) {
				i = 1;
				posicao_fire.setCordY(posicao_fire.getCordY() - 1);
				// faz clear
				distancias.clear();
				indexes.add(i);
			} else if (distancias.get(0) == dist_direita) {
				i = 2;
				posicao_fire.setCordX(posicao_fire.getCordX() + 1);
				// faz clear
				distancias.clear();
				;
				indexes.add(i);
			} else if (distancias.get(0) == dist_esquerda) {
				i = 3;
				posicao_fire.setCordX(posicao_fire.getCordX() - 1);
				// faz clear
				distancias.clear();
				;
				indexes.add(i);
			}
		}

		return indexes;
	}



	// função que dada a posição do incêndio verifica se possui o combustível necessário para chegar
	// a essa dada posição.
	public boolean verificaCombustivel(Posicao p) {
		Posicao pos_agent = this.getPosicao();
		// gasta 0.1 de combustível por cada movimento
		float gasto_comb = (float) 0.1;
		ArrayList<Integer> cam = calculaProximo(p,this.getPosicao());
		double comb_necessario = cam.size() * gasto_comb; 	// multiplica o numero de casas pelo gasto
		if (comb_necessario > this.getCapAtComb()) {
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
			cam = calculaProximo(p,this.getPosicao());
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
		double dist_ag_abastecer = calculaProximo(p,this.getPosicao()).size();
		double dist_ab_inc = calculaProximo(p,inc).size();
		double dist_total = dist_ag_abastecer + dist_ab_inc;
		return (dist_total)/(this.getVelocidade());
	}

	// função que verifica se o firefighter necessita de abastecer ou não após apagar incêndio
	public boolean verificaAbastecerCombustivel() {
		double threshold = 0.5;
		System.out.println("calculo comb: " + (this.getCapMaxComb()));
		if ( ((this.getCapAtComb())/(this.getCapMaxComb())) < threshold) {
			return true;
		} else {
			return false;
		}
	}

	public class RecebeMSG extends CyclicBehaviour{

		private FirefightersAgent f;

		public RecebeMSG(FirefightersAgent fr) {
			this.f = fr;
		}

		@Override
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.REQUEST) {
					f.addBehaviour(new RecebePedidos(this.f,msg));
				} else if (msg.getPerformative() == ACLMessage.PROPOSE) {
					f.addBehaviour(new RecebeProposta(this.f,msg));
				}
			}
		}
	}

	public class RecebePedidos extends OneShotBehaviour{

		private FirefightersAgent fire;
		private ACLMessage msg;

		public RecebePedidos(FirefightersAgent f, ACLMessage m) {
			this.fire = f;
			this.msg = m;
		}

		@Override
		public void action() {
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
			send(reply);

		}
	}

	public class RecebeProposta extends OneShotBehaviour{

		private FirefightersAgent fire;
		private ACLMessage msg;

		public RecebeProposta(FirefightersAgent f,ACLMessage m) {
			this.fire = f;
			this.msg = m;
		}

		public void action() {

			try {
				InfoEscolhido i = (InfoEscolhido) msg.getContentObject();
				Posicao inc = i.getPosicao();
				double tempo_a_chegar = i.getTempo();
				boolean possui = verificaCombustivel(inc);
				System.out.println("Sou o bombeiro: " + fire.getLocalName() + " e tenho disp: " + fire.getDisponivel());
				if (fire.getDisponivel() == false) {
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.REFUSE);
					try {
						reply.setContentObject(i);
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("Não estou disponivel!\n");
					send(reply);
				}
				// caso possua recuros suficientes para apagar o incêndio move-se para esse incêndio
				if (possui == true && fire.getCapAtAgua() > 0 && fire.getDisponivel() == true) {
					fire.setDisponivel(false);
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					System.out.println("Tenho combustivel e água suficiente, e aceitei a proposta\n");
					try {
						reply.setContentObject(i);
					} catch (IOException e) {
						e.printStackTrace();
					}
					send(reply);

					//System.out.println("Cheguei aqui, e tenho posicao " + fire.getPosicao().getCordX() + " " + fire.getPosicao().getCordY());
					ArrayList<Integer> caminho_ind = calculaProximo(inc,fire.getPosicao());
					addBehaviour(new MovimentaApaga(fire, caminho_ind, inc));

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

	public class EnviaPosicaoInicial extends OneShotBehaviour{

		private FirefightersAgent f;

		public EnviaPosicaoInicial(FirefightersAgent f) {
			this.f = f;
		}

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);

			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("interfaceAG");
			template.addServices(sd);
			DFAgentDescription[] disponiveis;
			try {
				disponiveis = DFService.search(this.myAgent, template);
				if (disponiveis.length > 0) {
					msg.addReceiver(disponiveis[0].getName());
				}
			} catch (FIPAException e1) {
				e1.printStackTrace();
			}

			try {
				msg.setContentObject(f.getPosicao());
			} catch (IOException e) {
				e.printStackTrace();
			}
			send(msg);

		}

	}


	public class MovimentaApaga extends OneShotBehaviour{

		private FirefightersAgent fire;
		private ArrayList<Integer> caminho;
		private Posicao inc;

		public MovimentaApaga(FirefightersAgent f, ArrayList<Integer> caminho, Posicao inc){
			this.fire = f;
			this.caminho = caminho;
			this.inc = inc;
		}

		public void action(){
			for (int j : this.caminho) {
				switch (j) {
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

				// simula o movimento
				try {
					int velocidade_agente = fire.getVelocidade();
					Thread.sleep(500/velocidade_agente);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//enviar mensagem à interface com a nova posição
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("interfaceAG");
				template.addServices(sd);
				DFAgentDescription[] disponiveis;
				try {
					disponiveis = DFService.search(this.myAgent, template);
					if (disponiveis.length > 0) {
						msg.addReceiver(disponiveis[0].getName());
					}
				} catch (FIPAException e1) {
					e1.printStackTrace();
				}

				try {
					msg.setContentObject(fire.getPosicao());
				} catch (IOException e) {
					e.printStackTrace();
				}
				send(msg);


				fire.setCapAtComb((float) (fire.getCapAtComb()-0.1));



			}
			// simula o tempo de apagar o incendio
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// decrementa 1 unidade de água
			fire.setCapAtAgua(fire.getCapAtAgua()-1);

			System.out.println("Apaguei incêndio!\n");

			//manda mensagem a avisar que apagou o incendio

			ACLMessage msg2 = new ACLMessage(ACLMessage.PROPOSE);

			DFAgentDescription template2 = new DFAgentDescription();
			ServiceDescription sd2 = new ServiceDescription();
			sd2.setType("interfaceAG");
			template2.addServices(sd2);
			DFAgentDescription[] disponiveis2;
			try {
				disponiveis2 = DFService.search(this.myAgent, template2);
				if (disponiveis2.length > 0) {
					msg2.addReceiver(disponiveis2[0].getName());
				}
			} catch (FIPAException e1) {
				e1.printStackTrace();
			}

			try {
				msg2.setContentObject(inc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			send(msg2);


			boolean necessita_abastecer = verificaAbastecerCombustivel();
			if (necessita_abastecer == true) {
				System.out.println("Vou abastecer e tenho comb: " + fire.getCapAtComb());
				Posicao posto = postoAbastecimentoMaisPerto(QuartelAgent.lista_combustiveis);
				ArrayList<Integer> caminho_ab = calculaProximo(posto,fire.getPosicao());
				for (int j : caminho_ab) {
					switch (j) {
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

					// simula o movimento
					try {
						int velocidade_agente = fire.getVelocidade();
						Thread.sleep(500/velocidade_agente);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					//enviar mensagem à interface com a nova posição
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("interfaceAG");
					template.addServices(sd);
					DFAgentDescription[] disponiveis;
					try {
						disponiveis = DFService.search(this.myAgent, template);
						if (disponiveis.length > 0) {
							msg.addReceiver(disponiveis[0].getName());
						}
					} catch (FIPAException e1) {
						e1.printStackTrace();
					}

					try {
						msg.setContentObject(fire.getPosicao());
					} catch (IOException e) {
						e.printStackTrace();
					}
					send(msg);

				}


				// demora 1 segundo a abastecer
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// e abastece o combustivel
				fire.setCapAtComb(fire.getCapMaxComb());
				System.out.println("Abasteci e tenho combustivel: " + fire.getCapAtComb());
			}

			// verifica capacidade atual de água
			if (fire.getCapAtAgua() == 0) {
				Posicao posto_ab = postoAbastecimentoMaisPerto(QuartelAgent.lista_aguas);
				System.out.println("Vou abastecer e tenho agua: " + fire.getCapAtAgua());
				ArrayList<Integer> caminho_ab = calculaProximo(posto_ab,fire.getPosicao());
				for (int j : caminho_ab) {
					switch (j) {
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

					// simula o movimento
					try {
						int velocidade_agente = fire.getVelocidade();
						Thread.sleep(500/velocidade_agente);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					//enviar mensagem à interface com a nova posição
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("interfaceAG");
					template.addServices(sd);
					DFAgentDescription[] disponiveis;
					try {
						disponiveis = DFService.search(this.myAgent, template);
						if (disponiveis.length > 0) {
							msg.addReceiver(disponiveis[0].getName());
						}
					} catch (FIPAException e1) {
						e1.printStackTrace();
					}

					try {
						msg.setContentObject(fire.getPosicao());
					} catch (IOException e) {
						e.printStackTrace();
					}
					send(msg);

				}
				// demora 1 segundo a abastecer
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				fire.setCapAtAgua(fire.getCapMaxAgua());
				System.out.println("Abasteci e tenho capacidade: " + fire.getCapAtAgua());
			}

			fire.setDisponivel(true);
		}
	}






}