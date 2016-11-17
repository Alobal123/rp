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
	private Street_Network network;
	
	public Socket_Writer(int portNumber, Street_Network network) throws IOException{
		this.server_socket = new ServerSocket(portNumber);
		this.setNetwork(network);
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
		json_out.value(getNetwork().settings.street_width);
		json_out.endObject();
		json_out.endArray();
		out.println();
		out.flush();
	}
	private void send_roads(PrintWriter out){
		JSONWriter json_out = new JSONWriter(out);
		json_out.array();
		for (int i = 0; i < getNetwork().nodes.size(); i++) {
			for (int j = 0; j < getNetwork().nodes.get(i).streets.size(); j++) {
				Street s = getNetwork().nodes.get(i).streets.get(j);
				if(s.built){
					json_out.object();
					json_out.key("center_x");
					json_out.value((s.node1.point.getX() + s.node2.point.getX())/2);
					json_out.key("center_y");
					json_out.value((s.node1.point.getY() + s.node2.point.getY())/2);
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
		for (int i = 0; i < getNetwork().quarters.size(); i++) {
			City_part quarter = getNetwork().quarters.get(i);
			for (int j = 0; j < quarter.contained_city_parts.size(); j++) {
				Block block = (Block)quarter.contained_city_parts.get(j);
				for (int k = 0; k < block.contained_city_parts.size(); k++) {
					Building building = ((Lot)block.contained_city_parts.get(k)).building;
					if(building != null){
						json_out.object();
						json_out.key("name");
						json_out.value(building.getName());
						json_out.key("center_x");
						json_out.value(building.center.getX());
						json_out.key("center_y");
						json_out.value(building.center.getY());
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
		for (int i = 0; i < getNetwork().nodes.size(); i++) {
			Node node = getNetwork().nodes.get(i);
			json_out.object();
			json_out.key("x");
			json_out.value(node.point.getX());
			json_out.key("y");
			json_out.value(node.point.getY());
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

	public Street_Network getNetwork() {
		return network;
	}

	public void setNetwork(Street_Network network) {
		this.network = network;
	}
	
	

	
}

