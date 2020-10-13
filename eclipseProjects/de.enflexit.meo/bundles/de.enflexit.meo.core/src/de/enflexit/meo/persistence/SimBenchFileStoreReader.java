package de.enflexit.meo.persistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import de.enflexit.common.csv.CsvDataController;
import energy.domain.DefaultDomainModelElectricity;
import energy.domain.DefaultDomainModelElectricity.CurrentType;
import energy.domain.DefaultDomainModelElectricity.Phase;
import energy.domain.DefaultDomainModelElectricity.PowerType;
import energy.helper.UnitConverter;
import energy.optionModel.AbstractUsageOfInterface;
import energy.optionModel.EnergyAmount;
import energy.optionModel.EnergyCarrier;
import energy.optionModel.EnergyFlowInWatt;
import energy.optionModel.EnergyUnitFactorPrefixSI;
import energy.optionModel.InterfaceSetting;
import energy.optionModel.Schedule;
import energy.optionModel.ScheduleList;
import energy.optionModel.TechnicalSystemStateEvaluation;
import energy.optionModel.TimeUnit;
import energy.optionModel.UsageOfInterfaceEnergy;
import energy.persistence.ScheduleList_StorageHandler;
import energy.schedule.loading.ScheduleTimeRange;

/**
 * The SimBenchFileLoader represents a singleton instance that enables to
 * load SimBench data and translate it into ScheduleList's 
 *  
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg-Essen
 */
public class SimBenchFileStoreReader {

	// --- From here variables for the time series import ---------------------
	private static final EnergyUnitFactorPrefixSI SI_PREFIX_FOR_ENERGY_FLOWS = EnergyUnitFactorPrefixSI.KILO_K_3;
	private static final EnergyUnitFactorPrefixSI SI_PREFIX_FOR_ENERGY_AMOUNTS = EnergyUnitFactorPrefixSI.KILO_K_3;
	private static final TimeUnit TIME_UNIT_FOR_ENERGY_AMOUNTS = TimeUnit.HOUR_H;
	
	private static final String DEFAULT_TSSE_CONFIG_ID = "Config";
	private static final String DEFAULT_TSSE_STATE_ID = "Prosuming";
	private static final String[] INTERFACE_IDs_Electricity = {"Electricity L1", "Electricity L2", "Electricity L3"};

	private SimpleDateFormat dateFormatter;
	
	
	// --------------------------------------------------------------------------------------------
	// --- From here: Methods to create ScheduelList's --------------------------------------------
	// --------------------------------------------------------------------------------------------
	/**
	 * Return the ScheduleList that can be found through a node in the specified data model row.
	 * @param rowSelected the selected row in the model
	 * @param scheduleTimeRange the ScheduleTimeRange to be considered
	 * @return the ScheduleList
	 */
	public ScheduleList getScheduleListByNode(int rowSelected, ScheduleTimeRange scheduleTimeRange) {
		
		// --- Get the profile and load factors -----------
		String nodeID = this.getNodeID(rowSelected);
		HashMap<String, String> loadRowHashMap = this.getDataRowHashMap(SimBenchFileStore.SIMBENCH_Load, "node", nodeID);
		String profile = loadRowHashMap.get("profile");
		double pLoadFactor = this.parseDouble(loadRowHashMap.get("pLoad"));
		double qLoadFactor = this.parseDouble(loadRowHashMap.get("qLoad"));
		
		// --- Create ScheduleList ------------------------
		Schedule schedule = this.getScheduleOfProfile(profile, pLoadFactor, qLoadFactor, scheduleTimeRange);
		ScheduleList sl = this.createScheduleList("Profile " + profile, nodeID);
		sl.getSchedules().add(schedule);
		return sl;
	}
	/**
	 * Return the ScheduleList that can be found through a load description in the specified data model row.
	 * @param rowSelected the selected row in the model
	 * @param scheduleTimeRange the ScheduleTimeRange to be considered
	 * @return the ScheduleList
	 */
	public ScheduleList getScheduleListByLoad(int rowSelected, ScheduleTimeRange scheduleTimeRange) {
		
		// --- Get the profile and load factors -----------
		String loadID = this.getLoadID(rowSelected);
		HashMap<String, String> loadRowHashMap = this.getDataRowHashMap(SimBenchFileStore.SIMBENCH_Load, "id", loadID);
		String profile = loadRowHashMap.get("profile");
		double pLoadFactor = this.parseDouble(loadRowHashMap.get("pLoad"));
		double qLoadFactor = this.parseDouble(loadRowHashMap.get("qLoad"));
		
		// --- Create ScheduleList ------------------------
		Schedule schedule = this.getScheduleOfProfile(profile, pLoadFactor, qLoadFactor, scheduleTimeRange);
		ScheduleList sl = this.createScheduleList("Profile " + profile, null);
		sl.getSchedules().add(schedule);
		return sl;
	}

