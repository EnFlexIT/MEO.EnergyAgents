package de.enflexit.meo.modellica.eomIntegration;


/**
 * This class describes the mapping for an FMU variable that represents a static parameer of the system.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuVariableMappingStaticParameter extends AbstractFmuVariableMapping {

	private static final long serialVersionUID = 7195937285623102830L;
	
	private Object value;
	private String unit;

	/**
	 * Instantiates a new static datamodel variable description.
	 * @param fmuVariableName the fmu variable name
	 * @param value the value
	 * @param unit the unit
	 */
	public FmuVariableMappingStaticParameter(String fmuVariableName, Object value, String unit) {
		this.setFmuVariableName(fmuVariableName);
		this.value = value;
		this.unit = unit;
	}

	/**
	 * Gets the value.
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the value for this variable, which should be an Integer, Boolean or Double object, according to the type of the corresponding FMU variable.
	 * @param value the new value
	 */
	public void setValue(Object value) {
		this.value = value;
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
