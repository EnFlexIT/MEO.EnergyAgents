package de.enflexit.meo.modellica.eomIntegration;

import java.io.Serializable;
import java.util.Vector;

import org.simpleframework.xml.Transient;

import de.enflexit.meo.modellica.eomIntegration.FmuVariableMapping.IoVariableType;
import energy.optionModel.TimeUnit;

/**
 * General static data model for FMU integration.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuStaticDataModel implements Serializable{
	
	private static final long serialVersionUID = 7476089670646709528L;
	
	private String fmuFilePath;
	private long modelStepSizeMilliSeconds = 1000;
	private TimeUnit stepSizeDisplayTimeUnit;

	private Vector<FmuParameterSettings> parameterSettings;
	private Vector<FmuVariableMapping> variableMappings;
	private Vector<FmuInterfaceFlowMapping> interfaceFlowMappings;
	
	@Transient
	protected FmuSimulationWrapper simulationWrapper;

	/**
	 * Gets the fmu file path.
	 * @return the fmu file path
	 */
	public String getFmuFilePath() {
		return fmuFilePath;
	}

	/**
	 * Sets the fmu file path.
	 * @param fmuFilePath the new fmu file path
	 */
	public void setFmuFilePath(String fmuFilePath) {
		this.fmuFilePath = fmuFilePath;
	}

	/**
	 * Gets the model step size in milliseconds.
	 * @return the model step size in milliseconds
	 */
	public long getModelStepSizeMilliSeconds() {
		return modelStepSizeMilliSeconds;
	}

	/**
	 * Sets the model step size in milliseconds
	 * @param modelStepSizeMilliSeconds the new model step size in milliseconds
	 */
	public void setModelStepSizeMilliSeconds(long modelStepSizeMilliSeconds) {
		this.modelStepSizeMilliSeconds = modelStepSizeMilliSeconds;
	}

	/**
	 * Gets the step size display time unit.
	 * @return the step size display time unit
	 */
	public TimeUnit getStepSizeDisplayTimeUnit() {
		return stepSizeDisplayTimeUnit;
	}

	/**
	 * Sets the step size display time unit.
	 * @param stepSizeDisplayTimeUnit the new step size display time unit
	 */
	public void setStepSizeDisplayTimeUnit(TimeUnit stepSizeDisplayTimeUnit) {
		this.stepSizeDisplayTimeUnit = stepSizeDisplayTimeUnit;
	}

	/**
	 * Gets the static parameters.
	 * @return the static parameters
	 */
	public Vector<FmuParameterSettings> getParameterSettings() {
		if (parameterSettings==null) {
			parameterSettings = new Vector<FmuParameterSettings>();
		}
		return parameterSettings;
	}
	
	/**
	 * Gets the io list mappings.
	 * @return the io list mappings
	 */
	public Vector<FmuVariableMapping> getVariableMappings() {
		if (variableMappings==null) {
			variableMappings = new Vector<FmuVariableMapping>();
		}
		return variableMappings;
	}
	
	/**
	 * Gets the interface flow mappings.
	 * @return the interface flow mappings
	 */
	public Vector<FmuInterfaceFlowMapping> getInterfaceFlowMappings() {
		if (interfaceFlowMappings==null) {
			interfaceFlowMappings = new Vector<FmuInterfaceFlowMapping>();
		}
		return interfaceFlowMappings;
	}
	
	/**
	 * Gets the IO variable mappings of the specified type.
	 * @param variableType the variable type
	 * @return the IO variables by type
	 */
	public Vector<FmuVariableMapping> getIoVariablesByType(IoVariableType variableType){
		Vector<FmuVariableMapping> variablesByType = new Vector<FmuVariableMapping>();
		for (int i=0; i<this.getVariableMappings().size(); i++){
			if (this.getVariableMappings().get(i).getVariableType()==variableType) {
				variablesByType.add(this.getVariableMappings().get(i));
			}
		}
		return variablesByType;
	}
	
	/**
	 * Gets the {@link FmuSimulationWrapper} for the FMU. This implementation returns a generic implementation,
	 * that can be used if all variables can be exchanged between the FMU and EOM models without any changes.
	 * If a pre- or postprocessing of variables is required, override this method to specify a model-specific
	 * implementation, which takes care of the necessary adjustments.  
	 * @return the fmu simulation wrapper
	 */
	public FmuSimulationWrapper getSimulationWrapper() {
		if (simulationWrapper==null) {
			simulationWrapper = new FmuSimulationWrapper(this);
		}
		return simulationWrapper;
	}
	
}
