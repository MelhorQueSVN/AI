package Agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import Classes.InfoEscolhido;
import Classes.InfoIncendio;
import Classes.Informacao;
import Classes.Posicao;
import Container.Map;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class QuartelAgent extends Agent {

	private static final long serialVersionUID = 1L;

	private HashMap<AID,Informacao> infoAgentes; 
	private List<InfoIncendio> infoIncendios;  
	public static List<Posicao> lista_combustiveis; 
	public static List<Posicao> lista_aguas; 
	public Map inter;
	
	public void setup() { 
		
		this.infoAgentes = new HashMap<>(); 
		this.infoIncendios = new ArrayList<>();  
		
		// regista agente nas p�ginas amarelas
		DFAgentDescription dfd = new DFAgentDescription(); 
		dfd.setName(getAID()); 
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType("quartel");
		sd.setName(getLocalName()); 
		dfd.addServices(sd); 
		try {
			DFService.register(this,dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}	 
		
		lista_combustiveis = new ArrayList<>(); 
		lista_aguas = new ArrayList<>();
		
		// declaração dos métodos que adicionam posições dos pontos de combustível e abastecimentos água
		addPosicoesComb(); 
		addPosicoesAgua(); 
	
		addBehaviour(new RecebePedidosInc(this)); 
	} 
	
	public void addPosicoesComb() { 
		Posicao c_p1 = new Posicao(10,12); 
		Posicao c_p2 = new Posicao(60,30); 
		Posicao c_p3 = new Posicao(30,47); 
		Posicao c_p4 = new Posicao(78,63); 
		Posicao c_p5 = new Posicao(90,14);
		lista_combustiveis.add(c_p1); 
		lista_combustiveis.add(c_p2); 
		lista_combustiveis.add(c_p3); 
		lista_combustiveis.add(c_p4); 
		lista_combustiveis.add(c_p5);
	} 
	
	public void addPosicoesAgua() {
		Posicao a_p1 = new Posicao(12,50); 
		Posicao a_p2 = new Posicao(73,13); 
		Posicao a_p3 = new Posicao(84,41); 
		Posicao a_p4 = new Posicao(30,75); 
		Posicao a_p5 = new Posicao(75,90); 
		lista_aguas.add(a_p1); 
		lista_aguas.add(a_p2); 
		lista_aguas.add(a_p3); 
		lista_aguas.add(a_p4); 
		lista_aguas.add(a_p5);
	}
	
	public List<InfoIncendio> getInfoIncendios(){ 
		return this.infoIncendios;
	} 
	
	public HashMap<AID,Informacao> getInfoAgentes(){ 
		return this.infoAgentes;
	}
	
	
	public class RecebePedidosInc extends CyclicBehaviour{
		
		private QuartelAgent quartel; 
		private Posicao inc_atual;
		private int recebidos = 0; 
		private int num_agents = 17;
		
		public RecebePedidosInc(QuartelAgent q) { 
			this.quartel = q;
		} 
		
		// dentro da hashmap e dada a posi��o do inc�ndio retorna o AID do agente mais r�pido para chegar ao inc�ndio
		public InfoEscolhido mais_rapido(HashMap<AID,Informacao> agentes, Posicao p) {
			AID atual; 
			AID min; 
			double dist_atual;  
			double rapido_atual, rapido_min; 
			rapido_min = 1000;
			min = null; 
			InfoEscolhido i = new InfoEscolhido();
			for(Entry<AID,Informacao> entry : agentes.entrySet()) { 
				dist_atual =  Math.sqrt(Math.pow((p.getCordX() - entry.getValue().getPos().getCordX()),2) 
							+ Math.pow((p.getCordY() - entry.getValue().getPos().getCordY()),2));  
				rapido_atual = (dist_atual) / (entry.getValue().getVelocidade());  
				atual = entry.getKey();
				if (rapido_atual < rapido_min) { 
					rapido_min = rapido_atual; 
					min = atual; 
				}
			} 
			i.setPosicao(p);
			i.setAgente(min); 
			i.setTempo(rapido_min);
			System.out.println("Agente mais r�pido: " + min);
			return i;
		}
		
		@Override
		public void action() {
			ACLMessage msg = receive(); 
			if (msg != null) { 
				// recebe informa��e do incendi�rio e manda propostas para os firefighters
				if (msg.getPerformative() == ACLMessage.INFORM) { 
					// instancia objeto infoinc�ndio e coloca no array de inc�ndios
					//InfoIncendio inf =  new InfoIncendio();
					try {
						InfoIncendio inf = (InfoIncendio) msg.getContentObject();
						quartel.getInfoIncendios().add(inf);  
						inc_atual = inf.getPos();
						System.out.println("Recebi mensagem de inc�ndio com posi��o: " + inf.getPos().getCordX() + " " + inf.getPos().getCordY());
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					// retira a lista de inc�ndios do quartel e adiciona o inc�ndio que recebeu do incendi�rio
					//quartel.getInfoIncendios().add(inf); 
					//System.out.println("Recebi mensagem de inc�ndio com posi��o: " + inf.getPos().getCordX() + " " + inf.getPos().getCordY());
					// agora pesquisa nas p�ginas amarelas pelos firefighters 
					DFAgentDescription template = new DFAgentDescription(); 
					ServiceDescription sd = new ServiceDescription(); 
					sd.setType("firefighter"); 
					DFAgentDescription[] disponiveis; 
					
					// adiciona parallelbehaviour para mandar para os firefighters 
					ParallelBehaviour b = new ParallelBehaviour(quartel, ParallelBehaviour.WHEN_ALL) {
						private static final long serialVersionUID = 1L;
					};					
					quartel.addBehaviour(b);

					try {
						disponiveis = DFService.search(this.myAgent, template);
						for (int i = 0; i<disponiveis.length ; i++) { 
							AID fire = disponiveis[i].getName(); 
							b.addSubBehaviour(new QuestionaFirefighter(fire));
						}
					} catch (FIPAException e) {

						e.printStackTrace();
					} 					
				// recebe resposta dos firefighters
				} else if (msg.getPerformative() == ACLMessage.PROPOSE) { 
					AID fire = msg.getSender(); 
					Posicao inc_a_apagar = inc_atual; 
					InfoEscolhido inf = new InfoEscolhido(); 
					try {
						Informacao i = (Informacao) msg.getContentObject(); 
						// atualiza o hashmap dos agentes.
						quartel.getInfoAgentes().put(fire,i);
						recebidos++; 
						// espera que receba todos os proposals dos firefighters
						if (recebidos == num_agents) {  
							System.out.println("Recebi de todos os agentes " + recebidos);
							inf = mais_rapido(quartel.getInfoAgentes(),inc_a_apagar); 
							ACLMessage msg_escolhido = new ACLMessage(ACLMessage.PROPOSE);  
							msg_escolhido.addReceiver(inf.getAgente());
							try {
								msg_escolhido.setContentObject(inf); 
							} catch (IOException e) {
								e.printStackTrace();
							} 
							System.out.println("Enviada mensagem para agente mais rápido " + inf.getAgente());
							send(msg_escolhido);
							// faz reset do recebidos;
							recebidos = 0;
						}
					
					} catch (UnreadableException e) {
						e.printStackTrace();
					} 
				// se receber um refuse da proposta no firefighteragents
				} else if (msg.getPerformative() == ACLMessage.REFUSE) { 
					try { 
						InfoEscolhido inf = (InfoEscolhido) msg.getContentObject(); 
						AID recebido = inf.getAgente(); 
						// remove a instância deste da agente da HashMap, uma vez que deu refuse
						infoAgentes.remove(recebido); 
						InfoEscolhido mais_rapido = mais_rapido(quartel.getInfoAgentes(),inf.getPosicao());  
						System.out.println("Escolhi o segundo mais rápido!\n"); 
						ACLMessage msg_segundo_escolhido = new ACLMessage(ACLMessage.PROPOSE); 
						msg_segundo_escolhido.addReceiver(mais_rapido.getAgente()); 
						try {
							msg_segundo_escolhido.setContentObject(mais_rapido);
						} catch (IOException e) {
							e.printStackTrace();
						} 
						send(msg_segundo_escolhido); 
					} catch (UnreadableException e) {
						e.printStackTrace();
					} 
				// testa se recebeu uma call for proposal do firefighteragents
				} else if (msg.getPerformative() == ACLMessage.CFP) { 
					InfoEscolhido inf;
					try {
						inf = (InfoEscolhido) msg.getContentObject();
						AID recebido = inf.getAgente(); 
						double t = inf.getTempo(); 
						infoAgentes.remove(recebido); 
						InfoEscolhido mais_rapido = mais_rapido(quartel.getInfoAgentes(),inf.getPosicao());   
						double segundo_tempo = mais_rapido.getTempo(); 
						// se o tempo recebido continua a ser menor então aceita o CFP enviado 
						if (t < segundo_tempo) { 
							ACLMessage reply = msg.createReply(); 
							reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL); 
							try {
								reply.setContentObject(inf);
							} catch (IOException e) {
								e.printStackTrace();
							}
							send(reply);
					// envia mensagem para segundo mais rápido se o tempo recebido for mais lento
						} else { 
							AID segundo_mais_rapido = mais_rapido.getAgente(); 
							ACLMessage new_msg = new ACLMessage(); 
							new_msg.setPerformative(ACLMessage.PROPOSE); 
							new_msg.addReceiver(segundo_mais_rapido); 
							try {
								new_msg.setContentObject(mais_rapido);
							} catch (IOException e) {
								e.printStackTrace();
							}
							send(new_msg);
						}
					
					} catch (UnreadableException e) {
						e.printStackTrace();
					} 
			}
		} 
		
	}
	
	public class QuestionaFirefighter extends SimpleBehaviour{

		private static final long serialVersionUID = 1L;
		private AID dest; 
		private boolean finished = false;
		
		public QuestionaFirefighter(AID dest) { 
			this.dest = dest;
		}
		
		public void action() {
			ACLMessage msgFire = new ACLMessage(ACLMessage.REQUEST); 
			msgFire.addReceiver(dest);
			msgFire.setContent("");  
			send(msgFire); 
			this.finished = true;
		}

		@Override
		public boolean done() {		
			return finished;
		} 
		
	}
	}  
}
