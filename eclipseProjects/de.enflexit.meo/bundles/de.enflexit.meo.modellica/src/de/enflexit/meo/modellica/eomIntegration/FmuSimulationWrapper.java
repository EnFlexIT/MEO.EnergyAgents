package de.enflexit.meo.modellica.eomIntegration;

import java.io.File;
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

/**
 * A generic wrapper for FMU simulations in the context of an EOM OptionModelCalculation
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuSimulationWrapper {
	private Simulation simulation;
	private FmuStaticDataModel fmuStaticModel;
	
	private boolean singleStepMode = true;
	private boolean firstExecution = true;
	
	private Vector<FmuVariableMappingIoList> setpointMappings;
	private Vector<FmuVariableMappingIoList> measurementMappings;
	private Vector<FmuVariableMappingIoList> resultMappings;
	
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

		// --- Reset the simulation first -----------------
		if (this.isSingleStepMode()==true || this.firstExecution==true) {
			this.resetFmuSimulation();
			this.firstExecution = false;
		}
		// --- Prepare static and dynamic inputs ----------
		for (int i=0; i<this.getSetpointMappings().size(); i++) {
			this.writeVariableToFMU(this.getSetpointMappings().get(i), tsse);
		}
		for (int i=0; i<this.getMeasurementMappings().size(); i++) {
			this.writeVariableToFMU(this.getMeasurementMappings().get(i), tsse);
		}
		
		// --- Perform the actual simulation --------------
		long stepSize = tsse.getStateTime() / this.getStaticModel().getModelStepSizeMilliSeconds();
		this.getSimulation().doStep(stepSize);

		for (int i=0; i<this.getResultMappings().size(); i++) {
			this.readVariableFromFMU(this.getResultMappings().get(i), tsse);
		}
	}
	
	/**
	 * Initialize simulation.
	 */
	private void resetFmuSimulation() {
		this.getSimulation().reset();
		
		//TODO figure out how to handle time
		this.getSimulation().init(0);
		
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
	 * Writes the value of a {@link FixedVariable} from a {@link TechnicalSystemStateEvaluation} to the corresponding FMU input variable
	 * @param variableMapping the variable mapping
	 * @param tsse the tsse
	 */
	private void writeVariableToFMU(FmuVariableMappingIoList variableMapping, TechnicalSystemStateEvaluation tsse) {
		FixedVariable eomVariable = TechnicalSystemStateHelper.getFixedVariable(tsse.getIOlist(), variableMapping.getEomVariableName());
		if (eomVariable instanceof FixedDouble) {
			this.getSimulation().write(variableMapping.getFmuVariableName()).with(((FixedDouble)eomVariable).getValue());
		} else if (eomVariable instanceof FixedInteger) {
			this.getSimulation().write(variableMapping.getFmuVariableName()).with(((FixedInteger)eomVariable).getValue());
		} else if (eomVariable instanceof FixedBoolean) {
			this.getSimulation().write(variableMapping.getFmuVariableName()).with(((FixedBoolean)eomVariable).isValue());
		}
		
		if (this.debug==true) {
			System.out.println("Set variable " + variableMapping.getFmuVariableName() + " to FMU: " + this.getVariableString(eomVariable));
		}
	}
	
	
	/**
	 * Reads the value from a FMU output variable and writes it to the corresponding {@link FixedVariable} of the {@link TechnicalSystemStateEvaluation}
	 * @param variableMapping the variable mapping
	 * @param tsse the tsse
	 */
	private void readVariableFromFMU(FmuVariableMappingIoList variableMapping, TechnicalSystemStateEvaluation tsse) {
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
	 * Returns a String representation of the {@link FixedVariable}'s value
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
}
