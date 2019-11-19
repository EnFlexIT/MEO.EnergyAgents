package de.enflexit.meo.plugIn.importer;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.awb.env.networkModel.GraphNode;
import org.awb.env.networkModel.NetworkComponent;
import org.awb.env.networkModel.NetworkModel;
import org.awb.env.networkModel.controller.GraphEnvironmentController;
import org.awb.env.networkModel.helper.NetworkComponentFactory;
import org.awb.env.networkModel.maps.MapSettings;
import org.awb.env.networkModel.maps.MapSettings.MapScale;
import org.awb.env.networkModel.settings.GeneralGraphSettings4MAS;

import agentgui.core.application.Application;
import agentgui.ontology.TimeSeriesChart;
import agentgui.ontology.TimeSeriesChartSettings;
import agentgui.simulationService.environment.AbstractEnvironmentModel;
import de.enflexit.common.csv.CsvDataController;
import de.enflexit.geography.coordinates.UTMCoordinate;
import de.enflexit.geography.coordinates.WGS84LatLngCoordinate;
import energy.GlobalInfo;
import energy.UnitConverter;
import energy.domain.DefaultDomainModelElectricity;
import energy.domain.DefaultDomainModelElectricity.CurrentType;
import energy.domain.DefaultDomainModelElectricity.Phase;
import energy.domain.DefaultDomainModelElectricity.PowerType;
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
import hygrid.csvFileImport.CSV_FileImporter;
import hygrid.globalDataModel.ontology.CableProperties;
import hygrid.globalDataModel.ontology.ElectricalNodeProperties;
import hygrid.globalDataModel.ontology.TransformerNodeProperties;
import hygrid.globalDataModel.ontology.TriPhaseCableState;
import hygrid.globalDataModel.ontology.TriPhaseElectricalNodeState;
import hygrid.globalDataModel.ontology.UnitValue;


/**
 * The Class SimBench_CsvTopologyImporter.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg-Essen
 */
public class SimBench_CsvTopologyImporter extends CSV_FileImporter {

	private static final String LAYOUT_DEFAULT_LAYOUT = GeneralGraphSettings4MAS.DEFAULT_LAYOUT_SETTINGS_NAME;
	private static final String LAYOUT_GeoCoordinates_WGS84 = "Geo-Coordinates WGS84";
	private static final String LAYOUT_GeoCoordinates_UTM = "Geo-Coordinates UTM";
	
	private static final String SIMBENCH_Coordinates	 = "Coordinates.csv";
	private static final String SIMBENCH_ExternalNet	 = "ExternalNet.csv";
	private static final String SIMBENCH_Line			 = "Line.csv";
	private static final String SIMBENCH_LineType		 = "LineType.csv";
	private static final String SIMBENCH_Load			 = "Load.csv";
	private static final String SIMBENCH_LoadProfile	 = "LoadProfile.csv";
	private static final String SIMBENCH_Measurement	 = "Measurement.csv";
	private static final String SIMBENCH_Node			 = "Node.csv";
	private static final String SIMBENCH_NodePFResult	 = "NodePFResult.csv";
	private static final String SIMBENCH_RES			 = "RES.csv";
	private static final String SIMBENCH_RESProfile		 = "RESProfile.csv";
	private static final String SIMBENCH_Storage		 = "Storage.csv";
	private static final String SIMBENCH_StorageProfile  = "StorageProfile.csv";
	private static final String SIMBENCH_StudyCases		 = "StudyCases.csv";
	private static final String SIMBENCH_Transformer	 = "Transformer.csv";
	private static final String SIMBENCH_TransformerType = "TransformerType.csv";

	private String errTitle;
	private String errMessage;

	
	// --- From here variables for the topology import ------------------------
	private NetworkModel networkModel;
	private AbstractEnvironmentModel abstractEnvModel;
	
	private String layoutIdDefault;
	private String layoutIdGeoCoordinateWGS84;
	private String layoutIdGeoCoordinateUTM;

	private MapSettings mapSettings;
	
	private Vector<Point2D> defaultPositionVector;
	
