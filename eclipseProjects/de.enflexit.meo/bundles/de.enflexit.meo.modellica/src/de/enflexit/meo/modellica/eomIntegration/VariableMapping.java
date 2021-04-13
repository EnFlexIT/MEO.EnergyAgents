package de.enflexit.meo.modellica.eomIntegration;

import energy.optionModel.SystemVariableDefinition;

/**
 * The Class VariableMapping defines a mapping between an EOM {@link SystemVariableDefinition} and a corresponding FMU variable. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class VariableMapping {
	private SystemVariableDefinition eomVariableDefinition;
	private String fmuVariableName;
	
	/**
	 * Gets the EOM {@link SystemVariableDefinition}.
	 * @return the EOM variable definition
	 */
	public SystemVariableDefinition getEomVariableDefinition() {
		return eomVariableDefinition;
	}
	
	/**
	 * Sets the EOM {@link SystemVariableDefinition}.
	 * @param eomVariableDefinition the new EOM variable definition
	 */
	public void setEomVariableDefinition(SystemVariableDefinition eomVariableDefinition) {
		this.eomVariableDefinition = eomVariableDefinition;
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
