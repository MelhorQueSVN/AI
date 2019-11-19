package Agents;

public class CamiaoAgent extends FirefightersAgent {

	private static final long serialVersionUID = 1L;
	
	public void setup() {
		super.setup();
		// aeronave tem 10 capacidade max de �gua 
		setCapMaxAgua(10); 
		// aeronave tem 10 capacidade max de comb
		setCapMaxComb(10);  
		// come�a com 10 de capacidade atual de agua 
		setCapAtAgua(10); 
		// come�a com 10 de capacidade atual de comb 
		setCapAtComb(10);
				
		System.out.println("Agent drone " + this.getLocalName() + " inicializado com cap atual de agua " + getCapAtAgua());
	}
}
