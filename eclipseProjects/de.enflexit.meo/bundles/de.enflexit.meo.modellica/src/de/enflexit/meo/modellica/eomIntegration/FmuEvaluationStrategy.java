package de.enflexit.meo.modellica.eomIntegration;

import java.util.Vector;

import javax.swing.JComponent;

import energy.OptionModelController;
import energy.evaluation.AbstractEvaluationStrategy;

/**
 * A general evaluation strategy for working with FMU models in an EOM context. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuEvaluationStrategy extends AbstractEvaluationStrategy {

	/**
	 * Instantiates a new fmu evaluation strategy.
	 * @param optionModelController the option model controller
	 */
	public FmuEvaluationStrategy(OptionModelController optionModelController) {
		super(optionModelController);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see energy.evaluation.AbstractEvaluationStrategy#getCustomToolBarElements()
	 */
	@Override
	public Vector<JComponent> getCustomToolBarElements() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see energy.evaluation.AbstractEvaluationStrategy#runEvaluation()
	 */
	@Override
	public void runEvaluation() {
		// TODO Auto-generated method stub

	}

}