	// --- From here variables for the time series import ---------------------
	private static final EnergyUnitFactorPrefixSI SI_PREFIX_FOR_ENERGY_FLOWS = EnergyUnitFactorPrefixSI.KILO_K_3;
	private static final EnergyUnitFactorPrefixSI SI_PREFIX_FOR_ENERGY_AMOUNTS = EnergyUnitFactorPrefixSI.KILO_K_3;
	private static final TimeUnit TIME_UNIT_FOR_ENERGY_AMOUNTS = TimeUnit.HOUR_H;
	
	private static final String DEFAULT_TSSE_CONFIG_ID = "Config";
	private static final String DEFAULT_TSSE_STATE_ID = "Prosuming";
	private static final String[] INTERFACE_IDs_Electricity = {"Electricity L1", "Electricity L2", "Electricity L3"};
	
	private SimpleDateFormat dateFormatter;
	private HashMap<String, Schedule> profileScheduleHashMap;
	
	
	/**
	 * Instantiates a new OAD networkType importer.
	 *
	 * @param graphController the graph controller
	 * @param fileTypeExtension the file type extension
	 * @param fileTypeDescription the file type description
	 */
	public SimBench_CsvTopologyImporter(GraphEnvironmentController graphController, String fileTypeExtension, String fileTypeDescription) {
		super(graphController, fileTypeExtension, fileTypeDescription);
	}
	
	/* (non-Javadoc)
	 * @see hygrid.csvFileImport.CSV_FileImporter#getListOfRequiredFileNames()
	 */
	@Override
	protected Vector<String> getListOfRequiredFileNames() {
		
		Vector<String> fileNameVector = new Vector<>(); 
		fileNameVector.add(SIMBENCH_Coordinates);
		fileNameVector.add(SIMBENCH_ExternalNet);
		fileNameVector.add(SIMBENCH_Line);
		fileNameVector.add(SIMBENCH_LineType);
		fileNameVector.add(SIMBENCH_Load);
		fileNameVector.add(SIMBENCH_LoadProfile);
		fileNameVector.add(SIMBENCH_Measurement);
		fileNameVector.add(SIMBENCH_Node);
		fileNameVector.add(SIMBENCH_NodePFResult);
		fileNameVector.add(SIMBENCH_RES);
		fileNameVector.add(SIMBENCH_RESProfile);
		fileNameVector.add(SIMBENCH_Storage);
		fileNameVector.add(SIMBENCH_StorageProfile);
		fileNameVector.add(SIMBENCH_StudyCases);
		fileNameVector.add(SIMBENCH_Transformer);
		fileNameVector.add(SIMBENCH_TransformerType);
		return fileNameVector;
	}
	
	/**
	 * Return the current NetworkModel that has to be generated by this importer.
	 * @return the networkType model
	 */
	protected NetworkModel getNetworkModel() {
		if (networkModel == null) {
			networkModel = new NetworkModel();
			networkModel.setGeneralGraphSettings4MAS(this.graphController.getNetworkModel().getGeneralGraphSettings4MAS());
			networkModel.getMapSettingsTreeMap().put(this.getLayoutIdGeoCoordinateUTM(), this.getMapSettings());
		}
		return networkModel;
	}
	
	/**
	 * Returns the {@link MapSettings}.
	 * @return the map settings
	 */
	private MapSettings getMapSettings() {
		if (mapSettings==null) {
			mapSettings = new MapSettings();
			mapSettings.setUTMLongitudeZone(32);
			mapSettings.setUTMLatitudeZone("U");
			mapSettings.setMapScale(MapScale.km);
			mapSettings.setShowMapTiles(true);
			mapSettings.setMapTileTransparency(50);
		}
		return mapSettings;
	}
	
