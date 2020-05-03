package de.enflexit.meo.db;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The Class DatabaseActivator starts the connection to the database.
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class DatabaseActivator implements BundleActivator {

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.startSessionFactory();
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		this.closeSessionFactory();
	}

	/**
	 * Start session factory.
	 */
	private void startSessionFactory() {
		DatabaseSessionFactoryHandler.startSessionFactory(false, true);
	}
	/**
	 * Close session factory.
	 */
	private void closeSessionFactory() {
		DatabaseSessionFactoryHandler.closeSessionFactory();
	}
	

}
