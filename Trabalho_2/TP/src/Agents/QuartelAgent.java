package Agents;

import java.util.ArrayList;
import java.util.List;

import Classes.InfoIncendio;
import Classes.Informacao;
import jade.core.Agent;

public class QuartelAgent extends Agent {

	private static final long serialVersionUID = 1L;

	private List<Informacao> infoAgentes; 
	private List<InfoIncendio> infoIncendios; 
	
	public void setup() { 
		
		this.infoAgentes = new ArrayList<>(); 
		this.infoIncendios = new ArrayList<>(); 
		
	}
	
}
