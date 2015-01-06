package core.network;

import java.io.IOException;
import java.net.Socket;

import core.cards.Table;
import core.network.packets.ConnectedUsersPacket;

public class ServerChat extends Thread {

	private SocketThread[] clients = new SocketThread[4];
	private Table table;
	private boolean open;
	
	public void run() {
		// TODO Get total seats from login lobby
		table = new Table(4);
		open = true;
	}
	
	public void broadcast(Packet packet, SocketThread client) {
		for(int x = 0; x<clients.length; x++) {
			if(clients[x] != client && clients[x] != null) {
				try {
					clients[x].getOutputStream().writeObject(packet);
					clients[x].getOutputStream().flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void broadcastTo(Packet packet, SocketThread client) {
		for(int x = 0; x<clients.length; x++) {
			if(clients[x] == client) {
				try {
					clients[x].getOutputStream().writeObject(packet);
					clients[x].getOutputStream().flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void closeServer() {
		removeAllClients();
		open = false;
	}
	
	public String sortName(int client) {
		switch(client) {
		case(0):
			return "Greaser";
		case(1):
			return "Cardshark";
		case(2):
			return "Patron";
		case(3):
			return "Bill";
		}
		return "Client";
	}

	public boolean hasFreeClient() {
		for(int x = 0; x<clients.length; x++) {
			if(clients[x] == null)
				return true;
		}
			
		return false;
	}
	
	public void addClient(Socket socket) {
		if(hasFreeClient()) {
			for(int x = 0; x<clients.length; x++) {
				if(clients[x] == null) {
					clients[x] = new SocketThread(socket, this, x);
					clients[x].start();
					break;
				}
			}
		} else {
			System.out.println("Excess client failed to connect.");
		}
	}
	
	public void removeClient(SocketThread socketThread) {
		System.out.println("Server: " + socketThread.getClientName() + " has disconnected.");
		for(int x = 0; x<clients.length; x++) {
			if(clients[x] == socketThread) {
				clients[x].terminate();
				clients[x] = null;
				break;
			}
		}
		
		broadcast(new ConnectedUsersPacket(0, getClientNames()), null);
	}
	
	public void removeAllClients() {
		for(int x = 0; x<clients.length; x++) {
			if(clients[x] != null) {
				clients[x].terminate();
				clients[x] = null;
			}
		}
	}
	
	public String[] getClientNames() {
		String[] temp = new String[clients.length];
		for(int x = 0; x<clients.length; x++) {
			if(clients[x] != null) {
				temp[x] = clients[x].getClientName();
			}
		}
		
		return temp;
	}
	
	public Table getTable() {
		return table;
	}
	
	public boolean isOpen() {
		return open;
	}
	
}
