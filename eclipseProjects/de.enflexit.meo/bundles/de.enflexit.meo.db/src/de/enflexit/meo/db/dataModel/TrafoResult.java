package de.enflexit.meo.db.dataModel;

import java.io.Serializable;
import java.util.Calendar;

public class TrafoResult implements Serializable {

	private static final long serialVersionUID = 1151410790396704632L;
	
	private int idScenarioResult;	
	private String idTrafo;	
	private Calendar timestamp;
	
	private double voltageReal;
	private double voltageComplex;
	private double voltageViolations;	
	private double residualLoadP;
	private double residualLoadQ;	
	private double trafoUtilization;	
	private double trafoLossesP;
	private double trafoLossesQ;
	
	
	public int getIdScenarioResult() {
		return idScenarioResult;
	}
	public void setIdScenarioResult(int idScenarioResult) {
		this.idScenarioResult = idScenarioResult;
	}
	
	public String getIdTrafo() {
		return idTrafo;
	}
	public void setIdTrafo(String idTrafo) {
		this.idTrafo = idTrafo;
	}
	
	public Calendar getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
	
	
	public double getVoltageReal() {
		return voltageReal;
	}
	public void setVoltageReal(double voltageReal) {
		this.voltageReal = voltageReal;
	}
	
	public double getVoltageComplex() {
		return voltageComplex;
	}
	public void setVoltageComplex(double voltageComplex) {
		this.voltageComplex = voltageComplex;
	}
	
	public double getVoltageViolations() {
		return voltageViolations;
	}
	public void setVoltageViolations(double voltageViolations) {
		this.voltageViolations = voltageViolations;
	}

	public double getResidualLoadP() {
		return residualLoadP;
	}
	public void setResidualLoadP(double residualLoadP) {
		this.residualLoadP = residualLoadP;
	}

	public double getResidualLoadQ() {
		return residualLoadQ;
	}
	public void setResidualLoadQ(double residualLoadQ) {
		this.residualLoadQ = residualLoadQ;
	}

	public double getTrafoUtilization() {
		return trafoUtilization;
	}
	public void setTrafoUtilization(double trafoUtilization) {
		this.trafoUtilization = trafoUtilization;
	}

	public double getTrafoLossesP() {
		return trafoLossesP;
	}
	public void setTrafoLossesP(double trafoLossesP) {
		this.trafoLossesP = trafoLossesP;
	}
	
	public double getTrafoLossesQ() {
		return trafoLossesQ;
	}
	public void setTrafoLossesQ(double trafoLossesQ) {
		this.trafoLossesQ = trafoLossesQ;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object compObejct) {
		
		if (compObejct==null || !(compObejct instanceof TrafoResult)) return false;
		TrafoResult trComp = (TrafoResult) compObejct;
		
		if (trComp.getIdScenarioResult()!=this.getIdScenarioResult()) return false;
		
		String idComp = trComp.getIdTrafo();
		String idLocal = this.getIdTrafo();
		if (idComp==null && idLocal==null) {
			// --- equals ---
		} else if ((idComp==null && idLocal!=null) || (idComp!=null && idLocal==null)) {
			return false;
		} else {
			if (idComp.equals(idLocal)==false) return false;
		}
		
		if (trComp.getTimestamp()==null && this.getTimestamp()==null) {
			// --- equals ---
		} else if ((trComp.getTimestamp()==null && this.getTimestamp()!=null) || (trComp.getTimestamp()!=null && this.getTimestamp()==null)) {
			return false;
		} else if (trComp.getTimestamp().equals(this.getTimestamp())==false) {
			return false;
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		String hashCodeString = "" + this.getIdScenarioResult();
		
		if (this.getIdTrafo()==null) {
			hashCodeString += "null";
		} else {
			hashCodeString += this.getIdTrafo();
		}

		if (this.getTimestamp()==null) {
			hashCodeString += "null";
		} else {
			hashCodeString += this.getTimestamp();
		}
		return hashCodeString.hashCode();
	}
}