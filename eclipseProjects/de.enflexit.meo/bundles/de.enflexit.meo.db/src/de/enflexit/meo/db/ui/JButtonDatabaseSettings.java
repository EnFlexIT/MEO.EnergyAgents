package de.enflexit.meo.db.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.awb.env.networkModel.controller.GraphEnvironmentController;
import org.awb.env.networkModel.controller.ui.toolbar.AbstractCustomToolbarComponent;

import de.enflexit.common.swing.OwnerDetection;
import de.enflexit.db.hibernate.gui.DatabaseConnectionSettingsDialog;
import de.enflexit.meo.db.BundleHelper;
import de.enflexit.meo.db.DatabaseSessionFactoryHandler;

/**
 * The Class JButtonDatabaseSettings that will be displayed in the toolbar of the GraphEnvironement.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg-Essen
 */
public class JButtonDatabaseSettings extends AbstractCustomToolbarComponent implements ActionListener {

	/**
	 * Instantiates a new JButtonImportFlowData.
	 * @param graphController the graph controller
	 */
	public JButtonDatabaseSettings(GraphEnvironmentController graphController) {
		super(graphController);
	}

	/* (non-Javadoc)
	 * @see org.awb.env.networkModel.controller.AbstractCustomToolbarComponent#getCustomComponent()
	 */
	@Override
	public JComponent getCustomComponent() {
		JButton jButtonExportLoadProfile = new JButton();
		jButtonExportLoadProfile.setIcon(BundleHelper.getImageIcon("DatabaseSettings.png"));
		jButtonExportLoadProfile.setToolTipText("MEO Result - Database Settings");
		jButtonExportLoadProfile.setPreferredSize(new Dimension(26, 26));
		jButtonExportLoadProfile.addActionListener(this);
		return jButtonExportLoadProfile;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {

		Frame owner = OwnerDetection.getOwnerFrameForComponent(this.graphController.getGraphEnvironmentControllerGUI());
		DatabaseConnectionSettingsDialog settingDialog = new DatabaseConnectionSettingsDialog(owner, DatabaseSessionFactoryHandler.SESSION_FACTORY_ID);
		settingDialog.setVisible(true);
		// --- Wait for the user action -----
	}
		
}
