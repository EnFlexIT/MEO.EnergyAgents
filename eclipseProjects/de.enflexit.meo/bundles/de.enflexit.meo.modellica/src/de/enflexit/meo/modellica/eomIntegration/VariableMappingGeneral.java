package de.enflexit.meo.modellica.eomIntegration;

import java.io.Serializable;

import energy.optionModel.SystemVariableDefinition;

/**
 * The Class VariableMapping defines a mapping between an EOM {@link SystemVariableDefinition} and a corresponding FMU variable. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class VariableMappingGeneral extends AbstractVariableMapping implements Serializable{
	
	private static final long serialVersionUID = 7569180894156845628L;
	
	private String unitString;
	
	/**
	 * Instantiates an empty new variable mapping.
	 */
	public VariableMappingGeneral() {}
	
	/**
	 * Instantiates a new variable mapping that is initialized with the provided parameters.
	 * @param eomVariableName the eom variable name
	 * @param fmuVariableName the fmu variable name
	 * @param variableType the variable type
	 */
	public VariableMappingGeneral(String eomVariableName, String fmuVariableName, VariableType variableType) {
		this(eomVariableName, fmuVariableName, variableType, null);
	}

	/**
	 * Instantiates a new variable mapping that is initialized with the provided parameters.
	 * @param eomVariableName the eom variable ID
	 * @param fmuVariableName the fmu variable name
	 * @param variableType the variable type
	 */
	public VariableMappingGeneral(String eomVariableName, String fmuVariableName, VariableType variableType, String unitString) {
		this.eomVariableName = eomVariableName;
		this.fmuVariableName = fmuVariableName;
		this.variableType = variableType;
		this.unitString = unitString;
	}

	public String getUnitString() {
		return unitString;
	}

	public void setUnitString(String unitString) {
		this.unitString = unitString;
	}
	
}
