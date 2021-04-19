package de.enflexit.meo.modellica.eomIntegration;

import energy.optionModel.AbstractDomainModel;

/**
 * The Class defines a variable mapping for interface flows (energy or good).
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class VariableMappingInterfaceFlow extends AbstractVariableMapping {

	private static final long serialVersionUID = 6840522592071561395L;
	
	private AbstractDomainModel domainModel;
	private Object unit;

	/**
	 * Instantiates a new variable mapping interface flow.
	 */
	public VariableMappingInterfaceFlow() {
		this.variableType = VariableType.INTERFACE_FLOW;
	}
	
	/**
	 * Instantiates a new variable mapping interface flow.
	 * @param eomVariableName the eom variable name
	 * @param fmuVariableName the fmu variable name
	 * @param unit the unit
	 */
	public VariableMappingInterfaceFlow(String eomVariableName, String fmuVariableName, Object unit) {
		this.eomVariableName = eomVariableName;
		this.fmuVariableName = fmuVariableName;
		this.unit = unit;
		this.variableType = VariableType.INTERFACE_FLOW;
	}

	/**
	 * Gets the domain model.
	 * @return the domain model
	 */
	public AbstractDomainModel getDomainModel() {
		return domainModel;
	}

	/**
	 * Sets the domain model.
	 * @param domainModel the new domain model
	 */
	public void setDomainModel(AbstractDomainModel domainModel) {
		this.domainModel = domainModel;
	}

	/**
	 * Gets the unit.
	 * @return the unit
	 */
	public Object getUnit() {
		return unit;
	}

	/**
	 * Sets the unit.
	 * @param unit the new unit
	 */
	public void setUnit(Object unit) {
		this.unit = unit;
	}

}
