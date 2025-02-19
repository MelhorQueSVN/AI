package Container;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;

public class MainContainer {

	Runtime rt;
	ContainerController container;

	public ContainerController initContainerInPlatform(String host, String port, String containerName) {
		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, host);
		profile.setParameter(Profile.MAIN_PORT, port);
		// create a non-main agent container
		ContainerController container = rt.createAgentContainer(profile);
		return container;
	}

	public void initMainContainerInPlatform(String host, String port, String containerName) {

		// Get the JADE runtime interface (singleton)
		this.rt = Runtime.instance();

		// Create a Profile, where the launch arguments are stored
		Profile prof = new ProfileImpl();
		prof.setParameter(Profile.CONTAINER_NAME, containerName);
		prof.setParameter(Profile.MAIN_HOST, host);
		prof.setParameter(Profile.MAIN_PORT, port);
		prof.setParameter(Profile.MAIN, "true");
		prof.setParameter(Profile.GUI, "true");

		// create a main agent container
		this.container = rt.createMainContainer(prof);
		rt.setCloseVM(true);

	}
	
	public void startAgentInPlatform(String name, String classpath) {
		try {
			AgentController ac = container.createNewAgent(name, classpath, new Object[0]);
			ac.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws ControllerException {
		MainContainer a = new MainContainer();

		a.initMainContainerInPlatform("localhost", "9888", "MainContainer");
		
		// inicializa o agente quartel
		a.startAgentInPlatform("quartel", "Agents.QuartelAgent");
		
		a.startAgentInPlatform("interface", "Agents.InterfaceAgent");
		
		// inicializa o agente incendi�rio  
		a.startAgentInPlatform("incendiario", "Agents.IncendiarioAgent"); 
		
		// inicializa 10 drones 
		int size_d = 10;
		for(int i=0;i<size_d;i++) { 
			a.startAgentInPlatform("drone" + i, "Agents.DroneAgent");
		} 
		
		// inicaliza 5 cami�es 
		int size_c = 5;
		for(int i=0;i<size_c;i++) { 
			a.startAgentInPlatform("camiao" + i, "Agents.CamiaoAgent");
		}
		
		// inicializa 2 aeronavaes 
		int size_a = 2;
		for(int i=0;i<size_a;i++) { 
			a.startAgentInPlatform("aeronave" + i, "Agents.AeronaveAgent");
		}
		
		
	} 
}
