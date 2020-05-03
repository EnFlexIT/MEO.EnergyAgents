package de.enflexit.meo.db.ui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.awb.env.networkModel.controller.GraphEnvironmentController;
import org.awb.env.networkModel.controller.ui.toolbar.AbstractCustomToolbarComponent;
import org.hibernate.cfg.Configuration;

import agentgui.core.application.Application;
import de.enflexit.db.hibernate.SessionFactoryMonitor.SessionFactoryState;
import de.enflexit.db.hibernate.gui.DatabaseSettings;
import de.enflexit.db.hibernate.gui.DatabaseSettingsDialog;
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
		jButtonExportLoadProfile.setIcon(new ImageIcon(getClass().getResource("/images/DatabaseSettings.png")));
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

		Frame owner = Application.getGlobalInfo().getOwnerFrameForComponent(this.graphController.getGraphEnvironmentControllerGUI());
		DatabaseSettingsDialog settingDialog = new DatabaseSettingsDialog(owner, this.getDatabaseSettings());
		settingDialog.setTitle("MEO Result - Database Settings");
		settingDialog.setHeaderText("MEO Result - Database Settings");
		settingDialog.setVisible(true);
		// --- Wait for the user action -----
		if (settingDialog.isCanceled()==false) {
			this.setDatabaseSettings(settingDialog.getDatabaseSettings());
		}
		settingDialog = null;
		
	}
	
	/**
	 * Gets the database settings form the {@link DatabaseBundleInfo}.
	 * @return the database settings
	 */
	private DatabaseSettings getDatabaseSettings() {
		String databaseSystemName = DatabaseSessionFactoryHandler.getDatabaseSystem();
		Configuration hiberanteConfig = DatabaseSessionFactoryHandler.getConfiguration();
		return new DatabaseSettings(databaseSystemName, hiberanteConfig);
	}
	/**
	 * Sets the database settings to the bundle configuration (eclipse preferences).
	 * @param dbSettings the new database settings
	 */
	private void setDatabaseSettings(DatabaseSettings dbSettings) {
		
		boolean hasChangedSettings = ! dbSettings.equals(this.getDatabaseSettings());
		DatabaseSessionFactoryHandler.setDatabaseSystem(dbSettings.getDatabaseSystemName());
		DatabaseSessionFactoryHandler.setEclipsePreferencesForDatabaseConnection(dbSettings.getHibernateDatabaseSettings());
		if (hasChangedSettings==true) {
			DatabaseSessionFactoryHandler.startSessionFactory(true, false);
		} else {
			if (this.isAllowSessionFactoryStart(DatabaseSessionFactoryHandler.getSessionFactoryMonitor().getSessionFactoryState())==true) {
				DatabaseSessionFactoryHandler.startSessionFactory(true, false);
			}
		}
	}
	/**
	 * Checks, based on the current state, if a new SessionFactory start is allowed.
	 *
	 * @param currentState the current state
	 * @return true, if is allow session start
	 */
	private boolean isAllowSessionFactoryStart(SessionFactoryState currentState) {
		
		boolean allowSessionStart = false;
		switch (currentState) {
		case CheckDBConectionFailed:
		case InitializationProcessFailed:
		case Destroyed:
			allowSessionStart = true;
			break;

		case NotAvailableYet:
		case CheckDBConnection:
		case InitializationProcessStarted:
		case Created:
			allowSessionStart = false;
			break;
		}
		return allowSessionStart;
	}
	
}
