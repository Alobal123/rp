package krabec.citysimulator;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simple_paint implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3071466066619399273L;
	public int size = 4000;
	double magn = 3; 

	boolean first = true;
	ArrayList<Lot> expanded = new ArrayList<>();
	
	City_part current_block =null;
	private Street_Network network;
	private City city;
	
	public Simple_paint(City city){
		this.city = city;
		this.network = city.network;
	}
	
	public void paint(Graphics g,boolean growth,boolean centers) {
		
		g.setColor(Color.black);
		g.fillRect(0, 0, size*10, size*10);
		for (int i = 0; i < network.quarters.size(); i++) {
			Quarter q = network.quarters.get(i);
			for (int j = 0; j < q.contained_city_parts.size(); j++) {
				Block b = (Block) q.contained_city_parts.get(j);
				
				if(b.lut != null && b.built){
					for (int k = 0; k < b.contained_city_parts.size(); k++) {
						color_part(b.contained_city_parts.get(k),g,true,b.lut.color);
					}
				}
			}
		}
		for (int i = 0; i < network.nodes.size(); i++) {
			Node n = network.nodes.get(i);
			if(n.isBuilt()){
				draw_node(n, g, Color.white);
			}
			for (int j = 0; j < n.streets.size(); j++) {
				Street s = n.streets.get(j);
				if(s.built){
					draw_street(s, g,null);
				}
			}
		}
		if(current_block != null){
			color_part(current_block, g,true, Color.BLUE);
		}
		
		if(centers)
			draw_city_centers(g);
		if(growth)
			draw_growth_centers(g);
	}
 	
	
	private void color_part(City_part q, Graphics g, Boolean blocks, Color color){
		if(q instanceof Quarter){
			for (int i = 0; i < q.streets.size(); i++) {
				draw_street(q.streets.get(i), g, Color.blue);
			}
		}
		
		if(q instanceof Block){
			
		for (int i = 0; i < ((Block)q).lot_borders.size(); i++) {
			Node n = ((Block)q).lot_borders.get(i);
			draw_node(n, g, Color.magenta);
			for(Street s: n.streets){
				if(s.major == Street_type.lot_border)
					draw_street(s, g, Color.magenta);
				else
					draw_street(s, g, Color.yellow);
			}
		}
	}
		

	g.setColor(color);
	if(q instanceof Lot && ((Lot) q).building != null){
		draw_building(((Lot) q).building, g);
	}
	}
	
	private void draw_node(Node n, Graphics g, Color color){
		g.setColor(color);
		//g.fillOval((int)(n.getPoint().getX()*magn/2)+size/2-3, (int)(-1*n.getPoint().getY()*magn/2)+size/2-3,6, 6);
	}
	
	private void draw_street(Street s, Graphics g,Color color){
		int x1 = (int)(s.node1.getPoint().getX()*magn/2) + size/2;
		int y1 = (int)(s.node1.getPoint().getY()*-magn/2) + size/2;
		int x2 = (int)(s.node2.getPoint().getX()*magn/2) + size/2;
		int y2 = (int)(s.node2.getPoint().getY()*-magn/2) + size/2;
		

			if(color == null){
				if(s.major == Street_type.major)
					g.setColor(Color.white);
				else if(s.major == Street_type.minor)
					g.setColor(Color.red);
				else
					g.setColor(Color.magenta);
			}
			g.setColor(color);
			g.drawLine(x1, y1, x2, y2);
		}

	
	private void draw_building(Building building,Graphics g){
		int [] xs = new int [2*building.borders.size()];
		int [] ys = new int [2*building.borders.size()];	
		ArrayList<Point> points  = building.placement;
		
		for (int i = 0; i < points.size(); i++) {
			xs[i] = ((int)(points.get(i).getX()*magn/2)+size/2);
			ys[i] = ((int)(points.get(i).getY()*-magn/2)+size/2);
			
		}
		g.fillPolygon(xs,ys,points.size());
	}

	public void zoom_in(){
		if(this.magn<=1.1)
			this.magn = this.magn * 1.25;
		else
			this.magn +=0.5;
		
	}
	public void zoom_out(){

		if(this.magn <= 1.1)
			this.magn = this.magn*0.75;
		else
			this.magn -=0.5;

	}
	public City_part find_part(int x, int y) {
		this.current_block = null;
		double x2 = (x - size/2.0)/(magn/2.0);
		double y2 = (y - size/2.0)/(magn/-2.0);
		Node trynode = new Node(x2, y2, Street_type.lot_border,null);
		for (Quarter q: network.quarters){
				if(q.check_if_inside(trynode) == Street_result.not_altered){
					this.current_block = q;
					for(City_part block: q.contained_city_parts){
						if(block.check_if_inside(trynode) == Street_result.not_altered){
							this.current_block = block;
						}
					}
				}
		}
		
		return this.current_block;	
	}
	
	public void draw_city_centers(Graphics g){
		for(Point p : network.citycenters){
			g.setColor(Color.yellow);
			g.fillOval((int)(p.getX()*magn/2)+size/2-3, (int)(-1*p.getY()*magn/2)+size/2-3,10, 10);
		}
	}
	public void draw_growth_centers(Graphics g){
		for(Point p : network.growthcenters){
			g.setColor(Color.green);
			g.fillOval((int)(p.getX()*magn/2)+size/2-3, (int)(-1*p.getY()*magn/2)+size/2-3,10, 10);
		}
	}
	
	public void find_center(int x, int y,boolean growth){
		List<Point> points = network.citycenters;
		if(growth)
			points = network.growthcenters;
		
		this.current_block = null;
		double x2 = (x - size/2.0)/(magn/2.0);
		double y2 = (y - size/2.0)/(magn/-2.0);
		Node trynode = new Node(x2, y2, Street_type.lot_border,null);
		
		Point closest = Point.get_closest(trynode.getPoint(), points);
		if(closest != null && Point.dist(closest, trynode.getPoint()) < 30.0/magn){
			
			points.remove(closest);
		}
		else
			points.add(trynode.getPoint());
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
		this.network = city.network;
	}

}
