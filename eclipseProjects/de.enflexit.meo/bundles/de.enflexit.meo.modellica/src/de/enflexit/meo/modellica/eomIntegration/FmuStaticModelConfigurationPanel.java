package de.enflexit.meo.modellica.eomIntegration;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.javafmi.modeldescription.ModelDescription;
import org.javafmi.modeldescription.ScalarVariable;
import org.javafmi.proxy.FmuFile;
import org.javafmi.wrapper.Simulation;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.Dimension;
import java.awt.Font;
import agentgui.core.application.Application;
import agentgui.core.config.GlobalInfo;
import energy.OptionModelController;
import energy.helper.UnitConverter;
import energy.optionModel.SystemVariableDefinition;
import energy.optionModel.SystemVariableDefinitionBoolean;
import energy.optionModel.SystemVariableDefinitionDouble;
import energy.optionModel.SystemVariableDefinitionInteger;
import energy.optionModel.TimeUnit;
import energy.optionModel.gui.components.TimeUnitComboBox;
import javax.swing.JSeparator;

/**
 * This panel provides a GUI to configure an {@link FmuStaticDataModel}. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FmuStaticModelConfigurationPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = -9051900502061409565L;
	
	private static final String IMAGE_ICON_FMU_FAIL = "MBcheckRed.png";
	private static final String IMAGE_ICON_FMU_OK = "MBcheckGreen.png";
	
	private FmuStaticDataModel staticDataModel;

	private JLabel jLabelFmuFile;
	private JTextField jTextFieldFmuFile;
	private JButton jButtonFmuFile;
	private JFileChooser fileChooserFmuFile;
	
	private JLabel jLabelFmuStepSize;
	private JTextField jTextFieldFmuStepSize;
	private TimeUnitComboBox comboBoxTimeUnit;
	private FmuStaticModelSubPanelParameters subPanelParameters;
	private FmuStaticModelSubPanelVariables subPanelVariables;
	private JSeparator jSeparator2;
	private JSeparator jSeparator1;
	private JLabel jLabelFmuState;
	
	private Simulation fmuModel;
	private Vector<String> fmuVariablesList;
	private Vector<String> eomVariablesList;
	
	private OptionModelController optionModelController;
	
	private Path projectFolderPath;
	
	/**
	 * Instantiates a new FMU static model configuration panel.
	 * @param staticDataModel the static data model
	 */
	public FmuStaticModelConfigurationPanel(FmuStaticDataModel staticDataModel, OptionModelController optionModelController) {
		this.setStaticDataModel(staticDataModel);
		this.optionModelController = optionModelController;
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
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
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
		GridBagConstraints gbc_jLabelFmuState = new GridBagConstraints();
		gbc_jLabelFmuState.insets = new Insets(0, 0, 5, 5);
		gbc_jLabelFmuState.gridx = 3;
		gbc_jLabelFmuState.gridy = 0;
		add(getJLabelFmuState(), gbc_jLabelFmuState);
		GridBagConstraints gbc_jButtonFmuFile = new GridBagConstraints();
		gbc_jButtonFmuFile.insets = new Insets(5, 0, 5, 0);
		gbc_jButtonFmuFile.gridx = 4;
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
		GridBagConstraints gbc_jSeparator1 = new GridBagConstraints();
		gbc_jSeparator1.gridwidth = 5;
		gbc_jSeparator1.insets = new Insets(0, 0, 5, 0);
		gbc_jSeparator1.gridx = 0;
		gbc_jSeparator1.gridy = 2;
		add(getJSeparator1(), gbc_jSeparator1);
		GridBagConstraints gbc_subPanelParameters = new GridBagConstraints();
		gbc_subPanelParameters.insets = new Insets(0, 0, 5, 0);
		gbc_subPanelParameters.gridwidth = 5;
		gbc_subPanelParameters.fill = GridBagConstraints.BOTH;
		gbc_subPanelParameters.gridx = 0;
		gbc_subPanelParameters.gridy = 3;
		add(getSubPanelParameters(), gbc_subPanelParameters);
		GridBagConstraints gbc_jSeparator2 = new GridBagConstraints();
		gbc_jSeparator2.gridwidth = 5;
		gbc_jSeparator2.insets = new Insets(0, 0, 5, 0);
		gbc_jSeparator2.gridx = 0;
		gbc_jSeparator2.gridy = 4;
		add(getJSeparator2(), gbc_jSeparator2);
		GridBagConstraints gbc_subPanelVariables = new GridBagConstraints();
		gbc_subPanelVariables.gridwidth = 5;
		gbc_subPanelVariables.fill = GridBagConstraints.BOTH;
		gbc_subPanelVariables.gridx = 0;
		gbc_subPanelVariables.gridy = 5;
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
	private JLabel getJLabelFmuState() {
		if (jLabelFmuState == null) {
			jLabelFmuState = new JLabel("");
			jLabelFmuState.setIcon(GlobalInfo.getInternalImageIcon(IMAGE_ICON_FMU_FAIL));
		}
		return jLabelFmuState;
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
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource()==this.getJButtonFmuFile()) {
			
			// --- Select an FMU file -------------------------------
			int fileChooserResult = this.getFileChooserFmuFile().showOpenDialog(this);
			if (fileChooserResult==JFileChooser.APPROVE_OPTION) {
				File fmuFile = this.getFileChooserFmuFile().getSelectedFile();
				if (fmuFile.exists()) {
					
					Path fmuFilePath = fmuFile.toPath();

					// --- Check if the FMU is inside the project folder ------
					if (fmuFilePath.startsWith(this.getProjectFolderPath())==false) {
						String errorMessage = "The FMU file must be located within the current AWP project folder!";
						JOptionPane.showMessageDialog(this, errorMessage, "Invalid FMU location", JOptionPane.ERROR_MESSAGE);
					} else {
						
						// --- Load the selected FMU file ---------------------
						this.loadFmuFromFile(fmuFile);
						
						if (this.fmuModel!=null) {
							// --- Set the relative path to the text field ----
							Path relativePath = this.getProjectFolderPath().relativize(fmuFilePath);
							this.getJTextFieldFmuFile().setText(relativePath.toString());
							Application.getGlobalInfo().setLastSelectedFolder(this.getFileChooserFmuFile().getCurrentDirectory());
							this.setFmuStateIcon(this.fmuModel!=null);
						}
					}
					
				}
			}
		}
	}
	
	/**
	 * Sets the fmu state.
	 * @param available the new fmu state
	 */
	private void setFmuStateIcon(boolean available) {
		ImageIcon stateIcon = null;
		if (available==true) {
			stateIcon = GlobalInfo.getInternalImageIcon(IMAGE_ICON_FMU_OK);
		} else {
			stateIcon = GlobalInfo.getInternalImageIcon(IMAGE_ICON_FMU_FAIL);
		}
		this.getJLabelFmuState().setIcon(stateIcon);
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
		if (this.staticDataModel.getFmuFilePath()!=null) {
			Path relativePath = Paths.get(this.staticDataModel.getFmuFilePath());
			Path absolutePath = this.getProjectFolderPath().resolve(relativePath);
			
			this.loadFmuFromFile(absolutePath);
			this.setFmuStateIcon(this.fmuModel!=null);
		}
		
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
			subPanelParameters = new FmuStaticModelSubPanelParameters(this);
		}
		return subPanelParameters;
	}
	private FmuStaticModelSubPanelVariables getSubPanelVariables() {
		if (subPanelVariables == null) {
			subPanelVariables = new FmuStaticModelSubPanelVariables(this);
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

	/**
	 * Loads an FMU model from the file with the specified path.
	 * @param path the path
	 */
	private void loadFmuFromFile(Path path) {
		this.loadFmuFromFile(path.toFile());
	}
	
	/**
	 * Loads an FMU model from the specified file.
	 * @param fmuFile the fmu file
	 */
	private void loadFmuFromFile(File fmuFile) {
		this.fmuModel = null;
		if (fmuFile!=null && fmuFile.exists()) {
			try {
				// --- Load the FMU from the file -----------------------------
				this.fmuModel = new Simulation(new FmuFile(fmuFile));
				// --- Reset the FMU variables list ---------------------------
				this.fmuVariablesList = null;
				// --- Clear the settings that depend on the FMU variables ----
				this.getSubPanelParameters().removeObsoleteParameterSettings();
				this.getSubPanelVariables().removeObsoleteVariableMappings();
			} catch (Exception ex) {
				System.err.println("[" + this.getClass().getSimpleName() + "] The selected file contains no valid FMU: " + fmuFile.getAbsolutePath());
			}
		} else {
			System.err.println("[" + this.getClass().getSimpleName() + "] The specified FMU file does not exist on this system, please check your path settings. " + fmuFile.getAbsolutePath());
		}
	}
	
	/**
	 * Gets the FMU variables list.
	 * @return the FMU variables list
	 */
	protected Vector<String> getFmuVariablesList(){
		
		if (this.fmuVariablesList==null) {
			this.fmuVariablesList = new Vector<String>();
			
			if (this.fmuModel!=null) {
				
				// --- Get the variables list from the FMU --------------
				ModelDescription modelDescription = this.fmuModel.getModelDescription();
				ScalarVariable[] modelVariables = modelDescription.getModelVariables();
				
				// --- Only include input and output variables ----------
				for (int i=0; i<modelVariables.length; i++) {
					ScalarVariable variable = modelVariables[i];
					if (variable.getCausality().equals("input") || variable.getCausality().equals("output")) {
						this.fmuVariablesList.add(variable.getName());
					}
				}
			}
			
		}
		return this.fmuVariablesList;
	}
	
	/**
	 * Gets the EOM variables list.
	 * @return the EOM variables list
	 */
	protected Vector<String> getEomVariablesList(){
		if (this.eomVariablesList==null) {
			this.eomVariablesList = new Vector<String>();
			List<SystemVariableDefinition> systemVariables = this.optionModelController.getTechnicalSystem().getSystemVariables();
			
			// --- Include boolean, double and integer variables -------- 
			for (int i=0; i<systemVariables.size(); i++) {
				SystemVariableDefinition sysVarDef = systemVariables.get(i);
				if (sysVarDef instanceof SystemVariableDefinitionBoolean 
						|| sysVarDef instanceof SystemVariableDefinitionDouble 
						|| sysVarDef instanceof SystemVariableDefinitionInteger) {
					this.eomVariablesList.add(sysVarDef.getVariableID());
				}
			}
		}
		
		return this.eomVariablesList;
	}
	
	/**
	 * Gets the project folder path.
	 * @return the project folder path
	 */
	public Path getProjectFolderPath() {
		if (projectFolderPath==null) {
			projectFolderPath = Paths.get(Application.getProjectFocused().getProjectFolderFullPath());
		}
		return projectFolderPath;
	}
	
}
