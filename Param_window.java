package krabec.citysimulator.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import krabec.citysimulator.Settings;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Param_window extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 741820597382211777L;
	Settings settings;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;
	private JTextField textField_9;
	private JTextField textField_10;
	private JTextField textField_11;
	private JTextField textField_12;
	private JTextField textField_13;
	private JTextField textField_14;
	private JTextField textField_15;
	
	
	/**
	 * Create the dialog.
	 */
	public Param_window(Settings settings){
		this.settings = settings;
		this.setTitle("Parameters");
		Param_window thiswindow = this;
		setBounds(100, 100, 468, 614);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		{
			JLabel lblNewLabel_2 = new JLabel("Lengths of streets");
			lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 11));
			contentPanel.add(lblNewLabel_2);
		}
		{
			JLabel lblNewLabel_1 = new JLabel("");
			contentPanel.add(lblNewLabel_1);
		}
		{
			JLabel lblNewLabel = new JLabel("Major street minimum length");
			contentPanel.add(lblNewLabel);
		}
		{
			textField = new JTextField();
			contentPanel.add(textField);
			textField.setText(Double.toString(settings.major_min_length));
			textField.setColumns(10);
			textField.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField.getText());
						  settings.major_min_length = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
		}
		{
			JLabel lblMajorStreetMaximum = new JLabel("Major street maximum length");
			contentPanel.add(lblMajorStreetMaximum);
		}
		{
			textField_1 = new JTextField();
			textField_1.setColumns(10);
			textField_1.setText(Double.toString(settings.major_max_length));
			contentPanel.add(textField_1);
			textField_1.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField_1.getText());
						  settings.major_max_length = n;
						  settings.major_prolongation = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
		}
		{
			JLabel lblMinorStreetMinimum = new JLabel("Minor street minimum length");
			contentPanel.add(lblMinorStreetMinimum);
		}
		{
			textField_2 = new JTextField();
			textField_2.setColumns(10);
			textField_2.setText(Double.toString(settings.minor_min_length));
			contentPanel.add(textField_2);
			textField_2.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField_2.getText());
						  settings.minor_min_length = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
		}
		{
			JLabel lblMinorStreetMaximum = new JLabel("Minor street maximum length");
			contentPanel.add(lblMinorStreetMaximum);
		}
		{
			textField_3 = new JTextField();
			textField_3.setColumns(10);
			textField_3.setText(Double.toString(settings.minor_max_length));
			textField_3.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField_3.getText());
						  settings.minor_max_length = n;
						  settings.minor_prolongation = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
			
			contentPanel.add(textField_3);
		}
		{
			JLabel lblWidthOfStreets = new JLabel("Width of streets");
			contentPanel.add(lblWidthOfStreets);
		}
		{
			textField_14 = new JTextField();
			textField_14.setText(Double.toString(settings.street_width));
			textField_14.setColumns(10);
			textField_14.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField_14.getText());
						  settings.street_width = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
			contentPanel.add(textField_14);
		}
		{
			JLabel lblBuildingOffset = new JLabel("Building Offset");
			contentPanel.add(lblBuildingOffset);
		}
		{
			textField_15 = new JTextField();
			textField_15.setText((String) null);
			textField_15.setColumns(10);
			textField_15.setText(Double.toString(settings.street_offset));
			contentPanel.add(textField_15);
			textField_15.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField_15.getText());
						  settings.street_offset = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
		}
		{
			JLabel lblNewLabel_3 = new JLabel("Allowed distance of streets");
			lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 11));
			contentPanel.add(lblNewLabel_3);
		}
		{
			JLabel label = new JLabel("");
			contentPanel.add(label);
		}
		{
			JLabel lblNewLabel_4 = new JLabel("Maximum distance of major nodes");
			contentPanel.add(lblNewLabel_4);
		}
		{
			textField_4 = new JTextField();
			textField_4.setColumns(10);
			textField_4.setText(Double.toString(settings.major_close_node_constant));
			textField_4.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField_4.getText());
						  settings.major_close_node_constant = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
			
			contentPanel.add(textField_4);
		}
		{
			JLabel lblMaximumDistanceOf = new JLabel("Maximum distance of major streets");
			contentPanel.add(lblMaximumDistanceOf);
		}
		{
			textField_5 = new JTextField();
			textField_5.setColumns(10);
			textField_5.setText(Double.toString(settings.major_close_street_constant));
			contentPanel.add(textField_5);
			textField_5.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField_5.getText());
						  settings.major_close_street_constant = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
		}
		{
			JLabel lblMaximumDistanceOf_1 = new JLabel("Maximum distance of minor nodes");
			contentPanel.add(lblMaximumDistanceOf_1);
		}
		{
			textField_6 = new JTextField();
			textField_6.setColumns(10);
			textField_6.setText(Double.toString(settings.minor_close_node_constant));
			contentPanel.add(textField_6);
			textField_6.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField_6.getText());
						  settings.minor_close_node_constant = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
		}
		{
			JLabel lblMaximumDistanceOf_2 = new JLabel("Maximum distance of minor streets");
			contentPanel.add(lblMaximumDistanceOf_2);
		}
		{
			textField_7 = new JTextField();
			textField_7.setColumns(10);
			textField_7.setText(Double.toString(settings.minor_close_street_constant));
			contentPanel.add(textField_7);
			textField_7.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField_7.getText());
						  settings.minor_close_street_constant = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
			
		}
		{
			JLabel lblResampleRates = new JLabel("Resample Rates");
			lblResampleRates.setFont(new Font("Tahoma", Font.BOLD, 11));
			contentPanel.add(lblResampleRates);
		}
		{
			JLabel lblNewLabel_5 = new JLabel("");
			contentPanel.add(lblNewLabel_5);
		}
		{
			JLabel lblNewLabel_6 = new JLabel("Traffic resample rate");
			contentPanel.add(lblNewLabel_6);
		}
		{
			textField_8 = new JTextField();
			textField_8.setColumns(10);
			textField_8.setText(Double.toString(settings.traffic_resample_rate));
			contentPanel.add(textField_8);
			textField_8.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField_8.getText());
						  settings.traffic_resample_rate = n;
					  }
					  catch(NumberFormatException e){
						  
					  }
				  }
				   
			});
		}
		{
			JLabel lblLandUseType = new JLabel("Land use type resample rate");
			contentPanel.add(lblLandUseType);
		}
		{
			textField_9 = new JTextField();
			textField_9.setColumns(10);
			textField_9.setText(Double.toString(settings.lut_resample_rate));
			contentPanel.add(textField_9);
			{
				JLabel lblCosts = new JLabel("Costs");
				lblCosts.setFont(new Font("Tahoma", Font.BOLD, 11));
				contentPanel.add(lblCosts);
			}
			{
				JLabel label = new JLabel("");
				contentPanel.add(label);
			}
			{
				JLabel lblStreetBuildingCost = new JLabel("Street building cost");
				contentPanel.add(lblStreetBuildingCost);
			}
			{
				textField_10 = new JTextField();
				textField_10.setColumns(10);
				textField_10.setText(Double.toString(settings.build_cost));
				contentPanel.add(textField_10);
				textField_10.getDocument().addDocumentListener(new DocumentListener() {
					  public void changedUpdate(DocumentEvent e) {
						  update();
					  }
					  public void removeUpdate(DocumentEvent e) {
						  update();
					  }
					  public void insertUpdate(DocumentEvent e) {
						  update();
					  }
					  private void update(){
						  try{
							  double n = Double.parseDouble(textField_10.getText());
							  settings.build_cost = n;
						  }
						  catch(NumberFormatException e){
							  
						  }
					  }
					   
				});
			}
			{
				JLabel lblLandUseType_1 = new JLabel("Land use type change cost");
				contentPanel.add(lblLandUseType_1);
			}
			{
				textField_11 = new JTextField();
				textField_11.setColumns(10);
				textField_11.setText(Double.toString(settings.lut_resample_cost));
				textField_11.getDocument().addDocumentListener(new DocumentListener() {
					  public void changedUpdate(DocumentEvent e) {
						  update();
					  }
					  public void removeUpdate(DocumentEvent e) {
						  update();
					  }
					  public void insertUpdate(DocumentEvent e) {
						  update();
					  }
					  private void update(){
						  try{
							  double n = Double.parseDouble(textField_12.getText());
							  settings.major_street_capacity = n;
						  }
						  catch(NumberFormatException e){
							  
						  }
					  }
					   
				});
				contentPanel.add(textField_11);
			}
			{
				JLabel lblMaximumTrafficValues = new JLabel("Maximum traffic values");
				lblMaximumTrafficValues.setFont(new Font("Tahoma", Font.BOLD, 11));
				contentPanel.add(lblMaximumTrafficValues);
			}
			{
				JLabel label = new JLabel("");
				contentPanel.add(label);
			}
			{
				JLabel lblMaximumMajorStreet = new JLabel("Maximum major street traffic");
				contentPanel.add(lblMaximumMajorStreet);
			}
			{
				textField_12 = new JTextField();
				textField_12.setColumns(10);
				textField_12.setText(Double.toString(settings.major_street_capacity));
				contentPanel.add(textField_12);
				textField_12.getDocument().addDocumentListener(new DocumentListener() {
					  public void changedUpdate(DocumentEvent e) {
						  update();
					  }
					  public void removeUpdate(DocumentEvent e) {
						  update();
					  }
					  public void insertUpdate(DocumentEvent e) {
						  update();
					  }
					  private void update(){
						  try{
							  double n = Double.parseDouble(textField_12.getText());
							  settings.major_street_capacity = n;
						  }
						  catch(NumberFormatException e){
							  
						  }
					  }
					   
				});
			}
			{
				JLabel lblMaximumMinorStreet = new JLabel("Maximum minor street traffic");
				contentPanel.add(lblMaximumMinorStreet);
			}
			{
				textField_13 = new JTextField();
				textField_13.setText("0.2");
				textField_13.setColumns(10);
				textField_13.setText(Double.toString(settings.minor_street_capacity));
				contentPanel.add(textField_13);
				{
					JLabel lblNewLabel_7 = new JLabel("Simulation Goals");
					lblNewLabel_7.setFont(new Font("Tahoma", Font.BOLD, 11));
					contentPanel.add(lblNewLabel_7);
				}
				{
					JLabel lblNewLabel_8 = new JLabel("");
					contentPanel.add(lblNewLabel_8);
				}
				{
					JLabel lblNewLabel_9 = new JLabel("Weight of global goals");
					contentPanel.add(lblNewLabel_9);
				}
				{
					JSlider slider = new JSlider();
					slider.setValue((int) (settings.global_weight*100));
					slider.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent arg0) {
							settings.global_weight = slider.getValue()/100.0;
						}
					});
					slider.setToolTipText("");
					contentPanel.add(slider);
				}
				textField_13.getDocument().addDocumentListener(new DocumentListener() {
					  public void changedUpdate(DocumentEvent e) {
						  update();
					  }
					  public void removeUpdate(DocumentEvent e) {
						  update();
					  }
					  public void insertUpdate(DocumentEvent e) {
						  update();
					  }
					  private void update(){
						  try{
							  double n = Double.parseDouble(textField_13.getText());
							  settings.minor_street_capacity= n;
						  }
						  catch(NumberFormatException e){
							  
						  }
					  }
					   
				});
			}
			textField_9.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
					  update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  private void update(){
					  try{
						  double n = Double.parseDouble(textField_9.getText());
						  settings.lut_resample_rate = n;
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
				buttonPane.add(okButton);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						thiswindow.dispose();
					}
				});
				getRootPane().setDefaultButton(okButton);
			}

		}
	}


}
