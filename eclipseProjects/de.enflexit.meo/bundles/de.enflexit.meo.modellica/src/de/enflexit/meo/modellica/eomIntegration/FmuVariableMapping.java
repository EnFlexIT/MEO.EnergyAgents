package de.enflexit.meo.modellica.eomIntegration;

import java.io.Serializable;

/**
 * This class describes the mapping between the variables in an EOM TSSE's I/O list and the input or output variables of an FMU. 
 * Each variable has one of three types, that are distinguished by the {@link IoVariableType} enumeration:
 * <ul>
 * 	<li>Setpoints, that can be set by the control strategy. Input variables for the FMU.</li>
 * 	<li>Measurements, that describe relevant environment information. Input variables for the FMU.</li>
 * 	<li>Results, that describes outcomes of the simulation. Output variables of the FMU.</li>
 * </ul>
 * 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuVariableMapping implements Serializable {

	private static final long serialVersionUID = -6112678420685751272L;
	
	public enum IoVariableType{
		SETPOINT, MEASUREMENT, RESULT
	}
	
	private String eomVariableName;
	private String fmuVariableName;
	private IoVariableType variableType;
	private String unit;
	
	/**
	 * Instantiates a new FMU variable mapping for an IO list variable
	 * @param eomVariableName the EOM variable name
	 * @param fmuVariableName the FMU variable name
	 * @param variableType the variable type
	 */
	public FmuVariableMapping(String eomVariableName, String fmuVariableName, IoVariableType variableType) {
		this(eomVariableName, fmuVariableName, variableType, null);
	}

	/**
	 * Instantiates a new FMU variable mapping for an IO list variable
	 * @param eomVariableName the EOM variable name
	 * @param fmuVariableName the TMU variable name
	 * @param variableType the variable type
	 * @param unit the unit
	 */
	public FmuVariableMapping(String eomVariableName, String fmuVariableName, IoVariableType variableType, String unit) {
		this.setFmuVariableName(fmuVariableName);
		this.setEomVariableName(eomVariableName);
		this.setVariableType(variableType);
		this.setUnit(unit);
	}

	/**
	 * Gets the eom variable name.
	 * @return the eom variable name
	 */
	public String getEomVariableName() {
		return eomVariableName;
	}

	/**
	 * Sets the eom variable name.
	 * @param eomVariableName the new eom variable name
	 */
	public void setEomVariableName(String eomVariableName) {
		this.eomVariableName = eomVariableName;
	}

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
	 * Gets the variable type.
	 * @return the variable type
	 */
	public IoVariableType getVariableType() {
		return variableType;
	}

	/**
	 * Sets the variable type.
	 * @param variableType the new variable type
	 */
	public void setVariableType(IoVariableType variableType) {
		this.variableType = variableType;
	}

	/**
	 * Gets the unit.
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * Sets the unit.
	 * @param unit the new unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
