package krabec.citysimulator.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import krabec.citysimulator.Lut;
import krabec.citysimulator.Mapping;
import krabec.citysimulator.Valuation;
import krabec.citysimulator.Valuation_Types;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class Lut_edit extends JDialog {

	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	Lut lut;
	Lut_panel parent;
	JPanel panel;
	private JTextField textField;
	private JSpinner textField_1;

	
	/**
	 * Create the dialog.
	 */
	public Lut_edit(Lut lut,Lut_panel parent) {
		this.setTitle("Editing "+ lut.getName());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.lut = lut;
		this.parent = parent;
		Lut_edit thiswindow = this;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
			panel.setLayout(new GridLayout(0, 2, 0, 0));
			{
				JLabel lblNewLabel = new JLabel("Name");
				panel.add(lblNewLabel);
			}
			{
				textField = new JTextField();
				panel.add(textField);
				textField.setText(lut.getName());
				textField.setColumns(10);
				textField.getDocument().addDocumentListener(new DocumentListener() {
					  public void changedUpdate(DocumentEvent e) {
					    lut.setName(textField.getText());
					  }
					  public void removeUpdate(DocumentEvent e) {
						  lut.setName(textField.getText());
					  }
					  public void insertUpdate(DocumentEvent e) {
						  lut.setName(textField.getText());
					  }
					   
				});
			}
			{
				JLabel lblNewLabel_1 = new JLabel("Residents density");
				panel.add(lblNewLabel_1);
			}
			{
				textField_1 = new JSpinner();
				
				textField_1.setModel(new SpinnerNumberModel(0.5, 0.5, 5.0, 0.5));
				textField_1.setValue(lut.residents);
				panel.add(textField_1);
				textField_1.addChangeListener(new ChangeListener() {
	
					@Override
					public void stateChanged(ChangeEvent e) {
						lut.residents = (double) textField_1.getValue();
					}
				});
			}

		}
		{
			panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new GridLayout(0, 1, 0, 0));
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						control_and_set();
						thiswindow.dispose();
						parent.namelabel.setText(lut.getName());
						
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton delete_button = new JButton("Delete");
				delete_button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						parent.parent.panel.remove(parent);
						parent.parent.panel.updateUI();;
						thiswindow.dispose();
					}
				});
				buttonPane.add(delete_button);
			}

		}
		for(Valuation val: lut.valuations){
			panel.add(new Val_panel(val,thiswindow));
		}
		JButton new_lut_button = new JButton("New");
		panel.add(new_lut_button);
		new_lut_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.remove(new_lut_button);
				Val_panel newpanel = new Val_panel(new Valuation(0, Valuation_Types.constant, Mapping.constant, 0, 1), thiswindow);
				panel.add(newpanel);
				parent.lut.valuations.add(newpanel.valuation);
				panel.add(new_lut_button);
				panel.updateUI();
			}
		});
	}

	
	private boolean control_and_set(){
		double residents;
		try{
			residents = (double) textField_1.getValue();
		}
		catch(NumberFormatException e){
			return false;
		}
		lut.setName(textField.getText());
		lut.residents = residents;
		return true;
			
	}
}
