package krabec.citysimulator;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JColorChooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTextField;

public class Lut_panel extends JPanel {

	private static final long serialVersionUID = 1L;
	Lut lut;
	JLabel namelabel;
	Lut_window parent;
	private JTextField textField;
	
	@Override
	public  Dimension getPreferredSize(){
		return new Dimension(500, 50);
	}
	
	public Lut_panel(Lut lut,Lut_window parent) {
		this.lut = lut;
		this.parent = parent;
		Lut_panel thispanel = this;
		
		JButton btnNewButton = new JButton("                       ");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 Color new_color = JColorChooser.showDialog(null, "JColorChooser Sample", lut.color);
				 lut.color = new_color;
				 btnNewButton.setBackground(new_color);
			}
		});
		setLayout(new BorderLayout(0, 0));
		btnNewButton.setBackground(lut.color);
		add(btnNewButton, BorderLayout.WEST);
		
		namelabel = new JLabel(" "+lut.name);
		namelabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(namelabel, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		panel.setLayout(new GridLayout(1, 1, 0, 0));
		
		textField = new JTextField();
		panel.add(textField);
		textField.setText(Double.toString(lut.wanted_percentage));
		textField.setColumns(5);

		textField.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update_weight();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update_weight();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update_weight();
				  }
				  private void update_weight(){
					  try{
						  double n = Double.parseDouble(textField.getText());
						  lut.wanted_percentage = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
		
		
		
		JButton btnNewButton_1 = new JButton("Edit");
		panel.add(btnNewButton_1);
		
		
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							Lut_edit lut_edit = new Lut_edit(lut,thispanel);
							lut_edit.setVisible(true);
							lut_edit.setAlwaysOnTop(true);
							lut_edit.pack();
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
				
		});
		
		}
}
