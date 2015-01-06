package core.network.packets;

import core.cards.Table;
import core.network.Packet;

public class GreetPacket extends Packet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String clientName;
	private Table table;
	
	public GreetPacket(String clientName) {
		super("ClientGreeting");
		this.clientName = clientName;
	}
	
	public GreetPacket(Table table) {
		super("ServerGreeting");
		this.table = table;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public Table getTable() {
		return table;
	}

	@Override
	public String toString() {
		return header + ":" + (clientName != null ? clientName : table);
	}
	
}
