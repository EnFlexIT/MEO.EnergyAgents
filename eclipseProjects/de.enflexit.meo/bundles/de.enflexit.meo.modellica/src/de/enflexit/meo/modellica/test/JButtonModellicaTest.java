package de.enflexit.meo.modellica.test;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.awb.env.networkModel.controller.GraphEnvironmentController;
import org.awb.env.networkModel.controller.ui.toolbar.AbstractCustomToolbarComponent;

import de.enflexit.ea.core.dataModel.ImageHelper;

public class JButtonModellicaTest extends AbstractCustomToolbarComponent implements ActionListener{
	
	private JButton jButtonScheduleEditor;
	
	/**
	 * Instantiates a new j button modellica test.
	 * @param graphController the graph controller
	 */
	public JButtonModellicaTest(GraphEnvironmentController graphController) {
		super(graphController);
	}

	/* (non-Javadoc)
	 * @see org.awb.env.networkModel.controller.ui.toolbar.AbstractCustomToolbarComponent#getCustomComponent()
	 */
	@Override
	public JComponent getCustomComponent() {
		return this.getJButtonScheduleEditor();
	}
	
	private JButton getJButtonScheduleEditor() {
		if (jButtonScheduleEditor==null) {
			jButtonScheduleEditor = new JButton();
			jButtonScheduleEditor.setIcon(ImageHelper.getImageIcon("ConstructionSite.png"));
			jButtonScheduleEditor.setToolTipText("Execute Modellica FMU/FMI test");
			jButtonScheduleEditor.setPreferredSize(new Dimension(26, 26));
			jButtonScheduleEditor.addActionListener(this);
		}
		return jButtonScheduleEditor;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		TestFMU testFMU = new TestFMU();
		System.out.println("[" + this.getClass().getSimpleName() + "] Executed " + testFMU.toString());
	}
	
}
