package de.enflexit.meo.modellica.eomIntegration;

import energy.OptionModelController;
import energy.calculations.AbstractOptionModelCalculation;
import energy.optionModel.Duration;
import energy.optionModel.TechnicalSystemStateEvaluation;

public abstract class FmuOptionModelCalculation extends AbstractOptionModelCalculation {

	protected FmuStaticDataModel staticModel;
	protected FmuSimulationWrapper simulationWrapper;

	public FmuOptionModelCalculation(OptionModelController optionModelController) {
		super(optionModelController);
	}

	@Override
	public Duration getDuration(DurationType durationType, TechnicalSystemStateEvaluation tsse) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Gets the static model.
	 * @return the static model
	 */
	public FmuStaticDataModel getStaticModel() {
		return staticModel;
	}
	
	/**
	 * Gets the simulation wrapper.
	 * @return the simulation wrapper
	 */
	public FmuSimulationWrapper getSimulationWrapper() {
		return simulationWrapper;
	}

}
