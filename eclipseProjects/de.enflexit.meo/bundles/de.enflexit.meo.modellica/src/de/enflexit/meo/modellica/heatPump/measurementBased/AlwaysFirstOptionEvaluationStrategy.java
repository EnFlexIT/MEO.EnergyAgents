package de.enflexit.meo.modellica.heatPump.measurementBased;

import java.util.Vector;

import javax.swing.JComponent;

import de.enflexit.meo.modellica.eomIntegration.FmuOptionModelCalculation;
import energy.OptionModelController;
import energy.evaluation.AbstractEvaluationStrategy;
import energy.evaluation.TechnicalSystemStateDeltaEvaluation;
import energy.optionModel.TechnicalSystemStateEvaluation;

/**
 * A simple evaluation strategy that always chooses the first possible delta evaluation.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class AlwaysFirstOptionEvaluationStrategy extends AbstractEvaluationStrategy {
	
	/**
	 * Instantiates a new follow measurement evaluation strategy.
	 * @param optionModelController the option model controller
	 */
	public AlwaysFirstOptionEvaluationStrategy(OptionModelController optionModelController) {
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
//			System.out.println("[" + this.getClass().getSimpleName() + "] " + deltaSteps.size() + " possible next steps");
			if (deltaSteps.size()==0) {
				this.print(this.getEvaluationThread().getName() + ": => No further delta steps possible => interrupt search!", true);
				break;
			}
			
			// --- Find the delta step with the corresponding setpoint value ------------
			int decisionIndex = 0;
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
