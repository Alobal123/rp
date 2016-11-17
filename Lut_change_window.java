package krabec.citysimulator.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import krabec.citysimulator.Block;
import krabec.citysimulator.City;
import krabec.citysimulator.Lut;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Lut_change_window extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 284591413770579771L;
	private final JPanel contentPanel = new JPanel();
	JComboBox<Lut> comboBox ;

	City city;
	JPanel panel;
	/**
	 * Create the dialog.
	 * @param citywindow 
	 */
	public Lut_change_window(City city,Block block,JPanel panel, City_window citywindow) {
		
		
		Lut_change_window thiswindow = this;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				thiswindow.dispose();
				citywindow.setEnabled(true);
			}
		});
		this.city = city;
		this.panel = panel;
		
		
		setBounds(100, 100, 419, 143);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lblNewLabel = new JLabel("Choose a lut for this block");
			lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblNewLabel, BorderLayout.NORTH);
		}
		{

			comboBox = new JComboBox<>();
			for(Lut lut: city.luts){
				comboBox.addItem(lut);
			}
			comboBox.setSelectedItem(block.lut);
			contentPanel.add(comboBox, BorderLayout.CENTER);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						city.reevaluate(block, (Lut) comboBox.getSelectedItem());
						panel.repaint();
						citywindow.setEnabled(true);
						thiswindow.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
