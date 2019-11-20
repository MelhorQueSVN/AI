package Agents;

public class AeronaveAgent extends FirefightersAgent{

	private static final long serialVersionUID = 1L;

	public void setup() { 
		super.setup();
		// aeronave tem 15 capacidade max de �gua 
		setCapMaxAgua(15); 
		// aeronave tem 20 capacidade max de comb
		setCapMaxComb(20);  
		// come�a com 15 de capacidade atual de agua 
		setCapAtAgua(15); 
		// come�a com 20 de capacidade atual de comb 
		setCapAtComb(20); 
		// velocidade 
		setCapVel(2);
		
		System.out.println("Agent drone " + this.getLocalName() + " inicializado com cap atual de agua " + getCapAtAgua());
	}
	
	
	
}
