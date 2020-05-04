package de.enflexit.meo.db.blackboard;

import de.enflexit.ea.core.dataModel.blackboard.Blackboard;
import de.enflexit.ea.core.dataModel.blackboard.BlackboardListenerService;

/**
 * The BlackboardListener connects to the {@link Blackboard} of the SimulationManager and thus
 * allows to receive new system states and save them to the database.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg-Essen
 */
public class BlackboardListener implements BlackboardListenerService {

	/* (non-Javadoc)
	 * @see de.enflexit.ea.core.dataModel.blackboard.BlackboardListenerService#onNetworkCalculationDone(de.enflexit.ea.core.dataModel.blackboard.Blackboard)
	 */
	@Override
	public void onNetworkCalculationDone(Blackboard blackboard) {
		System.out.println(this.getClass().getName() + " received new blackboard state ... ");
	}

}
