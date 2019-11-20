package Agents;

public class DroneAgent extends FirefightersAgent{

	private static final long serialVersionUID = 1L;
	
	public void setup() {
		super.setup();
		// aeronave tem 2 capacidade max de água 
		setCapMaxAgua(2); 
		// aeronave tem 5 capacidade max de comb
		setCapMaxComb(5);  
		// começa com 2 de capacidade atual de agua 
		setCapAtAgua(2); 
		// começa com 5 de capacidade atual de comb 
		setCapAtComb(5); 
		// velocidade 
		setCapVel(4);
		
		System.out.println("Agent drone " + this.getLocalName() + " inicializado com capcidade atual de agua " + getCapAtAgua());
	}
}
