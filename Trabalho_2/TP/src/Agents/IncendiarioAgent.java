package Agents;

import java.util.Random;

import Classes.Posicao;
import jade.core.Agent;

public class IncendiarioAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private Posicao p;
	
	public void setup() {
		/*	
		 * 	Incendiário começa numa posição aleatória do mapa de [0..100] 
		 */
		this.p = new Posicao(); 
		Random rand = new Random();
		int cord_x = (int) rand.nextInt(101); 
		int cord_y = (int) rand.nextInt(101);
		p.setCordX(cord_x); 
		p.setCordY(cord_y); 
		
		System.out.println("Incendiario " + this.getLocalName() + " começou na posicao ( " + cord_x + " , " + cord_y + " )");
	}
}
