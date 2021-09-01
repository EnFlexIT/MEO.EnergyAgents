package de.enflexit.meo.modellica.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
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
	
	private static final String INPUT_VALUES_FILE_PATH = "D:\\Documents\\Projekte\\MEO\\Co-Simulation\\FMU_Inputs_stufig.csv";
//	private static final String INPUT_VALUES_FILE_PATH = "D:\\Documents\\Projekte\\MEO\\Co-Simulation\\FMU_Inputs_linear.csv";
	
	private static final int T_INIT_SOC_100 = 50;
	
	
	private static final String NUMBER_FORMAT_SHORT = "0.00";
	private static final String NUMBER_FORMAT_LONG = "0.00000";
	private DecimalFormat numberFormatShort;
	private DecimalFormat numberFormatLong;
	
	private String fmuFilePath;
	private Simulation simulation;
	
	private double tInitCalc = 50;
	
	private HashMap<Integer, Vector<Double>> inputValues;

	@Override
	public void run() {
		super.run();
		this.test();
	}

	private void test() {
		
		this.runMultiStepSimulation();
		
	}
	
	@SuppressWarnings("unused")
	private void runOneStepSimulation() {
		
		int[][] switchSignals = {{0,0},{0,1},{1,0},{1,1}};
		
		// --- Time configuration -------------------------
		int startTime = 0;
		int stopTime = 60;
		int stepSize = 60;
		
		// --- Initialization of parameters ---------------
		double npHeatPumpThermal = 7.0;			// Nominal thermal power of the HeatPump
		double npHeatPumpElectrical = 2.0;		// Nominal electrical power of the HeatPump
		double npCoil = 3.0;					// Nominal power of the Coil
		double tInit = 48.01;					// Initial storage temperature
		
		Simulation simulation = this.getSimulation();
		this.printFmuStateHeader();
		try {
			
			for (int i=0; i<switchSignals.length; i++) {
				// --- Initialize the simulation --------------
				simulation.write(NOMINAL_POWER_HEAT, NOMINAL_POWER_ELECTRIC, NOMINAL_POWER_COIL).with(npHeatPumpThermal, npHeatPumpElectrical, npCoil);
				simulation.write(INITIAL_STORAGE_TEMPERATURE).with(tInit);
				simulation.init(startTime, stopTime);
				
				double hpState = switchSignals[i][0];
				double coilState = switchSignals[i][1];
				double tAmb = 22.84;
				double pTh = 12.39;
				
				int j = 0;
				for (j=0; j < stopTime; j+=stepSize) {
					simulation.write(AMBIENT_TEMPERATURE).with(tAmb);
					simulation.write(THERMAL_LOAD).with(pTh);
					simulation.write(SWITCH_HEATPUMP).with(hpState);
					simulation.write(SWITCH_COIL).with(coilState);
					simulation.write(AMBIENT_TEMPERATURE).with(tAmb);
					simulation.write(THERMAL_LOAD).with(pTh);
					simulation.doStep(stepSize);
					this.printFmuState();
				}
				
				simulation.reset();
			}
			
			simulation.terminate();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Runs a multi-step simulation based on a series of inputs.
	 */
	@SuppressWarnings("unused")
	private void runMultiStepSimulation() {
		// --- Time configuration -------------------------
		int startTime = 0;
		int stopTime = 3600*3;
		int stepSize = 60;
		
		// --- Initialization of parameters ---------------
		double npHeatPumpThermal = 7.0;			// Nominal thermal power of the HeatPump
		double npHeatPumpElectrical = 2.0;		// Nominal electrical power of the HeatPump
		double npCoil = 3.0;					// Nominal power of the Coil
		double tInit = 50.0;					// Initial storage temperature
		
		try {
			
			// --- Prepare the simulation -----------------
			Simulation simulation = this.getSimulation();
			simulation.write(NOMINAL_POWER_HEAT, NOMINAL_POWER_ELECTRIC, NOMINAL_POWER_COIL).with(npHeatPumpThermal, npHeatPumpElectrical, npCoil);
			simulation.write(INITIAL_STORAGE_TEMPERATURE).with(tInit);
			
			// --- Use the first set of input values for t0
			simulation.write(SWITCH_HEATPUMP).with(0);
			simulation.write(AMBIENT_TEMPERATURE).with(22.8);
			simulation.write(THERMAL_LOAD).with(12.5);
			
			simulation.init(startTime, stopTime);

			this.printFmuStateHeader();
			
//			ModelDescription md = this.getSimulation().getModelDescription();
//			System.out.println("FMI version " + md.getFmiVersion());
//			System.out.println("Input variables:" + this.getVariableNamesByCausality("input"));
//			System.out.println("Onput variables:" + this.getVariableNamesByCausality("output"));

			double tAmb = 25.0;
			double pTh = 10.0;
			
			while (simulation.getCurrentTime() < stopTime) {

				int stepEndTime = new Double(simulation.getCurrentTime()+stepSize).intValue();
				
				Vector<Double> stepValues = this.getInputValues().get(stepEndTime);
				if (stepValues!=null) {
					simulation.write(AMBIENT_TEMPERATURE).with(stepValues.get(0));
					simulation.write(THERMAL_LOAD).with(stepValues.get(1));
					simulation.write(SWITCH_HEATPUMP).with(stepValues.get(2));
					this.printFmuState();
					simulation.doStep(stepSize);
					this.printFmuState();
				} else {
					System.err.println("No input values found for FMU time " + stepEndTime);
					break;
				}
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
	@SuppressWarnings("unused")
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
	
	
	private void printFmuStateHeader() {
		System.out.print("time\t");
		System.out.print("tAmb\t");
		System.out.print("pTh\t");
		System.out.print("tInit\t");
		System.out.print("hp\t");
		System.out.print("coil\t");
		System.out.print("pElHp\t");
		System.out.print("pElCoil\t");
		System.out.print("pRes\t");
		System.out.println("SOC\t");
	}
	
	
	private void printFmuState() {
		
		double tAmp = this.getSimulation().read(AMBIENT_TEMPERATURE).asDouble();
		double pTh = this.getSimulation().read(THERMAL_LOAD).asDouble();
//		double tInit = this.getSimulation().read(INITIAL_STORAGE_TEMPERATURE).asDouble();
		double tInit = this.tInitCalc;	//  Calculated tInit from the previus state
		double setpointHeatpump = this.getSimulation().read(SWITCH_HEATPUMP).asDouble();
		double setpointCoil = this.getSimulation().read(SWITCH_COIL).asDouble();
		double pElHeatPump = this.getSimulation().read(ELECTRICAL_LOAD_HEATPUMP).asDouble();
		double pElCoil = this.getSimulation().read(ELECTRICAL_LOAD_COIL).asDouble();
		double pRes = this.getSimulation().read(RESIDUAL_LOAD).asDouble();
		double soc = this.getSimulation().read(STORAGE_LOAD).asDouble();
		
		System.out.print(this.getSimulation().getCurrentTime() + "\t");
		System.out.print(this.getNumberFormatShort().format(tAmp) + "\t");
		System.out.print(this.getNumberFormatShort().format(pTh) + "\t");
		System.out.print(this.getNumberFormatShort().format(tInit) + "\t");
		System.out.print(setpointHeatpump + "\t");
		System.out.print(setpointCoil + "\t");
		System.out.print(this.getNumberFormatShort().format(pElHeatPump) + "\t");
		System.out.print(this.getNumberFormatShort().format(pElCoil) + "\t");
		System.out.print(this.getNumberFormatShort().format(pRes) + "\t");
		System.out.println(this.getNumberFormatLong().format(soc));
		
		// Remember calculated tInit for the next step
		this.tInitCalc = T_INIT_SOC_100 * soc;
	}
	
	private HashMap<Integer, Vector<Double>> getInputValues() {
		if (inputValues==null) {
			inputValues = this.readInputsFromCsv(INPUT_VALUES_FILE_PATH);
		}
		return inputValues;
	}
	
	private HashMap<Integer, Vector<Double>> readInputsFromCsv(String csvFileFullPath){
		HashMap<Integer, Vector<Double>> inputValues = new HashMap<Integer, Vector<Double>>();
		File csvFile = new File(csvFileFullPath);
		
		if (csvFile.exists()) {
			BufferedReader fileReader = null;
			try {
				fileReader = new BufferedReader(new FileReader(csvFile));
				String line;
				fileReader.readLine();	// Ignore headers
				while((line = fileReader.readLine()) != null){
					String[] parts = line.split(";");
					
					int fmuTime = Integer.parseInt(parts[0]);
					double tAmb = Double.parseDouble(parts[1]);
					double pTh = Double.parseDouble(parts[2]);
					double hpSetpoint = Double.parseDouble(parts[3]);
					
					Vector<Double> lineInputs = new Vector<Double>();
					lineInputs.add(tAmb);
					lineInputs.add(pTh);
					lineInputs.add(hpSetpoint);
					
					inputValues.put(fmuTime, lineInputs);
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				
				if (fileReader!=null) {
					try {
						fileReader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			System.err.println("Input file not found at " + csvFile.getAbsolutePath());
		}
		return inputValues;
	}
	
}