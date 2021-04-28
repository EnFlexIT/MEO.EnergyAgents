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
	
	private Vector<FmuVariableMappingIoList> setpointMappings;
	private Vector<FmuVariableMappingIoList> measurementMappings;
	private Vector<FmuVariableMappingIoList> resultMappings;
	
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
		this.initializeFmuSimulation();
		// --- Prepare static and dynamic inputs ----------
		for (int i=0; i<this.getSetpointMappings().size(); i++) {
			this.writeVariableToFMU(this.getSetpointMappings().get(i), tsse);
		}
		for (int i=0; i<this.getMeasurementMappings().size(); i++) {
			this.writeVariableToFMU(this.getMeasurementMappings().get(i), tsse);
		}
		
		// --- Perform the actual simulation --------------
		long stepSize = tsse.getStateTime() / this.getStaticModel().getModelStepSizeMilliSeconds();
		this.getFmuSimulation().doStep(stepSize);

		for (int i=0; i<this.getResultMappings().size(); i++) {
			this.readVariableFromFMU(this.getResultMappings().get(i), tsse);
		}
	}
	
	/**
	 * Initialize simulation.
	 */
	private void initializeFmuSimulation() {
		this.getFmuSimulation().reset();
		
		//TODO figure out how to handle time
		// --- Initialize the FMU with the static parameters --------
		for (int i=0; i<this.getStaticParameters().size(); i++) {
			String fmuName = this.getStaticParameters().get(i).getFmuVariableName();
			Object parameter = this.getStaticParameters().get(i).getValue();
			if (parameter instanceof Double) {
				this.getFmuSimulation().write(fmuName).with((Double)parameter);
			} else if (parameter instanceof Integer) {
				this.getFmuSimulation().write(fmuName).with((Integer)parameter);
			} else if (parameter instanceof Boolean) {
				this.getFmuSimulation().write(fmuName).with((Boolean)parameter);
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
			this.getFmuSimulation().write(variableMapping.getFmuVariableName()).with(((FixedDouble)eomVariable).getValue());
		} else if (eomVariable instanceof FixedInteger) {
			this.getFmuSimulation().write(variableMapping.getFmuVariableName()).with(((FixedInteger)eomVariable).getValue());
		} else if (eomVariable instanceof FixedBoolean) {
			this.getFmuSimulation().write(variableMapping.getFmuVariableName()).with(((FixedBoolean)eomVariable).isValue());
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
			double fmuValue = this.getFmuSimulation().read(variableMapping.getFmuVariableName()).asDouble();
			((FixedDouble)eomVariable).setValue(fmuValue);
		} else if (eomVariable instanceof FixedInteger) {
			int fmuValue = this.getFmuSimulation().read(variableMapping.getFmuVariableName()).asInteger();
			((FixedInteger)eomVariable).setValue(fmuValue);
		} else if (eomVariable instanceof FixedBoolean) {
			boolean fmuValue = this.getFmuSimulation().read(variableMapping.getFmuVariableName()).asBoolean();
			((FixedBoolean)eomVariable).setValue(fmuValue);
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
	public Simulation getFmuSimulation() {
		
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
}
