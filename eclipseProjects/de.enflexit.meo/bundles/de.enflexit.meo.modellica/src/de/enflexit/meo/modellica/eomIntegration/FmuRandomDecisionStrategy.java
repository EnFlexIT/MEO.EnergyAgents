package de.enflexit.meo.modellica.eomIntegration;

import energy.OptionModelController;
import energy.samples.strategies.RandomDecisionStrategy;


/**
 * FMU version of the {@link RandomDecisionStrategy}, terminating the FMU simulation after the evaluation is done.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuRandomDecisionStrategy extends RandomDecisionStrategy {

	public FmuRandomDecisionStrategy(OptionModelController optionModelController) {
		super(optionModelController);
	}
	
	/* (non-Javadoc)
	 * @see energy.evaluation.AbstractEvaluationStrategy#runEvaluation()
	 */
	@Override
	public void runEvaluation() {
		// --- Let the superclass perform the evaluation ------------
		super.runEvaluation();
		// --- Terminate the FMU after the evaluation is done -------
		((FmuOptionModelCalculation)this.getOptionModelCalculation()).getSimulationWrapper().terminateSimulation();
	}

	
}
