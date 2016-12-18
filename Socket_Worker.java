package krabec.citysimulator.network;

import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.SwingWorker;

import krabec.citysimulator.City;

public class Socket_Worker extends Thread {

	Socket_Writer writer;
	ServerSocket server_socket;
	
	public Socket_Worker(City city) throws IOException{
		System.out.println("worker is being created");
		this.server_socket = new ServerSocket(8787);
		this.writer = new Socket_Writer(city.network);
		this.start();
	}
	
	@Override
	public void run(){
		System.out.println("worker is working");
		try {
			writer.Send_city(server_socket);
			server_socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
