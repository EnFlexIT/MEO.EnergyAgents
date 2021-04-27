package de.enflexit.meo.modellica.eomIntegration;

import energy.OptionModelController;
import energy.calculations.AbstractOptionModelCalculation;
import energy.optionModel.AbstractInterfaceFlow;
import energy.optionModel.Duration;
import energy.optionModel.EnergyFlowInWatt;
import energy.optionModel.TechnicalInterface;
import energy.optionModel.TechnicalSystemStateEvaluation;

/**
 * A general OptionModelCalculation, translating FMU output variables to flows and loads.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 *
 */
public class FmuOptionModelCalculation extends AbstractOptionModelCalculation {

	public FmuOptionModelCalculation(OptionModelController optionModelController) {
		super(optionModelController);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Duration getDuration(DurationType durationType, TechnicalSystemStateEvaluation tsse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractInterfaceFlow getEnergyOrGoodFlow(TechnicalSystemStateEvaluation tsse, TechnicalInterface ti,
			boolean isManualConfiguration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnergyFlowInWatt getEnergyFlowForLosses(TechnicalSystemStateEvaluation tsse) {
		// TODO Auto-generated method stub
		return null;
	}

}