	/**
	 * Creates a ScheduleList with the specified systemID.
	 *
	 * @param systemID the system ID to use
	 * @param netCompID the ID of the corresponding NetworkComponent 
	 * @return the schedule list
	 */
	private ScheduleList createScheduleList(String systemID, String netCompID) {
		
		ScheduleList sl = new ScheduleList();
		sl.setSystemID(systemID);
		sl.setNetworkID(netCompID);
		sl.setDescription("Load data imported for " + systemID + "");
		
		// --- Define domain model and interface settings for all phases ----------------
		for (int i=0; i < INTERFACE_IDs_Electricity.length; i++) {

			// --- Active power ----------------------------------------
			String activeInterfaceID = INTERFACE_IDs_Electricity[i] + " P";
			DefaultDomainModelElectricity domainModelActivePower = new DefaultDomainModelElectricity();
			domainModelActivePower.setPowerType(PowerType.ActivePower);
			domainModelActivePower.setCurrentType(CurrentType.AC);
			domainModelActivePower.setPhase(Phase.values()[i+1]);
			domainModelActivePower.setFrequency(50);
			domainModelActivePower.setRatedVoltage(230);
			
			InterfaceSetting interfaceSettingActivePower = new InterfaceSetting();
			interfaceSettingActivePower.setInterfaceID(activeInterfaceID);
			interfaceSettingActivePower.setDomainModel(domainModelActivePower);
			interfaceSettingActivePower.setDomain(EnergyCarrier.ELECTRICITY.value());
			
			sl.getInterfaceSettings().add(interfaceSettingActivePower);
			
			// --- Reactive power -------------------------------------
			String reactiveInterfaceID = INTERFACE_IDs_Electricity[i] + " Q";
			DefaultDomainModelElectricity domainModelReactivePower = new DefaultDomainModelElectricity();
			domainModelReactivePower.setPowerType(PowerType.ReactivePower);
			domainModelReactivePower.setCurrentType(CurrentType.AC);
			domainModelReactivePower.setPhase(Phase.values()[i+1]);
			domainModelReactivePower.setFrequency(50);
			domainModelReactivePower.setRatedVoltage(230);
			
			InterfaceSetting interfaceSettingsReactivePower = new InterfaceSetting();
			interfaceSettingsReactivePower.setInterfaceID(reactiveInterfaceID);
			interfaceSettingsReactivePower.setDomainModel(domainModelReactivePower);
			interfaceSettingsReactivePower.setDomain(EnergyCarrier.ELECTRICITY.value());
			
			sl.getInterfaceSettings().add(interfaceSettingsReactivePower);
			
		}
		return sl;
	}
	
