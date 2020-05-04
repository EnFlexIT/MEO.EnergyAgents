package de.enflexit.meo.db.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.awb.env.networkModel.controller.GraphEnvironmentController;
import org.awb.env.networkModel.controller.ui.toolbar.AbstractCustomToolbarComponent;

import agentgui.core.project.Project;
import agentgui.core.project.setup.SimulationSetupNotification;
import agentgui.core.project.setup.SimulationSetupNotification.SimNoteReason;
import de.enflexit.meo.db.BundleHelper;

/**
 * The Class JButtonDatabaseSettings that will be displayed in the toolbar of the GraphEnvironement.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg-Essen
 */
public class JButtonIdScenarioResult extends AbstractCustomToolbarComponent implements ActionListener, Observer {

	private JButton jButtonAdjustIdScenaarioResult;
	
	/**
	 * Instantiates a new JButtonImportFlowData.
	 * @param graphController the graph controller
	 */
	public JButtonIdScenarioResult(GraphEnvironmentController graphController) {
		super(graphController);
		if (graphController.getProject()!=null) {
			graphController.getProject().addObserver(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.awb.env.networkModel.controller.AbstractCustomToolbarComponent#getCustomComponent()
	 */
	@Override
	public JComponent getCustomComponent() {
		return this.getJButtonIdScenarioResult();
	}
	/**
	 * Returns the JButton to configure the idScenarioResult.
	 * @return the j button id scenario result
	 */
	private JButton getJButtonIdScenarioResult() {
		if (jButtonAdjustIdScenaarioResult==null) {
			jButtonAdjustIdScenaarioResult = new JButton();
			jButtonAdjustIdScenaarioResult.setText("0");
			jButtonAdjustIdScenaarioResult.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonAdjustIdScenaarioResult.setHorizontalAlignment(JButton.CENTER);
			jButtonAdjustIdScenaarioResult.setToolTipText("MEO Result-ID: 'idScenarioResult'");
			jButtonAdjustIdScenaarioResult.setPreferredSize(new Dimension(26, 26));
			jButtonAdjustIdScenaarioResult.addActionListener(this);
		}
		return jButtonAdjustIdScenaarioResult;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {

		int oldID = BundleHelper.getIdScenarioResultForSetup();
		
		String title = "Set database-ID for MEO result database";
		String message = "Please, set the MEO databasse-ID to be used as key for the results of the state calculations.";
		Object newIDObject = JOptionPane.showInputDialog(this.getCustomComponent(), message, title, JOptionPane.INFORMATION_MESSAGE, null, null, oldID);
		if (newIDObject!=null) {
			String newIDString = (String) newIDObject;
			if (newIDString.isEmpty()==false) {
				try {
					int newID = Integer.parseInt(newIDString); 
					if (newID!=oldID) {
						BundleHelper.setIdScenarioResultForSetup(newID);
						this.getJButtonIdScenarioResult().setText(newID + "");
					}
					
				} catch (Exception e) {
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object updateObject) {
		
		if (observable instanceof Project) {
			
			if (updateObject == Project.VIEW_TabsLoaded) {
				this.loadIDScenarioResult();
			
			} else if (updateObject instanceof SimulationSetupNotification) {
				SimulationSetupNotification simNote = (SimulationSetupNotification) updateObject;
				if (simNote.getUpdateReason()==SimNoteReason.SIMULATION_SETUP_LOAD) {
					this.loadIDScenarioResult();
				}
			}
			
		}
	}
	/**
	 * Load ID scenario result and set's its value to the JButton.
	 */
	private void loadIDScenarioResult() {
		this.getJButtonIdScenarioResult().setText(BundleHelper.getIdScenarioResultForSetup() + "");
	}
	
}
