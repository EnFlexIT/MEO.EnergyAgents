package de.enflexit.meo.modellica.eomIntegration;

import java.io.Serializable;

public abstract class AbstractVariableMapping implements Serializable{

	private static final long serialVersionUID = -5838443906804601750L;
	
	/**
	 * Possible interpretations of FMU variables in the EOM context.
	 * - STATIC_DATAMODEL: FMU input variables describing static parameters of the system. Set once initially.
	 * - MEASUREMENT: FMU input variables describing external measurements. Set before each evaluation step.
	 * - SETPOINT: FMU input variables describing system setpoints. Set before each evaluation step.
	 * - RESULT: FMU output variables describing simulation results, like flows or storage loads. Read after each evaluation step.  
	 * - INTERFACE_FLOW: FMU output variables describing the energy or good flow at an interface. Set the domain name as eomVariableID in this case!
	 */
	public enum VariableType{
		STATIC_DATAMODEL,
		MEASUREMENT,
		SETPOINT,
		RESULT,
		INTERFACE_FLOW
	}
	
	protected VariableType variableType;
	protected String eomVariableName;
	protected String fmuVariableName;
	
	/**
	 * Gets the variable type.
	 * @return the variable type
	 */
	public VariableType getVariableType() {
		return variableType;
	}

	/**
	 * Sets the variable type.
	 * @param variableType the new variable type
	 */
	public void setVariableType(VariableType variableType) {
		this.variableType = variableType;
	}

	/**
	 * Gets the EOM variable ID.
	 * @return the EOM variable ID
	 */
	public String getEomVariableName() {
		return eomVariableName;
	}
	/**
	 * Sets the EOM variable name.
	 * @param eomVariableName the new EOM variable name
	 */
	public void setEomVariableName(String eomVariableName) {
		this.eomVariableName = eomVariableName;
	}
	/**
	 * Gets the FMU variable name.
	 * @return the FMU variable name
	 */
	public String getFmuVariableName() {
		return fmuVariableName;
	}
	/**
	 * Sets the FMU variable name.
	 * @param fmuVariableName the new FMU variable name
	 */
	public void setFmuVariableName(String fmuVariableName) {
		this.fmuVariableName = fmuVariableName;
	}

}
