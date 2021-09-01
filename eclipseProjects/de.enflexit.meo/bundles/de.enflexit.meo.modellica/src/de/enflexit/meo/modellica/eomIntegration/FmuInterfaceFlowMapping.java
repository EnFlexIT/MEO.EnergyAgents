package de.enflexit.meo.modellica.eomIntegration;

import java.io.Serializable;

import energy.optionModel.AbstractDomainModel;

/**
 * This class describes the mapping from an FMU output variable to an EOM interface flow. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuInterfaceFlowMapping implements Serializable {

	private static final long serialVersionUID = -5019215289964512272L;
	
	private String fmuVariableName; 
	private String domain;
	private AbstractDomainModel domainModel;
	private Object unit;
	
	/**
	 * Gets the FMU variable name for this interface flow.
	 * @return the FMU variable name
	 */
	public String getFmuVariableName() {
		return fmuVariableName;
	}

	/**
	 * Sets the FMU variable name for this interface flow.
	 * @param fmuVariableName the new FMU variable name
	 */
	public void setFmuVariableName(String fmuVariableName) {
		this.fmuVariableName = fmuVariableName;
	}

	/**
	 * Gets the domain.
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}
	
	/**
	 * Sets the domain.
	 * @param domain the new domain
	 */
	public void setDomain(String domain) {
		this.domain = domain;
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
