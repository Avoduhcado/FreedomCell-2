package core.network.packets;

import core.network.Packet;

public class DisconnectClientPacket extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DisconnectClientPacket() {
		super("DisconnectClient");
	}

}
