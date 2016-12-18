package krabec.citysimulator.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import krabec.citysimulator.Block;
import krabec.citysimulator.Building;
import krabec.citysimulator.City;
import krabec.citysimulator.Crossroad;
import krabec.citysimulator.Lut;
import krabec.citysimulator.Mapping;
import krabec.citysimulator.Node;
import krabec.citysimulator.Settings;
import krabec.citysimulator.Simple_paint;
import krabec.citysimulator.network.Socket_Manager;
import krabec.citysimulator.Street;
import krabec.citysimulator.Street_Network;
import krabec.citysimulator.Street_type;
import krabec.citysimulator.Valuation;
import krabec.citysimulator.Valuation_Types;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JCheckBox;

public class City_window extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	City city;
	CityPanel panel;
	public boolean paused = true;
	boolean started = false;
	Simple_paint simple_paint;
	JButton start_button;
	JCheckBox growth_box;
	JCheckBox center_box;
	
	Socket_Manager socket_manager;
	
	public Timer timer = new Timer(10, new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			run_simulation();
			
		}
	});
	
	
	 class CityPanel extends JPanel implements Scrollable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			simple_paint.paint(g,growth_box.isSelected(),center_box.isSelected());
		}
		
		@Override
		public Dimension getPreferredSize(){
			return new Dimension(2000,2000);
			
		}
		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}
		@Override
		public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
			return 0;
		}
		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
		@Override
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}
		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			return 0;
		}
		
		
	}
	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					City_window frame = new City_window();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public City_window() {
		this.setTitle("City Growth Simulator");
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		City_window thiswindow = this;
		this.city = create_city();
		simple_paint = new Simple_paint(city);
		this.socket_manager = new Socket_Manager(city);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 822, 432);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showOpenDialog(thiswindow) == JFileChooser.APPROVE_OPTION) {
				  File file = fileChooser.getSelectedFile();
				  try{
				  FileInputStream fis = new FileInputStream(file);
					ObjectInputStream ois = new ObjectInputStream(fis);
					City result = (City) ois.readObject();
					change_city(result);
					thiswindow.repaint();
					ois.close();
				  }
				  catch(StreamCorruptedException e){
					  JOptionPane.showMessageDialog(thiswindow, "Wrong type of file! Loading failed!");
				  }
				
				catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(thiswindow, "Loading failed!");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(thiswindow, "Loading failed!");
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(thiswindow, "Loading failed!");
				}
			}
			}
		});
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("New Simulation (Default Settings)");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				change_city(create_city());
				panel.repaint();
			}
		});
		mnFile.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("New City (Current Settings)");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				city.network = new Street_Network(city.network.all_crossroads,city.getSettings());
				change_city(city);
				panel.repaint();
			}
		});
		mnFile.add(mntmNewMenuItem_2);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Save");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(thiswindow) == JFileChooser.APPROVE_OPTION) {
				  File file = fileChooser.getSelectedFile();
				  try{
					  FileOutputStream fos = new FileOutputStream(file);
					  ObjectOutputStream oos = new ObjectOutputStream(fos);
					  oos.writeObject(city);
					  oos.close();
				  }
				  catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				  

				}
			}
		});
		mnFile.add(mntmNewMenuItem);
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSaveImage = new JMenuItem("Save Image");
		mntmSaveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showSaveDialog(thiswindow) == JFileChooser.APPROVE_OPTION) {
				  File file = fileChooser.getSelectedFile();
				  file = (new File(file.getAbsolutePath() + ".png"));
				  try{
					  BufferedImage im = new BufferedImage(simple_paint.size,simple_paint.size,1);
					  simple_paint.paint(im.getGraphics(), growth_box.isSelected(), center_box.isSelected());
					  ImageIO.write(im, "PNG", file);
				  }
				  catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				  

				}
			}
		});
		mnFile.add(mntmSaveImage);
		
		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);
		
		JMenuItem mntmCrossroads = new JMenuItem("Crossroads");
		
		mnSettings.add(mntmCrossroads);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		City_window citywindow = this;
		
		mntmCrossroads.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						Cross_window cross_window = new Cross_window(city.network.all_crossroads,citywindow);
						cross_window.setVisible(true);
						cross_window.setAlwaysOnTop(true);
						if(!paused){
							start_button.setText("Start");
							paused = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			}
		});
		
		
		JMenuItem mntmLandUseTypes = new JMenuItem("Land Use Types");
		mntmLandUseTypes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							
							Lut_window lut_window = new Lut_window(city.luts,citywindow);
							lut_window.setVisible(true);
							lut_window.setAlwaysOnTop(true);
							if(!paused){
								start_button.setText("Start");
								paused = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
			}
		});
		mnSettings.add(mntmLandUseTypes);
		
		JMenuItem mntmParameters = new JMenuItem("Parameters");
		mntmParameters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							
							Param_window param_window = new Param_window(city.getSettings());
							param_window.setVisible(true);
							param_window.setAlwaysOnTop(true);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		JMenuItem mntmBuildings = new JMenuItem("Buildings");
		mntmBuildings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					
					Building_window building_window = new Building_window (city.luts);
					building_window.setVisible(true);
					building_window.setAlwaysOnTop(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mnSettings.add(mntmBuildings);
		mnSettings.add(mntmParameters);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.CENTER);
		
		JButton button3D = new JButton("Show in 3D");
		button3D.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					socket_manager.Send();
			}
		});
		panel_2.add(button3D);
		
		JButton btnNewButton = new JButton("Start");
		panel_2.add(btnNewButton);
		start_button = btnNewButton;
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(paused){
					btnNewButton.setText("Stop");
					paused = false;
					if(!timer.isRunning())
						timer.start();
				}
				else{
					btnNewButton.setText("Start");
					paused = true;
					
				}
			}
		});
		
		JButton btnNewButton_2 = new JButton("+");
		panel_2.add(btnNewButton_2);
		
		JButton btnNewButton_1 = new JButton("-");
		panel_2.add(btnNewButton_1);
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.WEST);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Draw and Show City Centers");
		center_box = chckbxNewCheckBox;
		chckbxNewCheckBox.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(growth_box.isSelected() && center_box.isSelected())
					growth_box.setSelected(false);
				panel.repaint();
				
			}
		});
		panel_3.add(chckbxNewCheckBox);
		
		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Draw and Show Growth Centers");
		growth_box = chckbxNewCheckBox_1;
		panel_3.add(chckbxNewCheckBox_1);
		chckbxNewCheckBox_1.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if(growth_box.isSelected() && center_box.isSelected())
					center_box.setSelected(false);
				panel.repaint();
				
			}
		});
		
		
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simple_paint.zoom_out();
				panel.repaint();
			}
		});
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simple_paint.zoom_in();
				panel.repaint();
			}
		});
		panel = new CityPanel();
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setViewportBorder(null);
		
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getValue() / 2+550);
		scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getMaximum());
		scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getValue() / 2 -800);
		
		
		scrollPane.addMouseListener(new MouseAdapter() {
			@Override	
			public void mouseClicked(MouseEvent e) {
				int x = scrollPane.getViewport().getViewPosition().x + e.getX();
				int y = scrollPane.getViewport().getViewPosition().y + e.getY();
				if(paused == true){
					if(growth_box.isSelected()){
						simple_paint.find_center(x, y,true);
					}
					else if(center_box.isSelected()){
						simple_paint.find_center(x, y,false);
					}
					else{
						
						Block b  = (Block)simple_paint.find_part(x,y);
						if(b != null){
							thiswindow.setEnabled(false);
							Lut_change_window win = new Lut_change_window(city, b,panel,thiswindow);
							win.setVisible(true);
							win.setAlwaysOnTop(true);
						}
					}
				}
				panel.repaint();
			}
		});
		getContentPane().add(scrollPane, BorderLayout.CENTER);

	}

	private void run_simulation(){
			started = true;
			StepWorker sw = new StepWorker(city, this,true);
			sw.execute();
	}
	
	private static void create_crossroad(List<Crossroad> all_crossroads,int n, double... angles){
		Crossroad c = new Crossroad(angles);
		all_crossroads.addAll(c.get_all_rotations());
	}
	public static Street create_street(Node n1, Node n2,Street_type type){
		Street s = new Street(n1,n2,type);
		n1.streets.add(s);
		n2.streets.add(s);
		return s;
		
	}
	
	public void repaintPanel(){
		panel.repaint();
	}
	private City create_city(){
		ArrayList<Crossroad> all_crossroads = new ArrayList<>();
		create_crossroad(all_crossroads,2, 180.0, 180.0);
		create_crossroad(all_crossroads,2, 90, 270);
		create_crossroad(all_crossroads,3, 90,90,180.0);
		create_crossroad(all_crossroads,4, 90.0,90.0,90.0,90.0);
		
		all_crossroads.add(Street_Network.end_of_road);
		
		for (Crossroad c: all_crossroads) {
			c.get_viable_crossroads(all_crossroads);
		}
		Street_Network network = new Street_Network(all_crossroads,new Settings());
		city = new City(network);
		
		Building big_factory = new Building(10,16,"Big Factory");
		
		Building small_factory = new Building(9,14,"Small Factory");
		
		Building office = new Building(10,8,"Office");
		
		Building house = new Building(6,8,"House");
		
		Building block_of_flats = new Building(14,8,"Block of flats");

		Building park = new Building(12,14,"Park");
		
		Building public_building = new Building(10,10,"Public Building");
		
		Lut low_d_residential =  new Lut("Low density residential", 1, 0.4,Color.blue,city.getSettings());
		low_d_residential.add_val(new Valuation((float) 0.45,Valuation_Types.traffic,Mapping.linear_down,0,50));
		low_d_residential.add_val(new Valuation((float) 0.4, Valuation_Types.influence, Mapping.linear_down, 0, 0.1));
		low_d_residential.add_val(new Valuation((float)0.15, Valuation_Types.influence, Mapping.linear_up, 0, 20));
		low_d_residential.getBuildings().add(house);
		
		Lut high_d_residential = new Lut("High density residential", 5, 0.2,Color.cyan,city.getSettings());
		high_d_residential.add_val(new Valuation((float)0.4, Valuation_Types.influence, Mapping.linear_down, 0, 0.1));
		high_d_residential.add_val(new Valuation((float)0.2, Valuation_Types.citycenter,Mapping.linear_down,0,20));
		high_d_residential.add_val(new Valuation((float)0.2, Valuation_Types.traffic, Mapping.linear_down, 5, 20));
		high_d_residential.add_val(new Valuation((float)0.2, Valuation_Types.influence, Mapping.linear_up, 0,0.4 ));
		high_d_residential.getBuildings().add(block_of_flats);
		
		Lut low_d_industrial = new Lut("Low density industrial", 2, 0.08,Color.orange,city.getSettings());
		low_d_industrial.add_val(new Valuation((float) 0.4, Valuation_Types.influence, Mapping.linear_up,0.1, 0.8));
		low_d_industrial.add_val(new Valuation((float) 0.3, Valuation_Types.citycenter, Mapping.linear_down, 0, 20));
		low_d_industrial.add_val(new Valuation((float) 0.3, Valuation_Types.constant, Mapping.constant, 1, 1));
		low_d_industrial.getBuildings().add(small_factory);
		low_d_industrial.getBuildings().add(office);
		
		Lut high_d_industrial = new Lut("High density industrial", 3,0.1,Color.red.brighter(),city.getSettings());
		high_d_industrial.add_val(new Valuation((float)0.4, Valuation_Types.citycenter, Mapping.linear_down, 0, 20));
		high_d_industrial.add_val(new Valuation((float)0.35, Valuation_Types.influence, Mapping.linear_up, 0, 0.3));
		high_d_industrial.add_val(new Valuation((float) 0.25, Valuation_Types.influence, Mapping.linear_down, 0, 0.2));
		high_d_industrial.getBuildings().add(big_factory);
		high_d_industrial.getBuildings().add(small_factory);
		high_d_industrial.getBuildings().add(office);
		
		Lut commercial = new Lut("Commercial", 3,0.015,Color.pink,city.getSettings());
		commercial.add_val(new Valuation((float) 0.75, Valuation_Types.traffic, Mapping.linear_up, 5, 20));
		commercial.add_val(new Valuation((float) 0.25, Valuation_Types.influence, Mapping.linear_up, 0.2, 0.8));
		commercial.getBuildings().add(office);
		
		Lut parks = new  Lut("Parks", 1,0.04,Color.green,city.getSettings());
		parks.add_val(new Valuation((float) 0.4, Valuation_Types.influence, Mapping.linear_up, 0, 1));
		parks.add_val(new Valuation((float) 0.6, Valuation_Types.citycenter, Mapping.linear_down, 0, 20));
		parks.getBuildings().add(park);
		
		Lut schools = new Lut("Public", 2, 0.03,Color.white,city.getSettings());
		schools.add_val(new Valuation((float) 0.2, Valuation_Types.influence, Mapping.linear_up, 0, 0.3));
		schools.add_val(new Valuation((float) 0.2, Valuation_Types.citycenter, Mapping.linear_down, 0, 20));
		schools.add_val(new Valuation((float) 0.6, Valuation_Types.influence, Mapping.linear_down, 0, 0.3));
		schools.getBuildings().add(public_building);
		low_d_residential.valuations.get(1).setInfluencing_lut(high_d_industrial);
		low_d_residential.valuations.get(2).setInfluencing_lut(commercial);
		
		high_d_residential.valuations.get(0).setInfluencing_lut(high_d_industrial);
		high_d_residential.valuations.get(3).setInfluencing_lut(commercial);
		
		low_d_industrial.valuations.get(0).setInfluencing_lut(low_d_residential);
		
		high_d_industrial.valuations.get(1).setInfluencing_lut(low_d_residential);
		high_d_industrial.valuations.get(2).setInfluencing_lut(schools);
		
		commercial.valuations.get(1).setInfluencing_lut(low_d_residential);
		
		parks.valuations.get(0).setInfluencing_lut(schools);
		
		schools.valuations.get(0).setInfluencing_lut(parks);
		schools.valuations.get(2).setInfluencing_lut(high_d_industrial);
		
		city.luts.add(low_d_residential);
		city.luts.add(high_d_residential);
		city.luts.add(low_d_industrial);
		city.luts.add(high_d_industrial);
		city.luts.add(commercial);
		city.luts.add(schools);
		city.luts.add(parks);
		
		for(Lut lut: city.luts){
			lut.find_min_area();
		}
		return city;
	}

	private void change_city(City city){
		this.city = city;
		simple_paint = new Simple_paint(city);
		socket_manager.setCity(city);
	}
	
}
