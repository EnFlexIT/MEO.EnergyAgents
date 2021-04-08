package de.enflexit.meo.modellica.test;

import java.io.File;

import org.javafmi.proxy.FmuFile;
import org.javafmi.wrapper.Simulation;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class TestFMU extends Thread {

	public TestFMU() {
		this.start();
	}
	
	@Override
	public void run() {
		super.run();
		this.printProcessID();
		this.test();
	}

	
	private void printProcessID() {
		System.setProperty("debug.native", "true");
		System.setProperty("jna.debug_load", "true");
		System.setProperty("jna.debug_load.jna", "true");

		MeinInterface.INSTANCE.puts("Helle World ... !");
	}
	public interface MeinInterface extends Library {
	    // Windows will wieder Sonderwurst beim Namen der Standardbibliothek
	    String libName = Platform.isWindows() ? "msvcrt" : "c";
	    MeinInterface INSTANCE = (MeinInterface) Native.loadLibrary(libName, MeinInterface.class);
	    // Es folgen die aufrufbaren C-Funktionen der Bibliothek 
	    void puts(String s);
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
			int i = 0;
			for (i=0; i < 2000; i++) {
				simulation.doStep(stepSize);
			}
			simulation.terminate();
			System.err.println("[" + this.getClass().getName() + "] Did " + (i) + " simulation steps!");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	
	
}