	/**
	 * Return a schedule for the specified profile .
	 *
	 * @param profile the profile
	 * @param pLoadFactor the load factor
	 * @param qLoadFactor the q load factor
	 * @param scheduleTimeRange the ScheduleTimeRange to be considered
	 * @return the schedule of profile
	 */
	private Schedule getScheduleOfProfile(String profile, double pLoadFactor, double qLoadFactor, ScheduleTimeRange scheduleTimeRange) {
		
		// --- Get time series from load profile ------------------------------
		CsvDataController loadProfileCsvController = this.getCsvDataControllerOfCsvFile(SimBenchFileStore.SIMBENCH_LoadProfile);
		Vector<Vector<String>> loadProfileDataVector = this.getDataVectorOfCsvFile(SimBenchFileStore.SIMBENCH_LoadProfile);

		String colNameTime = "time";
		String colNamePLoad = profile + "_pload";
		String colNameQLoad = profile + "_qload";
		
		int ciTime  = loadProfileCsvController.getDataModel().findColumn(colNameTime);
		int ciPLoad = loadProfileCsvController.getDataModel().findColumn(colNamePLoad);
		int ciQLoad = loadProfileCsvController.getDataModel().findColumn(colNameQLoad);
		
		long timeStampPrev = -1;
		TechnicalSystemStateEvaluation tssePrev = null;

		// --- Create the Schedule --------------------------------------------
		Schedule schedule = new Schedule();
		schedule.setStrategyClass(this.getClass().getSimpleName());
		List<TechnicalSystemStateEvaluation> tsseList = schedule.getTechnicalSystemStateList();
		
		// --- Avoid double time stamps ---------------------------------------
		HashSet<Long> timeStampHashSet = new HashSet<Long>();
		long timeFrom = 0;
		long timeTo   = 0; 

		// --- Get data rows for the profile ----------------------------------
		int iMax = loadProfileDataVector.size();
		for (int i = 0; i < iMax; i++) {

			Vector<String> row = loadProfileDataVector.get(i);
			String stringTime = row.get(ciTime);
			String stringPLoad = row.get(ciPLoad);
			String stringQLoad = row.get(ciQLoad);
			
			try {
				// --- Get the time stamp -------------------------------------
				Date timeDate = this.getDateFormatter().parse(stringTime);
				long timeStamp = timeDate.getTime();
				
				// --- Set time from-to ---------------------------------------
				if (i==0) timeFrom = timeStamp;
				timeTo = Math.max(timeTo, timeStamp) ;
				
				// --- Avoid double time stamps -------------------------------
				if (timeStampHashSet.contains(timeStamp)==true) continue;
				timeStampHashSet.add(timeStamp);
				
				// --- Consider the ScheduleTimeRange? ------------------------
				if (scheduleTimeRange!=null && scheduleTimeRange.getRangeType()!=null) {
					switch (scheduleTimeRange.getRangeType()) {
					case TimeRange: 
						if (timeStamp < scheduleTimeRange.getTimeFrom() || timeStamp > scheduleTimeRange.getTimeTo()) {
							continue;
						}
						break;
					case StartTimeAndNumber:
						if (timeStamp < scheduleTimeRange.getTimeFrom() || tsseList.size() >= scheduleTimeRange.getNumberOfSystemStates()) {
							continue;
						}
						break;
					}
				}
				
				// --- Calculate power values ---------------------------------
				double pLoadMWAllPhases = this.parseDouble(stringPLoad) * pLoadFactor;
				double qLoadMWAllPhases = this.parseDouble(stringQLoad) * qLoadFactor;
				
				long durationMillis = 0;
				if (timeStampPrev>0) {
					durationMillis = timeStamp - timeStampPrev;
				}
				
				// --- Create TechnicalSystemStateEvaluation ------------------
				TechnicalSystemStateEvaluation tsse = this.createTechnicalSystemStateEvaluation(timeStamp, durationMillis, pLoadMWAllPhases, qLoadMWAllPhases, tssePrev); 
				tsseList.add(0, tsse);
			
				// --- Remind for next iteration ------------------------------
				timeStampPrev = timeStamp;
				tssePrev = tsse;
				
			} catch (ParseException pEx) {
				pEx.printStackTrace();
			}
		} // end for

		
		// --- Finally set the state time range ------------------------------- 
		schedule.setStateTimeFrom(timeFrom);
		schedule.setStateTimeTo(timeTo);
		
		// --- Create the usual tree structure of Schedules -------------------
		ScheduleList_StorageHandler.convertToTreeSchedule(schedule);
		
		return schedule;
	}
	
	/**
	 * Creates the technical system state evaluation.
	 *
	 * @param time the time
	 * @param duration the duration
	 * @param pLoadMWAllPhases the P load in MW for all phases
	 * @param qLoadMWAllPhases the q load MW all phases
	 * @param tssePrev the previous {@link TechnicalSystemStateEvaluation}
	 * @return the technical system state evaluation
	 */
	private TechnicalSystemStateEvaluation createTechnicalSystemStateEvaluation(long time, long duration, double pLoadMWAllPhases, double qLoadMWAllPhases, TechnicalSystemStateEvaluation tssePrev) {
		
		TechnicalSystemStateEvaluation tsse = new TechnicalSystemStateEvaluation();
		tsse.setConfigID(DEFAULT_TSSE_CONFIG_ID);
		tsse.setStateID(DEFAULT_TSSE_STATE_ID);
		tsse.setGlobalTime(time);
		tsse.setStateTime(duration);

		// --- Calculate to single phase ------------------
		double pLoadkWAllPhases = pLoadMWAllPhases * 1000.0;
		double qLoadkWAllPhases = qLoadMWAllPhases * 1000.0;
				
		double activePower   = pLoadkWAllPhases / 3.0;
		double reactivePower = qLoadkWAllPhases / 3.0;
		
		// --- Iterate over interfaces --------------------
		for (int i=0; i < INTERFACE_IDs_Electricity.length; i++) {
			// --- Active power ---------------------------
			String activeInterfaceID = INTERFACE_IDs_Electricity[i] + " P";
			EnergyAmount cumActive = this.getCumulatedEnergyAmount(tssePrev, activeInterfaceID);
			UsageOfInterfaceEnergy uoiActive = this.createUsageOfInterfaces(activeInterfaceID, duration, activePower, cumActive);
			tsse.getUsageOfInterfaces().add(uoiActive);
			// --- Reactive power -------------------------
			String reactiveInterfaceID = INTERFACE_IDs_Electricity[i] + " Q";
			EnergyAmount cumReactive = this.getCumulatedEnergyAmount(tssePrev, reactiveInterfaceID);
			UsageOfInterfaceEnergy uoiReactive = this.createUsageOfInterfaces(reactiveInterfaceID, duration, reactivePower, cumReactive);
			tsse.getUsageOfInterfaces().add(uoiReactive);
		}
		return tsse;
	}
	
