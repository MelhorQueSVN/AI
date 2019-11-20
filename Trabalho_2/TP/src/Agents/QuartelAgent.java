package Agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import Classes.InfoIncendio;
import Classes.Informacao;
import Classes.Posicao;
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
	
	public void setup() { 
		
		this.infoAgentes = new HashMap<>(); 
		this.infoIncendios = new ArrayList<>();  
		
		// regista agente nas páginas amarelas
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
	
		addBehaviour(new RecebePedidosInc(this)); 
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
		
		// dentro da hashmap e dada a posição do incêndio retorna o AID do agente mais rápido para chegar ao incêndio
		public AID mais_rapido(HashMap<AID,Informacao> agentes, Posicao p) {
			AID atual; 
			AID min; 
			double dist_atual;  
			double rapido_atual, rapido_min; 
			rapido_min = 1000;
			min = null;
			for(Entry<AID,Informacao> entry : agentes.entrySet()) { 
				dist_atual =  Math.sqrt(Math.pow((p.getCordX() - entry.getValue().getPos().getCordX()),2) 
							+ Math.pow((p.getCordY() - entry.getValue().getPos().getCordY()),2));  
				System.out.println("DISTÂNCIA ENTRE AGENTE E FOGO: " + dist_atual);
				rapido_atual = (dist_atual) / (entry.getValue().getVelocidade());  
				System.out.println("rapido_atual " + rapido_atual);
				atual = entry.getKey();
				if (rapido_atual < rapido_min) { 
					rapido_min = rapido_atual; 
					min = atual; 
					System.out.println("AGENTE MIN: " + min);
				}
			}
			System.out.println("Agente mais rápido: " + min);
			return min;
		}
		
		@Override
		public void action() {
			ACLMessage msg = receive(); 
			if (msg != null) { 
				// recebe informaçõe do incendiário e manda propostas para os firefighters
				if (msg.getPerformative() == ACLMessage.INFORM) { 
					// instancia objeto infoincêndio e coloca no array de incêndios
					//InfoIncendio inf =  new InfoIncendio();
					try {
						InfoIncendio inf = (InfoIncendio) msg.getContentObject();
						quartel.getInfoIncendios().add(inf);  
						inc_atual = inf.getPos();
						System.out.println("Recebi mensagem de incêndio com posição: " + inf.getPos().getCordX() + " " + inf.getPos().getCordY());
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					// retira a lista de incêndios do quartel e adiciona o incêndio que recebeu do incendiário
					//quartel.getInfoIncendios().add(inf); 
					//System.out.println("Recebi mensagem de incêndio com posição: " + inf.getPos().getCordX() + " " + inf.getPos().getCordY());
					// agora pesquisa nas páginas amarelas pelos firefighters 
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
					try {
						Informacao i = (Informacao) msg.getContentObject(); 
						quartel.getInfoAgentes().put(fire,i);
						recebidos++; 
						// espera que receba todos os proposals dos firefighters
						if (recebidos == num_agents) { 
							AID escolhido = mais_rapido(quartel.getInfoAgentes(),inc_a_apagar); 
							ACLMessage msg_escolhido = new ACLMessage(ACLMessage.PROPOSE);  
							msg_escolhido.addReceiver(escolhido);
							try {
								msg_escolhido.setContentObject(inc_a_apagar); 
							} catch (IOException e) {
								e.printStackTrace();
							} 
							System.out.println("Enviada mensagem para agente mais rápido " + escolhido);
							send(msg_escolhido);
						}
					
					} catch (UnreadableException e) {
						e.printStackTrace();
					} 
					
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
			System.out.println("Mandei mensagem para firefighters!\n");
			send(msgFire); 
			this.finished = true;
		}

		@Override
		public boolean done() {		
			return finished;
		} 
		
	}
	
}
