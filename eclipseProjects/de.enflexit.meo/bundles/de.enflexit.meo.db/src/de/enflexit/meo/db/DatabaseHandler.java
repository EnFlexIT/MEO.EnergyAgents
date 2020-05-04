package de.enflexit.meo.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import agentgui.core.application.Application;
import de.enflexit.meo.db.dataModel.AbstractStateResult;
import de.enflexit.meo.db.dataModel.NetworkState;


/**
 * The Class DatabaseHandler can be used to save the MEO state results.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg-Essen
 */
public class DatabaseHandler {
	 
	private List<NetworkState> networkStateListToSave;
	private boolean doTerminateThread;
	
	private Session session;

	
	/**
	 * Instantiates a new database handler.
	 */
	public DatabaseHandler() { }
	/**
	 * Instantiates a new database handler.
	 * @param session the session instance to use
	 */
	public DatabaseHandler(Session session) {
		this.setSession(session);
	}
	
	/**
	 * Returns the current session instance.
	 * @return the session
	 */
	public Session getSession() {
		if (session==null) {
			session = DatabaseSessionFactoryHandler.getNewDatabaseSession();
		}
		return session;
	}
	/**
	 * Sets the current session instance.
	 * @param session the new session
	 */
	public void setSession(Session session) {
		if (this.session!=null) {
			if (session==null) {
				this.session.close();
			} else {
				if (this.session!=session) {
					this.session.close();
				}
			}
		}
		this.session = session;
	}
	/**
	 * Disposes this database handler by closing the database session.
	 */
	public void dispose() {
		this.setSession(null);
	}
	/**
	 * Returns the hibernate batch size.
	 * @return the hibernate batch size
	 */
	private int getHibernateBatchSize() {
		return DatabaseSessionFactoryHandler.getHibernateBatchSize();
	}
	
	// --------------------------------------------------------------
	// --- From here, working on data -------------------------------
	// --------------------------------------------------------------	
	
	public List<NetworkState> getNetworkStateListToSave() {
		if (networkStateListToSave==null) {
			networkStateListToSave = new ArrayList<>();
		}
		return networkStateListToSave;
	}
	
	/**
	 * Adds a NetworkState that is to save.
	 * @param networkState the network state
	 */
	public void addNetworkStateToSave(NetworkState networkState) {
		this.getNetworkStateListToSave().add(networkState);
		synchronized (this.getNetworkStateListToSave()) {
			this.getNetworkStateListToSave().notifyAll();
		}
	}
	/**
	 * Stop network state save thread if no further job is to be done.
	 */
	public void stopNetworkStateSaveThread() {
		this.doTerminateThread = true;
		synchronized (this.getNetworkStateListToSave()) {
			this.getNetworkStateListToSave().notifyAll();
		}

	}
	
	/**
	 * Start network state save thread.
	 */
	public void startNetworkStateSaveThread() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				while (true) {
					// --- Wait for the trigger to save -------------
					if (DatabaseHandler.this.getNetworkStateListToSave().isEmpty()==true) {
						// --- Terminate? ---------------------------
						if (doTerminateThread==true) break;
						// --- Wait for the next state --------------
						try {
							synchronized (DatabaseHandler.this.getNetworkStateListToSave()) {
								DatabaseHandler.this.getNetworkStateListToSave().wait();
							}
							
						} catch (InterruptedException iEx) {
							iEx.printStackTrace();
						}
					}
					
					// --- Get first NetworkState -------------------
					if (DatabaseHandler.this.getNetworkStateListToSave().isEmpty()==false) {
						NetworkState networkState = DatabaseHandler.this.getNetworkStateListToSave().remove(0);
						if (doTerminateThread==true) {
							Calendar calTimeStamp = networkState.getEdgeResultList().get(0).getTimestamp();
							String timeText = new SimpleDateFormat("dd.MM.yy HH:mm").format(calTimeStamp.getTime());
							String statusText = "Saving network state for " + timeText + " - " + DatabaseHandler.this.getNetworkStateListToSave().size() + " network states remaining.";
							Application.setStatusBarMessage(statusText);
						}
						DatabaseHandler.this.saveNetworkState(networkState);
					}
				} // end while
				
				Application.setStatusBarMessageReady();
			}
		}, "NetworkState-SaveThread").start();
	}
	
	/**
	 * Saves the specified NetworkState in the current thread.
	 * @param networkState the network state
	 */
	public void saveNetworkState(NetworkState networkState) {
		
		Session session = this.getSession();
		try {

			this.saveStateResult(networkState.getNodeResultList(), session);
			this.saveStateResult(networkState.getEdgeResultList(), session);
			this.saveStateResult(networkState.getTrafoResultList(), session);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Saves the specified state results.
	 *
	 * @param networkStateList the network state list
	 * @return true, if successful
	 */
	public boolean saveStateResult(List<? extends AbstractStateResult> networkStateList) {
		return this.saveStateResult(networkStateList, this.getSession());
	}
	/**
	 * Saves the specified state results.
	 *
	 * @param networkStateList the network element state list
	 * @param sessionToUse the session to use
	 * @return true, if successful
	 */
	public boolean saveStateResult(List<? extends AbstractStateResult> networkStateList, Session sessionToUse) {
		
		if (networkStateList==null) return false;
		
		boolean successful = false;
		Transaction transaction = null;
		boolean isOpenTransaction = sessionToUse.getTransaction()!=null && sessionToUse.getTransaction().isActive();
		
		try {

			// --- Saving in own transaction? --- 
			if (isOpenTransaction==false) {
				transaction = sessionToUse.beginTransaction();
			}
			
			// --- Set IdNetworkState -----------
			int batchSize = this.getHibernateBatchSize();
			for (int i = 0; i < networkStateList.size(); i++) {
				
				AbstractStateResult stateResult = networkStateList.get(i);
				
				// --- Save the state -----------
				sessionToUse.save(stateResult);
				if (i % batchSize==0) { 
			        // --- Release memory -------
					sessionToUse.flush();
					sessionToUse.clear();
			    }
			}
			
			// --- Saving in own transaction? ---
			if (isOpenTransaction==false) {
				sessionToUse.flush();
				sessionToUse.clear();
				transaction.commit();
			}
			successful = true;
			
		} catch (Exception ex) {
			if (transaction!=null) transaction.rollback();
			ex.printStackTrace();
			successful = false;
		}
		return successful;
	}
	
	
}
