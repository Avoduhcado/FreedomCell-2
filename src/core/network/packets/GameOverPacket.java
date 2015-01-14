package core.network.packets;

import core.network.Packet;

public class GameOverPacket extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String winner;
	
	public GameOverPacket(String winner) {
		super("GameOver");
		this.winner = winner;
	}
	
	public String getWinner() {
		return winner;
	}

}
