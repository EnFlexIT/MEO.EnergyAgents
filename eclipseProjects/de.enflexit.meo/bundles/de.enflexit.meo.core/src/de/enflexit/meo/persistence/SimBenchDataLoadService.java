package de.enflexit.meo.persistence;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import agentgui.core.application.Application;
import de.enflexit.common.csv.CSV_FilePreview;
import de.enflexit.meo.BundleHelper;
import energy.GlobalInfo;
import energy.optionModel.ScheduleList;
import energy.persistence.service.PersistenceConfigurator;
import energy.persistence.service.PersistenceHandler.PersistenceAction;
import energy.persistence.service.PersistenceServiceScheduleList;
import energy.schedule.ScheduleController;
import energy.schedule.loading.ScheduleTimeRange;
import energy.schedule.loading.gui.ScheduleTimeRangeSelectionPanel;

/**
 * The SimBenchDataLoadService is used to read ScheduelList data from SimBench csv files. 
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg-Essen
 */
public class SimBenchDataLoadService implements PersistenceServiceScheduleList {

	private boolean isDebug = true;
	private PersistenceConfigurator persistenceConfigurator;
	
	/**
	 * Creates a new SimBenchDataLoadService.
	 */
	public SimBenchDataLoadService() { }
	
	
	// --------------------------------------------------------------------------------------------
	// --- The visual elements for the database handling ------------------------------------------
	// --------------------------------------------------------------------------------------------	
	/* (non-Javadoc)
	 * @see energy.persistence.service.PersistenceService#getJMenueItem(energy.persistence.service.PersistenceHandler.PersistenceAction)
	 */
	@Override
	public JMenuItem getJMenueItem(PersistenceAction action) {
		
		JMenuItem jMenuItem = null;
		switch (action) {
		case LOAD:
			jMenuItem = new JMenuItem();
			jMenuItem.setText("Open SimBench csv-File");
			jMenuItem.setIcon(BundleHelper.getImageIcon("ImportSB.png"));
			break;

		default:
			break;
		}
		return jMenuItem;
	}
	/* (non-Javadoc)
	 * @see energy.persistence.service.PersistenceService#getIndexPositionOfMenuItem()
	 */
	@Override
	public int getIndexPositionOfMenuItem() {
		return 2;
	}
	/* (non-Javadoc)
	 * @see energy.persistence.service.PersistenceService#setPersistenceConfigurator(energy.persistence.service.PersistenceConfigurator)
	 */
	@Override
	public void setPersistenceConfigurator(PersistenceConfigurator persistenceConfigurator) {
		this.persistenceConfigurator = persistenceConfigurator;
	}
	
	// --------------------------------------------------------------------------------------------
	// --- The actual data loading method --------------------------------------------------------- 
	// --------------------------------------------------------------------------------------------	
	/* (non-Javadoc)
	 * @see energy.persistence.service.PersistenceServiceScheduleList#loadScheduleList(ScheduleController, ScheduleTimeRange, Component)
	 */
	@Override
	public ScheduleList loadScheduleList(ScheduleController sc, ScheduleTimeRange scheduleTimeRange, Component invoker) {
		
		// --- Get file selection from user -----------------------------------
		File simBenchFileSelected = this.selectFile(sc, PersistenceAction.LOAD, invoker);
		if (simBenchFileSelected==null) return null;

		// --- Ensure that the SimBench model will be loaded ------------------ 
		SimBenchFileLoader.getInstance().setSimBenchDirectoryFile(simBenchFileSelected, this.isDebug);
		
		CSV_FilePreview preview = this.getCustomizedCsvFilePreview();
		
		
		
		
		
		return null;
	}

