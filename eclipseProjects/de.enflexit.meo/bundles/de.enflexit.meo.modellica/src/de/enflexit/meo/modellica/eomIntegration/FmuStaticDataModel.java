package de.enflexit.meo.modellica.eomIntegration;

import java.io.Serializable;
import java.util.Vector;

import de.enflexit.meo.modellica.eomIntegration.AbstractVariableMapping.VariableType;


/**
 * General static data model for FMU integration.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuStaticDataModel implements Serializable{
	
	private static final long serialVersionUID = 7476089670646709528L;
	
	private String fmuFilePath;
	private long modelStepSizeMilliSeconds = 1000;
	
	private Vector<AbstractVariableMapping> variableMappings;

	/**
	 * Gets the fmu file path.
	 * @return the fmu file path
	 */
	public String getFmuFilePath() {
		return fmuFilePath;
	}

	/**
	 * Sets the fmu file path.
	 * @param fmuFilePath the new fmu file path
	 */
	public void setFmuFilePath(String fmuFilePath) {
		this.fmuFilePath = fmuFilePath;
	}

	/**
	 * Gets the model step size in milliseconds.
	 * @return the model step size in milliseconds
	 */
	public long getModelStepSizeMilliSeconds() {
		return modelStepSizeMilliSeconds;
	}

	/**
	 * Sets the model step size in milliseconds
	 * @param modelStepSizeMilliSeconds the new model step size in milliseconds
	 */
	public void setModelStepSizeMilliSeconds(long modelStepSizeMilliSeconds) {
		this.modelStepSizeMilliSeconds = modelStepSizeMilliSeconds;
	}

	/**
	 * Gets the variable mappings.
	 * @return the variable mappings
	 */
	public Vector<AbstractVariableMapping> getVariableMappings() {
		if (variableMappings==null) {
			variableMappings = new Vector<AbstractVariableMapping>();
		}
		return variableMappings;
	}
	
	/**
	 * Gets the variable mapping with the specified EOM variable name
	 * @param eomName the EOM variable name
	 * @return the variable mapping, null if not found
	 */
	public AbstractVariableMapping getVariableMappingByEomName(String eomName) {
		for (int i=0; i<this.getVariableMappings().size(); i++) {
			if (this.getVariableMappings().get(i).getEomVariableName().equals(eomName)) {
				return this.getVariableMappings().get(i);
			}
		}
		return null;
	}
	
	/**
	 * Gets the variable mapping with the specified FMU variable name.
	 * @param fmuName the FMU variable name
	 * @return the variable mapping, null if not found
	 */
	public AbstractVariableMapping getVariableMappingByFmuName(String fmuName) {
		for (int i=0; i<this.getVariableMappings().size(); i++) {
			if (this.getVariableMappings().get(i).getFmuVariableName().equals(fmuName)) {
				return this.getVariableMappings().get(i);
			}
		}
		return null;
	}
	
	/**
	 * Gets all mappings for variables of the specified type.
	 * @param type the type
	 * @return the variable mappings
	 */
	public Vector<AbstractVariableMapping> getVariableMappingsByType(VariableType type){
		Vector<AbstractVariableMapping> mappings = new Vector<AbstractVariableMapping>();
		for (int i=0; i<this.getVariableMappings().size(); i++) {
			if (this.getVariableMappings().get(i).getVariableType() == type) {
				mappings.add(this.getVariableMappings().get(i));
			}
		}
		return mappings;
	}
	
}
