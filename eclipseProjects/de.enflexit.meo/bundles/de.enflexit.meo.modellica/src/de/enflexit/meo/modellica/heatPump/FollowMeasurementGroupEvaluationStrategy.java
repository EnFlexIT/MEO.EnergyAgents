package de.enflexit.meo.modellica.heatPump;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;

import de.enflexit.meo.modellica.eomIntegration.FmuSimulationWrapper;
import de.enflexit.meo.modellica.eomIntegration.FmuStaticDataModel;
import energy.OptionModelController;
import energy.domain.DefaultDomainModelElectricity;
import energy.domain.DefaultDomainModelElectricity.PowerType;
import energy.evaluation.TechnicalSystemStateDeltaEvaluation;
import energy.helper.TechnicalSystemStateHelper;
import energy.helper.UnitConverter;
import energy.optionModel.AbstractFlow;
import energy.optionModel.EnergyAmount;
import energy.optionModel.EnergyFlowInWatt;
import energy.optionModel.EnergyUnitFactorPrefixSI;
import energy.optionModel.FixedBoolean;
import energy.optionModel.Schedule;
import energy.optionModel.ScheduleList;
import energy.optionModel.SystemVariableDefinition;
import energy.optionModel.SystemVariableDefinitionStaticModel;
import energy.optionModel.TechnicalInterface;
import energy.optionModel.TechnicalSystem;
import energy.optionModel.TechnicalSystemStateEvaluation;
import energy.optionModel.TechnicalSystemStateTime;
import energy.optionModel.TimeUnit;
import energy.optionModel.UsageOfInterfaceEnergy;
import energygroup.GroupController.GroupMemberType;
import energygroup.GroupTreeNodeObject;
import energygroup.calculation.FlowsMeasuredGroup;
import energygroup.calculation.FlowsMeasuredGroupMember;
import energygroup.evaluation.AbstractGroupEvaluationStrategy;
import energygroup.sequentialNetworks.AbstractSequentialNetworkCalculation;

