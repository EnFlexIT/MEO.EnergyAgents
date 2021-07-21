package de.enflexit.meo.modellica.heatPump.realOutput;

import java.util.List;

import de.enflexit.meo.modellica.eomIntegration.FmuOptionModelCalculation;
import de.enflexit.meo.modellica.eomIntegration.FmuSimulationWrapper;
import de.enflexit.meo.modellica.eomIntegration.FmuStaticDataModel;
import energy.OptionModelController;
import energy.domain.DefaultDomainModelElectricity;
import energy.domain.DefaultDomainModelElectricity.PowerType;
import energy.optionModel.AbstractInterfaceFlow;
import energy.optionModel.Duration;
import energy.optionModel.EnergyFlowInWatt;
import energy.optionModel.EnergyUnitFactorPrefixSI;
import energy.optionModel.SystemVariableDefinition;
import energy.optionModel.SystemVariableDefinitionStaticModel;
import energy.optionModel.TechnicalInterface;
import energy.optionModel.TechnicalSystemStateEvaluation;

/**
 * A general OptionModelCalculation, translating FMU output variables to flows and loads.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 *
 */
public class HeatPumpFmuOptionModelCalculation extends FmuOptionModelCalculation {
	
	private TechnicalSystemStateEvaluation tsseLastCalculated;
	
	private double pElPerPhaseKW;

	/**
	 * Instantiates a new fmu option model calculation.
	 * @param optionModelController the option model controller
	 */
	public HeatPumpFmuOptionModelCalculation(OptionModelController optionModelController) {
		super(optionModelController);
	}

	/* (non-Javadoc)
	 * @see energy.calculations.AbstractOptionModelCalculation#getDuration(energy.calculations.AbstractOptionModelCalculation.DurationType, energy.optionModel.TechnicalSystemStateEvaluation)
	 */
	@Override
	public Duration getDuration(DurationType durationType, TechnicalSystemStateEvaluation tsse) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see energy.calculations.AbstractOptionModelCalculation#getEnergyOrGoodFlow(energy.optionModel.TechnicalSystemStateEvaluation, energy.optionModel.TechnicalInterface, boolean)
	 */
	@Override
	public AbstractInterfaceFlow getEnergyOrGoodFlow(TechnicalSystemStateEvaluation tsse, TechnicalInterface ti, boolean isManualConfiguration) {
		
		EnergyFlowInWatt interfaceFlow = null;
		
		// --- Let the FMU simulate this step if necessary ----------
		if (tsse!=this.tsseLastCalculated || isManualConfiguration==true) {
			
			// --- Let the FMU perform the simulation step ----------
			this.getSimulationWrapper().simulateTsse(tsse);
			tsseLastCalculated = tsse;
			
			// --- Calculate the total active power per phase -------
			double pElHeatPumpKW = this.getSimulationWrapper().getSimulation().read("Pel_HP").asDouble();
//			System.out.println("Read variable Pel_HP from FMU: " + pElHeatPumpKW);
			double pElCoilKW = this.getSimulationWrapper().getSimulation().read("Pel_COIL").asDouble();
//			System.out.println("Read variable Pel_COIL from FMU: " + pElCoilKW);
			this.pElPerPhaseKW = (pElHeatPumpKW+pElCoilKW)/3;
		}
		
		// --- Generate the energy flow instance for the interface --
		if (ti.getDomain().equals("Electricity")) {
			interfaceFlow = new EnergyFlowInWatt();
			interfaceFlow.setSIPrefix(EnergyUnitFactorPrefixSI.KILO_K_3);
			DefaultDomainModelElectricity domainModel = (DefaultDomainModelElectricity) ti.getDomainModel();
			if (domainModel.getPowerType()==PowerType.ActivePower) {
				// --- Active power ---------------------------------
				interfaceFlow.setValue(this.pElPerPhaseKW);
			} else {
				// --- Reactive power -------------------------------
				interfaceFlow.setValue(0);
			}
		}
		
		return interfaceFlow;
	}

	/* (non-Javadoc)
	 * @see energy.calculations.AbstractOptionModelCalculation#getEnergyFlowForLosses(energy.optionModel.TechnicalSystemStateEvaluation)
	 */
	@Override
	public EnergyFlowInWatt getEnergyFlowForLosses(TechnicalSystemStateEvaluation tsse) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Gets the static model.
	 * @return the static model
	 */
	public FmuStaticDataModel getStaticModel() {
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
			
			//TODO workaround until the null problem is fixed -------
			if (staticModel==null) {
				staticModel = new HeatPumpFmuStaticDataModel();
			}
		}
		return staticModel;
	}
	
	/**
	 * Gets the simulation wrapper.
	 * @return the simulation wrapper
	 */
	public FmuSimulationWrapper getSimulationWrapper() {
		if (simulationWrapper==null) {
			simulationWrapper = new FmuSimulationWrapper(this.getStaticModel());
		}
		return simulationWrapper;
	}
	
}
