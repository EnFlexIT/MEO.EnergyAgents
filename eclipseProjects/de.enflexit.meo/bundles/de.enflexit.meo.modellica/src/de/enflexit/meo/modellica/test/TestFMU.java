package de.enflexit.meo.modellica.test;

import java.io.File;

import org.javafmi.proxy.FmuFile;
import org.javafmi.wrapper.Simulation;

public class TestFMU extends Thread {

	public TestFMU() {
		this.start();
	}
	
	@Override
	public void run() {
		super.run();
		this.test();
	}
	
	private void test() {
		
		int startTime = 1;
		int stopTime = 2000;
		int stepSize = 1;
		
		try {
			
			String pathFMU = "D:\\20_GIT\\07_MEO.EnergyAgents\\awbProjects\\meo-energy-agents\\fmuModels\\mGRiDS_CoSimFMI_HPSystem\\mGRiDS_CoSimFMI_HPSystem.fmu";
			File fileFMU = new File(pathFMU);
			if (fileFMU.exists()==false) {
				System.err.println("[" + this.getClass().getSimpleName() + "] Could not find FMU file '" + fileFMU.getAbsolutePath() + "'");
				return;
			}
			
			// --- Open the FMU file --------------------------
			FmuFile fmuFile = new FmuFile(fileFMU);
			
			Simulation simulation = new Simulation(fmuFile);
			simulation.init(startTime, stopTime);
			for(int i=0; i < 2000; i++) {
				simulation.doStep(stepSize);
				System.err.println("[" + this.getClass().getName() + "] Do simulation step no" + i+1 + "  ");
			}
			simulation.terminate();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	
	
}
