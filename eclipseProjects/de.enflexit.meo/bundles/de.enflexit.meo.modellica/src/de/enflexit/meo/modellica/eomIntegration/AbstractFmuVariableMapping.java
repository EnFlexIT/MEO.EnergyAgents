package de.enflexit.meo.modellica.eomIntegration;

import java.io.Serializable;

/**
 * Abstract superclass for descriptions of FMU variables in the EOM context 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public abstract class AbstractFmuVariableMapping implements Serializable{
	
	private static final long serialVersionUID = -8288847937265359631L;
	
	private String fmuVariableName;
	private String fmuDataType;

	/**
	 * Gets the fmu variable name.
	 * @return the fmu variable name
	 */
	public String getFmuVariableName() {
		return fmuVariableName;
	}

	/**
	 * Sets the fmu variable name.
	 * @param fmuVariableName the new fmu variable name
	 */
	public void setFmuVariableName(String fmuVariableName) {
		this.fmuVariableName = fmuVariableName;
	}

	/**
	 * Gets the fmu data type.
	 * @return the fmu data type
	 */
	public String getFmuDataType() {
		return fmuDataType;
	}

	/**
	 * Sets the fmu data type.
	 * @param fmuDataType the new fmu data type
	 */
	public void setFmuDataType(String fmuDataType) {
		this.fmuDataType = fmuDataType;
	}
	
	
}