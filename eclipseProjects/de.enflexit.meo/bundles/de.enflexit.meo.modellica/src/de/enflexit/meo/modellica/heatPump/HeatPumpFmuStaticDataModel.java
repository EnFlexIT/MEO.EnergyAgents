package de.enflexit.meo.modellica.heatPump;

import java.io.Serializable;

import agentgui.core.application.Application;
import de.enflexit.meo.modellica.eomIntegration.FmuStaticDataModel;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMappingInterfaceFlow;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMappingIoList;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMappingIoList.IoVariableType;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMappingStaticParameter;
import energy.optionModel.EnergyUnitFactorPrefixSI;

/**
 * The static data model for the heat pump FMU 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class HeatPumpFmuStaticDataModel extends FmuStaticDataModel implements Serializable {

	private static final long serialVersionUID = -1847258408954464865L;
	
	/**
	 * Instantiates a new heat pump fmu data model.
	 */
	public HeatPumpFmuStaticDataModel() {
		System.out.println("Initializing HeatPump FMU, Verison 3.0");
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
		this.getStaticParameters().add(new FmuVariableMappingStaticParameter("Waermepumpe_Elektrischeverbrauch_Nominal", 3.5, "kW"));
		this.getStaticParameters().add(new FmuVariableMappingStaticParameter("Waermepumpe_ThermischeLeistung_Nominal", 10.0, "kW"));
		this.getStaticParameters().add(new FmuVariableMappingStaticParameter("Heizstab_Nominal_Leistung", 10.0, "kW"));
		this.getStaticParameters().add(new FmuVariableMappingStaticParameter("Tinit_bottom", 50.0, "°C"));
		
		// --- Add IO variables -----------------------
		this.getIoListMappings().add(new FmuVariableMappingIoList("SOC", "SOC", IoVariableType.RESULT, "%"));
		this.getIoListMappings().add(new FmuVariableMappingIoList("coilSetpoint", "Schaltsignal_Heizstab", IoVariableType.SETPOINT));
		this.getIoListMappings().add(new FmuVariableMappingIoList("hpSetpoint", "Schaltsignal_Waermepumpe", IoVariableType.SETPOINT));
		this.getIoListMappings().add(new FmuVariableMappingIoList("pTh", "ThermischeLast", IoVariableType.MEASUREMENT, "°C"));
		this.getIoListMappings().add(new FmuVariableMappingIoList("tAmb", "UmgebungsTemperatur", IoVariableType.MEASUREMENT, "°C"));
		
		// --- Add interface flows --------------------
		FmuVariableMappingInterfaceFlow energyFlowHeatPump = new FmuVariableMappingInterfaceFlow();
		energyFlowHeatPump.setFmuVariableName("Pel_HP");
		energyFlowHeatPump.setDomain("Electricity");
		energyFlowHeatPump.setUnit(EnergyUnitFactorPrefixSI.KILO_K_3);
		//TODO add domain model?
		this.getInterfaceFlowMappings().add(energyFlowHeatPump);
		
		FmuVariableMappingInterfaceFlow energyFlowCoil = new FmuVariableMappingInterfaceFlow();
		energyFlowCoil.setFmuVariableName("Pel_COIL");
		energyFlowCoil.setDomain("Electricity");
		energyFlowCoil.setUnit(EnergyUnitFactorPrefixSI.KILO_K_3);
		//TODO add domain model?
		this.getInterfaceFlowMappings().add(energyFlowCoil);
	}
}