	/**
	 * Return the customized {@link CSV_FilePreview} that enables the further proceeding  
	 * @return the customized CSV_FilePreview with an added control panel  
	 */
	private CSV_FilePreview getCustomizedCsvFilePreview() {
		
		// --- Get the current preview dialog ---------------------------------
		CSV_FilePreview preview = SimBenchFileLoader.getInstance().getCSVFilePreviewDialog();
		if (preview!=null) {
			// --- Set temporary invisible ------------------------------------
			preview.setVisible(false);
			// --- Create SimBenchProceedSelectionPanel -----------------------
			SimBenchProceedSelectionPanel proceedPanel = new SimBenchProceedSelectionPanel();
			proceedPanel.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ae) {
					
					// --- Get the SimBenchProceedSelectionPanel --------------
					JButton button = (JButton) ae.getSource();
					SimBenchProceedSelectionPanel proceedPanel = (SimBenchProceedSelectionPanel) button.getParent();
					
					// --- Get the instance of the CSV_FilePreview ------------ 
					Dialog parentDialog = Application.getGlobalInfo().getOwnerDialogForComponent((JComponent) ae.getSource());
					if (parentDialog instanceof CSV_FilePreview) {
						CSV_FilePreview preview = (CSV_FilePreview) parentDialog;
						if (proceedPanel.isCanceled()==true) {
							preview.setVisible(false);
							return;
						}
						
						// --- Check the selection in the 'node' table --------
						
						
						
						
					}
				}
			});
			// --- Add to CSV_FilePreview -------------------------------------
			preview.getContentPane().add(proceedPanel, BorderLayout.SOUTH);
			preview.validate();
			preview.repaint();
			
			// --- Set focus to 'node' file -----------------------------------
			preview.setTabFocusToFile(SimBenchFileLoader.SIMBENCH_Node);
			
			// --- Set the dialog to appear modal -----------------------------
			preview.setModal(true);
			preview.setVisible(true);
			// - - Wait for user - - - - - - - - - - - - - - - - - - - - - - -
		}
		return preview;
	}
	
	
	
	
	/**
	 * Select file to load or save ScheduleList information.
	 *
	 * @param sc the current ScheduleController
	 * @param action the PersistenceAction
	 * @param parentComponent the parent component
	 * @return the selected file instance
	 */
	public File selectFile(ScheduleController sc, PersistenceAction action, Component parentComponent) {
		
		File fileSelected = null;
		File dirSelected = null;
		boolean isCreatedDirSelected = false; 

		// --- Consider a persistence configurator? -------
		if (this.persistenceConfigurator!=null) {
			// --- Get predefined settings ----------------
			HashMap<String, Object> predefinedSettings = this.persistenceConfigurator.getPersistenceSettingsBeforeUserInteraction(sc, action, this, parentComponent); 
			if (predefinedSettings!=null) {
				Object fileObject = predefinedSettings.get(PersistenceConfigurator.FILE_INSTANCE);
				if (fileObject!=null && fileObject instanceof File) {
					fileSelected = (File) fileObject;
				}
			}
		}
		
		// --- Allow user to select a file ----------------
		if (this.persistenceConfigurator==null || (this.persistenceConfigurator!=null && this.persistenceConfigurator.isAllowUserInteraction(sc, action, this)==true)) {
			// --- Some preparations ----------------------
			String caption = "";
			boolean allowNewFolder = true;
			switch (action) {
			case LOAD:
				caption = "Open File";
				allowNewFolder = false;
				break;
			case SAVE:
				caption = "Save File";
				break;
			case SAVE_AS:
				caption = "Save File As";
				break;
			}
			
			// --- The possible time range selection ------
			ScheduleTimeRangeSelectionPanel timeRangeSelectionPanel = null;
			
			//---------------------------------------------
			// --- Create file choose instance ------------
			//---------------------------------------------
			FileFilter fileFilterCSV = new FileNameExtensionFilter("SimBench csv file", "csv");
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(GlobalInfo.getLastSelectedDirectory());
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.addChoosableFileFilter(fileFilterCSV);
			fileChooser.setFileFilter(fileFilterCSV);
			if (allowNewFolder==false) this.disableNewFolderButton(fileChooser);	
			
			// --- Use predefined file selection? --------- 
			if (fileSelected!=null) {
				dirSelected = fileSelected.getParentFile();
				if (dirSelected.exists()==false) {
					dirSelected.mkdirs();
					isCreatedDirSelected = true;
				}
				fileChooser.setCurrentDirectory(dirSelected);
				fileChooser.setSelectedFile(fileSelected);	
			}
			
			//---------------------------------------------
			// --- Configure a ScheduleTimeRange? ---------
			//---------------------------------------------
			if (action==PersistenceAction.LOAD) {
				timeRangeSelectionPanel = new ScheduleTimeRangeSelectionPanel(sc.getScheduleTimeRange());
				fileChooser.setAccessory(timeRangeSelectionPanel);
			}
			
			// --- Show file selection dialog -------------
			int ret = fileChooser.showDialog(parentComponent, caption);
			// - - - - - - - - - - - - - - - - - - - - - -  
			
			if (ret == JFileChooser.APPROVE_OPTION) {
				fileSelected = fileChooser.getSelectedFile();
				String fileExtension = null;
				if (fileChooser.getFileFilter()==fileFilterCSV) {
					fileExtension = ".csv";
				}
				if (fileSelected.getAbsolutePath().toLowerCase().endsWith(fileExtension)==false) {
					fileSelected = new File(fileSelected.getAbsoluteFile() + fileExtension);	
				}
				GlobalInfo.setLastSelectedDirectory(fileChooser.getCurrentDirectory());
				
				// --- Valid time range selection? --------
				if (timeRangeSelectionPanel!=null) {
					ScheduleTimeRange scheduleTimeRange = timeRangeSelectionPanel.getScheduleTimeRange();
					if (scheduleTimeRange==null || scheduleTimeRange.isValidScheduleTimeRange()==false) {
						sc.setScheduleTimeRange(null);
					} else {
						sc.setScheduleTimeRange(scheduleTimeRange);
					}
				}
				
			} else {
				fileSelected = null;
				if (isCreatedDirSelected==true) {
					dirSelected.delete();
				}
			}
		}
		
		// --- Validate the selected file? ----------------
		if (fileSelected!=null && this.persistenceConfigurator!=null) {
			HashMap<String, Object> finalSettings = new HashMap<>();
			finalSettings.put(PersistenceConfigurator.FILE_INSTANCE, fileSelected);
			if (this.persistenceConfigurator.isValidPersistenceSettings(sc, action, this, parentComponent, finalSettings)==false) {
				fileSelected = null;
			}
		}
		return fileSelected;
	}
	/**
	 * Disable new folder button.
	 * @param checkContainer the container to check
	 */
	private void disableNewFolderButton(Container checkContainer) {
		int len = checkContainer.getComponentCount();
		for (int i = 0; i < len; i++) {
			Component comp = checkContainer.getComponent(i);
			if (comp instanceof JButton) {
				JButton button = (JButton) comp;
				Icon icon = button.getIcon();
				if (icon!=null && icon==UIManager.getIcon("FileChooser.newFolderIcon")) {
					button.setEnabled(false);
					return;
				}
			} else if (comp instanceof Container) {
				disableNewFolderButton((Container) comp);
			}
		}
	}
	
	
	
	
	// --------------------------------------------------------------------------------------------
	// --- The methods below are not required for this service - read only ------------------------ 
	// --------------------------------------------------------------------------------------------	
	@Override
	public boolean saveScheduleList(ScheduleController sc, ScheduleList slSave, Component invoker) {
		return false;
	}
	@Override
	public boolean saveScheduleListAs(ScheduleController sc, ScheduleList slSaveAs, Component invoker) {
		return false;
	}

}