	/**
	 * Return the layout-ID for default coordinates.
	 * @return the layout id OGE internal
	 */
	public String getLayoutIdDefault() {
		if (layoutIdDefault==null) {
			layoutIdDefault = this.getNetworkModel().getGeneralGraphSettings4MAS().getLayoutIdByLayoutName(LAYOUT_DEFAULT_LAYOUT);
		}
		return layoutIdDefault;
	}
	/**
	 * Returns the layout-ID for WGS84 geo coordinate.
	 * @return the layout id geo coordinate
	 */
	public String getLayoutIdGeoCoordinateWGS84() {
		if (layoutIdGeoCoordinateWGS84==null) {
			layoutIdGeoCoordinateWGS84 = this.getNetworkModel().getGeneralGraphSettings4MAS().getLayoutIdByLayoutName(LAYOUT_GeoCoordinates_WGS84); 
		}
		return layoutIdGeoCoordinateWGS84;
	}
	/**
	 * Returns the layout-ID for UTM geo coordinate WGS84.
	 * @return the layout id geo coordinate
	 */
	public String getLayoutIdGeoCoordinateUTM() {
		if (layoutIdGeoCoordinateUTM==null) {
			layoutIdGeoCoordinateUTM = this.getNetworkModel().getGeneralGraphSettings4MAS().getLayoutIdByLayoutName(LAYOUT_GeoCoordinates_UTM); 
		}
		return layoutIdGeoCoordinateUTM;
	}
	
	
	/* (non-Javadoc)
	 * @see org.awb.env.networkModel.controller.NetworkModelFileImporter#getAbstractEnvironmentModel()
	 */
	@Override
	public AbstractEnvironmentModel getAbstractEnvironmentModel() {
		return abstractEnvModel;
	}
	/**
	 * Sets the abstract environment model.
	 */
	private void setAbstractEnvironmentModel() {

		// --- Define the abstract environment model ----------------
		// TODO
		this.abstractEnvModel = null;
		
	}
	
	/* (non-Javadoc)
	 * @see org.awb.env.networkModel.persistence.AbstractNetworkModelFileImporter#cleanupImporter()
	 */
	@Override
	public void cleanupImporter() {
		
		super.cleanupImporter();
		
		this.errTitle = null;
		this.errMessage = null;
		
		this.networkModel = null;
		this.abstractEnvModel = null;
		
		this.layoutIdDefault = null;
		this.layoutIdGeoCoordinateWGS84 = null;
		this.layoutIdGeoCoordinateUTM = null;
		
		this.mapSettings = null;
		
		this.defaultPositionVector = null;
		
		this.dateFormatter = null;
		this.profileScheduleHashMap = null;
		
	}
	
