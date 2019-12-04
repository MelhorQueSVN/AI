package Agents;

import java.io.IOException;
import java.util.Random;

import Classes.InfoIncendio;
import Classes.Posicao;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class IncendiarioAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private Posicao p;
	
	public void setup() {
		// cria novo inc�ndio a cada 10 segundos. 
		addBehaviour(new EnviaPosicaoIncendio(this,10000));
	}

	public class EnviaPosicaoIncendio extends TickerBehaviour{
		
		private IncendiarioAgent ia;
		
		public EnviaPosicaoIncendio(Agent a, long period) {
			super(a, period); 
			this.ia = (IncendiarioAgent) a;
		}

		@Override
		protected void onTick() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			// Cria objeto posicao com posi��o aleat�ria do inc�ndio
			Posicao p = new Posicao(); 
			Random rand = new Random();
			int cord_x = (int) rand.nextInt(101); 
			int cord_y = (int) rand.nextInt(101);
			p.setCordX(cord_x); 
			p.setCordY(cord_y); 
			// cria objeto infoinc�ndio para mandar
			InfoIncendio inf = new InfoIncendio(); 
			inf.setPosicao(p);  
			inf.setGravidade(0); 
			try {
				msg.setContentObject(inf);
			} catch (IOException e) {
				e.printStackTrace();
			}  
			// agora pesquisa nas p�ginas amarelas pelo quartel 
			DFAgentDescription template = new DFAgentDescription(); 
			ServiceDescription sd = new ServiceDescription(); 
			sd.setType("quartel"); 
			template.addServices(sd);
			DFAgentDescription[] disponiveis;  
			try {
				disponiveis = DFService.search(myAgent, template); 
				if (disponiveis.length > 0) { 
					msg.addReceiver( disponiveis[0].getName());  
					System.out.println("WADWDWD: " + disponiveis[0].getName());
					/*
					for(int i=0;i<disponiveis.length;i++) { 
						System.out.println(disponiveis[i].getName());
					} 
					*/
					System.out.println("Criei novo inc�ndio na posi��o: " + inf.getPos().getCordX() + " " + inf.getPos().getCordY());
					send(msg);
				} 	
			} catch (FIPAException e) {
				e.printStackTrace();
			} 
			
			/* 
			 * 	ENVIAR PARA A INTERFACE A POSIÇÃO CRIADA DO INCÊNDIO
			 */
			ACLMessage msg_int = new ACLMessage(ACLMessage.INFORM); 
			try {
				msg_int.setContentObject(p);
			} catch (IOException e1) {
				e1.printStackTrace();
			} 

			
			DFAgentDescription template_int = new DFAgentDescription(); 
			ServiceDescription sd_int = new ServiceDescription(); 
			sd_int.setType("interfaceAG"); 
			template_int.addServices(sd_int); 
			DFAgentDescription[] disponiveis_int;   
			try {
				disponiveis_int = DFService.search(myAgent, template_int);
				if (disponiveis_int.length > 0) { 
					msg_int.addReceiver(disponiveis_int[0].getName()); 
					send(msg_int);
				}
			} catch (FIPAException e) {
				e.printStackTrace();
			} 
			
			
		} 
		
	}
}
