package de.enflexit.meo.modellica.eomIntegration;

import java.io.Serializable;
import java.util.Vector;

import org.simpleframework.xml.Transient;

import de.enflexit.meo.modellica.eomIntegration.FmuVariableMappingIoList.IoVariableType;

/**
 * General static data model for FMU integration.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuStaticDataModel implements Serializable{
	
	private static final long serialVersionUID = 7476089670646709528L;
	
	private String fmuFilePath;
	private long modelStepSizeMilliSeconds = 1000;

	private Vector<FmuVariableMappingStaticParameter> staticParameters;
	private Vector<FmuVariableMappingIoList> ioListMappings;
	private Vector<FmuVariableMappingInterfaceFlow> interfaceFlowMappings;
	
	@Transient
	private FmuSimulationWrapper fmuSimulationWrapper;

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
	 * Gets the static parameters.
	 * @return the static parameters
	 */
	public Vector<FmuVariableMappingStaticParameter> getStaticParameters() {
		if (staticParameters==null) {
			staticParameters = new Vector<FmuVariableMappingStaticParameter>();
		}
		return staticParameters;
	}
	
	/**
	 * Gets the io list mappings.
	 * @return the io list mappings
	 */
	public Vector<FmuVariableMappingIoList> getIoListMappings() {
		if (ioListMappings==null) {
			ioListMappings = new Vector<FmuVariableMappingIoList>();
		}
		return ioListMappings;
	}
	
	/**
	 * Gets the interface flow mappings.
	 * @return the interface flow mappings
	 */
	public Vector<FmuVariableMappingInterfaceFlow> getInterfaceFlowMappings() {
		if (interfaceFlowMappings==null) {
			interfaceFlowMappings = new Vector<FmuVariableMappingInterfaceFlow>();
		}
		return interfaceFlowMappings;
	}
	
	/**
	 * Gets the IO variable mappings of the specified type.
	 * @param variableType the variable type
	 * @return the IO variables by type
	 */
	public Vector<FmuVariableMappingIoList> getIoVariablesByType(IoVariableType variableType){
		Vector<FmuVariableMappingIoList> variablesByType = new Vector<FmuVariableMappingIoList>();
		for (int i=0; i<this.getIoListMappings().size(); i++){
			if (this.getIoListMappings().get(i).getVariableType()==variableType) {
				variablesByType.add(this.getIoListMappings().get(i));
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
	public FmuSimulationWrapper getFmuSimulationWrapper() {
		if (fmuSimulationWrapper==null) {
			fmuSimulationWrapper = new FmuSimulationWrapper(this);
		}
		return fmuSimulationWrapper;
	}
}