	/* (non-Javadoc)
	 * @see org.awb.env.networkModel.persistence.AbstractNetworkModelFileImporter#importNetworkModelFromFile(java.io.File)
	 */
	@Override
	public NetworkModel importNetworkModelFromFile(File directoryFile) {
		
		try {
			// --- Set status information -------------------------------------
			Application.setStatusBarMessage(this.getClass().getSimpleName() + ": Import files from " + directoryFile.getParent() + " ... ");

			this.debug = true;
			
			// --- Read the csv files -----------------------------------------
			this.readCsvFiles(directoryFile, true); 
			
			// --- Show import preview if this.debug is set to true -----------
			this.showImportPreview();
		
			// --- The main import work to be done ----------------------------
			this.createNodes();
			this.createLines();
			
			// --- Create the AWB NetworkModel --------------------------------
			this.setAbstractEnvironmentModel();
			
			this.showError();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			Application.setStatusBarMessageReady();
		}
		
		// --- Return the NetworkModel ----------------------------------------
		return this.getNetworkModel();
	}

	
	// --------------------------------------------------------------------------------------------
	// --- From here: some methods for getting the lines ------------------------------------------
	// --------------------------------------------------------------------------------------------	
	/**
	 * Creates the lines.
	 */
	private void createLines() {
		
		CsvDataController nodeCsvController = this.getCsvDataControllerOfCsvFile(SIMBENCH_Line);
		Vector<Vector<String>> lineDataVector = this.getDataVectorOfCsvFile(SIMBENCH_Line);
			
		String colNameID = "id";
		String colNameNodeA = "nodeA";
		String colNameNodeB = "nodeB";
		String colNameType = "type";
		String colNameLength = "length";
		
		int ciID = nodeCsvController.getDataModel().findColumn(colNameID);
		int ciNodeA = nodeCsvController.getDataModel().findColumn(colNameNodeA);
		int ciNodeB = nodeCsvController.getDataModel().findColumn(colNameNodeB);
		int ciType = nodeCsvController.getDataModel().findColumn(colNameType);
		int ciLength = nodeCsvController.getDataModel().findColumn(colNameLength);
		
		for (int i = 0; i < lineDataVector.size(); i++) {
			
			// --- Get line data row ----------------------------------------------------
			Vector<String> row = lineDataVector.get(i);
			String lineID  = row.get(ciID);
			String nodeA = row.get(ciNodeA);
			String nodeB = row.get(ciNodeB);
			String lineType = row.get(ciType);
			String lineLength = row.get(ciLength);

			Float lengthInKilometer = this.parseFloat(lineLength);
			Float lengthInMeter = null;
			if (lengthInKilometer!=null) {
				lengthInMeter = lengthInKilometer * 1000f;
			}
			
			HashMap<String, String> dataRowHashMap = this.getDataRowHashMap(SIMBENCH_LineType, "id", lineType);
			Float linResistance = this.parseFloat(dataRowHashMap.get("r"));
			Float linReactance = this.parseFloat(dataRowHashMap.get("x"));
			Float maxCurrent = this.parseFloat(dataRowHashMap.get("iMax"));
			
			
			// --- Prepare new NetworkComponent -----------------------------------------
			NetworkModel newCompNM = null;
			CableProperties cableProperties = null;
			
			// --- Create new NetworkComponent by using the NetworkComponentFactory -----
			// POSSIBLY a case separation for specific NetworkComponent types? 
			newCompNM = NetworkComponentFactory.getNetworkModel4NetworkComponent(this.getNetworkModel(), "Cable");
			cableProperties = new CableProperties();
			
			
			// --- Get NetworkComponent and GrahNode for renaming -----------------------
			NetworkComponent newNetComp = newCompNM.getNetworkComponents().values().iterator().next();
			Object[] graphNodes = newCompNM.getGraphElementsOfNetworkComponent(newNetComp, new GraphNode()).toArray();
			GraphNode newGraphNodeFrom = (GraphNode) graphNodes[0];
			GraphNode newGraphNodeTo = (GraphNode) graphNodes[1];

			// --- Rename NetworkComponent and GraphNode --------------------------------
			newCompNM.renameNetworkComponent(newNetComp.getId(), lineID);
			newCompNM.renameGraphNode(newGraphNodeFrom.getId(), nodeA);
			newCompNM.renameGraphNode(newGraphNodeTo.getId(), nodeB);
			
			
			// --- Set the parameters for the component ------------------------
			cableProperties.setLength(new UnitValue(lengthInMeter, "m"));
			cableProperties.setLinearResistance(new UnitValue(linResistance, "Ω/km"));
			cableProperties.setLinearReactance(new UnitValue(linReactance, "Ω/km"));
			cableProperties.setMaxCurrent(new UnitValue(maxCurrent, "A"));

			// --- Add an empty time series chart object to match the requirements of the adapter ------
			TimeSeriesChart tsc = new TimeSeriesChart();
			tsc.setTimeSeriesVisualisationSettings(new TimeSeriesChartSettings());

			// --- Set the data model --------------
			Object[] dataModel = new Object[3];
			dataModel[0] = cableProperties;
			dataModel[1] = new TriPhaseCableState();
			dataModel[2] = tsc;
			newNetComp.setDataModel(dataModel);
			
			// --- Merge into the new NetworkModel --------------------------------------
			this.getNetworkModel().mergeNetworkModel(newCompNM, null, false);
			
		} // end for
		
	}

