package krabec.citysimulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Simple_paint {

	public static void paint(Street_Network network)
	{
		Random rnd = new Random();
		Image bg = new BufferedImage(800,800,1);
		Graphics g = bg.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 800);
		for (Quarter q: network.quarters){
		
			int [] xs = new int [2*q.main_streets.size()];
			int [] ys = new int [2*q.main_streets.size()];
			
			ArrayList<Point> points = new ArrayList<>();
			Street prev = null;
			for (Street s: q.main_streets) {
				if(points.size() == 0){
					
					points.add(s.node2.point);
					points.add(s.node1.point);
					prev = s;
				}
				else{
					if(s.node1 == prev.node1 || s.node1 == prev.node2){
						if(s.node2.streets.size()>1){
							points.add(s.node2.point);
							prev = s;
						}
					}
					else{
						if(s.node2.streets.size()>1){
							points.add(s.node1.point);
							prev = s;
						}
					}	
				}
			}
			
			for (int i = 0; i < points.size(); i++) {
				xs[i] = ((int)(points.get(i).x*200)+400);
				ys[i] = ((int)(points.get(i).y*-200)+400);
				
			}
			g.setColor(Color.BLUE);
			g.fillPolygon(xs,ys,points.size());
			
			
			
		}
		
		for(Node n: network.nodes){
			g.setColor(Color.white);
			//System.out.println(n);
			//System.out.println( n.compute_angle());
			g.fillOval((int)(n.point.x*200)+400-3, (int)(-1*n.point.y*200)+400-3,6, 6);
			for(Street s : n.streets){
				float r = rnd.nextFloat();
				float gr = rnd.nextFloat();
				float b = rnd.nextFloat();
				Color randomColor = new Color(r, gr, b);
				if(s.major)
					g.setColor(Color.red);
				else
					g.setColor(Color.white);
				int x1 = (int)(s.node1.point.x*200) + 400;
				int y1 = (int)(s.node1.point.y*-200) + 400;
				int x2 = (int)(s.node2.point.x*200) + 400;
				int y2 = (int)(s.node2.point.y*-200) + 400;
					
				g.drawLine(x1, y1, x2, y2);
				
			}
		}
		
		
		
		try {
			ImageIO.write((RenderedImage) bg, "jpg", new File("C:\\Users\\mirek\\Pictures\\network.bmp"));
		} catch (IOException e) {
			System.exit(1);
			
		}
	}
	
	
}
