package core.network.packets;

import core.network.Packet;

public class CloseServerPacket extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CloseServerPacket() {
		super("CloseServer");
	}

}
