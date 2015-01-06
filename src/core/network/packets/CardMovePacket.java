package core.network.packets;

import core.network.Packet;

public class CardMovePacket extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int toMove;
	private int moveTo;
	
	/**
	 * Create a packet that handles card movements.
	 * @param toMove The card stack index to move a card from
	 * @param moveTo The card stack index to move a card to
	 */
	public CardMovePacket(int toMove, int moveTo) {
		super("MoveCard");
		this.toMove = toMove;
		this.moveTo = moveTo;
	}
	
	public int getToMove() {
		return toMove;
	}
	
	public int getMoveTo() {
		return moveTo;
	}
	
	@Override
	public String toString() {
		return header + ":" + toMove + ":" + moveTo;
	}
	
}