	/**
	 * Return the cumulated energy amount for the specified interface.
	 *
	 * @param tssePrev the previous {@link TechnicalSystemStateEvaluation} that serves as base for the calculations.
	 * @param interfaceID the interface ID
	 * @return the cumulated energy amount
	 */
	private EnergyAmount getCumulatedEnergyAmount(TechnicalSystemStateEvaluation tssePrev, String interfaceID) {
		
		EnergyAmount eaCum = null;
		if (tssePrev!=null) {
			for (int i = 0; i < tssePrev.getUsageOfInterfaces().size(); i++) {
				AbstractUsageOfInterface uoi = tssePrev.getUsageOfInterfaces().get(i);
				if (uoi instanceof UsageOfInterfaceEnergy) {
					UsageOfInterfaceEnergy uoiEnergy = (UsageOfInterfaceEnergy) uoi;
					if (uoiEnergy.getInterfaceID().equals(interfaceID)==true) {
						eaCum = uoiEnergy.getEnergyAmountCumulated();
						break;
					}
				}
			}
		}
		return eaCum;
	}
	
	/**
	 * Create a UsageOfInterfaces instance with energy flow and cumulated energy amount.
	 *
	 * @param interfaceID The interface ID
	 * @param durationMillis the duration in milliseconds
	 * @param energyFlowValueKW The energy flow
	 * @param prevEnergyAmountCum the previous energy amount cumulated
	 * @return the UsageOfInterfaces instance
	 */
	private UsageOfInterfaceEnergy createUsageOfInterfaces(String interfaceID, long durationMillis, double energyFlowValueKW, EnergyAmount prevEnergyAmountCum){
		
		// --- Cumulate energy amounts --------------------
		double durationMillisInHours = UnitConverter.convertDuration(durationMillis, TIME_UNIT_FOR_ENERGY_AMOUNTS);
		double deltaEnergyAmount = energyFlowValueKW * durationMillisInHours;
		double cumulated = deltaEnergyAmount;
		if (prevEnergyAmountCum!=null){
			cumulated += prevEnergyAmountCum.getValue();
		}
	
		// --- Define energy flow -------------------------
		EnergyFlowInWatt eFlow = new EnergyFlowInWatt();
		eFlow.setValue(energyFlowValueKW);
		eFlow.setSIPrefix(SI_PREFIX_FOR_ENERGY_FLOWS);
		
		// --- Prepare energy amount instance -------------
		EnergyAmount eaCumulated = new EnergyAmount();
		eaCumulated.setSIPrefix(SI_PREFIX_FOR_ENERGY_AMOUNTS);
		eaCumulated.setTimeUnit(TIME_UNIT_FOR_ENERGY_AMOUNTS);
		eaCumulated.setValue(cumulated);
		
		// --- Create the usage of energy -----------------
		UsageOfInterfaceEnergy uoi = new UsageOfInterfaceEnergy();
		uoi.setInterfaceID(interfaceID);
		uoi.setEnergyFlow(eFlow);
		uoi.setEnergyAmountCumulated(eaCumulated);
		return uoi;
	}
	
	
	/**
	 * Return the node ID for the specified row of table nodes. 
	 * @param rowIndex within the model
	 * @return the nodeID
	 */
	private String getNodeID(int rowIndex) {
		
		CsvDataController nodeCsvController = this.getCsvDataControllerOfCsvFile(SimBenchFileStore.SIMBENCH_Node);
		Vector<Vector<String>> nodeDataVector = this.getDataVectorOfCsvFile(SimBenchFileStore.SIMBENCH_Node);
		
		int ciID = nodeCsvController.getDataModel().findColumn("id");
		Vector<String> dataRow = nodeDataVector.get(rowIndex);
		return dataRow.get(ciID);
	}
	
