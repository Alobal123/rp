package krabec.citysimulator.network;

import java.io.IOException;
import krabec.citysimulator.City;
public class Socket_Manager{
	
	Socket_Worker worker;
	
	private City city;
	public Socket_Manager(City city) {
		this.city = city;
	}
	public void setCity(City city){
		this.city = city;
	}
	
	public void Send(){
		if(worker != null){
			try {
				worker.server_socket.close();
				worker.interrupt();
				worker = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
			if(worker == null){
				try {
					System.out.println("manager busy");
					worker = new Socket_Worker(city);
					
				} catch (IOException e) {
					System.out.println(e);
					worker.interrupt();

				}
			}		
		}
}
