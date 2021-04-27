package de.enflexit.meo.modellica.eomIntegration;

import java.io.Serializable;
import java.util.Vector;

/**
 * General static data model for FMU integration.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuStaticDataModel implements Serializable{
	
	private static final long serialVersionUID = 7476089670646709528L;
	
	private String fmuFilePath;
	private long modelStepSizeMilliSeconds = 1000;

	private Vector<FmuVariableMappingStaticParameter> systemParameters;
	private Vector<FmuVariableMappingIoList> ioVariables;
	private Vector<FmuVariableMappingInterfaceFlow> flowVariables;

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
	 * Gets the system parameters.
	 * @return the system parameters
	 */
	public Vector<FmuVariableMappingStaticParameter> getSystemParameters() {
		if (systemParameters==null) {
			systemParameters = new Vector<FmuVariableMappingStaticParameter>();
		}
		return systemParameters;
	}
	
	/**
	 * Gets the io variables.
	 * @return the io variables
	 */
	public Vector<FmuVariableMappingIoList> getIoVariables() {
		if (ioVariables==null) {
			ioVariables = new Vector<FmuVariableMappingIoList>();
		}
		return ioVariables;
	}
	
	/**
	 * Gets the flow variables.
	 * @return the flow variables
	 */
	public Vector<FmuVariableMappingInterfaceFlow> getInterfaceFlowVariables() {
		if (flowVariables==null) {
			flowVariables = new Vector<FmuVariableMappingInterfaceFlow>();
		}
		return flowVariables;
	}
	
}