	// --------------------------------------------------------------------------------------------
	// --- From here: some methods for getting the nodes ------------------------------------------
	// --------------------------------------------------------------------------------------------	
	/**
	 * Creates the nodes in the NetworkModel.
	 */
	private void createNodes() {
		
		CsvDataController nodeCsvController = this.getCsvDataControllerOfCsvFile(SIMBENCH_Node);
		Vector<Vector<String>> nodeDataVector = this.getDataVectorOfCsvFile(SIMBENCH_Node);
			
		String colNameID = "id";
		String colNameCoord = "coordID";
		
		int ciID = nodeCsvController.getDataModel().findColumn(colNameID);
		int ciCoordID = nodeCsvController.getDataModel().findColumn(colNameCoord);
		
		for (int i = 0; i < nodeDataVector.size(); i++) {
			
			// --- Get node data row ----------------------------------------------------
			Vector<String> row = nodeDataVector.get(i);
			String nodeID  = row.get(ciID);
			String coordID = row.get(ciCoordID);
			
			// --- Provide some user information ----------------------------------------
			Application.setStatusBarMessage(this.getClass().getSimpleName() + ": Import node '" + nodeID  + "' - (" + (i+1) +  "/" + nodeDataVector.size() + ")");

			// --- Get the corresponding data row of the coordinates --------------------
			HashMap<String, String> dataRowHashMap = this.getDataRowHashMap(SIMBENCH_Coordinates, "id", coordID);
			String coordXString = dataRowHashMap.get("x");
			String coordYString = dataRowHashMap.get("y");
			
			// --- Prepare new NetworkComponent -----------------------------------------
			NetworkModel newCompNM = null;
			String newNetCompID = nodeID; 
			
			// --- Create new NetworkComponent by using the NetworkComponentFactory -----
			Object netCompDataModel = null;
			
			// --- Case separation for NetworkCmponents ---------------------------------
			boolean isCreatingTransformer = false;
			String transformerID = this.getTransformerID(nodeID);
			if (transformerID!=null) {
				// --- Create transformer ? ---------------------------------------------
				HashMap<String, String> transformerRowHashMap = this.getDataRowHashMap(SIMBENCH_Transformer, "id", transformerID);
				String nodeLV = transformerRowHashMap.get("nodeLV");
				// --- Only create low voltage node of SimBench model ------------------- 
				if (nodeLV.equals(nodeID) && this.getNetworkModel().getNetworkComponent(transformerID)==null) {
					newCompNM = NetworkComponentFactory.getNetworkModel4NetworkComponent(this.getNetworkModel(), "Transformer");
					newNetCompID = transformerID;
					isCreatingTransformer = true;
				} else {
					// --- Do not create this component again ---------------------------
					continue;
				}
				
			} else {
				// --- Prosumer or CableCabinet ? ---------------------------------------
				HashMap<String, String> loadRowHashMap = this.getDataRowHashMap(SIMBENCH_Load, "node", nodeID);
				if (loadRowHashMap==null) {
					// --- Create CableCabinet ------------------------------------------
					newCompNM = NetworkComponentFactory.getNetworkModel4NetworkComponent(this.getNetworkModel(), "CableCabinet");
					
				} else {
					// --- Create Prosumer model ---------------------------------------- 
					newCompNM = NetworkComponentFactory.getNetworkModel4NetworkComponent(this.getNetworkModel(), "Prosumer");
					// --- Create ScheduleList as data model ----------------------------
					String profile = loadRowHashMap.get("profile");
					Schedule schedule = this.getScheduleOfProfile(profile);
					ScheduleList sl = this.createScheduleList(nodeID);
					sl.getSchedules().add(schedule);
					netCompDataModel = sl;
				}
				
			}
			
			// --- Get the actual NetworkComponent --------------------------------------
			NetworkComponent newComp = newCompNM.getNetworkComponents().values().iterator().next();
			String nodeName = newComp.getGraphElementIDs().iterator().next();
			GraphNode graphNode = (GraphNode) newCompNM.getGraphElement(nodeName);

			// --- Rename the elements --------------------------------------------------
			newCompNM.renameNetworkComponent(newComp.getId(), newNetCompID);
			newCompNM.renameGraphNode(graphNode.getId(), nodeID);
			
			// --- Define the GraphNode positions ---------------------------------------
			double wgs84Long = this.parseDouble(coordXString);
			double wgs84Lat  = this.parseDouble(coordYString);
			this.setGraphNodeCoordinates(graphNode, wgs84Lat, wgs84Long);

			
			// --------------------------------------------------------------------------
			// --- Define first part of the GraphNode's data model ----------------------
			ElectricalNodeProperties nodeProps = new ElectricalNodeProperties();
			if (isCreatingTransformer==true) {
				// --- Create low voltage node data model -----------
				UnitValue uValue = new UnitValue();
				uValue.setValue(400f);
				uValue.setUnit("V");
				TransformerNodeProperties transformerProps = new TransformerNodeProperties();
				transformerProps.setRatedVoltage(uValue);
				nodeProps = transformerProps;
			}
			nodeProps.setIsLoadNode(true);
			
			// --- Define TimeSeriesChart as second part of the GraphNode data model ----
			TimeSeriesChart tsc = new TimeSeriesChart();
			tsc.setTimeSeriesVisualisationSettings(new TimeSeriesChartSettings());
			
			// --------------------------------------------------------------------------
			// --- Set the data model to the GraphNode ----------------------------------
			// TODO
			Object[] dataModel = new Object[3];
			dataModel[0] = nodeProps;
			dataModel[1] = new TriPhaseElectricalNodeState();
			dataModel[2] = tsc;
			graphNode.setDataModel(dataModel);

			// --------------------------------------------------------------------------
			// --- Set the data model to the NetworkComponent ---------------------------
			newComp.setDataModel(netCompDataModel);
			
			// --- Merge into the new NetworkModel --------------------------------------
			this.getNetworkModel().mergeNetworkModel(newCompNM, null, false);
			
		} // end for
		
		// --- Adjust the node positions for the default layout ------------------------- 
		this.adjustDefaultPositions();
	}
	
