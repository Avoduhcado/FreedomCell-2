package core.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import core.utilities.Config;

public class Server extends Thread {
	
	private int portNumber = 34336;
	private volatile boolean listening;
	private ServerChat serverChat;
	private ServerSocket serverSocket;
	
	public Server(int portNumber) {
		this.portNumber = portNumber;
	}
	
	public void run() {
		listen();
	}
	
	public void terminate() {
		closeSocket();
		
		listening = false;
	}
	
	private void closeSocket() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void listen() {
		listening = true;
		serverChat = new ServerChat();
		serverChat.start();
		
		System.out.println("Server started.\nNow listening on port: " + portNumber);
				
		while (listening) {
			try {
				serverSocket = new ServerSocket(portNumber);
				serverSocket.setSoTimeout(5000);
				serverChat.addClient(serverSocket.accept());
				serverSocket.close();
			} catch (SocketTimeoutException e) {
				//System.out.println("Socket timed out.");
				closeSocket();
				if(!serverChat.isOpen()) {
					listening = false;
				}
			} catch (SocketException e) {
				System.out.println("Socket error, server closing.");
				listening = false;
			} catch (IOException e) {
				System.err.println("Exception while listening for connections.\nCould not listen on port: " + portNumber);
				System.exit(-1);
			}
		}
		
		System.out.println("Server closed.");
	}
	
	public static void main(String[] args) {
		Server server = new Server(args.length == 0 ? Config.hostPort : Integer.parseInt(args[0]));
		server.listen();
	}
}
