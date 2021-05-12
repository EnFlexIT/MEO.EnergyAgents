package de.enflexit.meo.modellica.test;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Vector;

import org.javafmi.modeldescription.ModelDescription;
import org.javafmi.modeldescription.ScalarVariable;
import org.javafmi.proxy.FmuFile;
import org.javafmi.wrapper.Simulation;

import agentgui.core.application.Application;

public class TestFMU extends Thread {
	
	// --- Variable names of input variables --------------
	private static final String NOMINAL_POWER_HEAT = "Waermepumpe_ThermischeLeistung_Nominal";
	private static final String NOMINAL_POWER_ELECTRIC = "Waermepumpe_Elektrischeverbrauch_Nominal";
	private static final String NOMINAL_POWER_COIL = "Heizstab_Nominal_Leistung";
	private static final String INITIAL_STORAGE_TEMPERATURE = "Tinit_bottom";
	private static final String AMBIENT_TEMPERATURE = "UmgebungsTemperatur";
	private static final String THERMAL_LOAD = "ThermischeLast";
	private static final String SWITCH_HEATPUMP = "Schaltsignal_Waermepumpe";
	private static final String SWITCH_COIL = "Schaltsignal_Heizstab";
	
	// --- Variable names of output variables -------------
	private static final String ELECTRICAL_LOAD_HEATPUMP = "Pel_HP";
	private static final String ELECTRICAL_LOAD_COIL = "Pel_COIL";
	private static final String RESIDUAL_LOAD = "Pth_Residual";
	private static final String STORAGE_LOAD = "SOC";
	
	
	private static final String NUMBER_FORMAT_SHORT = "0.00";
	private static final String NUMBER_FORMAT_LONG = "0.00000";
	private DecimalFormat numberFormatShort;
	private DecimalFormat numberFormatLong;
	
	private String fmuFilePath;
	private Simulation simulation; 

	public TestFMU() {
		this.start();
	}
	
	@Override
	public void run() {
		super.run();
		this.test();
	}

