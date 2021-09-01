package de.enflexit.meo.modellica.eomIntegration;

import java.io.Serializable;

/**
 * This class describes the configuration of a system parameter of an FMU model, i.e. a
 * variable describing a system property, that is set once when the FMU is initialized.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuParameterSettings implements Serializable {

	private static final long serialVersionUID = 7195937285623102830L;
	
	private String fmuVariableName;
	private Object value;
	private String unit;

	/**
	 * Instantiates a new static datamodel variable description.
	 * @param fmuVariableName the fmu variable name
	 * @param value the value
	 * @param unit the unit
	 */
	public FmuParameterSettings(String fmuVariableName, Object value, String unit) {
		this.setFmuVariableName(fmuVariableName);
		this.value = value;
		this.unit = unit;
	}
	
	/**
	 * Gets the FMU variable name for this system parameter.
	 * @return the FMU variable name
	 */
	public String getFmuVariableName() {
		return fmuVariableName;
	}

	/**
	 * Sets the FMU variable name for this system parameter.
	 * @param fmuVariableName the new FMU variable name
	 */
	public void setFmuVariableName(String fmuVariableName) {
		this.fmuVariableName = fmuVariableName;
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
