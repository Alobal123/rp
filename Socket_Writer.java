package krabec.citysimulator.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;

import org.json.*;

import krabec.citysimulator.Block;
import krabec.citysimulator.Building;
import krabec.citysimulator.City_part;
import krabec.citysimulator.Lot;
import krabec.citysimulator.Node;
import krabec.citysimulator.Street;
import krabec.citysimulator.Street_Network;

public class Socket_Writer{

	private Street_Network network;
	
	public Socket_Writer(Street_Network network) throws IOException{
		this.setNetwork(network);
	}
	
	public void Send_city(ServerSocket server_socket) throws IOException{
		System.out.println("sending");
		try{
			Socket socket = server_socket.accept();
			System.out.println("accepted");
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			send_settings(out);
			send_roads(out);
			send_buildings(out);
			send_crossroads(out);
			out.close();
			socket.close();
		}
		catch(SocketException e){
			System.out.println("Interupted and not sent");
		}
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
		HashSet<Street> already_sent = new HashSet<>();
		JSONWriter json_out = new JSONWriter(out);
		json_out.array();
		for (int i = 0; i < getNetwork().nodes.size(); i++) {
			for (int j = 0; j < getNetwork().nodes.get(i).streets.size(); j++) {
				Street s = getNetwork().nodes.get(i).streets.get(j);
				if(!already_sent.contains(s) && s.built){
					json_out.object();
					json_out.key("center_x");
					json_out.value((s.node1.getPoint().getX() + s.node2.getPoint().getX())/2);
					json_out.key("center_y");
					json_out.value((s.node1.getPoint().getY() + s.node2.getPoint().getY())/2);
					json_out.key("length");
					json_out.value(s.length);
					json_out.key("angle");
					json_out.value(s.get_absolute_angle(s.node1));
					json_out.endObject();
					already_sent.add(s);
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
			if(node.isBuilt()){
				json_out.object();
				json_out.key("x");
				json_out.value(node.getPoint().getX());
				json_out.key("y");
				json_out.value(node.getPoint().getY());
				json_out.key("angle");
				json_out.value(node.angle);
				json_out.key("angles");
				JSONArray angle_array = new JSONArray(node.crossroad.angles);
				json_out.value(angle_array);	
				json_out.endObject();
			}
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

