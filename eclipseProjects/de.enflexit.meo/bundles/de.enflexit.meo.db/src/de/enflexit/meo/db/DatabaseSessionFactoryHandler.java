package de.enflexit.meo.db;

import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.prefs.BackingStoreException;

import de.enflexit.db.hibernate.HibernateDatabaseService;
import de.enflexit.db.hibernate.HibernateUtilities;
import de.enflexit.db.hibernate.SessionFactoryMonitor;

/**
 * The Class DatabaseSessionFactoryHandler provides static help functions 
 * to control the SessionFactory of the H2 bundle.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class DatabaseSessionFactoryHandler {

	public static final String SESSION_FACTORY_ID = "de.enflexit.meo.db";
	
	private static final String cfgFile = "/de/enflexit/meo/db/cfg/hibernate.cfg.xml";
	private static final String mappingFilesPackage = "/de/enflexit/meo/db/mappings/";
	
	private static IEclipsePreferences eclipsePreferences;
	private static Bundle localBundle;
	
	private static String databaseSystem;
	private static Configuration configuration;

	private static int hibernateBatchSize = 50;
	
	
	// ------------------------------------------------------------------------
	// --- Bundle information about database system and preferences -----------
	// ------------------------------------------------------------------------
	/**
	 * Returns the current database system to be used with hibernate.
	 * @return the database system
	 */
	public static String getDatabaseSystem() {
		if (databaseSystem==null) {
			databaseSystem = getEclipsePreferences().get("meo.DatabaseSystem", "MySQL");
		}
		return databaseSystem;
	}
	/**
	 * Sets the database system to be used.
	 * @param newDatabaseSystem the new database system
	 */
	public static void setDatabaseSystem(String newDatabaseSystem) {
		if (newDatabaseSystem==null) throw new NullPointerException("The database system for the H2-Readiness session factory is not allowed to be null!");
		try {
			databaseSystem = newDatabaseSystem;
			getEclipsePreferences().put("meo.DatabaseSystem", databaseSystem);
			getEclipsePreferences().flush();
			
		} catch (BackingStoreException bsEx) {
			bsEx.printStackTrace();
		}
	}
	
	/**
	 * Sets the eclipse preferences for the H2-Readiness database connection.
	 * @param hibernateDatabaseSettings the new eclipse preferences for the database connection
	 */
	public static void setEclipsePreferencesForDatabaseConnection(Properties hibernateDatabaseSettings) {
		
		if (hibernateDatabaseSettings==null) return;
		
		try {
			ArrayList<Object> propertyList = new ArrayList<>(hibernateDatabaseSettings.keySet());
			for (int i = 0; i < propertyList.size(); i++) {
				// --- Save the eclipse preferences -----------------
				String propName = (String) propertyList.get(i);
				String propValue = hibernateDatabaseSettings.getProperty(propName);
				getEclipsePreferences().put(propName, propValue);
				// --- Change the hibernate configuration -----------
				getConfiguration().setProperty(propName, propValue);
			}
			getEclipsePreferences().flush();
			
		} catch (BackingStoreException bsEx) {
			bsEx.printStackTrace();
		}
	}
	
	// ------------------------------------------------------------------------
	// --- Eclipse preferences ------------------------------------------------
	// ------------------------------------------------------------------------
	/**
	 * Returns the eclipse preferences.
	 * @return the eclipse preferences
	 */
	public static IEclipsePreferences getEclipsePreferences() {
		if (eclipsePreferences==null) {
			IScopeContext iScopeContext = ConfigurationScope.INSTANCE;
			eclipsePreferences = iScopeContext.getNode(getLocalBundle().getSymbolicName());
		}
		return eclipsePreferences;
	}
	
	// ------------------------------------------------------------------------
	// --- Handling for DB session factory and its configuration --------------
	// ------------------------------------------------------------------------
	/**
	 * Returns the session factory monitor.
	 * @return the session factory monitor
	 */
	public static SessionFactoryMonitor getSessionFactoryMonitor() {
		return HibernateUtilities.getSessionFactoryMonitor(SESSION_FACTORY_ID);
	}
	
	/**
	 * Gets the new hibernate database session.
	 * @return the new database session
	 */
	public static Session getNewDatabaseSession() {
		return getNewDatabaseSession(false);
	}
	/**
	 * Gets the new hibernate database session.
	 *
	 * @param isResetSessionFactory the reset session factory
	 * @return the new database session
	 */
	public static Session getNewDatabaseSession(boolean isResetSessionFactory) {
		Session session = null;
		SessionFactory sf = DatabaseSessionFactoryHandler.getSessionFactory(isResetSessionFactory, false);
		if (sf!=null) {
			session = sf.openSession();
		}
		return session;
	}
	
	/**
	 * Start the H2-Readiness SessionFactory within an extra thread.
	 * @param isResetSessionFactory the is reset session factory
	 * @param doSilentConnectionCheck the do silent connection check
	 */
	public static void startSessionFactory(final boolean isResetSessionFactory, final boolean doSilentConnectionCheck) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				DatabaseSessionFactoryHandler.getSessionFactory(isResetSessionFactory, doSilentConnectionCheck);
			}
		}, "MEO-Results-SessionFactory-(Re)Start").start();
	}
	/**
	 * Closes the current session factory.
	 */
	public static void closeSessionFactory() {
		HibernateUtilities.closeSessionFactory(SESSION_FACTORY_ID);
	}

	
	/**
	 * Sets the statistics for the SessionFactory enabled (or not).
	 * @param setEnabled the set enabled
	 */
	public static void setStatisticsEnabled(boolean setEnabled) {
		HibernateUtilities.setStatisticsEnabled(SESSION_FACTORY_ID, setEnabled);
	}
	/**
	 * Write the SessionFactory statistics.
	 */
	public static void writeStatistics() {
		HibernateUtilities.writeStatistics(SESSION_FACTORY_ID);
	}
	
	
	/**
	 * Returns the hibernate session factory.
	 * @return the session factory
	 */
	public static SessionFactory getSessionFactory() {
		return getSessionFactory(false, false);
	}
	/**
	 * Returns the hibernate session factory.
	 * @param doSilentConnectionCheck set true, if you want to make a silent connection check
	 * @return the session factory
	 */
	public static SessionFactory getSessionFactory(boolean doSilentConnectionCheck) {
		return getSessionFactory(false, doSilentConnectionCheck);
	}
	/**
	 * Returns the hibernate session factory.
	 * @param isResetSessionFactory the is reset session factory
	 * @param doSilentConnectionCheck set true, if you want to make a silent connection check
	 * @return the session factory
	 */
	public static SessionFactory getSessionFactory(boolean isResetSessionFactory, boolean doSilentConnectionCheck) {
		return HibernateUtilities.getSessionFactory(SESSION_FACTORY_ID, getConfiguration(), isResetSessionFactory, doSilentConnectionCheck); 
	}
	
	/**
	 * Returns the configuration for the hibernate connection.
	 * @return the configuration
	 */
	public static synchronized Configuration getConfiguration() {
		if (configuration==null) {
			getSessionFactoryMonitor();
			Bundle thisBundle = getLocalBundle();
			URL url = thisBundle.getResource(cfgFile);
			configuration = new Configuration().configure(url);
			addInternalHibernateProperties(configuration);
			loadDatabaseConfigurationProperties(configuration);
			addMappingFileResources(configuration);
		}
		return configuration;
	}
	/**
	 * Adds internal hibernate configuration properties.
	 * @param configuration the configuration to be used
	 */
	private static void addInternalHibernateProperties(Configuration configuration) {
		configuration.setProperty("hibernate.jdbc.batch_size", ((Integer)getHibernateBatchSize()).toString());
	}
	/**
	 * Load configuration properties.
	 * @param configuration the configuration
	 */
	private static void loadDatabaseConfigurationProperties(Configuration configuration) {
		
		HibernateDatabaseService hds = HibernateUtilities.getDatabaseService(getDatabaseSystem());
		if (hds!=null) {
			// --- Get the key required for the database system -----
			Properties defaultProperties = hds.getHibernateDefaultPropertySettings();
			Vector<String> dbPropertiyNames = hds.getHibernateConfigurationPropertyNamesForDbCheckOnJDBC();
			for (int i = 0; i < dbPropertiyNames.size(); i++) {
				// --- Get property name and value ------------------
				String propertyName = dbPropertiyNames.get(i);
				String propertyValue = getEclipsePreferences().get(propertyName, null);
				if (propertyValue==null) {
					// --- Set the default value --------------------
					propertyValue = defaultProperties.getProperty(propertyName);
				}
				// -- Set to hibernate configuration ----------------
				if (propertyValue!=null) {
					configuration.setProperty(propertyName, propertyValue);
				}
			}
			
		} else {
			System.err.println("No HibernateDatabaseService could be found for the database system '" + getDatabaseSystem() + "'");
		}
	}
	/**
	 * Adds the hibernate mapping files to the configuration.
	 * @param conf the current hibernate configuration
	 */
	private static void addMappingFileResources(Configuration conf) {
		
		Bundle bundle = getLocalBundle();
		if (conf==null || bundle==null) return;
		
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		if (bundleWiring==null) return; 
		
		Vector<String> mappingResources = new Vector<>(bundleWiring.listResources(mappingFilesPackage, "*.hbm.xml", BundleWiring.LISTRESOURCES_LOCAL));
		for (int i = 0; i < mappingResources.size(); i++) {
			String mappingResource = mappingResources.get(i);
			conf.addResource(mappingResource);
		}
	}
	
	/**
	 * Gets the hibernate batch size.
	 * @return the hibernate batch size
	 */
	public static int getHibernateBatchSize() {
		return hibernateBatchSize;
	}
	
	// ------------------------------------------------------------------------
	// --- The local bundle instance ------------------------------------------
	// ------------------------------------------------------------------------
	/**
	 * Gets the local bundle.
	 * @return the local bundle
	 */
	public static Bundle getLocalBundle() {
		if (localBundle==null) {
			localBundle = FrameworkUtil.getBundle(DatabaseSessionFactoryHandler.class);
		}
		return localBundle;
	}
	/**
	 * Sets the local bundle.
	 * @param localBundle the new local bundle
	 */
	public static void setLocalBundle(Bundle localBundle) {
		DatabaseSessionFactoryHandler.localBundle = localBundle;
	}

}
