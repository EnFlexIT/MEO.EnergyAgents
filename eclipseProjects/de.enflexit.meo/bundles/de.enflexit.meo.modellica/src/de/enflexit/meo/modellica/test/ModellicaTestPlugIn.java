package de.enflexit.meo.modellica.test;

import org.awb.env.networkModel.controller.GraphEnvironmentController;
import org.awb.env.networkModel.controller.ui.BasicGraphGui.ToolBarSurrounding;
import org.awb.env.networkModel.controller.ui.BasicGraphGui.ToolBarType;
import org.awb.env.networkModel.controller.ui.toolbar.CustomToolbarComponentDescription;

import agentgui.core.plugin.PlugIn;
import agentgui.core.project.Project;

/**
 * The ModellicaTestPlugIn (only used during developments).
 *
 * @author Christian Derksen - SOFTEC - ICB - University of Duisburg-Essen
 */
public class ModellicaTestPlugIn extends PlugIn {
	
	private GraphEnvironmentController graphController;

	/**
	 * Instantiates a new modellica test plug in.
	 * @param currProject the current project
	 */
	public ModellicaTestPlugIn(Project currProject) {
		super(currProject);
	}

	/* (non-Javadoc)
	 * @see agentgui.core.plugin.PlugIn#getName()
	 */
	@Override
	public String getName() {
		return "Modellica FMU / FMI Test PlugIn";
	}

	/* (non-Javadoc)
	 * @see agentgui.core.plugin.PlugIn#onPlugIn()
	 */
	@Override
	public void onPlugIn() {
		this.getGraphController().addCustomToolbarComponentDescription(new CustomToolbarComponentDescription(ToolBarType.EditControl, ToolBarSurrounding.ConfigurationOnly, JButtonModellicaTest.class, null, true));
		super.onPlugIn();
	}

	/* (non-Javadoc)
	 * @see agentgui.core.plugin.PlugIn#onPlugOut()
	 */
	@Override
	public void onPlugOut() {
		super.onPlugOut();
	}
	
	/**
	 * Gets the graph controller.
	 * @return the graph controller
	 */
	private GraphEnvironmentController getGraphController() {
		if (graphController==null) {
			graphController = (GraphEnvironmentController) this.project.getEnvironmentController();
		}
		return graphController;
	}

}
