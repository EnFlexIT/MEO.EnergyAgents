package de.enflexit.meo.modellica.eomIntegration;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import org.javafmi.proxy.FmuFile;
import org.javafmi.wrapper.Simulation;

import de.enflexit.meo.modellica.eomIntegration.FmuVariableMappingIoList.IoVariableType;
import energy.helper.TechnicalSystemStateHelper;
import energy.optionModel.FixedBoolean;
import energy.optionModel.FixedDouble;
import energy.optionModel.FixedInteger;
import energy.optionModel.FixedVariable;
import energy.optionModel.TechnicalSystemStateEvaluation;

// TODO: Auto-generated Javadoc
/**
 * A generic wrapper for FMU simulations in the context of an EOM OptionModelCalculation.
 *
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuSimulationWrapper {
	
	/** The simulation. */
	private Simulation simulation;
	
	/** The fmu static model. */
	private FmuStaticDataModel fmuStaticModel;
	
	/** The single step mode. */
	private boolean singleStepMode = true;
	
	/** The first execution. */
	private boolean firstExecution = true;
	
	/** The setpoint mappings. */
	private Vector<FmuVariableMappingIoList> setpointMappings;
	
	/** The measurement mappings. */
	private Vector<FmuVariableMappingIoList> measurementMappings;
	
	/** The result mappings. */
	private Vector<FmuVariableMappingIoList> resultMappings;
	
	/** The debug. */
	private boolean debug = false;
	
	/**
	 * Instantiates a new fmu simulation wrapper.
	 * @param fmuStaticModel the fmu static model
	 */
	public FmuSimulationWrapper(FmuStaticDataModel fmuStaticModel) {
		this.fmuStaticModel = fmuStaticModel;
	}

	/**
	 * Simulate a single {@link TechnicalSystemStateEvaluation} with the FMU.
	 * @param tsse the tsse
	 */
	public void simulateTsse(TechnicalSystemStateEvaluation tsse) {

		// --- Reset the simulation first ---------------------------
		if (this.isSingleStepMode()==true || this.firstExecution==true || tsse.getDescription().equals("Initial State")) {
			
			this.getSimulation().reset();
			this.initializeParameters();
			this.performCustomInitializations(tsse);
			if (this.isSingleStepMode()) {
				// --- Simulate one single TSSE ---------------------
				this.getSimulation().init(0, tsse.getStateTime() / this.getStaticModel().getModelStepSizeMilliSeconds());
			} else {
				// --- Simulate the whole time without reset --------
				this.getSimulation().init(0);	//TODO end time necessary? If so, use evaluation end time here.
			}
			if (this.firstExecution==true) {
//				this.printFmuStateHeader();
			}
			this.firstExecution = false;
		}
		
		// --- Set values for measurements and setpoints ------------
		for (int i=0; i<this.getMeasurementMappings().size(); i++) {
			this.writeVariableToFMU(this.getMeasurementMappings().get(i), tsse);
		}
		for (int i=0; i<this.getSetpointMappings().size(); i++) {
			this.writeVariableToFMU(this.getSetpointMappings().get(i), tsse);
		}
		
		// --- Perform the actual simulation ------------------------
		long stepSize = tsse.getStateTime() / this.getStaticModel().getModelStepSizeMilliSeconds();
		
//		this.printFmuState(tsse);
		this.getSimulation().doStep(stepSize);
//		this.printFmuState(tsse);

		// --- Get "result measurements" from the FMU ---------------
		for (int i=0; i<this.getResultMappings().size(); i++) {
			this.readVariableFromFMU(this.getResultMappings().get(i), tsse);
		}
	}
	
	/**
	 * Initialize simulation.
	 */
	private void initializeParameters() {
		
		// --- Initialize the FMU with the static parameters --------
		for (int i=0; i<this.getStaticParameters().size(); i++) {
			String fmuName = this.getStaticParameters().get(i).getFmuVariableName();
			Object parameter = this.getStaticParameters().get(i).getValue();
			if (parameter instanceof Double) {
				this.getSimulation().write(fmuName).with((Double)parameter);
			} else if (parameter instanceof Integer) {
				this.getSimulation().write(fmuName).with((Integer)parameter);
			} else if (parameter instanceof Boolean) {
				this.getSimulation().write(fmuName).with((Boolean)parameter);
			} 
		}
	}
	
	/**
	 * This method can be overridden to implement FMU-specific initialization tasks that go beyond
	 * just initializing parameters with static values. The default implementation is empty.
	 * @param tsse the tsse to be simulated
	 */
	protected void performCustomInitializations(TechnicalSystemStateEvaluation tsse) {
		// --- Empty default implementation.  
	}
	
	/**
	 * Writes the value of a {@link FixedVariable} from a {@link TechnicalSystemStateEvaluation} to the corresponding FMU input variable.
	 * This generic implementation will just transfer the values unchanged, according to the corresponding {@link FmuVariableMappingIoList}.
	 * If a variable requires a special pre-processing, this can be implemented in a model-specific subclass. 
	 * @param variableMapping the variable mapping
	 * @param tsse the tsse
	 */
	protected void writeVariableToFMU(FmuVariableMappingIoList variableMapping, TechnicalSystemStateEvaluation tsse) {
		FixedVariable eomVariable = TechnicalSystemStateHelper.getFixedVariable(tsse.getIOlist(), variableMapping.getEomVariableName());
		if (eomVariable instanceof FixedDouble) {
			this.getSimulation().write(variableMapping.getFmuVariableName()).with(((FixedDouble)eomVariable).getValue());
		} else if (eomVariable instanceof FixedInteger) {
			this.getSimulation().write(variableMapping.getFmuVariableName()).with(((FixedInteger)eomVariable).getValue());
		} else if (eomVariable instanceof FixedBoolean) {
			if (((FixedBoolean)eomVariable).isValue()==true) {
				this.getSimulation().write(variableMapping.getFmuVariableName()).with(1.0);
			} else {
				this.getSimulation().write(variableMapping.getFmuVariableName()).with(0.0);
			}
		}
		
		if (this.debug==true) {
			System.out.println("Set variable " + variableMapping.getFmuVariableName() + " to FMU: " + this.getVariableString(eomVariable));
		}
	}
	
	
	/**
	 * Reads the value from a FMU output variable and writes it to the corresponding {@link FixedVariable} of the {@link TechnicalSystemStateEvaluation}.
	 * This generic implementation will just transfer the values unchanged, according to the corresponding {@link FmuVariableMappingIoList}.
	 * If a variable requires a special post-processing, this can be implemented in a model-specific subclass. 
	 * @param variableMapping the variable mapping
	 * @param tsse the tsse
	 */
	protected void readVariableFromFMU(FmuVariableMappingIoList variableMapping, TechnicalSystemStateEvaluation tsse) {
		FixedVariable eomVariable = TechnicalSystemStateHelper.getFixedVariable(tsse.getIOlist(), variableMapping.getEomVariableName());
		
		if (eomVariable instanceof FixedDouble) {
			double fmuValue = this.getSimulation().read(variableMapping.getFmuVariableName()).asDouble();
			((FixedDouble)eomVariable).setValue(fmuValue);
		} else if (eomVariable instanceof FixedInteger) {
			int fmuValue = this.getSimulation().read(variableMapping.getFmuVariableName()).asInteger();
			((FixedInteger)eomVariable).setValue(fmuValue);
		} else if (eomVariable instanceof FixedBoolean) {
			boolean fmuValue = this.getSimulation().read(variableMapping.getFmuVariableName()).asBoolean();
			((FixedBoolean)eomVariable).setValue(fmuValue);
		}
		
		if (this.debug==true) {
			System.out.println("Read variable " + variableMapping.getFmuVariableName() + " from FMU: " + this.getVariableString(eomVariable));
		}
	}
	
	/**
	 * Gets the static model describing the FMU.
	 * @return the static model
	 */
	private FmuStaticDataModel getStaticModel() {
		return fmuStaticModel;
	}
	
	/**
	 * Gets the simulation.
	 * @return the simulation
	 */
	public Simulation getSimulation() {
		
		if (simulation==null) {
			File fmuFile = new File(this.getStaticModel().getFmuFilePath());
			if (fmuFile.exists()==false) {
				System.err.println("[" + this.getClass().getSimpleName() + "] Could not find FMU file '" + fmuFile.getAbsolutePath() + "'");
				return null;
			}
			simulation = new Simulation(new FmuFile(fmuFile));
		}
		return simulation;
	}
	
	/**
	 * Gets the static parameters.
	 * @return the static parameters
	 */
	private Vector<FmuVariableMappingStaticParameter> getStaticParameters() {
		return this.getStaticModel().getStaticParameters();
	}
	
	/**
	 * Gets the FMU variable mappings for setpoint variables.
	 * @return the setpoint mappings
	 */
	private Vector<FmuVariableMappingIoList> getSetpointMappings() {
		if (setpointMappings==null) {
			setpointMappings = this.getStaticModel().getIoVariablesByType(IoVariableType.SETPOINT);
		}
		return setpointMappings;
	}
	
	/**
	 * Gets the FMU variable mappings for measurement variables.
	 * @return the measurement mappings
	 */
	private Vector<FmuVariableMappingIoList> getMeasurementMappings() {
		if (measurementMappings==null) {
			measurementMappings = this.getStaticModel().getIoVariablesByType(IoVariableType.MEASUREMENT);
		}
		return measurementMappings;
	}
	
	/**
	 * Gets the FMU variable mappings for result variables.
	 * @return the result mappings
	 */
	public Vector<FmuVariableMappingIoList> getResultMappings() {
		if (resultMappings==null) {
			resultMappings = this.getStaticModel().getIoVariablesByType(IoVariableType.RESULT);
		}
		return resultMappings;
	}

	/**
	 * Checks if this wrapper is single step mode. If true, the simulation is performed step-wise,
	 * i.e. it is reset and re-initialized for each simulation step, which is required for the 
	 * typical EOM evaluation behavior to check all alternatives for the next step. If false, the
	 * simulation is performed in the "regular" sequential way, which will not work for EOM 
	 * evaluations if there is more than one possible next step.    
	 * @return true, if is single step mode
	 */
	public boolean isSingleStepMode() {
		return singleStepMode;
	}
	
	/**
	 * Terminates the FMU simulation.
	 */
	public void terminateSimulation() {
		this.getSimulation().terminate();
	}

	/**
	 * Enabled or disables single step mode. If enabled, the simulation is performed step-wise,
	 * i.e. it is reset and re-initialized for each simulation step, which is required for the 
	 * typical EOM evaluation behavior to check all alternatives for the next step. If disabled, 
	 * the simulation is performed in the "regular" sequential way, which will not work for EOM 
	 * evaluations if there is more than one possible next step.
	 * @param singleStepMode the new single step mode
	 */
	public void setSingleStepMode(boolean singleStepMode) {
		this.singleStepMode = singleStepMode;
	}
	
	/**
	 * Returns a String representation of the {@link FixedVariable}'s value.
	 *
	 * @param variable the variable
	 * @return the string
	 */
	private String getVariableString(FixedVariable variable) {
		if (variable instanceof FixedBoolean) {
			return "" + ((FixedBoolean)variable).isValue();
		} else if (variable instanceof FixedDouble){
			return "" + ((FixedDouble)variable).getValue();
		} else if (variable instanceof FixedInteger) {
			return "" + ((FixedInteger)variable).getValue();
		} else {
			return "";
		}
	}
	
	
	// --------------------------------------------------------------
	// --- From here, stuff for debugging TODO remove!!! ------------
	// --------------------------------------------------------------
	
	/** The Constant NUMBER_FORMAT_SHORT. */
	private static final String NUMBER_FORMAT_SHORT = "0.00";
	
	/** The Constant NUMBER_FORMAT_LONG. */
	private static final String NUMBER_FORMAT_LONG = "0.00000";
	
	/** The Constant TIME_FORMAT. */
	private static final String TIME_FORMAT = "HH:mm";
	
	/** The number format short. */
	private DecimalFormat numberFormatShort;
	
	/** The number format long. */
	private DecimalFormat numberFormatLong;
	
	/** The time format. */
	private SimpleDateFormat timeFormat;
	
	/**
	 * Prints the header for the debug output of FMU variables - see printFmuState method below.
	 */
	@SuppressWarnings("unused")
	private void printFmuStateHeader() {
		System.out.print("timeEOM\t");
		System.out.print("timeFMU\t");
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
	
	
	/**
	 * Prints the current state of the relevant variables of the Heatpump-FMU.
	 *
	 * @param tsse the tsse
	 */
	@SuppressWarnings("unused")
	private void printFmuState(TechnicalSystemStateEvaluation tsse) {

		// --- Get the values from the FMU ----------------
		double tAmp = this.getSimulation().read("UmgebungsTemperatur").asDouble();
		double pTh = this.getSimulation().read("ThermischeLast").asDouble();
		double tInit = this.getSimulation().read("Tinit_bottom").asDouble();
		double setpointHeatpump = this.getSimulation().read("Schaltsignal_Waermepumpe").asDouble();
		double setpointCoil = this.getSimulation().read("Schaltsignal_Heizstab").asDouble();
		double pElHeatPump = this.getSimulation().read("Pel_HP").asDouble();
		double pElCoil = this.getSimulation().read("Pel_COIL").asDouble();
		double pRes = this.getSimulation().read("Pth_Residual").asDouble();
		double soc = this.getSimulation().read("SOC").asDouble();
		
		// --- Print the values ---------------------------
		System.out.print(this.getTimeFormat().format(new Date(tsse.getGlobalTime())) + "\t");
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
	}
	
	/**
	 * Gets the number format short.
	 * @return the number format short
	 */
	private DecimalFormat getNumberFormatShort() {
		if (numberFormatShort==null) {
			numberFormatShort = new DecimalFormat(NUMBER_FORMAT_SHORT);
			numberFormatShort.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
			numberFormatShort.setRoundingMode(RoundingMode.HALF_UP);
		}
		return numberFormatShort;
	}
	
	/**
	 * Gets the number format long.
	 * @return the number format long
	 */
	private DecimalFormat getNumberFormatLong() {
		if (numberFormatLong==null) {
			numberFormatLong = new DecimalFormat(NUMBER_FORMAT_LONG);
			numberFormatLong.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
			numberFormatLong.setRoundingMode(RoundingMode.HALF_UP);
		}
		return numberFormatLong;
	}
	
	/**
	 * Gets the time format.
	 * @return the time format
	 */
	public SimpleDateFormat getTimeFormat() {
		if (timeFormat==null) {
			timeFormat = new SimpleDateFormat(TIME_FORMAT);
		}
		return timeFormat;
	}
}
