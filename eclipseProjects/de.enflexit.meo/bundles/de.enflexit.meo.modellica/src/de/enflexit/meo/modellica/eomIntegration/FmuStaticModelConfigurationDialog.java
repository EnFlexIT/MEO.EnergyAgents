package de.enflexit.meo.modellica.eomIntegration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import energy.optionModel.gui.sysVariables.AbstractStaticModel;
import energy.optionModel.gui.sysVariables.AbstractStaticModelDialog;

/**
 * A dialog to edit the heatpump's static data model.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuStaticModelConfigurationDialog extends AbstractStaticModelDialog implements ActionListener{
	
	private static final long serialVersionUID = -879697198569914579L;
	
	private FmuStaticModelConfigurationPanel configurationPanel;
	
	private JPanel jPanelButtons;
	private JButton jButtonOk;
	private JButton jButtonCancel;
	
	private boolean canceled;

	/**
	 * Instantiates a new heat pump fmu static model dialog.
	 * @param owner the owner
	 * @param staticModel the static model
	 */
	public FmuStaticModelConfigurationDialog(Window owner, AbstractStaticModel staticModel) {
		super(owner, staticModel);
		this.initialize();
	}
	
	/**
	 * Initializes the GUI elements.
	 */
	private void initialize() {
		this.setTitle("FMU Configuration");
		this.setSize(620, 580);
		this.setLayout(new BorderLayout());
		this.add(this.getConfigurationPanel(), BorderLayout.CENTER);
		this.add(this.getJPanelButtons(), BorderLayout.SOUTH);
	}
	
	/**
	 * Gets the configuration panel.
	 * @return the configuration panel
	 */
	private FmuStaticModelConfigurationPanel getConfigurationPanel() {
		if (configurationPanel==null) {
			configurationPanel = new FmuStaticModelConfigurationPanel((FmuStaticDataModel) this.staticModel.getStaticDataModel(), this.staticModel.getOptionModelController());
		}
		return configurationPanel;
	}
	
	/**
	 * Gets the buttons panel.
	 * @return the buttons panel
	 */
	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel();
			GridBagLayout gbl_jPanelButtons = new GridBagLayout();
			gbl_jPanelButtons.columnWidths = new int[]{0, 0, 0};
			gbl_jPanelButtons.rowHeights = new int[]{0, 0};
			gbl_jPanelButtons.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
			gbl_jPanelButtons.rowWeights = new double[]{0.0, Double.MIN_VALUE};
			jPanelButtons.setLayout(gbl_jPanelButtons);
			GridBagConstraints gbc_jButtonOk = new GridBagConstraints();
			gbc_jButtonOk.insets = new Insets(10, 10, 15, 0);
			gbc_jButtonOk.gridx = 0;
			gbc_jButtonOk.gridy = 0;
			jPanelButtons.add(getJButtonOk(), gbc_jButtonOk);
			GridBagConstraints gbc_jButtonCancel = new GridBagConstraints();
			gbc_jButtonCancel.insets = new Insets(10, 0, 15, 10);
			gbc_jButtonCancel.gridx = 1;
			gbc_jButtonCancel.gridy = 0;
			jPanelButtons.add(getJButtonCancel(), gbc_jButtonCancel);
		}
		return jPanelButtons;
	}
	
	/**
	 * Gets the ok button.
	 * @return the ok button
	 */
	private JButton getJButtonOk() {
		if (jButtonOk == null) {
			jButtonOk = new JButton("OK");
			jButtonOk.setPreferredSize(new Dimension(85, 26));
			jButtonOk.setForeground(new Color(0, 153, 0));
			jButtonOk.setFont(new Font("Dialog", Font.BOLD, 11));
			jButtonOk.addActionListener(this);
		}
		return jButtonOk;
	}
	
	/**
	 * Gets the cancel button.
	 * @return the cancel button
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton("Cancel");
			jButtonCancel.setPreferredSize(new Dimension(85, 26));
			jButtonCancel.setForeground(new Color(153, 0, 0));
			jButtonCancel.setFont(new Font("Dialog", Font.BOLD, 11));
			jButtonCancel.addActionListener(this);
		}
		return jButtonCancel;
	}
	
	/* (non-Javadoc)
	 * @see energy.optionModel.gui.sysVariables.AbstractStaticModelDialog#isCanceled()
	 */
	@Override
	public boolean isCanceled() {
		return this.canceled;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonOk()) {
			this.canceled = false;
			this.getConfigurationPanel().loadFormToStaticModel();
			this.dispose();
		} else if (ae.getSource()==this.getJButtonCancel()) {
			this.canceled = true;
			this.dispose();
		}
	}

}
