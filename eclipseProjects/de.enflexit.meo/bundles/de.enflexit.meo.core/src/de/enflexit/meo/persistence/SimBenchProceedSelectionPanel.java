package de.enflexit.meo.persistence;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimBenchProceedSelectionPanel extends JPanel {
	
	private static final long serialVersionUID = -5208144521402665174L;
	
	private boolean canceled;
	
	private JLabel jLabelExplain;
	private JButton jButtonProceed;
	private JButton jButtonCancel;
	
	
	public SimBenchProceedSelectionPanel() {
		this.initialize();
	}

	private void initialize() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		GridBagConstraints gbc_jLabelExplain = new GridBagConstraints();
		gbc_jLabelExplain.anchor = GridBagConstraints.WEST;
		gbc_jLabelExplain.insets = new Insets(5, 10, 0, 5);
		gbc_jLabelExplain.gridx = 0;
		gbc_jLabelExplain.gridy = 0;
		add(getJLabelExplain(), gbc_jLabelExplain);
		GridBagConstraints gbc_jButtonCancel = new GridBagConstraints();
		gbc_jButtonCancel.insets = new Insets(5, 10, 5, 0);
		gbc_jButtonCancel.gridx = 1;
		gbc_jButtonCancel.gridy = 0;
		add(getJButtonCancel(), gbc_jButtonCancel);
		GridBagConstraints gbc_jButtonProceed = new GridBagConstraints();
		gbc_jButtonProceed.insets = new Insets(5, 10, 5, 10);
		gbc_jButtonProceed.gridx = 2;
		gbc_jButtonProceed.gridy = 0;
		add(getJButtonProceed(), gbc_jButtonProceed);
		
	}

	private JLabel getJLabelExplain() {
		if (jLabelExplain == null) {
			jLabelExplain = new JLabel("Please, select the node (and thus the corresponding profile) that is to be imported.");
			jLabelExplain.setFont(new Font("Dialog", Font.BOLD, 12));
		}
		return jLabelExplain;
	}
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton("Cancel");
			jButtonCancel.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonCancel.setForeground(new Color(183, 0, 0));
			jButtonCancel.setPreferredSize(new Dimension(90, 26));
			jButtonCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SimBenchProceedSelectionPanel.this.canceled = true;
					SimBenchProceedSelectionPanel.this.getJButtonProceed().doClick();
				}
			});
		}
		return jButtonCancel;
	}
	private JButton getJButtonProceed() {
		if (jButtonProceed == null) {
			jButtonProceed = new JButton("Proceed");
			jButtonProceed.setFont(new Font("Dialog", Font.BOLD, 12));
			jButtonProceed.setForeground(new Color (0, 183, 0));
			jButtonProceed.setPreferredSize(new Dimension(90, 26));
		}
		return jButtonProceed;
	}
	public boolean isCanceled() {
		return canceled;
	}
	
	/**
	 * Can be used to register an action listener to JButton of the panel. 
	 * @param listener the action listener
	 */
	public void addActionListener(ActionListener listener) {
		this.getJButtonProceed().addActionListener(listener);
	}
	
	
}
