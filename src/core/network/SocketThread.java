package core.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import core.network.packets.CardMovePacket;
import core.network.packets.ChatPacket;
import core.network.packets.CloseServerPacket;
import core.network.packets.ConnectedUsersPacket;
import core.network.packets.DisconnectClientPacket;
import core.network.packets.GameStartPacket;
import core.network.packets.GreetPacket;

public class SocketThread extends Thread {

	private ServerChat server;
	private Socket socket;
	private String clientName = "Client";
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private volatile boolean listening;
	private int clientNumber;
	
	public SocketThread(Socket socket, ServerChat server, int number) {
		this.socket = socket;
		this.server = server;
		this.clientNumber = number;
		
		try {
			out = new ObjectOutputStream(this.socket.getOutputStream());
			out.flush();
			
			in = new ObjectInputStream(this.socket.getInputStream());
			listening = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		Object input;
		
		while(listening) {			
			try {
				// Read object from stream
				input = in.readObject();
				
				// If object exists, process update
				if(input != null) {
					System.out.println(input.toString());
					readPacket((Packet) input);
				}
			} catch (SocketException e) {
				listening = false;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(25);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		server.removeClient(this);
	}
	
	public void terminate() {
		listening = false;
	}
	
	public void readPacket(Packet packet) {
		if(packet instanceof GreetPacket) {
			setClientName(((GreetPacket) packet).getClientName().matches("Client") ?
					server.sortName(clientNumber) : ((GreetPacket) packet).getClientName());
			System.out.println("Server: " + getClientName() + " has connected.");
			server.broadcast(new ConnectedUsersPacket(clientNumber, server.getClientNames()), null);
		} else if(packet instanceof GameStartPacket) {
			server.startGame(((GameStartPacket) packet).getSeed());
		} else if(packet instanceof CardMovePacket) {
			server.getTable().getStack(((CardMovePacket) packet).getMoveTo()).addCard(
					server.getTable().getStack(((CardMovePacket) packet).getToMove()).getCards().removeLast());
			server.broadcast(packet, this);
		} else if(packet instanceof ChatPacket) {
			server.broadcast(new ChatPacket("corange>" + clientName + "<cwhite>: " + ((ChatPacket) packet).getMessage()), null);
		} else if(packet instanceof DisconnectClientPacket) {
			terminate();
		} else if(packet instanceof CloseServerPacket) {
			server.closeServer();
		}
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public void setClientName(String name) {
		this.clientName = name;
	}
	
	public ObjectOutputStream getOutputStream() {
		return out;
	}
	
	public ObjectInputStream getInputStream() {
		return in;
	}
	
	public int getClientNumber() {
		return clientNumber;
	}
	
	public void setClientNumber(int number) {
		this.clientNumber = number;
	}
	
}
