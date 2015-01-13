package core.network.packets;

import core.network.Packet;

public class GameStartPacket extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long seed;
	
	public GameStartPacket(long seed) {
		super("StartGame");
		this.seed = seed;
	}
	
	public long getSeed() {
		return seed;
	}

}
