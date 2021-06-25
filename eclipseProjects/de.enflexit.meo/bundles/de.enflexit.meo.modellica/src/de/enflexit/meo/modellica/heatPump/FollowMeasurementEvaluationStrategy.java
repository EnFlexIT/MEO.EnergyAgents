package de.enflexit.meo.modellica.heatPump;

import java.util.Vector;

import javax.swing.JComponent;

import de.enflexit.meo.modellica.eomIntegration.FmuOptionModelCalculation;
import energy.OptionModelController;
import energy.evaluation.AbstractEvaluationStrategy;
import energy.evaluation.TechnicalSystemStateDeltaEvaluation;
import energy.helper.TechnicalSystemStateHelper;
import energy.optionModel.FixedBoolean;
import energy.optionModel.TechnicalSystemStateEvaluation;

/**
 * A simple evaluation strategy for the HeatPump-FMU that sets the setpoint according to the measurement series.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FollowMeasurementEvaluationStrategy extends AbstractEvaluationStrategy {
	
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
		TechnicalSystemStateEvaluation tsse = this.getInitialTechnicalSystemStateEvaluation();
		
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
				// --- Set next state as new current state ------------------------------
				tsse = tsseNext;
			}
			// --- Stop evaluation ? ----------------------------------------------------
			if (isStopEvaluation()==true) break;
		} // end while
		
		// --- Add the schedule found to the list of results ----------------------------
		this.addStateToResults(tsse);
		// --- Done ! -------------------------------------------------------------------
		
		((FmuOptionModelCalculation)this.getOptionModelCalculation()).getSimulationWrapper().terminateSimulation();
	}

}
