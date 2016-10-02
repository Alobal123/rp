package krabec.citysimulator;

import javax.swing.JPanel;

import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Val_panel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	Valuation valuation;
	Lut_edit parent;
	JLabel type_label;
	JLabel type_label2;
	private JTextField weight_field;
	/**
	 * Create the panel.
	 */
	public Val_panel(Valuation valuation,Lut_edit parent) {
		Val_panel thispanel = this;
		this.parent = parent;
		this.valuation = valuation;
		setLayout(new GridLayout(1, 0, 0, 0));
		
		type_label = new JLabel();
		add(type_label);
		type_label.setText(valuation.type.toString());
		
		JLabel mapping_label = new JLabel();
		type_label2 = mapping_label;
		add(mapping_label);
		mapping_label.setText(valuation.mapping.toString());
		
		weight_field = new JTextField();
		add(weight_field);
		weight_field.setColumns(10);
		weight_field.setText(Float.toString(valuation.weight));
		weight_field.getDocument().addDocumentListener(new DocumentListener() {
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
					  float n = Float.parseFloat(weight_field.getText());
					  valuation.weight = n;
				  }
				  catch(NumberFormatException e){
					  
				  }
			  }
			   
		});
		
		JButton edit_button = new JButton("Edit");
		edit_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							Val_Edit val_window = new Val_Edit(valuation,thispanel);
							val_window.setVisible(true);
							val_window.setAlwaysOnTop(true);
							val_window.repaint();
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		add(edit_button);

	}

}
