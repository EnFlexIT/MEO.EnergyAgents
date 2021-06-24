package de.enflexit.meo.modellica.heatPump.measurementBased;

import java.awt.Frame;
import java.io.Serializable;

import de.enflexit.meo.modellica.eomIntegration.FmuStaticModelDialog;
import energy.OptionModelController;
import energy.optionModel.gui.sysVariables.AbstractStaticModel;
import energy.optionModel.gui.sysVariables.AbstractStaticModelDialog;

/**
 * The Class HeatPumpFmuStaticModel.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class HeatPumpMeasurementBasedFmuStaticModel extends AbstractStaticModel {
	
	private HeatPumpMeasurementBasedFmuStaticDataModel staticModel;

	/**
	 * Instantiates a new heat pump fmu static model.
	 * @param optionModelController the option model controller
	 */
	public HeatPumpMeasurementBasedFmuStaticModel(OptionModelController optionModelController) {
		super(optionModelController);
	}

	/* (non-Javadoc)
	 * @see energy.optionModel.gui.sysVariables.AbstractStaticModel#getStaticDataModel()
	 */
	@Override
	public Serializable getStaticDataModel() {
		return this.staticModel;
	}

	/* (non-Javadoc)
	 * @see energy.optionModel.gui.sysVariables.AbstractStaticModel#setStaticDataModel(java.io.Serializable)
	 */
	@Override
	public void setStaticDataModel(Serializable staticModel) {
		this.staticModel = (HeatPumpMeasurementBasedFmuStaticDataModel) staticModel;
	}

	/* (non-Javadoc)
	 * @see energy.optionModel.gui.sysVariables.AbstractStaticModel#getNewModelDialog(java.awt.Frame)
	 */
	@Override
	public AbstractStaticModelDialog getNewModelDialog(Frame owner) {
		return new FmuStaticModelDialog(owner, this);
	}

}
