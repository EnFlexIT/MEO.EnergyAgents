package de.enflexit.meo.db;

import javax.swing.ImageIcon;

import org.osgi.service.prefs.BackingStoreException;

import agentgui.core.application.Application;


/**
 * The Class BundleHelper provides some static help methods to be used within the bundle.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg - Essen
 */
public class BundleHelper {

	private static final String imagePackage = "/images/";
	
	/**
	 * Gets the image package location as String.
	 * @return the image package
	 */
	public static String getImagePackage() {
		return imagePackage;
	}
	/**
	 * Gets the image icon for the specified image.
	 *
	 * @param fileName the file name
	 * @return the image icon
	 */
	public static ImageIcon getImageIcon(String fileName) {
		String imagePackage = getImagePackage();
		ImageIcon imageIcon=null;
		try {
			imageIcon = new ImageIcon(BundleHelper.class.getResource((imagePackage + fileName)));
		} catch (Exception err) {
			System.err.println("Error while searching for image file '" + fileName + "' in " + imagePackage);
			err.printStackTrace();
		}	
		return imageIcon;
	}
	
	

	/**
	 * Returns the id scenario result for the current setup.
	 * @return the id scenario result, -1 in case of errors or missing information or 0 if no ID was specified yet
	 */
	public static int getIdScenarioResultForSetup() {
		String prefKey = getIdScenarioResultKey();
		if (prefKey==null) return -1;
		return DatabaseSessionFactoryHandler.getEclipsePreferences().getInt(prefKey, 0);
	}
	/**
	 * Sets the id scenario result for the current setup and stores it in the bundle properties.
	 * @param id the new id scenario result
	 */
	public static void setIdScenarioResultForSetup(int id) {
		
		String prefKey = getIdScenarioResultKey();
		if (prefKey!=null) {
			try {
				DatabaseSessionFactoryHandler.getEclipsePreferences().putInt(prefKey, id);
				DatabaseSessionFactoryHandler.getEclipsePreferences().flush();
			} catch (BackingStoreException bse) {
				bse.printStackTrace();
			}
			
		} else {
			System.err.println("[" + BundleHelper.class.getSimpleName() + "] Error while receiving the preference key for the current idScenarioResult");
		}
	}
	
	/**
	 * Returns the key for the id scenario result of the current setup.
	 * @return the id scenario result key
	 */
	private static String getIdScenarioResultKey() {
		String setupName =  getSetupName();
		if (setupName==null || setupName.isEmpty()==true) return null;
		return "meo.idScenarioResult." + setupName;
	}
	/**
	 * Returns the current setup name.
	 * @return the setup name
	 */
	private static String getSetupName() {
		if (Application.getProjectFocused()!=null) {
			return Application.getProjectFocused().getSimulationSetupCurrent();
		}
		return null;
	}
	
}
