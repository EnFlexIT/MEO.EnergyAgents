package de.enflexit.meo.modellica.eomIntegration;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;

/**
 * A {@link TableCellEditor} implementation for {@link FmuParameterSettings}s
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ParameterSettingsTableCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1188167148924142922L;
	
	private static final int COLUMN_INDEX_FMU_VARIABLE_NAME = 0;
	private static final int COLUMN_INDEX_DATA_TYPE = 1;
	private static final int COLUMN_INDEX_VALUE = 2;
	private static final int COLUMN_INDEX_UNIT = 3;

	private JTable table;
	private int row;
	private int column;
	
	private Object initialValue;
	private Object editedValue;
	private JComponent editorComponent;
	
	private FmuParameterSettings parameterSettings;
	
	private FmuStaticModelSubPanelParameters parentPanel;

	/**
	 * Instantiates a new parameter settings table cell editor.
	 * @param parentPanel the parent panel
	 */
	public ParameterSettingsTableCellEditor(FmuStaticModelSubPanelParameters parentPanel) {
		super();
		this.parentPanel = parentPanel;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {
		return this.editedValue;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.table = table;
		this.row = row;
		this.column = column;
		this.initialValue = value;
		this.editedValue = value;
		
		String fmuName = (String) table.getValueAt(row, COLUMN_INDEX_FMU_VARIABLE_NAME);
		this.parameterSettings = this.parentPanel.getParameterSettingsByFmuName(fmuName);
		
		//TODO implement combo box for variable name selection
		 if (this.column==COLUMN_INDEX_DATA_TYPE) {
			 this.editorComponent = this.getDataTypesComboBox();
		 } else {
			 this.editorComponent = this.getTextFieldEditorComponent();
		 }
		
		return this.editorComponent;
	}
	
	/**
	 * Gets a combo box for selecting the parameters data type.
	 * @return the data types combo box
	 */
	private JComboBox<String> getDataTypesComboBox(){
		String[] possibleTypes = {Double.class.getSimpleName(), Boolean.class.getSimpleName(), Integer.class.getSimpleName()};
		JComboBox<String> dataTypesComboBox = new JComboBox<String>(possibleTypes);
		dataTypesComboBox.setSelectedItem(this.initialValue);
		dataTypesComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ParameterSettingsTableCellEditor.this.editedValue = dataTypesComboBox.getSelectedItem();
				
				// --- Parse the string from the value column with the new data type
				if (ParameterSettingsTableCellEditor.this.editedValue != ParameterSettingsTableCellEditor.this.initialValue) {
					String valueString = (String) ParameterSettingsTableCellEditor.this.table.getValueAt(ParameterSettingsTableCellEditor.this.row, COLUMN_INDEX_VALUE);
					ParameterSettingsTableCellEditor.this.updateParameterValue(valueString);
				}
			}
		});
		return dataTypesComboBox;
	}

	/**
	 * Gets a text field for editing the string-based columns.
	 * @return the text field editor component
	 */
	private JTextField getTextFieldEditorComponent() {
		JTextField textField = new JTextField((String) initialValue);
		textField.setOpaque(true);
		textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		textField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				this.updateValue();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				this.updateValue();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				this.updateValue();
			}
			
			private void updateValue() {
				String newValue = textField.getText();
				ParameterSettingsTableCellEditor.this.editedValue = newValue;
				
				if (column == COLUMN_INDEX_FMU_VARIABLE_NAME) {
					//TODO Check for duplicate
					ParameterSettingsTableCellEditor.this.parameterSettings.setFmuVariableName(newValue);
				} else if (column == COLUMN_INDEX_VALUE) {
					ParameterSettingsTableCellEditor.this.updateParameterValue(newValue);
				} else if (column == COLUMN_INDEX_UNIT) {
					ParameterSettingsTableCellEditor.this.parameterSettings.setUnit(newValue);
				}
			}
		});
		return textField;
	}
	
	/**
	 * Parses the string from the table's value column and sets it to the parameter settings. 
	 * @param valueString the value string
	 */
	private void updateParameterValue(String valueString) {
		if (valueString!=null && valueString.length()>0) {
			Object valueObject = null;
			String dataType = (String) this.table.getValueAt(this.row, COLUMN_INDEX_DATA_TYPE);
			try {
				if (dataType.equals(Double.class.getSimpleName())) {
					valueObject = Double.parseDouble(valueString);
				} else if (dataType.equals(Integer.class.getSimpleName())) {
					valueObject = Integer.parseInt(valueString);
				} else if (dataType.equals(Boolean.class.getSimpleName())) {
					valueObject = Boolean.parseBoolean(valueString);
				}
			} catch (NumberFormatException nfe) {
				System.err.println("[" + this.getClass().getSimpleName() + "] Could not parse non-numeric data - please check the specified value and data type!");
			}
			ParameterSettingsTableCellEditor.this.parameterSettings.setValue(valueObject);
		}
	}
}
