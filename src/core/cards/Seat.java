package core.cards;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class Seat implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Cascade[] cascades;
	private Foundation[] foundations;
	
	public Seat(List<Card> list) {
		cascades = new Cascade[8];
		for(int x = 0; x<cascades.length; x++)
			cascades[x] = new Cascade();
		
		foundations = new Foundation[4];
		for(int x = 0; x<foundations.length; x++)
			foundations[x] = new Foundation();
		
		Iterator<Card> iter = list.iterator();
		while(iter.hasNext()) {
			for(int x = 0; x<cascades.length; x++) {
				if(!iter.hasNext())
					break;
				cascades[x].addCard(iter.next());
			}
		}
	}
	
	public void update() {
		for(CardStack c : cascades) {
			c.update();
		}
		
		for(CardStack c : foundations) {
			c.update();
		}
	}
	
	public void draw() {
		for(CardStack c : cascades) {
			c.draw();
		}
		
		for(CardStack c : foundations) {
			c.draw();
		}
	}
	
	public boolean canStartNewFoundation(Card card) {
		if(card.getRank() != 0) {
			return false;
		} else {
			for(CardStack c : foundations) {
				if(c.getTopCard() != null && c.getTopCard().getSuit().equals(card.getSuit())) {
					return false;
				}
			}
		}
		
		return true;
	}

	public Cascade[] getCascades() {
		return cascades;
	}

	public void setCascades(Cascade[] cascades) {
		this.cascades = cascades;
	}

	public Foundation[] getFoundations() {
		return foundations;
	}

	public void setFoundations(Foundation[] foundations) {
		this.foundations = foundations;
	}
	
	public CardStack getStack(int index) {
		for(CardStack c : foundations) {
			if(c.getTableIndex() == index)
				return c;
		}
		
		for(CardStack c : cascades) {
			if(c.getTableIndex() == index)
				return c;
		}
		
		return null;
	}
	
	public void setTableIndices(int offset) {
		int index = 4 + (offset * (foundations.length + cascades.length));
		
		for(CardStack c : foundations) {
			c.setTableIndex(index);
			index++;
		}
		
		for(CardStack c : cascades) {
			c.setTableIndex(index);
			index++;
		}
	}
	
}
