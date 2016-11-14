package krabec.citysimulator;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.*;

public class Socket_Writer{

	ServerSocket server_socket;
	Street_Network network;
	
	public Socket_Writer(int portNumber, Street_Network network) throws IOException{
		this.server_socket = new ServerSocket(portNumber);
		this.network = network;
	}
	
	public void Send_city() throws IOException{
		Socket socket = server_socket.accept();
		PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		send_settings(out);
		send_roads(out);
		send_buildings(out);
		send_crossroads(out);
		out.close();
		socket.close();
	}
	
	private void send_settings(PrintWriter out){
		JSONWriter json_out = new JSONWriter(out);
		json_out.array();
		json_out.object();
		json_out.key("StreetWidth");
		json_out.value(network.settings.street_width);
		json_out.endObject();
		json_out.endArray();
		out.println();
		out.flush();
	}
	private void send_roads(PrintWriter out){
		JSONWriter json_out = new JSONWriter(out);
		json_out.array();
		for (int i = 0; i < network.nodes.size(); i++) {
			for (int j = 0; j < network.nodes.get(i).streets.size(); j++) {
				Street s = network.nodes.get(i).streets.get(j);
				if(s.built){
					json_out.object();
					json_out.key("center_x");
					json_out.value((s.node1.point.x + s.node2.point.x)/2);
					json_out.key("center_y");
					json_out.value((s.node1.point.y + s.node2.point.y)/2);
					json_out.key("length");
					json_out.value(s.length);
					json_out.key("angle");
					json_out.value(s.get_absolute_angle(s.node1));
					json_out.endObject();
				}
			}
		}
		json_out.endArray();
		out.println();
		out.flush();
	}
	private void send_buildings(PrintWriter out){
		JSONWriter json_out = new JSONWriter(out);
		json_out = new JSONWriter(out);
		json_out.array();
		for (int i = 0; i < network.quarters.size(); i++) {
			City_part quarter = network.quarters.get(i);
			for (int j = 0; j < quarter.contained_city_parts.size(); j++) {
				Block block = (Block)quarter.contained_city_parts.get(j);
				for (int k = 0; k < block.contained_city_parts.size(); k++) {
					Building building = ((Lot)block.contained_city_parts.get(k)).building;
					if(building != null){
						json_out.object();
						json_out.key("name");
						json_out.value(building.name);
						json_out.key("center_x");
						json_out.value(building.center.x);
						json_out.key("center_y");
						json_out.value(building.center.y);
						json_out.key("frontLength");
						json_out.value(building.get_front_length());
						json_out.key("sideLength");
						json_out.value(building.get_side_length());
						json_out.key("angle");
						json_out.value(building.angle);							
						json_out.key("red");
						json_out.value(block.lut.color.getRed());	
						json_out.key("green");
						json_out.value(block.lut.color.getGreen());	
						json_out.key("blue");
						json_out.value(block.lut.color.getBlue());	
						json_out.endObject();
					}
				}
			}
		}
		json_out.endArray();
		out.println();
		out.flush();
	}
	private void send_crossroads(PrintWriter out){
		JSONWriter json_out = new JSONWriter(out);
		
		json_out = new JSONWriter(out);
		json_out.array();
		for (int i = 0; i < network.nodes.size(); i++) {
			Node node = network.nodes.get(i);
			System.out.println(node.crossroad + " " +node.angle);
			json_out.object();
			json_out.key("x");
			json_out.value(node.point.x);
			json_out.key("y");
			json_out.value(node.point.y);
			json_out.key("angle");
			json_out.value(node.angle);
			json_out.key("angles");
			JSONArray angle_array = new JSONArray(node.crossroad.angles);
			json_out.value(angle_array);	
			json_out.endObject();
		}
		json_out.endArray();
		out.println();
		out.flush();
	}
	
	

	
}

