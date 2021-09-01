package de.enflexit.meo.modellica.heatPump;

import java.util.Vector;

import javax.swing.JComponent;

import de.enflexit.meo.modellica.eomIntegration.AbstractEvaluationStrategyForFMU;
import energy.OptionModelController;
import energy.domain.DefaultDomainModelElectricity;
import energy.domain.DefaultDomainModelElectricity.PowerType;
import energy.evaluation.TechnicalSystemStateDeltaEvaluation;
import energy.helper.TechnicalSystemStateHelper;
import energy.helper.UnitConverter;
import energy.optionModel.EnergyAmount;
import energy.optionModel.EnergyFlowInWatt;
import energy.optionModel.EnergyUnitFactorPrefixSI;
import energy.optionModel.FixedBoolean;
import energy.optionModel.TechnicalInterface;
import energy.optionModel.TechnicalSystemStateEvaluation;
import energy.optionModel.TimeUnit;
import energy.optionModel.UsageOfInterfaceEnergy;

/**
 * A simple evaluation strategy for the HeatPump-FMU that sets the setpoint according to the measurement series.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FollowMeasurementEvaluationStrategy extends AbstractEvaluationStrategyForFMU {
	
	private static final String VARIABLE_ID_HEATPUMP_MEASUREMENT = "hpMeasurement";
	private static final String VARIABLE_ID_HEATPUMP_SETPOINT = "hpSetpoint";
	private static final String VARIABLE_ID_COIL_SETPOINT = "coilSetpoint";
	

	/**
	 * Instantiates a new follow measurement evaluation strategy.
	 * @param optionModelController the option model controller
	 */
	public FollowMeasurementEvaluationStrategy(OptionModelController optionModelController) {
		super(optionModelController);
	}

	/* (non-Javadoc)
	 * @see energy.evaluation.AbstractEvaluationStrategy#getCustomToolBarElements()
	 */
	@Override
	public Vector<JComponent> getCustomToolBarElements() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see energy.evaluation.AbstractEvaluationStrategy#runEvaluation()
	 */
	@Override
	public void runEvaluation() {
		// --- Initialize search --------------------------------------------------------
		TechnicalSystemStateEvaluation tsse = this.getInitialTechnicalSystemStateEvaluation().getParent();
		
		this.getSimulationWrapper().initializeFmu(tsse, this.getEndTime());
		
		// --- Search by walking through time -------------------------------------------
		while (tsse.getGlobalTime() < this.getEndTime() ) {
			
			// --- Get the possible subsequent steps and states -------------------------
			Vector<TechnicalSystemStateDeltaEvaluation> deltaSteps = this.getAllDeltaEvaluationsStartingFromTechnicalSystemState(tsse);
			if (deltaSteps.size()==0) {
				this.print(this.getEvaluationThread().getName() + ": => No further delta steps possible => interrupt search!", true);
				break;
			}
			
			// --- Get the setpoint value for the heatpump from the measurement (same for all deltas)
			TechnicalSystemStateEvaluation nextTSSE = deltaSteps.get(0).getTechnicalSystemStateEvaluation();
			FixedBoolean hpMeasurement = (FixedBoolean) TechnicalSystemStateHelper.getFixedVariable(nextTSSE.getIOlist(), VARIABLE_ID_HEATPUMP_MEASUREMENT);
			
			// --- Find the delta step with the corresponding setpoint value ------------
			int decisionIndex = -1;
			for (int i=0; i<deltaSteps.size(); i++) {
				TechnicalSystemStateEvaluation deltaTSSE = deltaSteps.get(i).getTechnicalSystemStateEvaluation();
				FixedBoolean heatPumpSetpoint = (FixedBoolean) TechnicalSystemStateHelper.getFixedVariable(deltaTSSE.getIOlist(), VARIABLE_ID_HEATPUMP_SETPOINT);
				FixedBoolean coilSetpoint = (FixedBoolean) TechnicalSystemStateHelper.getFixedVariable(deltaTSSE.getIOlist(), VARIABLE_ID_COIL_SETPOINT);
				if (heatPumpSetpoint.isValue()==hpMeasurement.isValue() && coilSetpoint.isValue()==false) {
					decisionIndex = i;
					break;
				}
			}
			
			// --- Set new current TechnicalSystemStateEvaluation -----------------------
			TechnicalSystemStateDeltaEvaluation tssDeltaDecision = deltaSteps.get(decisionIndex);
			TechnicalSystemStateEvaluation tsseNext = this.getNextTechnicalSystemStateEvaluation(tsse, tssDeltaDecision);
			
			if (tsseNext==null) {
				this.print(this.getEvaluationThread().getName() + ": => Error while using selected delta => interrupt search!", true);
				break;
			} else {
				// --- Use the FMU to simulate the selected state -----------------------
				this.getSimulationWrapper().simulateTsse(tsseNext);
				// --- Update the TSSE's interfaces according to the results ------------
				this.updateInterfaces(tsseNext);
				tsse = tsseNext;
			}
			// --- Stop evaluation ? ----------------------------------------------------
			if (isStopEvaluation()==true) break;
		} // end while
		
		// --- Add the schedule found to the list of results ----------------------------
		this.addStateToResults(tsse);
		// --- Done ! -------------------------------------------------------------------
		
		this.getSimulationWrapper().terminateSimulation();
	}
	
	/**
	 * Sets the interface flows and amounts for the active power interfaces according to the FMU results.
	 * @param tsse the tsse
	 */
	private void updateInterfaces(TechnicalSystemStateEvaluation tsse) {
		// --- Calculate the total active power per phase ---------------------
		double pElHeatPumpKW = this.getSimulationWrapper().getSimulation().read("Pel_HP").asDouble();
		double pElCoilKW = this.getSimulationWrapper().getSimulation().read("Pel_COIL").asDouble();
		double pElPerPhaseKW = (pElHeatPumpKW+pElCoilKW)/3;
		
		// --- Set the flows and amounts for all interfaces -------------------
		for (int i=0; i<tsse.getUsageOfInterfaces().size(); i++) {
			
			UsageOfInterfaceEnergy uoi = (UsageOfInterfaceEnergy) tsse.getUsageOfInterfaces().get(i);

			// --- Set flow and amount for active power electricity interfaces
			TechnicalInterface ti = this.optionModelController.getTechnicalInterface(tsse.getConfigID(), uoi.getInterfaceID());
			if (ti.getDomain().equals("Electricity")) {
				
				DefaultDomainModelElectricity domainModel = (DefaultDomainModelElectricity) ti.getDomainModel();

				if (domainModel.getPowerType()==PowerType.ActivePower) {
					// --- Set the flow for the current state -----------------
					EnergyFlowInWatt interfaceFlow = new EnergyFlowInWatt();
					interfaceFlow.setValue(pElPerPhaseKW);
					interfaceFlow.setSIPrefix(EnergyUnitFactorPrefixSI.KILO_K_3);
					uoi.setEnergyFlow(interfaceFlow);
					
					// --- Calculate the amount for the current state ---------
					double stateTimeHours = UnitConverter.convertDuration(tsse.getStateTime(), TimeUnit.HOUR_H);
					double energyAmountKwh = pElPerPhaseKW * stateTimeHours;
					
					// --- Add the previous amount if applicable --------------
					if (tsse.getParent()!=null) {
						UsageOfInterfaceEnergy previousUsage = (UsageOfInterfaceEnergy) TechnicalSystemStateHelper.getUsageOfInterfaces(tsse.getParent().getUsageOfInterfaces(), uoi.getInterfaceID());
						if (previousUsage!=null && previousUsage.getEnergyAmountCumulated()!=null) {
							energyAmountKwh += previousUsage.getEnergyAmountCumulated().getValue();
						}
					}
					
					// --- Set the cumulated amount ---------------------------
					EnergyAmount amountCumulated = new EnergyAmount();
					amountCumulated.setValue(energyAmountKwh);
					amountCumulated.setSIPrefix(EnergyUnitFactorPrefixSI.KILO_K_3);
					amountCumulated.setTimeUnit(TimeUnit.HOUR_H);
					
					uoi.setEnergyAmountCumulated(amountCumulated);
				}
			}
		}
	}

}
