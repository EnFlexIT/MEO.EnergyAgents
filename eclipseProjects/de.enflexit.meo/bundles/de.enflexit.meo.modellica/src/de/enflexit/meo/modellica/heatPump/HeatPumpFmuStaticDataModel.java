package de.enflexit.meo.modellica.heatPump;

import java.io.Serializable;

import de.enflexit.meo.modellica.eomIntegration.FmuSimulationWrapper;
import de.enflexit.meo.modellica.eomIntegration.FmuStaticDataModel;

/**
 * The static data model for the heat pump FMU 
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class HeatPumpFmuStaticDataModel extends FmuStaticDataModel implements Serializable {

	private static final long serialVersionUID = -1847258408954464865L;
	
	/* (non-Javadoc)
	 * @see de.enflexit.meo.modellica.eomIntegration.FmuStaticDataModel#getFmuSimulationWrapper()
	 */
	@Override
	public FmuSimulationWrapper getSimulationWrapper() {
		if (simulationWrapper==null) {
			simulationWrapper = new HeatPumpFmuSimulationWrapper(this);
		}
		return simulationWrapper;
	}
}
