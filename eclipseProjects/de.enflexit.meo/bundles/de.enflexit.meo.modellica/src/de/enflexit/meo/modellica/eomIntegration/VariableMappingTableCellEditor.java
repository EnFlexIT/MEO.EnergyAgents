package de.enflexit.meo.modellica.eomIntegration;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;

import de.enflexit.meo.modellica.eomIntegration.FmuVariableMapping.IoVariableType;

/**
 * A {@link TableCellEditor} implementation for {@link FmuVariableMapping}s.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class VariableMappingTableCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 2009509601351765818L;
	
	private static final int COLUMN_INDEX_EOM_VARIABLE_NAME = 0;
	private static final int COLUMN_INDEX_FMU_VARIABLE_NAME = 1;
	private static final int COLUMN_INDEX_VARIABLE_TYPE = 2;
	private static final int COLUMN_INDEX_VARIABLE_UNIT = 3;
	
	private Object initialValue;
	private Object editedValue;
	private int column;
	private JComponent editorComponent;
	
	private FmuVariableMapping variableMapping;
	
	private FmuStaticModelSubPanelVariables parentPanel;

	/**
	 * Instantiates a new variable mapping table cell editor.
	 * @param parentPanel the parent panel
	 */
	public VariableMappingTableCellEditor(FmuStaticModelSubPanelVariables parentPanel) {
		super();
		this.parentPanel = parentPanel;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {
		return editedValue;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.column = column;
		this.initialValue = value;
		this.editedValue = value;
		
		String eomName = (String) table.getValueAt(row, COLUMN_INDEX_EOM_VARIABLE_NAME);
		this.variableMapping = parentPanel.getVariableMappingByEomName(eomName);
		
		switch(this.column) {
		case COLUMN_INDEX_EOM_VARIABLE_NAME:
		case COLUMN_INDEX_FMU_VARIABLE_NAME:
			this.editorComponent = this.getVariableSelectionComboBox();
			break;
		case COLUMN_INDEX_VARIABLE_TYPE:
			this.editorComponent = this.getTypeSelectionComboBox();
			break;
		default:
			this.editorComponent = this.getTextFieldEditorComponent();
		}
		
		return this.editorComponent;
	}
	
	/**
	 * Gets a JComboBox for selecting the {@link IoVariableType} for the mapping. 
	 * @return the type selection combo box
	 */
	private JComboBox<IoVariableType> getTypeSelectionComboBox() {
		JComboBox<IoVariableType> typeSelectionComboBox = new JComboBox<FmuVariableMapping.IoVariableType>(IoVariableType.values());
		typeSelectionComboBox.setSelectedItem(this.initialValue);
		typeSelectionComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				IoVariableType selectedType = (IoVariableType) typeSelectionComboBox.getSelectedItem();
				VariableMappingTableCellEditor.this.variableMapping.setVariableType(selectedType);
				VariableMappingTableCellEditor.this.editedValue = selectedType;
			}
		});
		return typeSelectionComboBox;
	}
	
	private JComboBox<String> getVariableSelectionComboBox(){
		Vector<String> options = new Vector<String>();
		options.add("Please Select");
		
		if (this.column==COLUMN_INDEX_EOM_VARIABLE_NAME) {
			options.addAll(this.parentPanel.getEomVariablesList());
		} else if (this.column==COLUMN_INDEX_FMU_VARIABLE_NAME) {
			options.addAll(this.parentPanel.getFmuVariablesList());
		}
		
		JComboBox<String> variableSelectionComboBox = new JComboBox<String>(options);
		if (this.initialValue!=null && options.contains(this.initialValue)) {
			variableSelectionComboBox.setSelectedItem(this.initialValue);
		}
		variableSelectionComboBox.addActionListener(new ActionListener() {
			
			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				if (variableSelectionComboBox.getSelectedIndex()>0) {
					String variableName = (String) variableSelectionComboBox.getSelectedItem();
					VariableMappingTableCellEditor.this.editedValue = variableName;
					if (VariableMappingTableCellEditor.this.column==COLUMN_INDEX_EOM_VARIABLE_NAME) {
						VariableMappingTableCellEditor.this.variableMapping.setEomVariableName(variableName);
					} else {
						VariableMappingTableCellEditor.this.variableMapping.setFmuVariableName(variableName);
					}
				}
			}
		});
		
		return variableSelectionComboBox;
	}

	/**
	 * Gets a JTextField for editing string-based table cells.
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
				VariableMappingTableCellEditor.this.editedValue = newValue;
				
				if (column == COLUMN_INDEX_EOM_VARIABLE_NAME) {
					//TODO Check for duplicate
					VariableMappingTableCellEditor.this.variableMapping.setEomVariableName(newValue);
				} else if (column == COLUMN_INDEX_FMU_VARIABLE_NAME) {
					//TODO Check for duplicate
					VariableMappingTableCellEditor.this.variableMapping.setFmuVariableName(newValue);
				} else if (column == COLUMN_INDEX_VARIABLE_UNIT) {
					VariableMappingTableCellEditor.this.variableMapping.setUnit(newValue);
				}
			}
		});
		return textField;
	}
	
}
