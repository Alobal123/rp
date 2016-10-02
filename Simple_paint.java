package krabec.citysimulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

public class Simple_paint implements Serializable{

	int size = 5000;
	int magn = 400; 
	City_part current_part = null;
	City_part current_block =null;
	Street_Network network;
	City city;
	
	public Simple_paint(Street_Network network){
		this.network = network;
	}
	
	public void paint(Graphics g,boolean growth,boolean centers) {
		Random rnd = new Random();
		g.setColor(Color.black);
		g.fillRect(0, 0, size*10, size*10);
		for (int i = 0; i < network.quarters.size(); i++) {
			Quarter q = network.quarters.get(i);
			for (int j = 0; j < q.contained_city_parts.size(); j++) {
				Block b = (Block) q.contained_city_parts.get(j);
				if(b.lut != null && b.built){
					for (int k = 0; k < b.contained_city_parts.size(); k++) {
						color_part(b.contained_city_parts.get(k),g,rnd,true,b.lut.color);
					}
				}
			}
		}
		for (int i = 0; i < network.nodes.size(); i++) {
			Node n = network.nodes.get(i);
			if(n.built){
				draw_node(n, g, Color.white);
			}
			for (int j = 0; j < n.streets.size(); j++) {
				Street s = n.streets.get(j);
				if(s.built){
					draw_street(s, g,null,rnd);
				}
			}
		}
		if(current_block != null){
			color_part(current_block, g, rnd,true, Color.BLUE);
			for (int i = 0; i < current_block.streets.size(); i++) {
				draw_street(current_block.streets.get(i), g, Color.orange, rnd);
			}	
		}
		if(centers)
			draw_city_centers(g);
		if(growth)
			draw_growth_centers(g);
	}
 	
	
	private void color_part(City_part q, Graphics g,Random rnd, Boolean blocks, Color color){

		int [] xs = new int [2*q.streets.size()];
		int [] ys = new int [2*q.streets.size()];	
		ArrayList<Point> points  = q.get_points();
		
		for (int i = 0; i < points.size(); i++) {
			xs[i] = ((int)(points.get(i).x*magn/2)+size/2);
			ys[i] = ((int)(points.get(i).y*-magn/2)+size/2);
			
		}
		if(color == null){
			float r = rnd.nextFloat();
			float g2 = rnd.nextFloat();
			float b = rnd.nextFloat();
		
			Color randomColor = new Color(r, g2, b);
			color = randomColor;
		}
		
	if(q instanceof Block){
		for (int i = 0; i < ((Block)q).lot_borders.size(); i++) {
			
			Node n = ((Block)q).lot_borders.get(i);
			draw_node(n, g, Color.magenta);
			for(Street s: n.streets){
				draw_street(s, g, Color.magenta,rnd);
			}
		}
		}

	g.setColor(color);
	if(q instanceof Lot && ((Lot) q).building != null){
		draw_building(((Lot) q).building, g);
	}
	else if((q instanceof Quarter)){
		g.fillPolygon(xs,ys,points.size());
	}
	}
	
	private void draw_node(Node n, Graphics g, Color color){
		g.setColor(color);
		g.fillOval((int)(n.point.x*magn/2)+size/2-3, (int)(-1*n.point.y*magn/2)+size/2-3,6, 6);
	}
	
	private void draw_street(Street s, Graphics g,Color color,Random rnd){
		
		int x1 = (int)(s.node1.point.x*magn/2) + size/2;
		int y1 = (int)(s.node1.point.y*-magn/2) + size/2;
		int x2 = (int)(s.node2.point.x*magn/2) + size/2;
		int y2 = (int)(s.node2.point.y*-magn/2) + size/2;
		
		if(s.built){
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
	}
	
	private void draw_building(Building building,Graphics g){
		int [] xs = new int [2*building.borders.size()];
		int [] ys = new int [2*building.borders.size()];	
		ArrayList<Point> points  = building.placement;
		
		for (int i = 0; i < points.size(); i++) {
			xs[i] = ((int)(points.get(i).x*magn/2)+size/2);
			ys[i] = ((int)(points.get(i).y*-magn/2)+size/2);
			
		}
		g.fillPolygon(xs,ys,points.size());
	}

	public void zoom_in(){
		this.magn +=50;
		
	}
	public void zoom_out(){
		this.magn -=50;
	}
	public City_part find_part(int x, int y) {
		this.current_part = null;
		this.current_block = null;
		double x2 = (x - size/2.0)/(magn/2.0);
		double y2 = (y - size/2.0)/(magn/-2.0);
		Node trynode = new Node(x2, y2, Street_type.lot_border);
		
		int qr = 0;
		int br = 0;
		int lr = 0;
		Collections.shuffle(network.quarters);
		for (Quarter q: network.quarters){
				if(q.check_if_inside(trynode) == Street_Result.not_altered){
					qr++;
					this.current_block = q;
					for(City_part block: q.contained_city_parts){
						if(block.check_if_inside(trynode) == Street_Result.not_altered){
							br++;
							this.current_block = block;
							for(City_part lot:block.contained_city_parts){
								if(lot.check_if_inside(trynode) == Street_Result.not_altered){
									lr++;
									this.current_part = lot;
								}
									
							}
							
						}
						
					}
				}
		}
		if(current_block != null){
			//System.out.println("Quarters "+qr);
			//System.out.println("BLocks "+br);
			//System.out.println("Lots "+lr);
		}
		return this.current_block;
		
		
	}
	
	public void draw_city_centers(Graphics g){
		for(Point p : network.citycenters){
			g.setColor(Color.yellow);
			g.fillOval((int)(p.x*magn/2)+size/2-3, (int)(-1*p.y*magn/2)+size/2-3,10, 10);
		}
	}
	public void draw_growth_centers(Graphics g){
		for(Point p : network.growthcenters){
			g.setColor(Color.green);
			g.fillOval((int)(p.x*magn/2)+size/2-3, (int)(-1*p.y*magn/2)+size/2-3,10, 10);
		}
	}
	
	public void find_center(int x, int y,boolean growth){
		List<Point> points = network.citycenters;
		if(growth)
			points = network.growthcenters;
		
		this.current_part = null;
		this.current_block = null;
		double x2 = (x - size/2.0)/(magn/2.0);
		double y2 = (y - size/2.0)/(magn/-2.0);
		Node trynode = new Node(x2, y2, Street_type.lot_border);
		
		Point closest = Point.get_closest(trynode.point, points);
		if(closest != null && Point.dist(closest, trynode.point) < 0.1){
			points.remove(closest);
		}
		else
			points.add(trynode.point);
	}
}
