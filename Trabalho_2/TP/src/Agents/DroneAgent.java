package Agents;

public class DroneAgent extends FirefightersAgent{

	private static final long serialVersionUID = 1L;
	
	public void setup() {
		super.setup();
		// aeronave tem 2 capacidade max de �gua 
		setCapMaxAgua(2); 
		// aeronave tem 5 capacidade max de comb
		setCapMaxComb(5);  
		// come�a com 2 de capacidade atual de agua 
		setCapAtAgua(2); 
		// come�a com 5 de capacidade atual de comb 
		setCapAtComb(5); 
		// velocidade 
		setCapVel(4);
		
		System.out.println("Agent drone " + this.getLocalName() + " inicializado com capcidade atual de agua " + getCapAtAgua());
	}
}
