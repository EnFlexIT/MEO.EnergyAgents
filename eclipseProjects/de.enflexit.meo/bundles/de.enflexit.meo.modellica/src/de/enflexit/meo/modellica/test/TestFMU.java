package de.enflexit.meo.modellica.test;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
	private static final String AMBIENT_TEMPERATURE = "UmgebungsTemperatur";
	private static final String THERMAL_LOAD = "ThermischeLast";
	private static final String SWITCH_HEATPUMP = "Schaltsignal_Waermepumpe";
	private static final String SWITCH_COIL = "Schaltsignal_Heizstab";
	
	// --- Variable names of output variables -------------
	private static final String ELECTRICAL_LOAD_HEATPUMP = "Pel_HP";
	private static final String ELECTRICAL_LOAD_COIL = "Pel_COIL";
	private static final String STORAGE_LOAD = "SOC";
	
	
	private static final String NUMBER_FORMAT_SHORT = "0.00";
	private static final String NUMBER_FORMAT_LONG = "0.00000";
	private DecimalFormat numberFormatShort;
	private DecimalFormat numberFormatLong;
	
	private String fmuFilePath;
	private Simulation simulation; 
	private Vector<String> inputVariableNames;
	private Vector<String> outputVariables;

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
			
			// --- Initialize the simulation --------------
			Simulation simulation = this.getSimulation();
			simulation.init(startTime, stopTime);
			simulation.write(NOMINAL_POWER_HEAT, NOMINAL_POWER_ELECTRIC, NOMINAL_POWER_COIL).with(10.0, 3.5, 10.0);
			
			ModelDescription md = this.getSimulation().getModelDescription();
			System.out.println("FMI version " + md.getFmiVersion());
			System.out.println("--- Input variables: ---");
			for (String var : this.getInputVariableNames()) {
				System.out.println(var);
			}
			System.out.println();
			System.out.println("--- Output variables: ---");
			for (String var : this.getOutputVariableNames()) {
				System.out.println(var);
			}
			System.out.println();
			
			this.printOutputHeaders();
			
			int i = 0;
			for (i=0; i < 2000; i++) {
				double hpState = 1.0 * (i%2);
//				double onOff = 0.0;
				simulation.write("Schaltsignal_Waermepumpe").with(hpState);
				simulation.doStep(stepSize);
				this.printFmuState();
			}
			simulation.terminate();
			System.err.println("[" + this.getClass().getName() + "] Did " + (i) + " simulation steps!");
			
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
			fmuFilePath = Application.getProjectFocused().getProjectFolderFullPath() + "fmuModels/mGRiDS_CoSimFMI_HPSystem/mGRiDS_CoSimFMI.HPSystem.fmu";
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
	 * Gets the input variable names.
	 * @return the input variable names
	 */
	private Vector<String> getInputVariableNames() {
		if (inputVariableNames==null) {
			inputVariableNames = this.getVariableNamesByCausality("input");
		}
		return inputVariableNames;
	}
	
	/**
	 * Gets the output variables.
	 * @return the output variables
	 */
	private Vector<String> getOutputVariableNames() {
		if (outputVariables==null) {
			outputVariables = this.getVariableNamesByCausality("output");
		}
		return outputVariables;
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
		System.out.print("SOC\t");
		System.out.println();
	}
	
	private void printFmuState() {
		double time = this.getSimulation().getCurrentTime();
		double tAmb = this.getSimulation().read(AMBIENT_TEMPERATURE).asDouble();
		double pTh = this.getSimulation().read(THERMAL_LOAD).asDouble();
		double heatPump = this.getSimulation().read(SWITCH_HEATPUMP).asDouble();
		double coil = this.getSimulation().read(SWITCH_COIL).asDouble();
		double pElHP = this.getSimulation().read(ELECTRICAL_LOAD_HEATPUMP).asDouble();
		double pElCoil = this.getSimulation().read(ELECTRICAL_LOAD_COIL).asDouble();
		double soc = this.getSimulation().read(STORAGE_LOAD).asDouble();
		
		System.out.println(time + "\t" + tAmb + "\t" + pTh + "\t" + heatPump + "\t" + this.getNumberFormatShort().format(pElHP) + "\t" + coil + "\t" + this.getNumberFormatShort().format(pElCoil) + "\t" + this.getNumberFormatLong().format(soc));
	}
	
	/**
	 * Gets the decimal format.
	 * @return the decimal format
	 */
	private DecimalFormat getNumberFormatShort() {
		if (numberFormatShort==null) {
			numberFormatShort = new DecimalFormat(NUMBER_FORMAT_SHORT);
			numberFormatShort.setRoundingMode(RoundingMode.HALF_UP);
		}
		return numberFormatShort;
	}
	
	private DecimalFormat getNumberFormatLong() {
		if (numberFormatLong==null) {
			numberFormatLong = new DecimalFormat(NUMBER_FORMAT_LONG);
			numberFormatLong.setRoundingMode(RoundingMode.HALF_UP);
		}
		return numberFormatLong;
	}
	
}