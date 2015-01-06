package core.network.packets;

import core.network.Packet;

public class ConnectedUsersPacket extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int seat;
	private String[] clientNames;

	public ConnectedUsersPacket(int seat, String[] clientNames) {
		super("ConnectedUsers");
		this.seat = seat;
		this.clientNames = clientNames;
	}

	public int getSeat() {
		return seat;
	}

	public String[] getClientNames() {
		return clientNames;
	}
	
	@Override
	public String toString() {
		String names = clientNames[0];
		for(int x = 1; x<clientNames.length; x++) 
			names += ", " + clientNames[x];
		return header + ":" + seat + ":{" + names + "}"; 
	}

}