	/**
	 * Return the load ID for the specified row of table load. 
	 * @param rowIndex within the model
	 * @return the loadID
	 */
	private String getLoadID(int rowIndex) {
		
		CsvDataController nodeCsvController = this.getCsvDataControllerOfCsvFile(SimBenchFileStore.SIMBENCH_Load);
		Vector<Vector<String>> nodeDataVector = this.getDataVectorOfCsvFile(SimBenchFileStore.SIMBENCH_Load);
		
		int ciID = nodeCsvController.getDataModel().findColumn("id");
		Vector<String> dataRow = nodeDataVector.get(rowIndex);
		return dataRow.get(ciID);
	}
	
	/**
	 * Check if the current node selection is a cable cabinet
	 * @param rowIndex the row index in the node table 
	 * @return true if the node selection is a cable cabinet
	 */
	public boolean isCableCabinetNodeSelection(int rowIndex) {
		HashMap<String, String> loadRowHashMap = this.getDataRowHashMap(SimBenchFileStore.SIMBENCH_Load, "node", this.getNodeID(rowIndex));
		if (loadRowHashMap==null) {
			return true;
		}
		return false;
	}
	
	
	// --------------------------------------------------------------------------------------------
	// --- From here: Some general help methods ---------------------------------------------------
	// --------------------------------------------------------------------------------------------
	/**
	 * Returns the local date formatter.
	 * @return the date formatter
	 */
	public SimpleDateFormat getDateFormatter() {
		if (dateFormatter==null) {
			dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		}
		return dateFormatter;
	}
	/**
	 * Return a data row as HashMap, where the key is the column name and the value the row value.
	 *
	 * @param csvFileName the csv file name
	 * @param keyColumnName the key column name
	 * @param keyValue the key value to search for
	 * @return the data row as HashMap
	 */
	private HashMap<String, String> getDataRowHashMap(String csvFileName, String keyColumnName, String keyValue) {
		
		HashMap<String, String> dataRowHashMap = null;
		
		CsvDataController csvController = this.getCsvDataControllerOfCsvFile(csvFileName);
		if (csvController!=null) {
			
			// --- Find the right row -------------------------------
			int idColumnIndex = csvController.getDataModel().findColumn(keyColumnName);
			if (idColumnIndex!=-1) {
				// --- Found key column -----------------------------
				int dataRowIndex = -1;
				for (int rowIndex = 0; rowIndex < csvController.getDataModel().getRowCount(); rowIndex++) {
					String currKeyValue = (String)csvController.getDataModel().getValueAt(rowIndex, idColumnIndex);
					if (currKeyValue.equals(keyValue)==true) {
						dataRowIndex = rowIndex;
						break;
					}
				}
				
				if (dataRowIndex!=-1) {
					// --- Found row, get values --------------------
					dataRowHashMap = new HashMap<>();
					for (int i = 0; i < csvController.getDataModel().getColumnCount(); i++) {
						String colName = csvController.getDataModel().getColumnName(i);
						String value = (String) csvController.getDataModel().getValueAt(dataRowIndex, i);
						dataRowHashMap.put(colName, value);
					}
				}
			}
		}
		return dataRowHashMap;
	}
	/**
	 * Returns the data vector of csv file.
	 *
	 * @param csvFileName the csv file name
	 * @return the data vector of csv file
	 */
	@SuppressWarnings("unchecked")
	private Vector<Vector<String>> getDataVectorOfCsvFile(String csvFileName) {
		CsvDataController nodeCsvController = this.getCsvDataControllerOfCsvFile(csvFileName);
		if (nodeCsvController!=null) {
			return new Vector<Vector<String>>(nodeCsvController.getDataModel().getDataVector());
		}
		return null;
	}
	/**
	 * Return the CsvDataController of the specified csv file.
	 *
	 * @param csvFileName the csv file name
	 * @return the CsvDataController or <code>null</code>
	 */
	private CsvDataController getCsvDataControllerOfCsvFile(String csvFileName) {
		return SimBenchFileStore.getInstance().getCsvDataController().get(csvFileName);
	}
	/**
	 * Parses the specified string to a double value .
	 *
	 * @param doubleString the double string
	 * @return the double value or null
	 */
	private Double parseDouble(String doubleString) {
		Double dValue = null;
		if (doubleString!=null && doubleString.isEmpty()==false) {
			// --- Replace decimal separator ? ----------------------
			if (doubleString.contains(",")==true) {
				doubleString = doubleString.replace(",", ".");
			}
			// --- Try to parse the double string -------------------
			try {
				dValue = Double.parseDouble(doubleString);
			} catch (Exception ex) {
				// --- No exception will be thrown ------------------
			}
		}
		return dValue;
	}

}
