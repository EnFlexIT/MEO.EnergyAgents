package de.enflexit.meo.modellica.heatPump.realOutput;

import java.io.Serializable;

import agentgui.core.application.Application;
import de.enflexit.meo.modellica.eomIntegration.FmuStaticDataModel;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMappingInterfaceFlow;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMappingIoList;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMappingIoList.IoVariableType;
import energy.optionModel.EnergyUnitFactorPrefixSI;

/**
 * The static data model for the heat pump FMU v2, where the initial SOC is fixed
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class HeatPumpFmuStaticDataModel extends FmuStaticDataModel implements Serializable {

	private static final long serialVersionUID = -1847258408954464865L;
	
	/**
	 * Instantiates a new heat pump FMU data model.
	 */
	public HeatPumpFmuStaticDataModel() {
		System.out.println("Initializing HeatPump FMU, RealOutput Version");
		this.setFmuFilePath(Application.getProjectFocused().getProjectFolderFullPath() + "fmuModels/HeatPumpFMU_RealOutputs/mGRiDS_CoSimFMI_HPSystem.fmu");
		this.setModelStepSizeMilliSeconds(1000);
		this.initializeVariableDescriptions();
	}

	/**
	 * Gets the variable mappings definition.
	 * @return the variable mappings definition
	 */
	private void initializeVariableDescriptions() {
		
		// --- Add IO variables -----------------------
		this.getIoListMappings().add(new FmuVariableMappingIoList("SOC", "SOC", IoVariableType.RESULT, "%"));
		this.getIoListMappings().add(new FmuVariableMappingIoList("heatPumpState", "Schaltsignal", IoVariableType.MEASUREMENT));
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
