package krabec.citysimulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
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
		
		System.out.println("Nodes:");
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
					g.setColor(randomColor);
					int x1 = (int)(s.node1.point.x*200) + 400;
					int y1 = (int)(-1*s.node1.point.y*200) + 400;
					int x2 = (int)(s.node2.point.x*200) + 400;
					int y2 = (int)(-1*s.node2.point.y*200) + 400;
					
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