	/**
	 * Checks if the specified node ID represents a transformer and returns either the 
	 * ID of the transformer or <code>null</code>:.
	 *
	 * @param nodeID the node ID
	 * @return the transformer ID or <code>null</code>
	 */
	private String getTransformerID(String nodeID) {
		
		CsvDataController transformerCsvController = this.getCsvDataControllerOfCsvFile(SIMBENCH_Transformer);
		Vector<Vector<String>> transformerDataVector = this.getDataVectorOfCsvFile(SIMBENCH_Transformer);
		
		String colID = "id";
		String colNodeHV = "nodeHV";
		String colNodeLV = "nodeLV";
		
		int ciID = transformerCsvController.getDataModel().findColumn(colID);
		int ciNodeHV = transformerCsvController.getDataModel().findColumn(colNodeHV);
		int ciNodeLV = transformerCsvController.getDataModel().findColumn(colNodeLV);
		
		for (int i = 0; i < transformerDataVector.size(); i++) {
		
			Vector<String> row = transformerDataVector.get(i);
			String id = row.get(ciID);
			String idNodeHV = row.get(ciNodeHV);
			String idNodeLV = row.get(ciNodeLV);
			
			if (idNodeHV.equals(nodeID) || idNodeLV.equals(nodeID)) {
				return id;
			}
		}
		return null;
	}
	
	
	
	/**
	 * Sets the GraphNode coordinates.
	 *
	 * @param graphNode the graph node
	 * @param latNorthSouth the latitude value (North / South)
	 * @param longEastWest the longitude value (East / West)
	 */
	private void setGraphNodeCoordinates(GraphNode graphNode, double latNorthSouth, double longEastWest) {
		
		// --- Set GraphNode position according to node -------------
		Point2D pointGeoWGS84 = new Point2D.Double(latNorthSouth, longEastWest);
		
		// --- Calculate to UTM coordinate --------------------------
		WGS84LatLngCoordinate coordWGS84 = new WGS84LatLngCoordinate(pointGeoWGS84.getX(), pointGeoWGS84.getY());
		UTMCoordinate coordUTM = coordWGS84.getUTMCoordinate(this.getMapSettings().getUTMLongitudeZone(), this.getMapSettings().getUTMLatitudeZone());
		Point2D pointUTM = new Point2D.Double(coordUTM.getEasting(), coordUTM.getNorthing());
		
		// --- Set default layout to Default ------------------------
		Point2D pointDefault = this.getDefaultLayoutPosition(pointGeoWGS84);
		graphNode.setPosition(pointDefault);
		this.getDefaultPositionVector().add(pointDefault);
		
		// --- Set positions to alternative layouts -----------------
		graphNode.getPositionTreeMap().put(this.getLayoutIdDefault(), pointDefault);
		graphNode.getPositionTreeMap().put(this.getLayoutIdGeoCoordinateWGS84(), pointGeoWGS84);
		graphNode.getPositionTreeMap().put(this.getLayoutIdGeoCoordinateUTM(), pointUTM);
	}
	/**
	 * Return the default layout position, derived from the WGS84 coordinates.
	 *
	 * @param wgs84Point the WGS84 point
	 * @return the default layout position
	 */
	private Point2D getDefaultLayoutPosition(Point2D wgs84Point) {
		double xPos = GlobalInfo.round(wgs84Point.getY() *  100000.0, 1);
		double yPos = GlobalInfo.round(wgs84Point.getX() * -100000.0, 1);
		return new Point2D.Double(xPos, yPos);
	}
	/**
	 * Reminder for the default positions and a later correction.
	 * @return the default position vector
	 */
	private Vector<Point2D> getDefaultPositionVector() {
		if (defaultPositionVector==null) {
			defaultPositionVector = new Vector<>();
		}
		return defaultPositionVector;
	}
	/**
	 * Adjusts the default node positions.
	 */
	private void adjustDefaultPositions() {

		if (this.getDefaultPositionVector().size()==0) return;
		
		// --- Get the spreading rectangle of the default positions -----------
		Point2D initialPoint = this.getDefaultPositionVector().get(0);
		Rectangle2D spreadRectangle = new Rectangle2D.Double(initialPoint.getX(), initialPoint.getY(), 0, 0);
		for (int i = 0; i < this.getDefaultPositionVector().size(); i++) {
			Point2D singlePos = this.getDefaultPositionVector().get(i);
			spreadRectangle.add(singlePos);
		}
		
		// --- Calculate movement ---------------------------------------------
		double moveX = 0;
		double moveY = 0;
		
		if (spreadRectangle.getX()>=0) {
			moveX = -spreadRectangle.getX(); 
		} else {
			moveX = spreadRectangle.getX();
		}
		
		if (spreadRectangle.getY()>=0) {
			moveY = spreadRectangle.getY();
		} else {
			moveY = -spreadRectangle.getY();
		}

		// --- Move the default positions -------------------------------------
		for (int i = 0; i < this.getDefaultPositionVector().size(); i++) {
			Point2D singlePos = this.getDefaultPositionVector().get(i);
			singlePos.setLocation(singlePos.getX() + moveX, singlePos.getY() + moveY);
		}
		
	}
	
