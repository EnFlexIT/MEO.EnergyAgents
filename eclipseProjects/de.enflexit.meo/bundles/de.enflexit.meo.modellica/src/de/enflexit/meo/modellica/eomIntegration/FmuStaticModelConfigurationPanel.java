package de.enflexit.meo.modellica.eomIntegration;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.Dimension;
import java.awt.Font;
import agentgui.core.application.Application;
import agentgui.core.config.GlobalInfo;
import energy.helper.UnitConverter;
import energy.optionModel.TimeUnit;
import energy.optionModel.gui.components.TimeUnitComboBox;
import javax.swing.JSeparator;

/**
 * This panel provides a GUI to configure an {@link FmuStaticDataModel}. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuStaticModelConfigurationPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = -9051900502061409565L;
	
	private FmuStaticDataModel staticDataModel;

	private JLabel jLabelFmuFile;
	private JTextField jTextFieldFmuFile;
	private JButton jButtonFmuFile;
	private JFileChooser fileChooserFmuFile;
	
	private JLabel jLabelFmuStepSize;
	private JTextField jTextFieldFmuStepSize;
	
	private JLabel jLabelSimulationWrapper;
	private JTextField jTextFieldSImulationWrapper;
	private JButton jButtonSimulationWrapper;
	private TimeUnitComboBox comboBoxTimeUnit;
	private FmuStaticModelSubPanelParameters subPanelParameters;
	private FmuStaticModelSubPanelVariables subPanelVariables;
	private JSeparator jSeparator2;
	private JSeparator jSeparator1;
	
	/**
	 * Instantiates a new FMU static model configuration panel.
	 * @param staticDataModel the static data model
	 */
	public FmuStaticModelConfigurationPanel(FmuStaticDataModel staticDataModel) {
		this.setStaticDataModel(staticDataModel);
		this.initialize();
		if (this.staticDataModel!=null) {
			this.loadStaticModelToForm();
		}
	}
	
	/**
	 * Sets the static data model.
	 * @param staticDataModel the new static data model
	 */
	private void setStaticDataModel(FmuStaticDataModel staticDataModel) {
		this.staticDataModel = staticDataModel;
	}
	
	/**
	 * Initializes the GUI components.
	 */
	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelFmuFile = new GridBagConstraints();
		gbc_jLabelFmuFile.insets = new Insets(5, 5, 5, 5);
		gbc_jLabelFmuFile.anchor = GridBagConstraints.EAST;
		gbc_jLabelFmuFile.gridx = 0;
		gbc_jLabelFmuFile.gridy = 0;
		add(getJLabelFmuFile(), gbc_jLabelFmuFile);
		GridBagConstraints gbc_jTextFieldFmuFile = new GridBagConstraints();
		gbc_jTextFieldFmuFile.gridwidth = 2;
		gbc_jTextFieldFmuFile.insets = new Insets(5, 0, 5, 5);
		gbc_jTextFieldFmuFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldFmuFile.gridx = 1;
		gbc_jTextFieldFmuFile.gridy = 0;
		add(getJTextFieldFmuFile(), gbc_jTextFieldFmuFile);
		GridBagConstraints gbc_jButtonFmuFile = new GridBagConstraints();
		gbc_jButtonFmuFile.insets = new Insets(5, 0, 5, 5);
		gbc_jButtonFmuFile.gridx = 3;
		gbc_jButtonFmuFile.gridy = 0;
		add(getJButtonFmuFile(), gbc_jButtonFmuFile);
		GridBagConstraints gbc_jLabelFmuStepSize = new GridBagConstraints();
		gbc_jLabelFmuStepSize.anchor = GridBagConstraints.EAST;
		gbc_jLabelFmuStepSize.insets = new Insets(0, 5, 5, 5);
		gbc_jLabelFmuStepSize.gridx = 0;
		gbc_jLabelFmuStepSize.gridy = 1;
		add(getJLabelFmuStepSize(), gbc_jLabelFmuStepSize);
		GridBagConstraints gbc_jTextFieldFmuStepSize = new GridBagConstraints();
		gbc_jTextFieldFmuStepSize.insets = new Insets(0, 0, 5, 5);
		gbc_jTextFieldFmuStepSize.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldFmuStepSize.gridx = 1;
		gbc_jTextFieldFmuStepSize.gridy = 1;
		add(getJTextFieldFmuStepSize(), gbc_jTextFieldFmuStepSize);
		GridBagConstraints gbc_comboBoxTimeUnit = new GridBagConstraints();
		gbc_comboBoxTimeUnit.anchor = GridBagConstraints.WEST;
		gbc_comboBoxTimeUnit.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxTimeUnit.gridx = 2;
		gbc_comboBoxTimeUnit.gridy = 1;
		add(getComboBoxTimeUnit(), gbc_comboBoxTimeUnit);
		GridBagConstraints gbc_jLabelSimulationWrapper = new GridBagConstraints();
		gbc_jLabelSimulationWrapper.anchor = GridBagConstraints.EAST;
		gbc_jLabelSimulationWrapper.insets = new Insets(0, 5, 5, 5);
		gbc_jLabelSimulationWrapper.gridx = 0;
		gbc_jLabelSimulationWrapper.gridy = 2;
		add(getJLabelSimulationWrapper(), gbc_jLabelSimulationWrapper);
		GridBagConstraints gbc_jTextFieldSImulationWrapper = new GridBagConstraints();
		gbc_jTextFieldSImulationWrapper.gridwidth = 2;
		gbc_jTextFieldSImulationWrapper.insets = new Insets(0, 0, 5, 5);
		gbc_jTextFieldSImulationWrapper.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFieldSImulationWrapper.gridx = 1;
		gbc_jTextFieldSImulationWrapper.gridy = 2;
		add(getJTextFieldSImulationWrapper(), gbc_jTextFieldSImulationWrapper);
		GridBagConstraints gbc_jButtonSimulationWrapper = new GridBagConstraints();
		gbc_jButtonSimulationWrapper.insets = new Insets(5, 0, 5, 5);
		gbc_jButtonSimulationWrapper.gridx = 3;
		gbc_jButtonSimulationWrapper.gridy = 2;
		add(getJButtonSimulationWrapper(), gbc_jButtonSimulationWrapper);
		GridBagConstraints gbc_jSeparator1 = new GridBagConstraints();
		gbc_jSeparator1.gridwidth = 4;
		gbc_jSeparator1.insets = new Insets(0, 0, 5, 5);
		gbc_jSeparator1.gridx = 0;
		gbc_jSeparator1.gridy = 3;
		add(getJSeparator1(), gbc_jSeparator1);
		GridBagConstraints gbc_subPanelParameters = new GridBagConstraints();
		gbc_subPanelParameters.insets = new Insets(0, 0, 5, 0);
		gbc_subPanelParameters.gridwidth = 4;
		gbc_subPanelParameters.fill = GridBagConstraints.BOTH;
		gbc_subPanelParameters.gridx = 0;
		gbc_subPanelParameters.gridy = 4;
		add(getSubPanelParameters(), gbc_subPanelParameters);
		GridBagConstraints gbc_jSeparator2 = new GridBagConstraints();
		gbc_jSeparator2.gridwidth = 4;
		gbc_jSeparator2.insets = new Insets(0, 0, 5, 0);
		gbc_jSeparator2.gridx = 0;
		gbc_jSeparator2.gridy = 5;
		add(getJSeparator2(), gbc_jSeparator2);
		GridBagConstraints gbc_subPanelVariables = new GridBagConstraints();
		gbc_subPanelVariables.gridwidth = 4;
		gbc_subPanelVariables.fill = GridBagConstraints.BOTH;
		gbc_subPanelVariables.gridx = 0;
		gbc_subPanelVariables.gridy = 6;
		add(getSubPanelVariables(), gbc_subPanelVariables);
	}

	private JLabel getJLabelFmuFile() {
		if (jLabelFmuFile == null) {
			jLabelFmuFile = new JLabel("FMU File");
			jLabelFmuFile.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelFmuFile;
	}
	private JTextField getJTextFieldFmuFile() {
		if (jTextFieldFmuFile == null) {
			jTextFieldFmuFile = new JTextField();
			jTextFieldFmuFile.setColumns(10);
		}
		return jTextFieldFmuFile;
	}
	private JButton getJButtonFmuFile() {
		if (jButtonFmuFile == null) {
			jButtonFmuFile = new JButton();
			jButtonFmuFile.setToolTipText("Select FMU file");
			jButtonFmuFile.setPreferredSize(new Dimension(45, 26));
			jButtonFmuFile.setIcon(GlobalInfo.getInternalImageIcon("MBopen.png"));
			jButtonFmuFile.addActionListener(this);
		}
		return jButtonFmuFile;
	}
	public JFileChooser getFileChooserFmuFile() {
		if (fileChooserFmuFile==null) {
			fileChooserFmuFile = new JFileChooser(Application.getGlobalInfo().getLastSelectedFolder());
			fileChooserFmuFile.setFileFilter(new FileNameExtensionFilter("FMU files", "fmu"));
		}
		return fileChooserFmuFile;
	}
	
	private JLabel getJLabelFmuStepSize() {
		if (jLabelFmuStepSize == null) {
			jLabelFmuStepSize = new JLabel("FMU Step SIze");
			jLabelFmuStepSize.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelFmuStepSize;
	}
	private JTextField getJTextFieldFmuStepSize() {
		if (jTextFieldFmuStepSize == null) {
			jTextFieldFmuStepSize = new JTextField();
			jTextFieldFmuStepSize.setColumns(10);
		}
		return jTextFieldFmuStepSize;
	}
	private JLabel getJLabelSimulationWrapper() {
		if (jLabelSimulationWrapper == null) {
			jLabelSimulationWrapper = new JLabel("Simulation Wrapper");
			jLabelSimulationWrapper.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jLabelSimulationWrapper;
	}
	private JTextField getJTextFieldSImulationWrapper() {
		if (jTextFieldSImulationWrapper == null) {
			jTextFieldSImulationWrapper = new JTextField();
			jTextFieldSImulationWrapper.setColumns(10);
		}
		return jTextFieldSImulationWrapper;
	}
	private JButton getJButtonSimulationWrapper() {
		if (jButtonSimulationWrapper == null) {
			jButtonSimulationWrapper = new JButton();
			jButtonSimulationWrapper.setToolTipText("Select class");
			jButtonSimulationWrapper.setPreferredSize(new Dimension(45, 26));
			jButtonSimulationWrapper.setIcon(GlobalInfo.getInternalImageIcon("edit.png"));
			jButtonSimulationWrapper.addActionListener(this);
		}
		return jButtonSimulationWrapper;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonFmuFile()) {
			// --- Select an FMU file -------------------------------
			int result = this.getFileChooserFmuFile().showOpenDialog(this);
			if (result==JFileChooser.APPROVE_OPTION) {
				File fmuFile = this.getFileChooserFmuFile().getSelectedFile();
				if (fmuFile.exists()) {
					this.getJTextFieldFmuFile().setText(fmuFile.getPath());
					Application.getGlobalInfo().setLastSelectedFolder(this.getFileChooserFmuFile().getCurrentDirectory());
				}
			}
		} else if (ae.getSource()==this.getJButtonSimulationWrapper()) {
			//TODO select a simulation wrapper class
		}
	}
	
	/**
	 * Updates the static model according to the current form inputs.
	 */
	protected void loadFormToStaticModel() {
		
		// --- Basic FMU settings -------------------------
		this.staticDataModel.setFmuFilePath(this.getJTextFieldFmuFile().getText());
		double stepSizeValue = Double.parseDouble(this.getJTextFieldFmuStepSize().getText());
		TimeUnit stepSizeTimeUnit = this.getComboBoxTimeUnit().getTimeUnit();
		this.staticDataModel.setModelStepSizeMilliSeconds(UnitConverter.convertDurationToMilliseconds(stepSizeValue, stepSizeTimeUnit));
		this.staticDataModel.setStepSizeDisplayTimeUnit(stepSizeTimeUnit);

		// --- System parameters --------------------------
		this.staticDataModel.getParameterSettings().clear();
		this.staticDataModel.getParameterSettings().addAll(this.getSubPanelParameters().getParameterSettings());
		
		// --- System variables ---------------------------
		this.staticDataModel.getVariableMappings().clear();
		this.staticDataModel.getVariableMappings().addAll(this.getSubPanelVariables().getVariableMappings());
	}
	
	/**
	 * Updates the GUI elements with the settings from the static model.
	 */
	protected void loadStaticModelToForm() {
		this.getJTextFieldFmuFile().setText(this.staticDataModel.getFmuFilePath());
		
		long stepSizeMilliSeconds = this.staticDataModel.getModelStepSizeMilliSeconds();
		TimeUnit stepSizeTimeUnit = this.staticDataModel.getStepSizeDisplayTimeUnit();
		if (stepSizeTimeUnit==null) {
			stepSizeTimeUnit = TimeUnit.MILLISECOND_MS;
		}
		this.getJTextFieldFmuStepSize().setText("" + UnitConverter.convertDuration(stepSizeMilliSeconds, stepSizeTimeUnit));
		this.getComboBoxTimeUnit().setSelectedItem(stepSizeTimeUnit);

		this.getSubPanelParameters().setParameterSettings(this.staticDataModel.getParameterSettings());
		this.getSubPanelVariables().setVariableMappings(this.staticDataModel.getVariableMappings());
	}
	
	private TimeUnitComboBox getComboBoxTimeUnit() {
		if (comboBoxTimeUnit == null) {
			comboBoxTimeUnit = new TimeUnitComboBox();
		}
		return comboBoxTimeUnit;
	}
	private FmuStaticModelSubPanelParameters getSubPanelParameters() {
		if (subPanelParameters == null) {
			subPanelParameters = new FmuStaticModelSubPanelParameters();
		}
		return subPanelParameters;
	}
	private FmuStaticModelSubPanelVariables getSubPanelVariables() {
		if (subPanelVariables == null) {
			subPanelVariables = new FmuStaticModelSubPanelVariables();
		}
		return subPanelVariables;
	}
	private JSeparator getJSeparator2() {
		if (jSeparator2 == null) {
			jSeparator2 = new JSeparator();
		}
		return jSeparator2;
	}
	private JSeparator getJSeparator1() {
		if (jSeparator1 == null) {
			jSeparator1 = new JSeparator();
		}
		return jSeparator1;
	}
}
