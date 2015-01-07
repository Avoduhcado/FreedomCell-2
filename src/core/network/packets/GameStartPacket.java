package core.network.packets;

import core.network.Packet;

public class GameStartPacket extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GameStartPacket() {
		super("StartGame");
	}

}
