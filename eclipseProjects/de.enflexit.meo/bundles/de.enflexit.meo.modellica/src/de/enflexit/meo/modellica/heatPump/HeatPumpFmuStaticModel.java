package de.enflexit.meo.modellica.heatPump;

import java.awt.Window;
import java.io.Serializable;

import de.enflexit.meo.modellica.eomIntegration.FmuStaticModelConfigurationDialog;
import energy.OptionModelController;
import energy.optionModel.gui.sysVariables.AbstractStaticModel;
import energy.optionModel.gui.sysVariables.AbstractStaticModelDialog;

/**
 * The Class HeatPumpFmuStaticModel.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class HeatPumpFmuStaticModel extends AbstractStaticModel {
	
	private HeatPumpFmuStaticDataModel staticModel;

	/**
	 * Instantiates a new heat pump fmu static model.
	 * @param optionModelController the option model controller
	 */
	public HeatPumpFmuStaticModel(OptionModelController optionModelController) {
		super(optionModelController);
	}

	/* (non-Javadoc)
	 * @see energy.optionModel.gui.sysVariables.AbstractStaticModel#getStaticDataModel()
	 */
	@Override
	public Serializable getStaticDataModel() {
		if (this.staticModel==null) {
			this.staticModel = new HeatPumpFmuStaticDataModel();
		}
		return this.staticModel;
	}

	/* (non-Javadoc)
	 * @see energy.optionModel.gui.sysVariables.AbstractStaticModel#setStaticDataModel(java.io.Serializable)
	 */
	@Override
	public void setStaticDataModel(Serializable staticModel) {
		this.staticModel = (HeatPumpFmuStaticDataModel) staticModel;
	}

	/* (non-Javadoc)
	 * @see energy.optionModel.gui.sysVariables.AbstractStaticModel#getNewModelDialog(java.awt.Frame)
	 */
	@Override
	public AbstractStaticModelDialog getNewModelDialog(Window owner) {
		return new FmuStaticModelConfigurationDialog(owner, this);
	}

}
