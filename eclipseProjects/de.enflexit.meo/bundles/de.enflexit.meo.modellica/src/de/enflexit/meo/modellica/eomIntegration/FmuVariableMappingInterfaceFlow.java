package de.enflexit.meo.modellica.eomIntegration;

import energy.optionModel.AbstractDomainModel;

/**
 * This class describes the mapping for an FMU variable that represents an EOM interface flow. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuVariableMappingInterfaceFlow extends AbstractFmuVariableMapping {

	private static final long serialVersionUID = -5019215289964512272L;
	
	private String domain;
	private AbstractDomainModel domainModel;
	private Object unit;
	
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
