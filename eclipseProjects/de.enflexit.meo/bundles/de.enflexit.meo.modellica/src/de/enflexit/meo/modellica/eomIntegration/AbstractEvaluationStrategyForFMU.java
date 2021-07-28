package de.enflexit.meo.modellica.eomIntegration;

import java.util.List;

import energy.OptionModelController;
import energy.evaluation.AbstractEvaluationStrategy;
import energy.optionModel.SystemVariableDefinition;
import energy.optionModel.SystemVariableDefinitionStaticModel;

/**
 * Abstract superclass for evaluation strategies for FMU models, providing access to 
 * a {@link FmuSimulationWrapper} for interaction with the FMU model.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public abstract class AbstractEvaluationStrategyForFMU extends AbstractEvaluationStrategy {
	
	private FmuStaticDataModel staticModel;
	private FmuSimulationWrapper simulationWapper; 

	/**
	 * Instantiates a new abstract evaluation strategy for FMU.
	 * @param optionModelController the option model controller
	 */
	public AbstractEvaluationStrategyForFMU(OptionModelController optionModelController) {
		super(optionModelController);
	}

	/**
	 * Gets the static model.
	 * @return the static model
	 */
	protected FmuStaticDataModel getStaticModel() {
		if (staticModel==null) {
			// --- Find the static model from the list of system variable definitions
			List<SystemVariableDefinition> systemVariableDefinitions = this.optionModelController.getTechnicalSystem().getSystemVariables();
			for (int i=0; i<systemVariableDefinitions.size(); i++) {
				if (systemVariableDefinitions.get(i) instanceof SystemVariableDefinitionStaticModel) {
					SystemVariableDefinitionStaticModel sysVarDefStaticModel = (SystemVariableDefinitionStaticModel) systemVariableDefinitions.get(i);
					Object sysVar = this.optionModelController.getStaticModelInstance(sysVarDefStaticModel);
					if (sysVar instanceof FmuStaticDataModel) {
						staticModel = (FmuStaticDataModel) sysVar;
						break;
					}
				}
			}
		}
		return staticModel;
	}
	
	/**
	 * Gets the simulation wapper.
	 * @return the simulation wapper
	 */
	protected FmuSimulationWrapper getSimulationWrapper() {
		if (simulationWapper==null) {
			simulationWapper = this.getStaticModel().getSimulationWrapper();
		}
		
		return simulationWapper;
	}
}