/**
 * The Class FollowMeasurementGroupEvaluationStrategy.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class FollowMeasurementGroupEvaluationStrategy extends AbstractGroupEvaluationStrategy{
	
	private static final String VARIABLE_ID_HEATPUMP_MEASUREMENT = "hpMeasurement";
	private static final String VARIABLE_ID_HEATPUMP_SETPOINT = "hpSetpoint";
	private static final String VARIABLE_ID_COIL_SETPOINT = "coilSetpoint";
	
	private HashMap<GroupTreeNodeObject, FmuSimulationWrapper> simulationWrappers;
	
	/**
	 * Instantiates a new follow measurement group evaluation strategy.
	 * @param optionModelController the option model controller
	 */
	public FollowMeasurementGroupEvaluationStrategy(OptionModelController optionModelController) {
		super(optionModelController);
	}

	/* (non-Javadoc)
	 * @see energygroup.evaluation.AbstractGroupEvaluationStrategy#getSequentialNetworkCalculation()
	 */
	@Override
	public AbstractSequentialNetworkCalculation<?> getSequentialNetworkCalculation() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see energygroup.evaluation.AbstractGroupEvaluationStrategy#isProduceTechnicalSystemGroupStateEvaluation()
	 */
	@Override
	public boolean isProduceTechnicalSystemGroupStateEvaluation() {
		return true;
	}

	/* (non-Javadoc)
	 * @see energygroup.evaluation.AbstractGroupEvaluationStrategy#doNetworkCalculation(javax.swing.tree.DefaultMutableTreeNode, java.util.List, energygroup.calculation.FlowsMeasuredGroup)
	 */
	@Override
	public FlowsMeasuredGroupMember doNetworkCalculation(DefaultMutableTreeNode currentParentNode, List<TechnicalInterface> outerInterfaces, FlowsMeasuredGroup fmGroup) {
		FlowsMeasuredGroupMember efmGroupMember = new FlowsMeasuredGroupMember();
		for (TechnicalInterface ti : outerInterfaces) {
			AbstractFlow afm = fmGroup.sumUpFlowMeasuredByDomainAndDomainModel(currentParentNode, ti.getInterfaceID(), ti.getDomain(), ti.getDomainModel());
			efmGroupMember.addAbstractFlowMeasured(afm, ti);
		}
		return efmGroupMember;
	}

	/* (non-Javadoc)
	 * @see energygroup.evaluation.AbstractGroupEvaluationStrategy#decideForDeltaStepToBeUsed(javax.swing.tree.DefaultMutableTreeNode, energygroup.GroupTreeNodeObject, java.util.Vector)
	 */
	@Override
	public TechnicalSystemStateDeltaEvaluation decideForDeltaStepToBeUsed(DefaultMutableTreeNode currentNode, GroupTreeNodeObject gtno, Vector<TechnicalSystemStateDeltaEvaluation> deltaSteps) {
		
		// --- Get the setpoint value for the heatpump from the measurement (same for all deltas)
		TechnicalSystemStateEvaluation nextTSSE = deltaSteps.get(0).getTechnicalSystemStateEvaluation();
		FixedBoolean hpMeasurement = (FixedBoolean) TechnicalSystemStateHelper.getFixedVariable(nextTSSE.getIOlist(), VARIABLE_ID_HEATPUMP_MEASUREMENT);
		
		// --- Find the delta step with the corresponding setpoint value ------------
		int decisionIndex = -1;
		for (int i=0; i<deltaSteps.size(); i++) {
			TechnicalSystemStateEvaluation deltaTSSE = deltaSteps.get(i).getTechnicalSystemStateEvaluation();
			FixedBoolean heatPumpSetpoint = (FixedBoolean) TechnicalSystemStateHelper.getFixedVariable(deltaTSSE.getIOlist(), VARIABLE_ID_HEATPUMP_SETPOINT);
			FixedBoolean coilSetpoint = (FixedBoolean) TechnicalSystemStateHelper.getFixedVariable(deltaTSSE.getIOlist(), VARIABLE_ID_COIL_SETPOINT);
			if (heatPumpSetpoint.isValue()==hpMeasurement.isValue() && coilSetpoint.isValue()==false) {
				decisionIndex = i;
				break;
			}
		}
		
		// --- Set new current TechnicalSystemStateEvaluation -----------------------
		TechnicalSystemStateDeltaEvaluation tssDeltaDecision = deltaSteps.get(decisionIndex);
		this.getSimulationWrapper(gtno).simulateTsse(tssDeltaDecision.getTechnicalSystemStateEvaluation());
		this.updateInterfaces(tssDeltaDecision.getTechnicalSystemStateEvaluation(), gtno);
		
		//TODO simulate the TSSE, update the interface flows
//		TechnicalSystemStateEvaluation tsseNext = this.getNextTechnicalSystemStateEvaluation(tsse, tssDeltaDecision);
//		if (tsseNext==null) {
//			this.print(this.getEvaluationThread().getName() + ": => Error while using selected delta => interrupt search!", true);
//			break;
//		} else {
//			// --- Use the FMU to simulate the selected state -----------------------
//			this.getSimulationWrapper(gtno).simulateTsse(tsseNext);
//			// --- Update the TSSE's interfaces according to the results ------------
//			this.updateInterfaces(tsseNext, gtno);
//		}
		
		return tssDeltaDecision;
	}

	/* (non-Javadoc)
	 * @see energygroup.evaluation.AbstractGroupEvaluationStrategy#decideForScheduleToBeUsed(javax.swing.tree.DefaultMutableTreeNode, energygroup.GroupTreeNodeObject, energy.optionModel.ScheduleList)
	 */
	@Override
	public Schedule decideForScheduleToBeUsed(DefaultMutableTreeNode currentNode, GroupTreeNodeObject gtno,
			ScheduleList scheduleList) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see energy.evaluation.AbstractEvaluationStrategy#getCustomToolBarElements()
	 */
	@Override
	public Vector<JComponent> getCustomToolBarElements() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see energy.evaluation.AbstractEvaluationStrategy#runEvaluation()
	 */
	@Override
	public void runEvaluation() {
		// --- Get Start situation ----------------------------------
		TechnicalSystemStateEvaluation tsse = this.getInitialTechnicalSystemStateEvaluation();
		// --- Search by walking through time -----------------------
		while (tsse.getGlobalTime() < this.getEndTime() ) {
			
			// --- Get the possible subsequent steps ----------------
			Vector<TechnicalSystemStateDeltaEvaluation> deltaSteps = this.getAllDeltaEvaluationsStartingFromTechnicalSystemState(tsse);
			if (deltaSteps.size()==0) {
				this.print("No further 'deltaStepsPossible' => interrupt search!", true);
				break;
			}
			
			// --- Choose the first option --------------------------
			int decisionIndex = 0;
			TechnicalSystemStateDeltaEvaluation tssDeltaEvaluation = deltaSteps.get(decisionIndex);
			TechnicalSystemStateEvaluation tsseNext = this.getNextTechnicalSystemStateEvaluation(tsse, tssDeltaEvaluation);
			
			// --------------------------------------------------------------------------
			// --- Set new current TechnicalSystemStateEvaluation -----------------------
			// --------------------------------------------------------------------------
			if (tsseNext==null) {
				this.print("No possible delta evaluation could be found!", true);
				break;
			} else {
				tsse = tsseNext;
			}
			
			// --- Stop evaluation ? ----------------------------------------------------
			if (isStopEvaluation()==true) break;
		}
		this.addStateToResults(tsse);
		this.terminateFMUs();
		
	}
	
	private void terminateFMUs() {
		for (FmuSimulationWrapper wrapper : this.getSimulationWrappers().values()) {
			wrapper.terminateSimulation();
		}
	}

	/**
	 * Gets the HashMap containing the {@link FmuSimulationWrapper}s for all FMUs in the aggregation
	 * @return the simulation wrappers
	 */
	private HashMap<GroupTreeNodeObject, FmuSimulationWrapper> getSimulationWrappers() {
		if (simulationWrappers==null) {
			simulationWrappers = new HashMap<GroupTreeNodeObject, FmuSimulationWrapper>();
		}
		return simulationWrappers;
	}
	
	/**
	 * Gets the {@link FmuSimulationWrapper} for the FMU represented by the specified {@link GroupTreeNodeObject}.
	 * @param gtno the GroupTreeNodeObject
	 * @return the simulation wrapper
	 */
	private FmuSimulationWrapper getSimulationWrapper(GroupTreeNodeObject gtno) {
		FmuSimulationWrapper simulationWrapper = this.getSimulationWrappers().get(gtno);
		
		// --- Initialize the SimulationWrapper if necessary --------
		if (simulationWrapper==null) {
			if (gtno.getGroupMemberType() == GroupMemberType.TechnicalSystem) {
				TechnicalSystem technicalSystem = gtno.getGroupMember().getControlledSystem().getTechnicalSystem();
				TechnicalSystemStateTime initialState = technicalSystem.getEvaluationSettings().getEvaluationStateList().get(0);
				FmuStaticDataModel staticModel = this.getStaticModel(technicalSystem);
				simulationWrapper = staticModel.getSimulationWrapper();
				simulationWrapper.initializeFmu(TechnicalSystemStateHelper.convertToTechnicalSystemStateEvaluation(initialState), this.getEvaluationProcess().getEndTime());
				this.getSimulationWrappers().put(gtno, simulationWrapper);
			}
		}
		
		return simulationWrapper;
	}
	
	/**
	 * Sets the interface flows and amounts for the active power interfaces according to the FMU results.
	 * @param tsse the {@link TechnicalSystemStateEvaluation} to update 
	 * @param gtno the {@link GroupTreeNodeObject} of the corresponding group member
	 */
	private void updateInterfaces(TechnicalSystemStateEvaluation tsse, GroupTreeNodeObject gtno) {
		// --- Calculate the total active power per phase ---------------------
		double pElHeatPumpKW = this.getSimulationWrapper(gtno).getSimulation().read("Pel_HP").asDouble();
		double pElCoilKW = this.getSimulationWrapper(gtno).getSimulation().read("Pel_COIL").asDouble();
		double pElPerPhaseKW = (pElHeatPumpKW+pElCoilKW)/3;
		
		// --- Set the flows and amounts for all interfaces -------------------
		for (int i=0; i<tsse.getUsageOfInterfaces().size(); i++) {
			
			UsageOfInterfaceEnergy uoi = (UsageOfInterfaceEnergy) tsse.getUsageOfInterfaces().get(i);

			// --- Set flow and amount for active power electricity interfaces
			TechnicalInterface ti = this.optionModelController.getTechnicalInterface(tsse.getConfigID(), uoi.getInterfaceID());
			if (ti.getDomain().equals("Electricity")) {
				
				DefaultDomainModelElectricity domainModel = (DefaultDomainModelElectricity) ti.getDomainModel();

				if (domainModel.getPowerType()==PowerType.ActivePower) {
					// --- Set the flow for the current state -----------------
					EnergyFlowInWatt interfaceFlow = new EnergyFlowInWatt();
					interfaceFlow.setValue(pElPerPhaseKW);
					interfaceFlow.setSIPrefix(EnergyUnitFactorPrefixSI.KILO_K_3);
					uoi.setEnergyFlow(interfaceFlow);
					
					// --- Calculate the amount for the current state ---------
					double stateTimeHours = UnitConverter.convertDuration(tsse.getStateTime(), TimeUnit.HOUR_H);
					double energyAmountKwh = pElPerPhaseKW * stateTimeHours;
					
					// --- Add the previous amount if applicable --------------
					if (tsse.getParent()!=null) {
						UsageOfInterfaceEnergy previousUsage = (UsageOfInterfaceEnergy) TechnicalSystemStateHelper.getUsageOfInterfaces(tsse.getParent().getUsageOfInterfaces(), uoi.getInterfaceID());
						if (previousUsage!=null && previousUsage.getEnergyAmountCumulated()!=null) {
							energyAmountKwh += previousUsage.getEnergyAmountCumulated().getValue();
						}
					}
					
					// --- Set the cumulated amount ---------------------------
					EnergyAmount amountCumulated = new EnergyAmount();
					amountCumulated.setValue(energyAmountKwh);
					amountCumulated.setSIPrefix(EnergyUnitFactorPrefixSI.KILO_K_3);
					amountCumulated.setTimeUnit(TimeUnit.HOUR_H);
					
					uoi.setEnergyAmountCumulated(amountCumulated);
				}
			}
		}
	}
	
	/**
	 * Gets the {@link FmuStaticDataModel} from the systems variable definitions of the specified {@link TechnicalSystem}.
	 * @param technicalSystem the technical system
	 * @return the static model, may be null if no instance of FmuStaticDataModel was found 
	 */
	private FmuStaticDataModel getStaticModel(TechnicalSystem technicalSystem) {
		FmuStaticDataModel staticModel = null;
		// --- Find the static model from the list of system variable definitions
		List<SystemVariableDefinition> systemVariableDefinitions = technicalSystem.getSystemVariables();
		for (int i=0; i<systemVariableDefinitions.size(); i++) {
			if (systemVariableDefinitions.get(i) instanceof SystemVariableDefinitionStaticModel) {
				SystemVariableDefinitionStaticModel sysVarDefStaticModel = (SystemVariableDefinitionStaticModel) systemVariableDefinitions.get(i);
				Object sysVar = this.optionModelController.getStaticModelInstance(sysVarDefStaticModel);
				if (sysVar instanceof FmuStaticDataModel) {
					staticModel = (FmuStaticDataModel) sysVar;
					break;
				}
			}
		}
		return staticModel;
	}
}
