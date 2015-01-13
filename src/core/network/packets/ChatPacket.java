package core.network.packets;

import core.network.Packet;

public class ChatPacket extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;
	
	public ChatPacket(String message) {
		super("Chat");
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return header + ":" + message;
	}

}