	private void test() {
		
		// --- Time configuration -------------------------
		int startTime = 0;
		int stopTime = 3600;
		int stepSize = 60;
		
		// --- Initialization of parameters ---------------
		double npHeatPumpThermal = 7.0;			// Nominal thermal power of the HeatPump
		double npHeatPumpElectrical = 2.0;		// Nominal electrical power of the HeatPump
		double npCoil = 3.0;					// Nominal power of the Coil
		double tInit = 40.0;					// Initial storage temperature
		
		try {
			
			// --- Initialize the simulation --------------
			Simulation simulation = this.getSimulation();
			simulation.write(NOMINAL_POWER_HEAT, NOMINAL_POWER_ELECTRIC, NOMINAL_POWER_COIL).with(npHeatPumpThermal, npHeatPumpElectrical, npCoil);
			simulation.write(INITIAL_STORAGE_TEMPERATURE).with(tInit);
			simulation.init(startTime, stopTime);
			
			ModelDescription md = this.getSimulation().getModelDescription();
			System.out.println("FMI version " + md.getFmiVersion());
			System.out.println("Input variables:" + this.getVariableNamesByCausality("input"));
			System.out.println("Onput variables:" + this.getVariableNamesByCausality("output"));

			double tAmb = 25.0;
			double pTh = 10.0;
			
			simulation.write(SWITCH_HEATPUMP).with(1.0);
			simulation.write(AMBIENT_TEMPERATURE).with(tAmb);
			simulation.write(THERMAL_LOAD).with(pTh);
			
			this.printOutputHeaders();
			this.printFmuState();
			int i = 0;
			for (i=0; i < stopTime; i+=stepSize) {
//				double hpState = 1.0 * (i%2);
//				double hpState = 0.0;
				double hpState = 1.0;
				simulation.write(SWITCH_HEATPUMP).with(hpState);
				simulation.write(AMBIENT_TEMPERATURE).with(tAmb);
				simulation.write(THERMAL_LOAD).with(pTh);
				simulation.doStep(stepSize);
				this.printFmuState();
			}
			
			simulation.terminate();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Gets the fmu file path.
	 * @return the fmu file path
	 */
	private String getFmuFilePath() {
		if (fmuFilePath==null) {
			fmuFilePath = Application.getProjectFocused().getProjectFolderFullPath() + "fmuModels/HeatPumpFMU_Version3.0/mGRiDS_CoSimFMI.HPSystem.fmu";
		}
		return fmuFilePath;
	}
	
	/**
	 * Gets the simulation.
	 * @return the simulation
	 */
	private Simulation getSimulation() {
		if (simulation==null) {
			File fmuFile = new File(this.getFmuFilePath());
			if (fmuFile.exists()==false) {
				System.err.println("[" + this.getClass().getSimpleName() + "] Could not find FMU file '" + fmuFile.getAbsolutePath() + "'");
				return null;
			}
			simulation = new Simulation(new FmuFile(fmuFile));
		}
		return simulation;
	}
	
	/**
	 * Gets the variable names by causality.
	 * @param causality the causality (input|output|local)
	 * @return the variable names
	 */
	private Vector<String> getVariableNamesByCausality(String causality){
		Vector<String> variableNames = new Vector<String>(); 
		ModelDescription modelDescription = this.getSimulation().getModelDescription();
		for (int i=0; i<modelDescription.getModelVariables().length; i++) {
			ScalarVariable variable = modelDescription.getModelVariables()[i];
			if (variable!=null && variable.getCausality().equals(causality)) {
				variableNames.add(variable.getName());
			}
		}
		return variableNames;
	}
	
	private void printOutputHeaders() {
		System.out.print("Time\t");
		System.out.print("tAmb\t");
		System.out.print("pTh\t");
		System.out.print("HP\t");
		System.out.print("pEl\t");
		System.out.print("Coil\t");
		System.out.print("pEl\t");
		System.out.print("pRes\t");
		System.out.print("Tinit\t");
		System.out.print("SOC\t");
		System.out.println();
	}
	
	private void printFmuState() {
		double time = this.getSimulation().getCurrentTime();
		double tAmb = this.getSimulation().read(AMBIENT_TEMPERATURE).asDouble();
		double pTh = this.getSimulation().read(THERMAL_LOAD).asDouble();
		double heatPump = this.getSimulation().read(SWITCH_HEATPUMP).asDouble();
		double pElHP = this.getSimulation().read(ELECTRICAL_LOAD_HEATPUMP).asDouble();
		double coil = this.getSimulation().read(SWITCH_COIL).asDouble();
		double pElCoil = this.getSimulation().read(ELECTRICAL_LOAD_COIL).asDouble();
		double pRes = this.getSimulation().read(RESIDUAL_LOAD).asDouble();
		double soc = this.getSimulation().read(STORAGE_LOAD).asDouble();
		
		double tInit = this.getSimulation().read(INITIAL_STORAGE_TEMPERATURE).asDouble();
		
		System.out.println(time + "\t" + tAmb + "\t" + pTh + "\t" + heatPump + "\t" + this.getNumberFormatShort().format(pElHP) + "\t" + coil + "\t" + this.getNumberFormatShort().format(pElCoil) + "\t" + this.getNumberFormatShort().format(pRes)+ "\t" + this.getNumberFormatShort().format(tInit) + "\t" + this.getNumberFormatLong().format(soc));
	}
	
	/**
	 * Gets the decimal format.
	 * @return the decimal format
	 */
	private DecimalFormat getNumberFormatShort() {
		if (numberFormatShort==null) {
			numberFormatShort = new DecimalFormat(NUMBER_FORMAT_SHORT);
			numberFormatShort.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
			numberFormatShort.setRoundingMode(RoundingMode.HALF_UP);
		}
		return numberFormatShort;
	}
	
	private DecimalFormat getNumberFormatLong() {
		if (numberFormatLong==null) {
			numberFormatLong = new DecimalFormat(NUMBER_FORMAT_LONG);
			numberFormatLong.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
			numberFormatLong.setRoundingMode(RoundingMode.HALF_UP);
		}
		return numberFormatLong;
	}
	
}