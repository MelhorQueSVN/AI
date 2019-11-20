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
		// cria novo incêndio a cada 10 segundos.
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
			// Cria objeto posicao com posição aleatória do incêndio
			Posicao p = new Posicao(); 
			Random rand = new Random();
			int cord_x = (int) rand.nextInt(101); 
			int cord_y = (int) rand.nextInt(101);
			p.setCordX(cord_x); 
			p.setCordY(cord_y); 
			// cria objeto infoincêndio para mandar
			InfoIncendio inf = new InfoIncendio(); 
			inf.setPosicao(p);  
			inf.setGravidade(0); 
			try {
				msg.setContentObject(inf);
			} catch (IOException e) {
				e.printStackTrace();
			}  
			// agora pesquisa nas páginas amarelas pelo quartel 
			DFAgentDescription template = new DFAgentDescription(); 
			ServiceDescription sd = new ServiceDescription(); 
			sd.setType("quartel"); 
			template.addServices(sd);
			DFAgentDescription[] disponiveis;  
			try {
				disponiveis = DFService.search(myAgent, template); 
				if (disponiveis.length > 0) { 
					msg.addReceiver( disponiveis[0].getName()); 
					/*
					for(int i=0;i<disponiveis.length;i++) { 
						System.out.println(disponiveis[i].getName());
					} 
					*/
					System.out.println("Criei novo incêndio na posição: " + inf.getPos().getCordX() + " " + inf.getPos().getCordY());
					send(msg);
				}
			} catch (FIPAException e) {
				e.printStackTrace();
			}
			
		} 
		
	}
}
