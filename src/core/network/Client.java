package core.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import core.network.packets.CardMovePacket;
import core.network.packets.ConnectedUsersPacket;
import core.network.packets.GreetPacket;
import core.setups.Stage;

public class Client extends Thread {

	private String hostName = "127.0.0.1";
	private int portNumber = 34336;
	private String clientName = "Client";
	private String[] playerNames = new String[]{" ", " ", " ", " "};
	private int seatNumber = -1;
	private Socket socket = null;
	private volatile boolean listening = true;
	private boolean connected = false;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private volatile Stage stage;
		
	public Client(String hostName, int port, String name, Stage stage) {
		this.hostName = hostName;
		this.portNumber = port;
		this.clientName = name;
		this.stage = stage;
	}
	
	public void run() {
		try {
			socket = new Socket(hostName, portNumber);
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			
			in = new ObjectInputStream(socket.getInputStream());

			listening = true;
			connected = true;
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			listening = false;
		} catch (IOException e) {
			System.out.println("Couldn't connect to host, trying localhost");
			try {
				socket = new Socket("localhost", portNumber);
				out = new ObjectOutputStream(socket.getOutputStream());
				out.flush();
				
				in = new ObjectInputStream(socket.getInputStream());
				
				listening = true;
				connected = true;
			} catch (UnknownHostException e2) {
				System.err.println("Don't know about host " + hostName);
				listening = false;
			} catch (IOException e2) {
				System.out.println("Couldn't get I/O for the connection to ..." + hostName);
				listening = false;
			}
		}
		
		// Send client name to server to greet
		sendData(new GreetPacket(clientName));
		
		receiveMessage();
	}
	
	public void terminate() {
		listening = false;
	}
	
	/**
	 * Listen for new messages from the server and then display any updates to client
	 */
	public void receiveMessage() {
		while(listening) {
			try {
				Packet fromServer;
	
				if((fromServer = (Packet) in.readObject()) != null) {
					System.out.println("From Server: " + fromServer.toString() + " " + fromServer.getClass());
					parseData(fromServer);
				}
			} catch (SocketException e) {
				listening = false;
				break;
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to " + hostName);
				e.printStackTrace();
				System.exit(1);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		if(!listening && !socket.isClosed()) {
			connected = false;
			try {
				socket.close();
			} catch(SocketException e) {
				System.err.println("Client " + clientName + " has disconnected unexpectedly!");
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Send message data to server for processing.
	 * 
	 * @param message
	 */
	public void sendData(Packet packet) {
		try {
			out.writeObject(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// TODO Add custom name colors
	public void sendChat(String message) {
		try {
			out.writeObject(clientName + ": " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void parseData(Packet packet) {
		if(packet instanceof CardMovePacket) {
			stage.getTable().moveCard(
					((CardMovePacket) packet).getToMove(), ((CardMovePacket) packet).getMoveTo());
		} else if(packet instanceof ConnectedUsersPacket) {
			if(this.seatNumber == -1) {
				this.seatNumber = ((ConnectedUsersPacket) packet).getSeat();
				stage.setSeat(seatNumber);
			}
			setPlayerNames(((ConnectedUsersPacket) packet).getClientNames());
		} else if(packet instanceof GreetPacket) {
			stage.setTable(((GreetPacket) packet).getTable());
		}
	}
	
	public void processChat() {
		/*if(textField.getText().startsWith("/name ") && textField.getText().trim().length() > 5) {
			String update = clientName;
			clientName = textField.getText().substring(textField.getText().indexOf(' ') + 1, textField.getText().length());
			textField.setText("");
			textField.requestFocus();
			sendUpdate(update + " is now known as: " + clientName);
		} else {
			sendChat(textField.getText());
			textField.setText("");
			textField.requestFocus();
		}*/
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public int getPortNumber() {
		return portNumber;
	}
	
	public void setPortNumber(int port) {
		this.portNumber = port;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public String getPlayerName(int x) {
		return playerNames[x];
	}
	
	public void setPlayerNames(String[] names) {
		for(int x = 0; x<playerNames.length; x++) {
			if(names[x] != null)
				playerNames[x] = names[x];
		}
	}
	
	public boolean isConnected() {
		return connected;
	}
	
}
