package de.enflexit.meo.modellica.eomIntegration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import org.javafmi.proxy.FmuFile;
import org.javafmi.wrapper.Simulation;

import agentgui.core.application.Application;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMapping.IoVariableType;
import energy.helper.TechnicalSystemStateHelper;
import energy.optionModel.FixedBoolean;
import energy.optionModel.FixedDouble;
import energy.optionModel.FixedInteger;
import energy.optionModel.FixedVariable;
import energy.optionModel.TechnicalSystemStateEvaluation;

/**
 * A generic wrapper for FMU simulations in the context of an EOM OptionModelCalculation.
 *
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuSimulationWrapper {
	
	private Simulation simulation;
	private FmuStaticDataModel fmuStaticModel;
	private Vector<FmuVariableMapping> setpointMappings;
	private Vector<FmuVariableMapping> measurementMappings;
	private Vector<FmuVariableMapping> resultMappings;
	
	private Path projectFolderPath;
	
	/**
	 * Instantiates a new fmu simulation wrapper.
	 * @param fmuStaticModel the fmu static model
	 */
	public FmuSimulationWrapper(FmuStaticDataModel fmuStaticModel) {
		this.fmuStaticModel = fmuStaticModel;
	}
	
	/**
	 * Initialize the FMU.
	 * @param initialTsse the initial {@link TechnicalSystemStateEvaluation} of the evaluation period
	 * @param evaluationEndTime the end of the evaluation period (Java timestamp)
	 */
	public void initializeFmu(TechnicalSystemStateEvaluation initialTsse, long evaluationEndTime) {

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
		this.performCustomInitializations(initialTsse);
		
		// --- Set values for measurements and setpoints ------------
		for (int i=0; i<this.getMeasurementMappings().size(); i++) {
			this.writeVariableToFMU(this.getMeasurementMappings().get(i), initialTsse);
		}
		for (int i=0; i<this.getSetpointMappings().size(); i++) {
			this.writeVariableToFMU(this.getSetpointMappings().get(i), initialTsse);
		}
		
		if (evaluationEndTime>0) {
			double fmuEndTime = (evaluationEndTime-initialTsse.getGlobalTime()) / this.getStaticModel().getModelStepSizeMilliSeconds();
			this.getSimulation().init(0, fmuEndTime);
		} else {
			this.getSimulation().init(0);
		}
		
	}

	/**
	 * Simulate a single {@link TechnicalSystemStateEvaluation} with the FMU.
	 * @param tsse the tsse
	 */
	public void simulateTsse(TechnicalSystemStateEvaluation tsse) {

		// --- Set values for measurements and setpoints ------------
		for (int i=0; i<this.getMeasurementMappings().size(); i++) {
			this.writeVariableToFMU(this.getMeasurementMappings().get(i), tsse);
		}
		for (int i=0; i<this.getSetpointMappings().size(); i++) {
			this.writeVariableToFMU(this.getSetpointMappings().get(i), tsse);
		}
		
		// --- Perform the actual simulation ------------------------
		long stepSize = tsse.getStateTime() / this.getStaticModel().getModelStepSizeMilliSeconds();
		
		this.getSimulation().doStep(stepSize);

		// --- Get "result measurements" from the FMU ---------------
		for (int i=0; i<this.getResultMappings().size(); i++) {
			this.readVariableFromFMU(this.getResultMappings().get(i), tsse);
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
	 * This generic implementation will just transfer the values unchanged, according to the corresponding {@link FmuVariableMapping}.
	 * If a variable requires a special pre-processing, this can be implemented in a model-specific subclass. 
	 * @param variableMapping the variable mapping
	 * @param tsse the tsse
	 */
	protected void writeVariableToFMU(FmuVariableMapping variableMapping, TechnicalSystemStateEvaluation tsse) {
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
	}
	
	
	/**
	 * Reads the value from a FMU output variable and writes it to the corresponding {@link FixedVariable} of the {@link TechnicalSystemStateEvaluation}.
	 * This generic implementation will just transfer the values unchanged, according to the corresponding {@link FmuVariableMapping}.
	 * If a variable requires a special post-processing, this can be implemented in a model-specific subclass. 
	 * @param variableMapping the variable mapping
	 * @param tsse the tsse
	 */
	protected void readVariableFromFMU(FmuVariableMapping variableMapping, TechnicalSystemStateEvaluation tsse) {
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
			Path fmuRelativePath = Paths.get(this.getStaticModel().getFmuFilePath());
			File fmuFile = this.getProjectFolderPath().resolve(fmuRelativePath).toFile();
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
	private Vector<FmuParameterSettings> getStaticParameters() {
		return this.getStaticModel().getParameterSettings();
	}
	
	/**
	 * Gets the FMU variable mappings for setpoint variables.
	 * @return the setpoint mappings
	 */
	private Vector<FmuVariableMapping> getSetpointMappings() {
		if (setpointMappings==null) {
			setpointMappings = this.getStaticModel().getIoVariablesByType(IoVariableType.SETPOINT);
		}
		return setpointMappings;
	}
	
	/**
	 * Gets the FMU variable mappings for measurement variables.
	 * @return the measurement mappings
	 */
	private Vector<FmuVariableMapping> getMeasurementMappings() {
		if (measurementMappings==null) {
			measurementMappings = this.getStaticModel().getIoVariablesByType(IoVariableType.MEASUREMENT);
		}
		return measurementMappings;
	}
	
	/**
	 * Gets the FMU variable mappings for result variables.
	 * @return the result mappings
	 */
	public Vector<FmuVariableMapping> getResultMappings() {
		if (resultMappings==null) {
			resultMappings = this.getStaticModel().getIoVariablesByType(IoVariableType.RESULT);
		}
		return resultMappings;
	}

	/**
	 * Terminates the FMU simulation.
	 */
	public void terminateSimulation() {
		this.getSimulation().terminate();
	}
	
	/**
	 * Gets the project folder path.
	 * @return the project folder path
	 */
	private Path getProjectFolderPath() {
		if (projectFolderPath==null) {
			projectFolderPath = Paths.get(Application.getProjectFocused().getProjectFolderFullPath());
		}
		return projectFolderPath;
	}
	
}
