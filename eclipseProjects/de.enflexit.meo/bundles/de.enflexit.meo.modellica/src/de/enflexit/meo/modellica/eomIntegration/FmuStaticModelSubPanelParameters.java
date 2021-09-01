package de.enflexit.meo.modellica.eomIntegration;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import agentgui.core.config.GlobalInfo;
import javax.swing.JLabel;
import java.awt.Font;

/**
 * A sub-panel for configuring system-specific static parameters, that will be embedded in the {@link FmuStaticModelConfigurationPanel}. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuStaticModelSubPanelParameters extends JPanel implements ActionListener {

	private static final long serialVersionUID = 523211795735719449L;
	
	private Vector<FmuParameterSettings> parameterSettings;
	
	private JButton jButtonAdd;
	private JButton jButtonRemove;
	private JScrollPane jScrollPaneParameters;
	private JTable jTableParameters;
	private DefaultTableModel tableModelParameters;
	private JLabel jLabelParameters;
	
	private FmuStaticModelConfigurationPanel parentPanel;

	/**
	 * Instantiates a new fmu static model sub panel parameters.
	 */
	public FmuStaticModelSubPanelParameters(FmuStaticModelConfigurationPanel parentPanel) {
		super();
		this.parentPanel = parentPanel;
		this.initialize();
	}
	/**
	 * Initializes the GUI components.
	 */
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelParameters = new GridBagConstraints();
		gbc_jLabelParameters.anchor = GridBagConstraints.WEST;
		gbc_jLabelParameters.insets = new Insets(0, 5, 0, 0);
		gbc_jLabelParameters.gridx = 0;
		gbc_jLabelParameters.gridy = 0;
		add(getJLabelParameters(), gbc_jLabelParameters);
		GridBagConstraints gbc_jButtonAdd = new GridBagConstraints();
		gbc_jButtonAdd.anchor = GridBagConstraints.EAST;
		gbc_jButtonAdd.insets = new Insets(0, 0, 0, 5);
		gbc_jButtonAdd.gridx = 1;
		gbc_jButtonAdd.gridy = 0;
		add(getJButtonAdd(), gbc_jButtonAdd);
		GridBagConstraints gbc_jButtonRemove = new GridBagConstraints();
		gbc_jButtonRemove.insets = new Insets(0, 0, 0, 5);
		gbc_jButtonRemove.gridx = 2;
		gbc_jButtonRemove.gridy = 0;
		add(getJButtonRemove(), gbc_jButtonRemove);
		GridBagConstraints gbc_jScrollPaneParameters = new GridBagConstraints();
		gbc_jScrollPaneParameters.insets = new Insets(0, 5, 5, 5);
		gbc_jScrollPaneParameters.gridwidth = 3;
		gbc_jScrollPaneParameters.fill = GridBagConstraints.BOTH;
		gbc_jScrollPaneParameters.gridx = 0;
		gbc_jScrollPaneParameters.gridy = 1;
		add(getJScrollPaneParameters(), gbc_jScrollPaneParameters);
	}

	private JLabel getJLabelParameters() {
		if (jLabelParameters == null) {
			jLabelParameters = new JLabel("System Parameters");
			jLabelParameters.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelParameters;
	}
	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setToolTipText("Add parameter definition");
			jButtonAdd.setPreferredSize(new Dimension(45, 26));
			jButtonAdd.setIcon(GlobalInfo.getInternalImageIcon("ListPlus.png"));
			jButtonAdd.addActionListener(this);
		}
		return jButtonAdd;
	}
	private JButton getJButtonRemove() {
		if (jButtonRemove == null) {
			jButtonRemove = new JButton();
			jButtonRemove.setToolTipText("Remove parameter definition");
			jButtonRemove.setPreferredSize(new Dimension(45, 26));
			jButtonRemove.setIcon(GlobalInfo.getInternalImageIcon("ListMinus.png"));
			jButtonRemove.addActionListener(this);
		}
		return jButtonRemove;
	}
	private JScrollPane getJScrollPaneParameters() {
		if (jScrollPaneParameters == null) {
			jScrollPaneParameters = new JScrollPane();
			jScrollPaneParameters.setViewportView(getJTableParameters());
		}
		return jScrollPaneParameters;
	}
	private JTable getJTableParameters() {
		if (jTableParameters == null) {
			jTableParameters = new JTable();
			jTableParameters.setFillsViewportHeight(true);
			jTableParameters.setModel(this.getTableModelParameters());
			for (int i=0; i<jTableParameters.getColumnCount(); i++) {
				jTableParameters.getColumnModel().getColumn(i).setCellEditor(new ParameterSettingsTableCellEditor(this));
			}
		}
		return jTableParameters;
	}
	private DefaultTableModel getTableModelParameters() {
		if (tableModelParameters==null) {
			String[] headers = {"FMU Name", "Data Type", "Value", "Unit"};
			tableModelParameters = new DefaultTableModel(headers, 0);
		}
		return tableModelParameters;
	}
	
	/**
	 * Adds a parameter settings instance to the table model.
	 * @param parameterSettings the parameter settings instance
	 */
	private void addParameterSettingsToTableModel(FmuParameterSettings parameterSettings) {
		
		String dataType;
		if (parameterSettings.getValue()!=null) {
			// --- Determine data type from the specified value object --------
			dataType = parameterSettings.getValue().getClass().getSimpleName();
		} else {
			// --- Use Double as default type ---------------------------------
			dataType = Double.class.getSimpleName();
		}
		
		Vector<Object> dataRow = new Vector<Object>();
		dataRow.add(parameterSettings.getFmuVariableName());
		dataRow.add(dataType);
		dataRow.add(parameterSettings.getValue());
		dataRow.add(parameterSettings.getUnit());
		
		this.getTableModelParameters().addRow(dataRow);
	}
	
	
	/**
	 * Updates the table model according to the data model of the parent panel.
	 */
	protected void updateTableModel() {
		this.getTableModelParameters().getDataVector().clear();
		for (int i=0; i<this.getParameterSettings().size(); i++) {
			this.addParameterSettingsToTableModel(this.getParameterSettings().get(i));
		}
	}
	
	/**
	 * Discards the currently defined parameter settings.
	 */
	protected void clear() {
		this.getParameterSettings().clear();
		this.updateTableModel();
		this.getTableModelParameters().fireTableDataChanged();
	}
	
	/**
	 * Removes obsolete parameter settings from both the table and the list. 
	 * @param parametersToKeep the parameters to keep
	 */
	protected void removeObsoleteParameterSettings(Vector<String> parametersToKeep) {
		for (int i=0; i<this.getJTableParameters().getRowCount(); i++) {
			String fmuName = (String) this.getJTableParameters().getValueAt(i, 0);
			if (parametersToKeep.contains(fmuName)==false) {
				this.removeParameterSettings(i);
				i--;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonAdd()) {
			// --- Add a new variable mapping to the table ----------
			FmuParameterSettings variableMapping = this.createNewParameterSettings();
			this.getParameterSettings().add(variableMapping);
			this.addParameterSettingsToTableModel(variableMapping);
		} else if (ae.getSource()==this.getJButtonRemove()) {
			int selectedRow = this.getJTableParameters().getSelectedRow();
			this.removeParameterSettings(selectedRow);
		}
	}
	
	/**
	 * Removes the parameter settings at the specified table row from both the table and the list.
	 * @param rowIndex the row index
	 */
	private void removeParameterSettings(int rowIndex) {
		String fmuName = (String) this.getJTableParameters().getValueAt(rowIndex, 0);
		FmuParameterSettings selectedParameter = this.getParameterSettingsByFmuName(fmuName);
		this.getParameterSettings().remove(selectedParameter);
		this.getTableModelParameters().removeRow(rowIndex);
	}
	
	/**
	 * Creates a new parameter settings instance with a unique FMU variable name.
	 * @return the parameter settings instance
	 */
	private FmuParameterSettings createNewParameterSettings() {
		// --- Find an initial FMU variable name that is not a duplicate ------
		String fmuBaseName = "Select FMU Variable";
		String fmuName = fmuBaseName;
		int suffix = 1;
		while (this.getParameterSettingsByFmuName(fmuName)!=null) {
			fmuName= fmuBaseName + suffix;
			suffix++;
		}
		
		return new FmuParameterSettings(fmuName, null, null);
	}
	/**
	 * Gets the current parameter settings.
	 * @return the parameter settings
	 */
	protected Vector<FmuParameterSettings> getParameterSettings() {
		if (parameterSettings==null) {
			parameterSettings = new Vector<FmuParameterSettings>();
		}
		return parameterSettings;
	}
	
	/**
	 * Sets the parameter settings to be displayed.
	 * @param parameterSettings the new parameter settings
	 */
	protected void setParameterSettings(Vector<FmuParameterSettings> parameterSettings) {
		this.getParameterSettings().clear();
		this.getParameterSettings().addAll(parameterSettings);
		this.updateTableModel();
	}
	
	/**
	 * Gets the parameter settings by their FMU variable name.
	 * @param fmuName the FMU variable name
	 * @return the corresponding parameter settings, null if not found
	 */
	protected FmuParameterSettings getParameterSettingsByFmuName(String fmuName) {
		for (int i=0; i<this.getParameterSettings().size(); i++) {
			FmuParameterSettings parameterSettings = this.getParameterSettings().get(i);
			if (parameterSettings.getFmuVariableName().equals(fmuName)) {
				return parameterSettings;
			}
		}
		return null;
	}
	
	/**
	 * Gets the fmu variables list.
	 * @return the fmu variables list
	 */
	protected Vector<String> getFmuVariablesList(){
		return this.parentPanel.getFmuVariablesList();
	}
}
