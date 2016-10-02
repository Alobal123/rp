package krabec.citysimulator;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

public class Cross_panel extends JPanel{

	private static final long serialVersionUID = 1L;
	Crossroad crossroad;
	JTextField fieldn;
	ArrayList<JTextField> fields = new ArrayList<>();
	Cross_window parent;
	/**
	 * Create the panel.
	 * @param crossroad 
	 */
	public Cross_panel(Crossroad crossroad, Cross_window parent) {
		this.crossroad = crossroad;
		this.parent = parent;
		Cross_panel thispanel = this;
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.EAST);
		
		fieldn = new JTextField(Integer.toString(crossroad.number_of_roads));
		fieldn.setColumns(10);
		fieldn.setHorizontalAlignment(SwingConstants.CENTER);
		add(fieldn, BorderLayout.WEST);
		
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		fieldn.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
			    update_crossroad();
			  }
			  public void removeUpdate(DocumentEvent e) {
				update_crossroad();
			  }
			  public void insertUpdate(DocumentEvent e) {
				update_crossroad();
			  }

			  public void update_crossroad() {
			    try{
			    	int n = Integer.parseInt(fieldn.getText());
			    	panel.removeAll();
			    	panel.setLayout(new GridLayout(1, 0, 0, 0));
			    	fields = new ArrayList<>();
					for(int i=0;i<n;i++){
						JTextField field = new  JTextField(Double.toString(0));
						panel_1.add(field);
						fields.add(field);
						field.setColumns(6);
					}
					JButton remove_button = new JButton("X");
					remove_button.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							parent.panel.remove(thispanel);	
							parent.panel.updateUI();
						}
					});
					panel.add(remove_button);
					panel.updateUI();
			    }
			    catch(NumberFormatException e){
			    	
			    }
			  }

			});

		fields = new ArrayList<>();
		for(Double angle: crossroad.angles){
			JTextField field = new  JTextField(Double.toString(angle));
			panel_1.add(field);
			fields.add(field);
			field.setColumns(6);
			
		}
		JButton remove_button = new JButton("X");
		remove_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.panel.remove(thispanel);	
				parent.panel.updateUI();
			}
		});
		panel.setLayout(new BorderLayout(0, 0));
		panel.add(remove_button, BorderLayout.CENTER);
		
		
		

	}

}
