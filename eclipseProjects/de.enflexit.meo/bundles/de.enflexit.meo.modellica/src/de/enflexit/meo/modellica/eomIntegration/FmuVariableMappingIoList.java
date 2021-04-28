package de.enflexit.meo.modellica.eomIntegration;

/**
 * This class describes the mapping between a variable from an EOM technical system's IO list and the corresponding FMU variable.  
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuVariableMappingIoList extends AbstractFmuVariableMapping {

	private static final long serialVersionUID = -6112678420685751272L;
	
	public enum IoVariableType{
		SETPOINT, MEASUREMENT, RESULT
	}
	
	private String eomVariableName;
	private String unit;
	private IoVariableType variableType;
	
	/**
	 * Instantiates a new FMU variable mapping for an IO list variable
	 * @param eomVariableName the EOM variable name
	 * @param fmuVariableName the FMU variable name
	 * @param variableType the variable type
	 */
	public FmuVariableMappingIoList(String eomVariableName, String fmuVariableName, IoVariableType variableType) {
		this(eomVariableName, fmuVariableName, variableType, null);
	}

	/**
	 * Instantiates a new FMU variable mapping for an IO list variable
	 * @param eomVariableName the EOM variable name
	 * @param fmuVariableName the TMU variable name
	 * @param variableType the variable type
	 * @param unit the unit
	 */
	public FmuVariableMappingIoList(String eomVariableName, String fmuVariableName, IoVariableType variableType, String unit) {
		this.setFmuVariableName(fmuVariableName);
		this.eomVariableName = eomVariableName;
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
