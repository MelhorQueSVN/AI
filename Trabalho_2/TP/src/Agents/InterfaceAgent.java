package Agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Classes.Posicao;
import Container.Map;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class InterfaceAgent extends Agent {

	public static HashMap<AID,Posicao> agentes = new HashMap<>();  
	public static List<Posicao> lista_inc = new ArrayList();
	public Map mapa = new Map();
	
	public void setup() { 
		
		// regista nas páginas
		DFAgentDescription dfd = new DFAgentDescription(); 
		dfd.setName(getAID()); 
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType("interfaceAG");  
		sd.setName(getLocalName());
		dfd.addServices(sd); 
		try {
			DFService.register(this,dfd);
		} catch (FIPAException e) {

			e.printStackTrace();
		}
		
		int conta = 0;
		while (conta < 17) {
			ACLMessage msg = receive();  
			if (msg != null) { 
				try {
					Posicao p = (Posicao) msg.getContentObject(); 
					AID sender = msg.getSender(); 
					agentes.put(sender,p); 
					conta++; 
				} catch (UnreadableException e) {
					e.printStackTrace();
				} 
			}
		}	
		
		mapa.updateAgents(agentes);
		//mapa.setVisible(true);
		
		addBehaviour(new RecebePosicoes()); 
		
	}
	 
	public HashMap<AID,Posicao> getHash(){ 
		return this.agentes;
	}
	
	public class RecebePosicoes extends CyclicBehaviour{

		private static final long serialVersionUID = 1L;
		private int conta = 0; 
		
		@Override
		public void action() {  
			ACLMessage msg = receive(); 
			if (msg != null) {  
				if (msg.getSender().getLocalName().equals("incendiario")) { 
					try {
						System.out.println("INTERFACE RECEBI: " + msg.getSender().getLocalName());
						Posicao p = (Posicao) msg.getContentObject(); 
						lista_inc.add(p);  
						System.out.println(" " +lista_inc.toString());
						// update mapa  
						mapa.updateIncendios(lista_inc);
					} catch (UnreadableException e) {
						e.printStackTrace();
					} 
					
				}
			}
		}	
	}
	
}
