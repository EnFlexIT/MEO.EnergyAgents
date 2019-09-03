package de.enflexit.meo.plugIn;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.awb.env.networkModel.controller.GraphEnvironmentController;
import org.awb.env.networkModel.controller.ui.toolbar.AbstractCustomToolbarComponent;

import de.enflexit.meo.BundleHelper;


/**
 * The Class JButtonConstructionSite that will be 
 * displayed in the toolbar of the GraphEnvironement.
 * 
 * @author Christian Derksen - DAWIS - ICB - University of Duisburg-Essen
 */
public class JButtonConstructionSite extends AbstractCustomToolbarComponent implements ActionListener {

	/**
	 * Instantiates a new JButtonConstructionSite.
	 * @param graphController the graph controller
	 */
	public JButtonConstructionSite(GraphEnvironmentController graphController) {
		super(graphController);
	}

	/* (non-Javadoc)
	 * @see org.awb.env.networkModel.controller.AbstractCustomToolbarComponent#getCustomComponent()
	 */
	@Override
	public JComponent getCustomComponent() {
		JButton jButtonConstructionSite = new JButton();
		jButtonConstructionSite.setIcon(BundleHelper.getImageIcon("ConstructionSite.png"));
		jButtonConstructionSite.setToolTipText("Construction Site ... ");
		jButtonConstructionSite.setPreferredSize(new Dimension(26, 26));
		jButtonConstructionSite.addActionListener(this);
		return jButtonConstructionSite;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		System.err.println("[" + this.getClass().getSimpleName() + "] Button was pressed! ");
	}
		
}
