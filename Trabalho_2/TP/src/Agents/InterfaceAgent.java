package Agents;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import Classes.Posicao;
import Container.Mapa;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class InterfaceAgent extends Agent {

    public static HashMap<AID,Posicao> agentes = new HashMap<>();
    public static List<Posicao> lista_inc = new ArrayList();
    public Mapa mapa = new Mapa(); 
    public int conta_inc_apagados = 0; 
    public List<String> agentes_apagaram = new ArrayList();  
    public String[] ags_unicos;
    public long time_now; 
    public long time_after; 
    public long elapsed_time; 
    public List<Long> tempos_apagar_incendios = new ArrayList<>();

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
        
        addBehaviour(new RecebePosicoes()); 
        addBehaviour(new ShowStats(this,20000));

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
                    	// guarda o tempo inicial quando é criado um incêndio
                        time_now = System.currentTimeMillis();
                        Posicao p = (Posicao) msg.getContentObject();
                        lista_inc.add(p);
                        mapa.escondeIncendios(lista_inc);
                        // update mapa
                        mapa.updateIncendios(lista_inc);
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }

                }
                else if(msg.getPerformative() == ACLMessage.PROPOSE){
                    
                	// Para efeitos estatísticos do sistema.
                	time_after = System.currentTimeMillis(); 
                    tempos_apagar_incendios.add(time_after - time_now); 
                    time_now = 0; 
                    time_after = 0;
                	// contador do número de incêndios apagados 
                	conta_inc_apagados++;
                	agentes_apagaram.add(msg.getSender().getLocalName()); 
                	
                	Posicao p = null;
                    try {
                        p = (Posicao) msg.getContentObject();
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                    mapa.escondeIncendios(lista_inc);

                    for (Iterator<Posicao> it = lista_inc.iterator(); it.hasNext(); ) {

                        Posicao pos = it.next();
                        if (pos.equals(p)){
                            it.remove();
                        }
                    }

                    mapa.updateIncendios(lista_inc);
                }

                else{
                    try {
                        Posicao p = (Posicao) msg.getContentObject();
                        AID sender = msg.getSender();
                        mapa.escondeAgentes(agentes);
                        agentes.put(sender,p);
                        // update mapa
                        mapa.updateAgents(agentes);
                    } catch (UnreadableException e) {}
                }
            }
        }
    }

    public class ShowStats extends TickerBehaviour{

		private static final long serialVersionUID = 1L;

		public ShowStats(Agent a, long period) {
			super(a, period);
		}
		
		private String getMostFrequent(List<String> a) { 
			Map<String,Long> counts = a.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting())); 
			System.out.println("Agentes que apagaram incêndios: " + counts.toString());
			String s = counts.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
			return s;
		} 
		
		private double mediaTempos(List<Long> tempos_apagar_incendios) { 
			long tam = tempos_apagar_incendios.size(); 
			long sum = 0;
			for (Long a : tempos_apagar_incendios) { 
				sum += a;
			}
			return (sum / tam);	
		} 
		
		private double round( double val )
		{
		    if( val < 0 ) return (val - 0.5);
		    return (val + 0.5);
		}
		
		private double removeDuplicates(){
			String[] stringArray = agentes_apagaram.toArray(new String[0]);
			Set<String> uniqueWords = new HashSet<String>(Arrays.asList(stringArray));  
			double size_set = uniqueWords.size(); 
			double value = size_set/17;  
			// para adicionar mais casas decimais base adicionar zeros nos números 1...d
			double value_rounded = 100 * ((double)Math.round(value * 10000d) / 10000d);
			return value_rounded;
		} 
		
		@Override
		protected void onTick() { 
			System.out.println("\n***************************** STATS *****************************\n"); 
			System.out.println("Incêndios apagados: " + conta_inc_apagados); 
			System.out.println("Agente que apagou mais incêndios: " + getMostFrequent(agentes_apagaram)); 
			System.out.println("Tempos entre surgimento e extinção do incêndio: " + tempos_apagar_incendios.toString()); 
			System.out.println("Média dos tempos: " + mediaTempos(tempos_apagar_incendios));
			System.out.println("Percentagem de agentes utilizados: " + removeDuplicates() + "%");
			System.out.println("\n*****************************************************************\n"); 
		} 
    	
    }
    
    
}