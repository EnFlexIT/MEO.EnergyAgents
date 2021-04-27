package de.enflexit.meo.modellica.eomIntegration;

import java.awt.Frame;

import energy.optionModel.gui.sysVariables.AbstractStaticModel;
import energy.optionModel.gui.sysVariables.AbstractStaticModelDialog;

/**
 * A dialog to edit the heatpump's static data model.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuStaticModelDialog extends AbstractStaticModelDialog {
	
	private static final long serialVersionUID = -879697198569914579L;

	/**
	 * Instantiates a new heat pump fmu static model dialog.
	 * @param owner the owner
	 * @param staticModel the static model
	 */
	public FmuStaticModelDialog(Frame owner, AbstractStaticModel staticModel) {
		super(owner, staticModel);
	}

}
