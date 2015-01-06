package core.network;

import java.io.Serializable;

public class Packet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String header;
	
	public Packet(String header) {
		this.header = header;
	}
	
	@Override
	public String toString() {
		return header;
	}
	
}
