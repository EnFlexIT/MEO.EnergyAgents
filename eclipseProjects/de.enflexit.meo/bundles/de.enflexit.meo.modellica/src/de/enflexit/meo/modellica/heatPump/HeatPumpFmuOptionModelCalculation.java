package de.enflexit.meo.modellica.heatPump;

import energy.OptionModelController;
import energy.calculations.AbstractOptionModelCalculation;
import energy.helper.TechnicalSystemStateHelper;
import energy.optionModel.AbstractInterfaceFlow;
import energy.optionModel.AbstractUsageOfInterface;
import energy.optionModel.Duration;
import energy.optionModel.EnergyFlowInWatt;
import energy.optionModel.TechnicalInterface;
import energy.optionModel.TechnicalSystemStateEvaluation;
import energy.optionModel.UsageOfInterfaceEnergy;
import energy.optionModel.UsageOfInterfaceGood;

public class HeatPumpFmuOptionModelCalculation extends AbstractOptionModelCalculation {

	public HeatPumpFmuOptionModelCalculation(OptionModelController optionModelController) {
		super(optionModelController);
	}

	@Override
	public Duration getDuration(DurationType durationType, TechnicalSystemStateEvaluation tsse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractInterfaceFlow getEnergyOrGoodFlow(TechnicalSystemStateEvaluation tsse, TechnicalInterface ti, boolean isManualConfiguration) {
		
		AbstractInterfaceFlow resultFlow = null;
		AbstractUsageOfInterface auoi = TechnicalSystemStateHelper.getUsageOfInterfaces(tsse.getUsageOfInterfaces(), ti.getInterfaceID());
		if (auoi instanceof UsageOfInterfaceEnergy) {
			resultFlow = ((UsageOfInterfaceEnergy)auoi).getEnergyFlow();
		} else if (auoi instanceof UsageOfInterfaceGood) {
			resultFlow = ((UsageOfInterfaceGood)auoi).getGoodFlow();
		}
		return resultFlow;
	}

	@Override
	public EnergyFlowInWatt getEnergyFlowForLosses(TechnicalSystemStateEvaluation tsse) {
		// TODO Auto-generated method stub
		return null;
	}

}
