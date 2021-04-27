package de.enflexit.meo.modellica.eomIntegration;

import java.io.File;
import java.util.Vector;

import javax.swing.JComponent;

import org.javafmi.proxy.FmuFile;
import org.javafmi.wrapper.Simulation;

import agentgui.core.application.Application;
import energy.OptionModelController;
import energy.evaluation.AbstractEvaluationStrategy;
import energy.optionModel.TechnicalSystemStateEvaluation;

/**
 * A general evaluation strategy for working with FMU models in an EOM context. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuEvaluationStrategy extends AbstractEvaluationStrategy {
	
	private Simulation fmuSimulation;
	
	private TechnicalSystemStateEvaluation technicalSystemStateEvaluation;
	
	private String fmuFilePath;
	private long fmuStepSizeMillis = 1000;

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
		while (this.getTechnicalSystemStateEvaluation().getGlobalTime()<this.getEvaluationProcess().getEndTime()) {
			//TODO evaluate
		}
	}
	
	private Simulation getFmuSimulation() {
		if (fmuSimulation==null) {
			File fmuFile = new File(this.getFmuFilePath());
			if (fmuFile.exists()==false) {
				System.err.println("[" + this.getClass().getSimpleName() + "] Could not find FMU file '" + fmuFile.getAbsolutePath() + "'");
				return null;
			}
			fmuSimulation = new Simulation(new FmuFile(fmuFile));
		}
		return fmuSimulation;
	}
	
	private String getFmuFilePath() {
		if (fmuFilePath==null) {
			fmuFilePath = Application.getProjectFocused().getProjectFolderFullPath() + "fmuModels/mGRiDS_CoSimFMI_HPSystem/mGRiDS_CoSimFMI.HPSystem.fmu";
		}
		return fmuFilePath;
	}
	
	public TechnicalSystemStateEvaluation getTechnicalSystemStateEvaluation() {
		if (technicalSystemStateEvaluation==null) {
			technicalSystemStateEvaluation = this.getInitialTechnicalSystemStateEvaluation();
		}
		return technicalSystemStateEvaluation;
	}

}
