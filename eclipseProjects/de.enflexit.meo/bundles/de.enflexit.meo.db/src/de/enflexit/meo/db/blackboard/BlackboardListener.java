package de.enflexit.meo.db.blackboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.awb.env.networkModel.NetworkComponent;
import org.awb.env.networkModel.NetworkModel;
import de.enflexit.ea.core.aggregation.AbstractAggregationHandler;
import de.enflexit.ea.core.blackboard.Blackboard;
import de.enflexit.ea.core.blackboard.BlackboardListenerService;
import de.enflexit.ea.core.dataModel.ontology.CableState;
import de.enflexit.ea.core.dataModel.ontology.ElectricalNodeState;
import de.enflexit.ea.core.dataModel.ontology.TriPhaseCableState;
import de.enflexit.ea.core.dataModel.ontology.TriPhaseElectricalNodeState;
import de.enflexit.ea.core.dataModel.ontology.UniPhaseCableState;
import de.enflexit.ea.core.dataModel.ontology.UniPhaseElectricalNodeState;
import de.enflexit.ea.electricity.aggregation.AbstractSubAggregationBuilderElectricity;
import de.enflexit.ea.electricity.aggregation.triPhase.SubNetworkConfigurationElectricalDistributionGrids;
import de.enflexit.ea.electricity.blackboard.SubBlackboardModelElectricity;
import de.enflexit.meo.db.BundleHelper;
import de.enflexit.meo.db.DatabaseHandler;
import de.enflexit.meo.db.dataModel.EdgeResult;
import de.enflexit.meo.db.dataModel.NetworkState;
import de.enflexit.meo.db.dataModel.NodeResult;
import de.enflexit.meo.db.dataModel.TrafoResult;
import energy.optionModel.TechnicalSystemState;

/**
 * The BlackboardListener connects to the {@link Blackboard} of the SimulationManager and thus
 * allows to receive new system states and save them to the database.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg-Essen
 */
public class BlackboardListener implements BlackboardListenerService {

	private NetworkModel networkModel;
	private Integer idScenarioResult;
	
	private List<String> nodeElementList;
	private List<String> edgeElementList;
	private List<String> transformerList;
	
	private DatabaseHandler dbHandler;
	