	// --------------------------------------------------------------------------------------------
	// --- From here: some methods for creating system schedules can be found ---------------------
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
	 * Creates a ScheduleList with the specified systemID.
	 *
	 * @param systemID the system ID to use
	 * @return the schedule list
	 */
	private ScheduleList createScheduleList(String systemID) {
		
		ScheduleList sl = new ScheduleList();
		sl.setSystemID(systemID);
//		sl.setNetworkID(value); // TODO
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
	 * Returns the profile to Schedule hash map.
	 * @return the profile schedule hash map
	 */
	private HashMap<String, Schedule> getProfileScheduleHashMap() {
		if (profileScheduleHashMap==null) {
			profileScheduleHashMap = new HashMap<>();
		}	
		return profileScheduleHashMap;
	}
	
	/**
	 * Return a schedule for the specified profile .
	 * @return the schedule of profile
	 */
	private Schedule getScheduleOfProfile(String profile) {
		
		// --- Check reminder HashMap first -----------------------------------  
		Schedule schedule = this.getProfileScheduleHashMap().get(profile); 
		if (schedule!=null) {
			return schedule;
		}
		
		// --- Get time series from load profile ------------------------------
		CsvDataController loadProfileCsvController = this.getCsvDataControllerOfCsvFile(SIMBENCH_LoadProfile);
		Vector<Vector<String>> loadProfileDataVector = this.getDataVectorOfCsvFile(SIMBENCH_LoadProfile);

		String colNameTime = "time";
		String colNamePLoad = profile + "_pload";
		String colNameQLoad = profile + "_qload";
		
		int ciTime  = loadProfileCsvController.getDataModel().findColumn(colNameTime);
		int ciPLoad = loadProfileCsvController.getDataModel().findColumn(colNamePLoad);
		int ciQLoad = loadProfileCsvController.getDataModel().findColumn(colNameQLoad);
		
		long timeStampPrev = -1;
		TechnicalSystemStateEvaluation tssePrev = null;

		// --- Define list of system states -----------------------------------
		List<TechnicalSystemStateEvaluation> tsseList = new ArrayList<>();
		// --- Get data rows for the profile ----------------------------------
		int iMax = loadProfileDataVector.size();
		iMax = 10; // TODO 
		for (int i = 0; i < iMax; i++) {

			Vector<String> row = loadProfileDataVector.get(i);
			String stringTime = row.get(ciTime);
			String stringPLoad = row.get(ciPLoad);
			String stringQLoad = row.get(ciQLoad);
			
			try {
				
				Date timeDate = this.getDateFormatter().parse(stringTime);
				long timeStamp = timeDate.getTime();
				double pLoadMWAllPhases = this.parseDouble(stringPLoad);
				double qLoadMWAllPhases = this.parseDouble(stringQLoad);
				
				long durationMillis = 0;
				if (timeStampPrev>0) {
					durationMillis = timeStamp - timeStampPrev;
				}
				
				// --- Create TechnicalSystemStateEvaluation ------------------
				TechnicalSystemStateEvaluation tsse = this.createTechnicalSystemStateEvaluation(timeStamp, durationMillis, pLoadMWAllPhases, qLoadMWAllPhases, tssePrev); 
				tsseList.add(tsse);
			
				// --- Remind for next iteration ------------------------------
				timeStampPrev = timeStamp;
				tssePrev = tsse;
				
			} catch (ParseException pEx) {
				pEx.printStackTrace();
			}
			
			
		} // end for
		
		// --- Finally, create Schedule ---------------------------------------
		schedule = new Schedule();
		schedule.setStrategyClass(this.getClass().getSimpleName());
		// --- Add List of TechnicalSystemStatesEvaluation to Schedule --------
		for (int i = tsseList.size()-1; i >= 0; i--) {
			schedule.getTechnicalSystemStateList().add(tsseList.get(i));
		}
		// --- Create the usual tree structure of Schedules -------------------
		ScheduleList_StorageHandler.convertToTreeSchedule(schedule);
		
		// --- Remind Schedule for multiple use -------------------------------
		this.getProfileScheduleHashMap().put(profile, schedule);
		
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
				
		// TODO
		double activePower = pLoadkWAllPhases;
		double reactivePower = qLoadkWAllPhases;
		
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
	 * @param tsse the tsse
	 * @param interfaceID the interface ID
	 * @return the cumulated energy amount
	 */
	private EnergyAmount getCumulatedEnergyAmount(TechnicalSystemStateEvaluation tsse, String interfaceID) {
		
		EnergyAmount eaCum = null;
		if (tsse!=null) {
			for (int i = 0; i < tsse.getUsageOfInterfaces().size(); i++) {
				AbstractUsageOfInterface uoi = tsse.getUsageOfInterfaces().get(i);
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
	 * @return The {@link UsageOfInterfaces} instance
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
	
	
	// --------------------------------------------------------------------------------------------
	// --- From here: Some general help methods ---------------------------------------------------
	// --------------------------------------------------------------------------------------------
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
	private Vector<Vector<String>> getDataVectorOfCsvFile(String csvFileName) {
		CsvDataController nodeCsvController = this.getCsvDataControllerOfCsvFile(csvFileName);
		if (nodeCsvController!=null) {
			@SuppressWarnings("unchecked")
			Vector<Vector<String>> tableModel = nodeCsvController.getDataModel().getDataVector();
			return tableModel;
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
		return this.getCsvDataController().get(csvFileName);
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

	/**
	 * Parses the specified string to a double value .
	 *
	 * @param floatString the float string
	 * @return the float value or null
	 */
	private Float parseFloat(String floatString) {
		Float fValue = null;
		if (floatString!=null && floatString.isEmpty()==false) {
			// --- Replace decimal separator ? ----------------------
			if (floatString.contains(",")==true) {
				floatString = floatString.replace(",", ".");
			}
			// --- Try to parse the double string -------------------
			try {
				fValue = Float.parseFloat(floatString);
			} catch (Exception ex) {
				// --- No exception will be thrown ------------------
			}
		}
		return fValue;
	}

	// --------------------------------------------------------------------------------------------
	// --- From here: some methods to display messages and errors on the console ------------------
	// --------------------------------------------------------------------------------------------	
	/**
	 * Prints the specified debug line.
	 * @param message the message
	 */
	private void printDebugLine(String message) {
		this.printDebugLine(message, false);
	}
	/**
	 * Prints the specified debug line.
	 *
	 * @param message the message
	 * @param isError the is error
	 */
	private void printDebugLine(String message, boolean isError) {
		if (isError) {
			System.err.println("[" + this.getClass().getSimpleName() + "] " + message);
		} else {
			System.out.println("[" + this.getClass().getSimpleName() + "] " + message);
		}
	}
	
}
