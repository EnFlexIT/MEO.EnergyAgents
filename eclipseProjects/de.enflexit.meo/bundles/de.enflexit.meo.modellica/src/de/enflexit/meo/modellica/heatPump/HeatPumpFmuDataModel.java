package de.enflexit.meo.modellica.heatPump;

import java.io.Serializable;

import agentgui.core.application.Application;
import de.enflexit.meo.modellica.eomIntegration.AbstractVariableMapping.VariableType;
import de.enflexit.meo.modellica.eomIntegration.FmuStaticDataModel;
import de.enflexit.meo.modellica.eomIntegration.VariableMappingGeneral;
import de.enflexit.meo.modellica.eomIntegration.VariableMappingInterfaceFlow;
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
		this.initializeVariableMappings();
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
	private void initializeVariableMappings() {
			
			// --- Add static system parameters -----------
			this.getVariableMappings().add(new VariableMappingGeneral("hpNominalElectricalLoad", "Waermepumpe_Elektrischeverbrauch_Nominal", VariableType.STATIC_DATAMODEL));
			this.getVariableMappings().add(new VariableMappingGeneral("hpNominalThermalPower", "Waermepumpe_ThermischeLeistung_Nominal", VariableType.STATIC_DATAMODEL));
			this.getVariableMappings().add(new VariableMappingGeneral("coilNominalPower", "Heizstab_Nominal_Leistung", VariableType.STATIC_DATAMODEL));
			
			// --- Add IO variables -----------------------
			this.getVariableMappings().add(new VariableMappingGeneral("SOC", "SOC", VariableType.RESULT, "%"));
			this.getVariableMappings().add(new VariableMappingGeneral("coilState", "Schaltsignal_Heizstab", VariableType.SETPOINT));
			this.getVariableMappings().add(new VariableMappingGeneral("neatPumpState", "Schaltsignal_Waermepumpe", VariableType.SETPOINT));
			this.getVariableMappings().add(new VariableMappingGeneral("pTh", "ThermischeLast", VariableType.MEASUREMENT, "°C"));
			this.getVariableMappings().add(new VariableMappingGeneral("tAmb", "UmgebungsTemperatur", VariableType.MEASUREMENT, "°C"));
			
			// --- Add interface flows --------------------
			// new VariableMappingGeneral("Electricity", "Pel_HP", VariableType.INTERFACE_FLOW);
			VariableMappingInterfaceFlow interfaceFlowHeatPump = new VariableMappingInterfaceFlow("Electricity", "Pel_HP", EnergyUnitFactorPrefixSI.KILO_K_3);
			this.getVariableMappings().add(interfaceFlowHeatPump);
			VariableMappingInterfaceFlow interfaceFlowCoil = new VariableMappingInterfaceFlow("Electricity", "Pel_COIL", EnergyUnitFactorPrefixSI.KILO_K_3);
			this.getVariableMappings().add(interfaceFlowCoil);
	}
}