	/* (non-Javadoc)
	 * @see de.enflexit.ea.core.dataModel.blackboard.BlackboardListenerService#onSimulationDone()
	 */
	@Override
	public void onSimulationDone() {
		
		// --- Reset local variables ------------
		this.networkModel = null;
		this.idScenarioResult = null;
		
		this.nodeElementList = null;
		this.edgeElementList = null;
		this.transformerList = null;
		
		this.getDatabaseHandler().stopNetworkStateSaveThread();
		this.dbHandler = null;
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.ea.core.dataModel.blackboard.BlackboardListenerService#onNetworkCalculationDone(de.enflexit.ea.core.dataModel.blackboard.Blackboard)
	 */
	@Override
	public void onNetworkCalculationDone(Blackboard blackboard) {
		
		// --- Check it the idScenarioResult was set ------
		if (this.getIDScenarioResult()<=0) return;
		
		// --- Set the NetworkModel -----------------------
		this.setNetworkModel(blackboard.getNetworkModel());
		
		// --- Get the state time -------------------------
		Calendar stateTime = Calendar.getInstance();
		stateTime.setTimeInMillis(blackboard.getStateTime());
		
		// --- Get the sub blackboard model ---------------
		//TODO Make sure to get the right blackboard! 
		AbstractAggregationHandler aggregationHandler = blackboard.getAggregationHandler();
		SubBlackboardModelElectricity subBlackboardModel = (SubBlackboardModelElectricity) aggregationHandler.getSubNetworkConfigurations().get(0).getSubBlackboardModel();
		
		// --- Get a quick copy of the sates --------------
		HashMap<String, ElectricalNodeState> nodeStates = new HashMap<>(subBlackboardModel.getGraphNodeStates());
		HashMap<String, CableState> cableStates = new HashMap<>(subBlackboardModel.getNetworkComponentStates());
		HashMap<String, TechnicalSystemState> transformerStates = new HashMap<>(subBlackboardModel.getTransformerStates());	

		// --- Create lists to save to database -----------
		NetworkState networkState = new NetworkState();
		networkState.setStateTime(stateTime);
		networkState.setNodeResultList(this.getNodeResults(nodeStates, stateTime));
		networkState.setEdgeResultList(this.getEdgeResults(cableStates, stateTime));
		networkState.setTrafoResultList(this.getTrafoResults(transformerStates, stateTime));
		
		// --- Save to database ---------------------------
		this.getDatabaseHandler().addNetworkStateToSave(networkState);
	}

	
	
	private List<TrafoResult> getTrafoResults(HashMap<String, TechnicalSystemState> transformerStates, Calendar calendar) {
		
		List<TrafoResult> trafoResultList = new ArrayList<>();
		
		List<String> trafoElementList = this.getTransformerList();
		for (int i = 0; i < trafoElementList.size(); i++) {
			
			String trafoID = trafoElementList.get(i);
			TechnicalSystemState tss = transformerStates.get(trafoID);

			// --- Create TrafoResult ---------------------
			// --- TODO !!!
			TrafoResult trafoResult = new TrafoResult();
			trafoResult.setIdScenarioResult(this.getIDScenarioResult());
			trafoResult.setIdTrafo(trafoID);
			trafoResult.setTimestamp(calendar);
			
			trafoResult.setVoltageReal(0.0);
			trafoResult.setVoltageComplex(0.0);
			trafoResult.setVoltageViolations(0.0);
			
			trafoResult.setResidualLoadP(0.0);
			trafoResult.setResidualLoadQ(0.0);
			
			trafoResult.setTrafoUtilization(0.0);
			
			trafoResult.setTrafoLossesP(0.0);
			trafoResult.setTrafoLossesQ(0.0);
			
			// --- Add to list ------------------------
			trafoResultList.add(trafoResult);
		}
		return trafoResultList;
	}

	private List<EdgeResult> getEdgeResults(HashMap<String, CableState> edgeStates, Calendar calendar) {
		
		List<EdgeResult> edgeResultList = new ArrayList<>();
		
		List<String> edgeElementList = this.getEdgeElementList();
		for (int i = 0; i < edgeElementList.size(); i++) {
			String edgeID = edgeElementList.get(i);
			CableState cableState = edgeStates.get(edgeID);
			if (cableState instanceof TriPhaseCableState) {
				
				TriPhaseCableState tpcs = (TriPhaseCableState) cableState;
				UniPhaseCableState upcsL1 = tpcs.getPhase1();
				UniPhaseCableState upcsL2 = tpcs.getPhase2();
				UniPhaseCableState upcsL3 = tpcs.getPhase3();
				
				// --- TODO !!!
				// --- Create EdgeResult ------------------
				EdgeResult edgeResult = new EdgeResult();
				edgeResult.setIdScenarioResult(this.getIDScenarioResult());
				edgeResult.setIdEdge(edgeID);
				edgeResult.setTimestamp(calendar);
				
				edgeResult.setLossesP(0.0);
				edgeResult.setLossesQ(0.0);
				edgeResult.setUtilization(upcsL1.getUtilization());
				
				// --- Add to list ------------------------
				edgeResultList.add(edgeResult);
			}
		}
		return edgeResultList;
	}
	
	private List<NodeResult> getNodeResults(HashMap<String, ElectricalNodeState> graphNodeStates, Calendar calendar) {
		
		List<NodeResult> nodeResultList = new ArrayList<>();
		
		List<String> nodeElementList = this.getNodeElementList();
		for (int i = 0; i < nodeElementList.size(); i++) {
			String nodeID = nodeElementList.get(i);
			ElectricalNodeState elNodeState = graphNodeStates.get(nodeID);
			if (elNodeState instanceof TriPhaseElectricalNodeState) {
				
				TriPhaseElectricalNodeState tens = (TriPhaseElectricalNodeState) elNodeState;
				UniPhaseElectricalNodeState upensL1 = tens.getL1();
				UniPhaseElectricalNodeState upensL2 = tens.getL2();
				UniPhaseElectricalNodeState upensL3 = tens.getL3();
				
				// --- Create NodeResult ------------------ 
				// --- TODO !!!
				NodeResult nodeResult = new NodeResult();
				nodeResult.setIdScenarioResult(this.getIDScenarioResult());
				nodeResult.setIdNode(nodeID);
				nodeResult.setTimestamp(calendar);
				
				nodeResult.setVoltageReal(upensL1.getVoltageReal().getValue());
				nodeResult.setVoltageComplex(upensL1.getVoltageImag().getValue());
				nodeResult.setVoltageViolations(0); // TODO
				
				// --- Add to list ------------------------
				nodeResultList.add(nodeResult);
			}
		}
		return nodeResultList;
	}

	
	
	private List<String> getNodeElementList() {
		if (nodeElementList==null) {
			nodeElementList = new ArrayList<>();
		}
		return nodeElementList;
	}
	private List<String> getEdgeElementList() {
		if (edgeElementList==null) {
			edgeElementList = new ArrayList<>();
		}
		return edgeElementList;
	}
	private List<String> getTransformerList() {
		if (transformerList==null) {
			transformerList = new ArrayList<>();
		}
		return transformerList;
	}
	
	private NetworkModel getNetworkModel() {
		if (networkModel==null) {
			networkModel = Blackboard.getInstance().getNetworkModel();
			this.fillResultIDs();
		}
		return networkModel;
	}
	
	private void setNetworkModel(NetworkModel networkModel) {
		if (this.networkModel==null) {
			this.networkModel = networkModel;
			this.fillResultIDs();
		}
	}
	/**
	 * Evaluate the NetworkModel for the IDs of the results.
	 */
	private void fillResultIDs() {
		
		if (this.nodeElementList==null || this.nodeElementList==null || this.transformerList==null) {
		
			Vector<NetworkComponent> netCompVector = this.getNetworkModel().getNetworkComponentVectorSorted();
			for (int i = 0; i < netCompVector.size(); i++) {

				NetworkComponent netComp = netCompVector.get(i);
				switch (netComp.getType()) {
				case "Prosumer":
				case "CableCabinet":
					this.getNodeElementList().add(netComp.getId());
					break;
				case "Cable":
					this.getEdgeElementList().add(netComp.getId());
					break;
				case "Transformer":
					this.getTransformerList().add(netComp.getId());
					break;
				}
			} // end for
		}
	}

	private int getIDScenarioResult() {
		if (idScenarioResult==null) {
			idScenarioResult = BundleHelper.getIdScenarioResultForSetup(); 
		}
		return idScenarioResult; 
	}
	
	private DatabaseHandler getDatabaseHandler() {
		if (dbHandler==null) {
			dbHandler = new DatabaseHandler();
			dbHandler.startNetworkStateSaveThread();
		}
		return dbHandler;
	}
}
