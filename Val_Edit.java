package krabec.citysimulator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class Val_Edit extends JDialog {

	private final JPanel contentPanel = new JPanel();

	Valuation val;
	Val_panel parent;
	private JTextField textField;
	private JTextField textField_1;
	private JComboBox<Lut> influencing;


	/**
	 * Create the dialog.
	 */
	public Val_Edit(Valuation val,Val_panel parent) {
		this.setTitle("Editing Valuation");
		this.val = val;
		this.parent = parent;
		Val_Edit thiswindow = this;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		{
			JLabel lblTypeOfValuation = new JLabel("Type of Valuation");
			lblTypeOfValuation.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblTypeOfValuation);
		}
		{
			JComboBox<Valuation_Types> comboBox = new JComboBox<>();
			comboBox.setSelectedItem(val);
			
			for(Valuation_Types valtype: Valuation_Types.values()){
				comboBox.addItem(valtype);
			}
			comboBox.setSelectedItem(val.type);
			comboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					val.type = (Valuation_Types) comboBox.getSelectedItem();
					if(val.type == Valuation_Types.influence)
						influencing.setEnabled(true);
					else if(val.type == Valuation_Types.clustering){
						influencing.setSelectedItem(parent.parent.lut);
						influencing.setEnabled(false);
					}
					else{
						influencing.setEnabled(false);
					}
						
				}
			});
			contentPanel.add(comboBox);
			comboBox.setSelectedItem(val);
		}
		{
			JLabel lblInfluencingType = new JLabel("Influencing type");
			lblInfluencingType.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblInfluencingType);
		}
		{
			JComboBox<Lut> comboBox2 = new JComboBox<>();
			if(val.type != Valuation_Types.influence)
				comboBox2.setEnabled(false);
			influencing = comboBox2;
			for(Lut lut: parent.parent.parent.parent.load_luts()){
				comboBox2.addItem(lut);
			}
			influencing.setSelectedItem(val.influencing_lut);
			comboBox2.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					val.influencing_lut = (Lut) comboBox2.getSelectedItem();
					
				}
			});
			contentPanel.add(comboBox2);
		}
		{
			JLabel lblMappingType = new JLabel("Mapping type");
			lblMappingType.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblMappingType);
		}
		{
			JComboBox<Mapping> comboBox3 = new JComboBox<>();
			for(Mapping m : Mapping.values())
				comboBox3.addItem(m);
			comboBox3.setSelectedItem(val.mapping);
			comboBox3.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					val.mapping = (Mapping) comboBox3.getSelectedItem();
					
				}
			});
			contentPanel.add(comboBox3);
		}
		{
			JLabel minimum_label = new JLabel("Minimum");
			minimum_label.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(minimum_label);
		}
		{
			textField = new JTextField();
			contentPanel.add(textField);
			textField.setColumns(10);
			textField.setText(Double.toString(val.min));
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
						  val.min = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
		}
		{
			JLabel lblNewLabel = new JLabel("Maximum");
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblNewLabel);
		}
		{
			textField_1 = new JTextField();
			contentPanel.add(textField_1);
			textField_1.setColumns(10);
			textField_1.setText(Double.toString(val.max));
			textField_1.getDocument().addDocumentListener(new DocumentListener() {
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
						  double n = Double.parseDouble(textField_1.getText());
						  val.max = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						parent.type_label.setText(val.type.toString());
						parent.type_label2.setText(val.mapping.toString());
						parent.updateUI();
						
						thiswindow.dispose();
					}
				});
				{
					JButton delete_button = new JButton("Delete");
					delete_button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							parent.parent.lut.valuations.remove(val);
							parent.parent.panel.remove(parent);
							parent.parent.repaint();
							parent.parent.panel.updateUI();
							thiswindow.dispose();
						}
					});
					buttonPane.add(delete_button);
				}
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
}
