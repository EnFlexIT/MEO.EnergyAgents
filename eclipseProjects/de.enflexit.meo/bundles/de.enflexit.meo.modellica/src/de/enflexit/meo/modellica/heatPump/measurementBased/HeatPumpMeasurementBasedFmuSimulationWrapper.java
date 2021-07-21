package de.enflexit.meo.modellica.heatPump.measurementBased;

import de.enflexit.meo.modellica.eomIntegration.FmuSimulationWrapper;
import de.enflexit.meo.modellica.eomIntegration.FmuStaticDataModel;
import energy.helper.TechnicalSystemStateHelper;
import energy.optionModel.FixedDouble;
import energy.optionModel.FixedVariable;
import energy.optionModel.TechnicalSystemStateEvaluation;

/**
 * A specialized {@link FmuSimulationWrapper} for the Heatpump-FMU, performing
 * necessary pre- and postprocessing on some variables. 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class HeatPumpMeasurementBasedFmuSimulationWrapper extends FmuSimulationWrapper {
	
	private static final String FMU_VARIABLE_T_INIT_BOTTOM = "Tinit_bottom";
	private static final String EOM_VARIABLE_SOC = "SOC";
	private static final int T_INIT_SOC_100 = 50;

	/**
	 * Instantiates a new heat pump FMU simulation wrapper.
	 * @param fmuStaticModel the FMU static model
	 */
	public HeatPumpMeasurementBasedFmuSimulationWrapper(FmuStaticDataModel fmuStaticModel) {
		super(fmuStaticModel);
	}
	
	/* (non-Javadoc)
	 * @see de.enflexit.meo.modellica.eomIntegration.FmuSimulationWrapper#performCustomInitializations(energy.optionModel.TechnicalSystemStateEvaluation)
	 */
	@Override
	protected void performCustomInitializations(TechnicalSystemStateEvaluation tsse) {

		// --- Initialize the FMU with the SOC from the parent TSSE -----------
		FixedVariable initialSOC = TechnicalSystemStateHelper.getFixedVariable(tsse.getIOlist(), EOM_VARIABLE_SOC);
		
		// --- Calculate the corresponding tInit for the current state --------
		if (initialSOC!=null) {
			double initialSOCValue = ((FixedDouble)initialSOC).getValue();
			double tInitValue = T_INIT_SOC_100 * initialSOCValue;
			this.getSimulation().write(FMU_VARIABLE_T_INIT_BOTTOM).with(tInitValue);
		}
	}
}
