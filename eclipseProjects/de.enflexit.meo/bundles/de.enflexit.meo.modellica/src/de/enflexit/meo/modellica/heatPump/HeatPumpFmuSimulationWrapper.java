package de.enflexit.meo.modellica.heatPump;

import de.enflexit.meo.modellica.eomIntegration.FmuSimulationWrapper;
import de.enflexit.meo.modellica.eomIntegration.FmuStaticDataModel;
import de.enflexit.meo.modellica.eomIntegration.FmuVariableMappingIoList;
import energy.helper.TechnicalSystemStateHelper;
import energy.optionModel.FixedDouble;
import energy.optionModel.FixedVariable;
import energy.optionModel.TechnicalSystemStateEvaluation;

/**
 * A specialized {@link FmuSimulationWrapper} for the Heatpump-FMU, performing
 * necessary pre- and postprocessing on some variables. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class HeatPumpFmuSimulationWrapper extends FmuSimulationWrapper {
	
	private static final String FMU_VARIABLE_T_INIT_BOTTOM = "Tinit_bottom";
	private static final int T_INIT_SOC_100 = 50;

	/**
	 * Instantiates a new heat pump FMU simulation wrapper.
	 * @param fmuStaticModel the FMU static model
	 */
	public HeatPumpFmuSimulationWrapper(FmuStaticDataModel fmuStaticModel) {
		super(fmuStaticModel);
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.meo.modellica.eomIntegration.FmuSimulationWrapper#writeVariableToFMU(de.enflexit.meo.modellica.eomIntegration.FmuVariableMappingIoList, energy.optionModel.TechnicalSystemStateEvaluation)
	 */
	@Override
	protected void writeVariableToFMU(FmuVariableMappingIoList variableMapping, TechnicalSystemStateEvaluation tsse) {
		
		if (variableMapping.getFmuVariableName().equals(FMU_VARIABLE_T_INIT_BOTTOM)) {
			
			// --- Pre-processing required for tInitBottom / SOC ----
			if (tsse.getParent()!=null) {
				// --- Get the SOC from the parent state ------------
				FixedVariable parentSOC  = TechnicalSystemStateHelper.getFixedVariable(tsse.getParent().getIOlist(), variableMapping.getEomVariableName());
				if (parentSOC!=null) {
					// --- Calculate the corresponding tInit for the current state 
					double parentSOCValue = ((FixedDouble)parentSOC).getValue();
					double tInitValue = T_INIT_SOC_100 * parentSOCValue;
					this.getSimulation().write(variableMapping.getFmuVariableName()).with(tInitValue);
				}
			}
			
		} else {
			// --- All other variables can be handled by the superclass
			super.writeVariableToFMU(variableMapping, tsse);
		}
	}

}
