package de.enflexit.meo.plugIn;

import org.awb.env.networkModel.controller.GraphEnvironmentController;
import org.awb.env.networkModel.controller.ui.BasicGraphGui.ToolBarSurrounding;
import org.awb.env.networkModel.controller.ui.BasicGraphGui.ToolBarType;
import org.awb.env.networkModel.controller.ui.toolbar.CustomToolbarComponentDescription;

import agentgui.core.application.Application;
import agentgui.core.plugin.PlugIn;
import agentgui.core.project.Project;

/**
 * The Class MEOPlugin provides specific control elements to Agent.Workbench.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class MEOPlugin extends PlugIn {

	/** The Constant that represents the regular expression for component ID's. */
	public static final String REGEX_FOR_ELEMENT_ID = "^([\\w_-]+)$";
	
	private GraphEnvironmentController graphController;
	
	
	/**
	 * Instantiates the AWB web server plugin.
	 * @param currProject the current project
	 */
	public MEOPlugin(Project currProject) {
		super(currProject);
	}
	/* (non-Javadoc)
	 * @see agentgui.core.plugin.PlugIn#getName()
	 */
	@Override
	public String getName() {
		return "MEO PlugIn";
	}
	/**
	 * Gets the graph controller.
	 * @return the graph controller
	 */
	public GraphEnvironmentController getGraphController() {
		if (graphController==null) {
			graphController = (GraphEnvironmentController) this.project.getEnvironmentController();
		}
		return graphController;
	}


	/* (non-Javadoc)
	 * @see agentgui.core.plugin.PlugIn#onPlugIn()
	 */
	@Override
	public void onPlugIn() {
		
		if (Application.isOperatingHeadless()==false) {
			// --- Add the OAD state data importer --------
			this.getGraphController().addCustomToolbarComponentDescription(new CustomToolbarComponentDescription(ToolBarType.EditControl, ToolBarSurrounding.ConfigurationOnly, JButtonConstructionSite.class, null, true));
			
		}
		super.onPlugIn();
	}
	
	/* (non-Javadoc)
	 * @see agentgui.core.plugin.PlugIn#onPlugOut()
	 */
	@Override
	public void onPlugOut() {
		super.onPlugOut();
	}
	
}
