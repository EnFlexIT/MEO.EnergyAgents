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
public class HeatPumpFmuDataModel extends FmuStaticDataModel implements Serializable {

	private static final long serialVersionUID = -1847258408954464865L;
	
	private double heatPumpNominalElectricalPower;
	private double heatPumpNominalThermalPower;
	private double coilNominalPower;
	
	
	/**
	 * Instantiates a new heat pump fmu data model.
	 */
	public HeatPumpFmuDataModel() {
		this.setFmuFilePath(Application.getProjectFocused().getProjectFolderFullPath() + "fmuModels/mGRiDS_CoSimFMI_HPSystem/mGRiDS_CoSimFMI.HPSystem.fmu");
		this.setModelStepSizeMilliSeconds(1000);
		this.initializeVariableDescriptions();
	}

	/**
	 * Gets the heat pump nominal electrical power.
	 * @return the heat pump nominal electrical power
	 */
	public double getHeatPumpNominalElectricalPower() {
		return heatPumpNominalElectricalPower;
	}
	
	/**
	 * Sets the heat pump nominal electrical power.
	 * @param heatPumpNominalElectricalPower the new heat pump nominal electrical power
	 */
	public void setHeatPumpNominalElectricalPower(double heatPumpNominalElectricalPower) {
		this.heatPumpNominalElectricalPower = heatPumpNominalElectricalPower;
	}
	
	/**
	 * Gets the heat pump nominal thermal power.
	 * @return the heat pump nominal thermal power
	 */
	public double getHeatPumpNominalThermalPower() {
		return heatPumpNominalThermalPower;
	}
	
	/**
	 * Sets the heat pump nominal thermal power.
	 * @param heatPumpNominalThermalPower the new heat pump nominal thermal power
	 */
	public void setHeatPumpNominalThermalPower(double heatPumpNominalThermalPower) {
		this.heatPumpNominalThermalPower = heatPumpNominalThermalPower;
	}
	
	/**
	 * Gets the coil nominal power.
	 * @return the coil nominal power
	 */
	public double getCoilNominalPower() {
		return coilNominalPower;
	}
	
	/**
	 * Sets the coil nominal power.
	 * @param coilNominalPower the new coil nominal power
	 */
	public void setCoilNominalPower(double coilNominalPower) {
		this.coilNominalPower = coilNominalPower;
	}
	
	/**
	 * Gets the variable mappings definition.
	 * @return the variable mappings definition
	 */
	private void initializeVariableDescriptions() {
		// --- Add static system parameters -----------
		this.getSystemParameters().add(new FmuVariableMappingStaticParameter("Waermepumpe_Elektrischeverbrauch_Nominal", 3.5, "kW"));
		this.getSystemParameters().add(new FmuVariableMappingStaticParameter("Waermepumpe_ThermischeLeistung_Nominal", 10.0, "kW"));
		this.getSystemParameters().add(new FmuVariableMappingStaticParameter("Heizstab_Nominal_Leistung", 10.0, "kW"));
		this.getSystemParameters().add(new FmuVariableMappingStaticParameter("Tinit_bottom", 50.0, "°C"));
		
		// --- Add IO variables -----------------------
		this.getIoVariables().add(new FmuVariableMappingIoList("SOC", "SOC", IoVariableType.RESULT, "%"));
		this.getIoVariables().add(new FmuVariableMappingIoList("coilState", "Schaltsignal_Heizstab", IoVariableType.SETPOINT));
		this.getIoVariables().add(new FmuVariableMappingIoList("heatPumpState", "Schaltsignal_Waermepumpe", IoVariableType.SETPOINT));
		this.getIoVariables().add(new FmuVariableMappingIoList("pTh", "ThermischeLast", IoVariableType.MEASUREMENT, "°C"));
		this.getIoVariables().add(new FmuVariableMappingIoList("tAmb", "UmgebungsTemperatur", IoVariableType.MEASUREMENT, "°C"));
		
		// --- Add interface flows --------------------
		FmuVariableMappingInterfaceFlow energyFlowHeatPump = new FmuVariableMappingInterfaceFlow();
		energyFlowHeatPump.setFmuVariableName("Pel_HP");
		energyFlowHeatPump.setDomain("Electricity");
		energyFlowHeatPump.setUnit(EnergyUnitFactorPrefixSI.KILO_K_3);
		//TODO add domain model?
		this.getInterfaceFlowVariables().add(energyFlowHeatPump);
		
		FmuVariableMappingInterfaceFlow energyFlowCoil = new FmuVariableMappingInterfaceFlow();
		energyFlowCoil.setFmuVariableName("Pel_COIL");
		energyFlowCoil.setDomain("Electricity");
		energyFlowCoil.setUnit(EnergyUnitFactorPrefixSI.KILO_K_3);
		//TODO add domain model?
		this.getInterfaceFlowVariables().add(energyFlowCoil);
	}
}
