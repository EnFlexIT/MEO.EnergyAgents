package de.enflexit.meo.db.ui;

import javax.swing.JDialog;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;

import de.enflexit.common.swing.AwbThemeColor;
import de.enflexit.common.swing.KeyAdapter4Numbers;
import de.enflexit.meo.db.BundleHelper;

import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;


public class JDialogScenarioResult extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 2664291814833101888L;
	
	private JLabel jLabelHeader;

	private JLabel jLabelDatabaseID;
	private JTextField jTextFieldDatabaseID;

	private JPanel jPanelButtons;
	private JButton jButtonSave;
	private JButton jButtonCancel;
	
	
	/**
	 * Instantiates a new j dialog scenario result.
	 */
	public JDialogScenarioResult(Window owner) {
		super(owner);
		this.initialize();
		this.loadDatabaseSettings();
	}
	
	private void initialize() {
		
		this.setTitle("Database Settings for MEO-Results");
		this.setModal(true);
		this.setSize(600, 160);
		this.setLocationRelativeTo(null);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelHeader = new GridBagConstraints();
		gbc_jLabelHeader.anchor = GridBagConstraints.WEST;
		gbc_jLabelHeader.gridwidth = 2;
		gbc_jLabelHeader.insets = new Insets(10, 10, 0, 10);
		gbc_jLabelHeader.gridx = 0;
		gbc_jLabelHeader.gridy = 0;
		getContentPane().add(getJLabelHeader(), gbc_jLabelHeader);
		GridBagConstraints gbc_jLabelDatabaseID = new GridBagConstraints();
		gbc_jLabelDatabaseID.insets = new Insets(10, 10, 0, 0);
		gbc_jLabelDatabaseID.anchor = GridBagConstraints.EAST;
		gbc_jLabelDatabaseID.gridx = 0;
		gbc_jLabelDatabaseID.gridy = 1;
		getContentPane().add(getJLabelDatabaseID(), gbc_jLabelDatabaseID);
		GridBagConstraints gbc_jTextFieldDatabaseID = new GridBagConstraints();
		gbc_jTextFieldDatabaseID.insets = new Insets(10, 5, 0, 10);
		gbc_jTextFieldDatabaseID.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldDatabaseID.gridx = 1;
		gbc_jTextFieldDatabaseID.gridy = 1;
		getContentPane().add(getJTextFieldDatabaseID(), gbc_jTextFieldDatabaseID);
		GridBagConstraints gbc_jPanelButtons = new GridBagConstraints();
		gbc_jPanelButtons.insets = new Insets(15, 5, 10, 0);
		gbc_jPanelButtons.anchor = GridBagConstraints.WEST;
		gbc_jPanelButtons.fill = GridBagConstraints.VERTICAL;
		gbc_jPanelButtons.gridx = 1;
		gbc_jPanelButtons.gridy = 2;
		getContentPane().add(getJPanelButtons(), gbc_jPanelButtons);
	}

	private JLabel getJLabelHeader() {
		if (jLabelHeader == null) {
			jLabelHeader = new JLabel("Please, set the MEO databasse-ID to be used as key for the results of the state calculations!");
			jLabelHeader.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelHeader;
	}

	private JLabel getJLabelDatabaseID() {
		if (jLabelDatabaseID == null) {
			jLabelDatabaseID = new JLabel("Database ID");
			jLabelDatabaseID.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelDatabaseID;
	}
	private JTextField getJTextFieldDatabaseID() {
		if (jTextFieldDatabaseID == null) {
			jTextFieldDatabaseID = new JTextField();
			jTextFieldDatabaseID.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTextFieldDatabaseID.addKeyListener(new KeyAdapter4Numbers(false));
		}
		return jTextFieldDatabaseID;
	}
	
	
	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			GridBagLayout gbl_jPanelButtons = new GridBagLayout();
			gbl_jPanelButtons.columnWidths = new int[]{0, 0, 0};
			gbl_jPanelButtons.rowHeights = new int[]{0, 0};
			gbl_jPanelButtons.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			gbl_jPanelButtons.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			jPanelButtons.setLayout(gbl_jPanelButtons);
			GridBagConstraints gbc_jButtonSave = new GridBagConstraints();
			gbc_jButtonSave.insets = new Insets(0, 0, 0, 50);
			gbc_jButtonSave.gridx = 0;
			gbc_jButtonSave.gridy = 0;
			jPanelButtons.add(getJButtonSave(), gbc_jButtonSave);
			GridBagConstraints gbc_jButtonCancel = new GridBagConstraints();
			gbc_jButtonCancel.gridx = 1;
			gbc_jButtonCancel.gridy = 0;
			jPanelButtons.add(getJButtonCancel(), gbc_jButtonCancel);
		}
		return jPanelButtons;
	}
	private JButton getJButtonSave() {
		if (jButtonSave == null) {
			jButtonSave = new JButton("Save");
			jButtonSave.setPreferredSize(new Dimension(80, 26));
			jButtonSave.setForeground(AwbThemeColor.ButtonTextGreen.getColor());
			jButtonSave.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonSave.addActionListener(this);
		}
		return jButtonSave;
	}
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton("Cancel");
			jButtonCancel.setPreferredSize(new Dimension(80, 26));
			jButtonCancel.setForeground(AwbThemeColor.ButtonTextRed.getColor());
			jButtonCancel.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonCancel.addActionListener(this);
		}
		return jButtonCancel;
	}
	
	/**
	 * Loads the database settings to this dialog.
	 */
	private void loadDatabaseSettings() {

		int dbID = BundleHelper.getIdScenarioResultForSetup();
		this.getJTextFieldDatabaseID().setText(dbID + "");
	}
	
	/**
	 * Saves the database settings.
	 */
	private void saveDatabaseSettings() {

		// --- Get new database ID --------------------------------------------
		int newID = 0;
		String newIDString = this.getJTextFieldDatabaseID().getText();
		if (newIDString!=null && newIDString.isEmpty()==false) {
			try {
				newID = Integer.parseInt(newIDString); 
			} catch (Exception e) {
			}
		}
		
		// --- Save in the preferences ----------------------------------------
		BundleHelper.setIdScenarioResultForSetup(newID);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		if (ae.getSource()==this.getJButtonSave()) {
			// --- Save the database settings ---
			this.saveDatabaseSettings();
		} else if (ae.getSource()==this.getJButtonCancel()) {
			// --- Nothing to do here yet -------
		}
		this.setVisible(false);
		this.dispose();
	}
	
}
