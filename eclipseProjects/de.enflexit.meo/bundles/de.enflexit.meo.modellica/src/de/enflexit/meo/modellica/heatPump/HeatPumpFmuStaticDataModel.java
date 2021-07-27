package de.enflexit.meo.modellica.heatPump;

import java.io.Serializable;

import agentgui.core.application.Application;
import de.enflexit.meo.modellica.eomIntegration.FmuSimulationWrapper;
import de.enflexit.meo.modellica.eomIntegration.FmuStaticDataModel;
import de.enflexit.meo.modellica.eomIntegration.FmuInterfaceFlowMapping;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMapping;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMapping.IoVariableType;
import de.enflexit.meo.modellica.eomIntegration.FmuParameterSettings;
import energy.optionModel.EnergyUnitFactorPrefixSI;

/**
 * The static data model for the heat pump FMU 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class HeatPumpFmuStaticDataModel extends FmuStaticDataModel implements Serializable {

	private static final long serialVersionUID = -1847258408954464865L;
	
	HeatPumpFmuSimulationWrapper simulationWrapper;
	
	/**
	 * Instantiates a new heat pump fmu data model.
	 */
	public HeatPumpFmuStaticDataModel() {
		this.setFmuFilePath(Application.getProjectFocused().getProjectFolderFullPath() + "fmuModels/HeatPumpFMU_Version3.0/mGRiDS_CoSimFMI.HPSystem.fmu");
		this.setModelStepSizeMilliSeconds(1000);
		this.initializeVariableDescriptions();
	}

	/**
	 * Gets the variable mappings definition.
	 * @return the variable mappings definition
	 */
	private void initializeVariableDescriptions() {
		// --- Add static system parameters -----------
		this.getParameterSettings().add(new FmuParameterSettings("Waermepumpe_Elektrischeverbrauch_Nominal", 2.0, "kW"));
		this.getParameterSettings().add(new FmuParameterSettings("Waermepumpe_ThermischeLeistung_Nominal", 7.0, "kW"));
		this.getParameterSettings().add(new FmuParameterSettings("Heizstab_Nominal_Leistung", 3.0, "kW"));
//		this.getStaticParameters().add(new FmuVariableMappingStaticParameter("Tinit_bottom", 50.0, "°C"));
		
		// --- Add IO variables -----------------------
		this.getVariableMappings().add(new FmuVariableMapping("SOC", "SOC", IoVariableType.RESULT, "%"));
		this.getVariableMappings().add(new FmuVariableMapping("coilSetpoint", "Schaltsignal_Heizstab", IoVariableType.SETPOINT));
		this.getVariableMappings().add(new FmuVariableMapping("hpSetpoint", "Schaltsignal_Waermepumpe", IoVariableType.SETPOINT));
		this.getVariableMappings().add(new FmuVariableMapping("pTh", "ThermischeLast", IoVariableType.MEASUREMENT, "°C"));
		this.getVariableMappings().add(new FmuVariableMapping("tAmb", "UmgebungsTemperatur", IoVariableType.MEASUREMENT, "°C"));
		
		// --- Add interface flows --------------------
		FmuInterfaceFlowMapping energyFlowHeatPump = new FmuInterfaceFlowMapping();
		energyFlowHeatPump.setFmuVariableName("Pel_HP");
		energyFlowHeatPump.setDomain("Electricity");
		energyFlowHeatPump.setUnit(EnergyUnitFactorPrefixSI.KILO_K_3);
		//TODO add domain model?
		this.getInterfaceFlowMappings().add(energyFlowHeatPump);
		
		FmuInterfaceFlowMapping energyFlowCoil = new FmuInterfaceFlowMapping();
		energyFlowCoil.setFmuVariableName("Pel_COIL");
		energyFlowCoil.setDomain("Electricity");
		energyFlowCoil.setUnit(EnergyUnitFactorPrefixSI.KILO_K_3);
		//TODO add domain model?
		this.getInterfaceFlowMappings().add(energyFlowCoil);
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.meo.modellica.eomIntegration.FmuStaticDataModel#getFmuSimulationWrapper()
	 */
	@Override
	public FmuSimulationWrapper getFmuSimulationWrapper() {
		if (simulationWrapper==null) {
			simulationWrapper = new HeatPumpFmuSimulationWrapper(this);
		}
		return simulationWrapper;
	}
}
