package de.enflexit.meo.modellica.eomIntegration;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import agentgui.core.config.GlobalInfo;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMapping.IoVariableType;

import javax.swing.JLabel;
import java.awt.Font;

/**
 * A sub-panel for configuring system-specific static parameters, that will be embedded in the {@link FmuStaticModelConfigurationPanel}.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuStaticModelSubPanelVariables extends JPanel implements ActionListener {

	private static final long serialVersionUID = 7827581176335868406L;
	
	private Vector<FmuVariableMapping> variableMappings;
	private FmuStaticModelConfigurationPanel parentPanel;
	
	private JButton jButtonAdd;
	private JButton jButtonRemove;
	private JScrollPane jScrollPaneVariables;
	private JTable jTableVariables;
	private DefaultTableModel tableModelVariables;
	private JLabel jLabelVariables;

	/**
	 * Instantiates a new fmu static model sub panel variables.
	 */
	public FmuStaticModelSubPanelVariables(FmuStaticModelConfigurationPanel parentPanel) {
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
		GridBagConstraints gbc_jLabelVariables = new GridBagConstraints();
		gbc_jLabelVariables.insets = new Insets(0, 5, 0, 0);
		gbc_jLabelVariables.gridx = 0;
		gbc_jLabelVariables.gridy = 0;
		add(getJLabelVariables(), gbc_jLabelVariables);
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
		add(getJScrollPaneVariables(), gbc_jScrollPaneParameters);
	}
	private JLabel getJLabelVariables() {
		if (jLabelVariables == null) {
			jLabelVariables = new JLabel("System Variables");
			jLabelVariables.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelVariables;
	}

	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setToolTipText("Add variable mapping");
			jButtonAdd.setPreferredSize(new Dimension(45, 26));
			jButtonAdd.setIcon(GlobalInfo.getInternalImageIcon("ListPlus.png"));
			jButtonAdd.addActionListener(this);
		}
		return jButtonAdd;
	}
	private JButton getJButtonRemove() {
		if (jButtonRemove == null) {
			jButtonRemove = new JButton();
			jButtonRemove.setToolTipText("Remove variable mapping");
			jButtonRemove.setPreferredSize(new Dimension(45, 26));
			jButtonRemove.setIcon(GlobalInfo.getInternalImageIcon("ListMinus.png"));
			jButtonRemove.addActionListener(this);
		}
		return jButtonRemove;
	}
	private JScrollPane getJScrollPaneVariables() {
		if (jScrollPaneVariables == null) {
			jScrollPaneVariables = new JScrollPane();
			jScrollPaneVariables.setViewportView(getJTableVariables());
		}
		return jScrollPaneVariables;
	}
	private JTable getJTableVariables() {
		if (jTableVariables == null) {
			jTableVariables = new JTable();
			jTableVariables.setFillsViewportHeight(true);
			jTableVariables.setFont(new Font("Dialog", Font.PLAIN, 12));
			jTableVariables.setRowHeight(20);
			jTableVariables.setModel(this.getTableModelVariables());
			for (int i=0; i<jTableVariables.getColumnCount(); i++) {
				jTableVariables.getColumnModel().getColumn(i).setCellEditor(new VariableMappingTableCellEditor(this));
			}
		}
		return jTableVariables;
	}
	private DefaultTableModel getTableModelVariables() {
		if (tableModelVariables==null) {
			String[] headers = {"EOM Name", "FMU Name", "Type", "Unit"};
			tableModelVariables = new DefaultTableModel(headers, 0);
		}
		return tableModelVariables;
	}
	
	/**
	 * Updates the table model according to the data model of the parent panel.
	 */
	protected void updateTableModel() {
		this.getTableModelVariables().getDataVector().clear();
		for (int i=0; i<this.getVariableMappings().size(); i++) {
			this.addVariableMappingToTableModel(this.getVariableMappings().get(i));
		}
	}
	
	/**
	 * Discards the currently defined variable mappings.
	 */
	protected void clear() {
		this.getVariableMappings().clear();
		this.updateTableModel();
		this.getTableModelVariables().fireTableDataChanged();
	}
	
	/**
	 * Removes obsolete variable mappings from both the table and the list. 
	 * @param variablesToKeep the variables to keep
	 */
	protected void removeObsoleteVariableMappings() {
		
		// --- Collect all mappings that should be removed ----------
		Vector<FmuVariableMapping> mappingsToRemove = new Vector<FmuVariableMapping>();
		for (int i=0; i<this.getVariableMappings().size(); i++) {
			FmuVariableMapping currentMapping = this.getVariableMappings().get(i);
			if (this.getFmuVariablesList().contains(currentMapping.getFmuVariableName())==false) {
				mappingsToRemove.add(currentMapping);
			}
		}
		
		// --- Remove the mappings and update the table -------------
		if (mappingsToRemove.size()>0) {
			this.getVariableMappings().removeAll(mappingsToRemove);
			this.updateTableModel();
			this.getTableModelVariables().fireTableDataChanged();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonAdd()) {
			// --- Add a new variable mapping to the table ----------
			FmuVariableMapping variableMapping = this.createNewVariableMapping();
			this.addVariableMappingToTableModel(variableMapping);
			this.getVariableMappings().add(variableMapping);
		} else if (ae.getSource()==this.getJButtonRemove()) {
			this.stopCellEditing();
			int selectedRow = this.getJTableVariables().getSelectedRow();
			this.removeVariableMapping(selectedRow);
			
			if (this.getJTableVariables().getRowCount()>0) {
				int newSelection = 0;
				if (selectedRow>0) {
					newSelection = selectedRow-1;
				}
				this.getJTableVariables().setRowSelectionInterval(newSelection, newSelection);
			}
		}
	}
	
	/**
	 * Stops cell editing if currently active.
	 */
	private void stopCellEditing() {
		if (this.getJTableVariables().getCellEditor()!=null) {
			this.getJTableVariables().getCellEditor().stopCellEditing();
		}
	}
	
	/**
	 * Removes the variable mapping.
	 * @param rowIndex the row index
	 */
	private void removeVariableMapping(int rowIndex) {
		String eomName = (String) this.getJTableVariables().getValueAt(rowIndex, 0);
		FmuVariableMapping selectedMapping = this.getVariableMappingByEomName(eomName);
		this.getVariableMappings().remove(selectedMapping);
		this.getTableModelVariables().removeRow(rowIndex);
	}
	
	/**
	 * Gets the variable mappings.
	 * @return the variable mappings
	 */
	protected Vector<FmuVariableMapping> getVariableMappings() {
		if (variableMappings==null) {
			variableMappings = new Vector<FmuVariableMapping>();
		}
		return variableMappings;
	}
	
	/**
	 * Sets the variable mappings.
	 * @param variableMappings the new variable mappings
	 */
	protected void setVariableMappings(Vector<FmuVariableMapping> variableMappings) {
		this.getVariableMappings().clear();
		this.getVariableMappings().addAll(variableMappings);
		this.updateTableModel();
	}
	/**
	 * Creates a new variable mapping.
	 * @return the variable mapping
	 */
	private FmuVariableMapping createNewVariableMapping() {
		
		// --- Find an initial EOM variable name that is not a duplicate ------
		String eomBaseName = "Select EOM Variable";
		String eomName = eomBaseName;
		int suffix = 1;
		while (this.getVariableMappingByEomName(eomName)!=null) {
			eomName= eomBaseName + suffix;
			suffix++;
		}
		
		// --- Find an initial FMU variable name that is not a duplicate ------
		String fmuBaseName = "Select FMU Variable";
		String fmuName = fmuBaseName;
		suffix = 1;
		while (this.getVariableMappingByFmuName(fmuName)!=null) {
			fmuName= fmuBaseName + suffix;
			suffix++;
		}
		
		return new FmuVariableMapping(eomName, fmuName, IoVariableType.MEASUREMENT, "");
	}
	
	/**
	 * Gets a variable mapping by its' eom name.
	 * @param eomName the eom name
	 * @return the variable mapping, null if not found
	 */
	protected FmuVariableMapping getVariableMappingByEomName(String eomName) {
		for(int i=0; i<this.getVariableMappings().size(); i++) {
			FmuVariableMapping variableMapping = this.getVariableMappings().get(i);
			if (variableMapping.getEomVariableName().equals(eomName)) {
				return variableMapping;
			}
		}
		return null;
	}
	
	/**
	 * Gets a variable mapping by its' fmu name.
	 * @param fmuName the fmu name
	 * @return the variable mapping, null if not found
	 */
	protected FmuVariableMapping getVariableMappingByFmuName(String fmuName) {
		for(int i=0; i<this.getVariableMappings().size(); i++) {
			FmuVariableMapping variableMapping = this.getVariableMappings().get(i);
			if (variableMapping.getFmuVariableName().equals(fmuName)) {
				return variableMapping;
			}
		}
		return null;
	}

	
	/**
	 * Adds a variable mapping to the table model.
	 * @param variableMapping the variable mapping
	 */
	private void addVariableMappingToTableModel(FmuVariableMapping variableMapping) {
		Vector<Object> dataRow = new Vector<Object>();
		dataRow.add(variableMapping.getEomVariableName());
		dataRow.add(variableMapping.getFmuVariableName());
		dataRow.add(variableMapping.getVariableType());
		dataRow.add(variableMapping.getUnit());
		
		this.getTableModelVariables().addRow(dataRow);
	}

	/**
	 * Gets the fmu variables list.
	 * @return the fmu variables list
	 */
	protected Vector<String> getFmuVariablesList(){
		return this.parentPanel.getFmuVariablesList();
	}
	
	/**
	 * Gets the eom variables list.
	 * @return the eom variables list
	 */
	protected Vector<String> getEomVariablesList(){
		return this.parentPanel.getEomVariablesList();
	}
}
