package core.cards;

import java.io.Serializable;

import core.Camera;

public class Table implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CardStack[] cells = new CardStack[4];
	private Seat[] seats;
	
	public Table(int totalSeats, long seed) {
		for(int i = 0; i<cells.length; i++) {
			cells[i] = new CardStack(Camera.get().getDisplayWidth(0.5f) - ((Card.size.width * (cells.length / 2)) - (Card.size.width * i)),
					Camera.get().getDisplayHeight(0.425f));
			cells[i].setTableIndex(i);
		}
		
		this.seats = new Seat[totalSeats];
				
		dealCards(seed);
	}
	
	public void dealCards(long seed) {
		Deck deck = new Deck(seats.length);
		deck.shuffle(seed);
		
		for(int x = 0; x<seats.length; x++) {
			seats[x] = new Seat(deck.getCards(x * Deck.size, Deck.size + (x * Deck.size)));
			seats[x].setTableIndices(x);
		}
	}
	
	public void arrangeSeats(int seat) {
		for(int x = 0; x<seats.length; x++) {
			for(int c = 0; c<seats[x].getCascades().length; c++) {
				seats[x].getCascades()[c].arrange((x % seats.length) - seat < 0 ?
						((x % seats.length) - seat) + seats.length : (x % seats.length) - seat, c);
			}
			
			for(int f = 0; f<seats[x].getFoundations().length; f++) {
				seats[x].getFoundations()[f].arrange((x % seats.length) - seat < 0 ?
						((x % seats.length) - seat) + seats.length : (x % seats.length) - seat, f);
			}
		}
	}
	
	public void update() {
		for(CardStack c : cells) {
			c.update();
		}
		
		for(Seat s : seats) {
			s.update();
		}
	}
	
	public void draw() {
		for(CardStack c : cells) {
			c.draw();
		}
		
		for(Seat s : seats) {
			s.draw();
		}
		
		for(CardStack c : cells) {
			if(c.getTopCard() != null && c.getTopCard().getDistance() != 0) {
				c.draw();
			}
		}
		
		// Moving cards, this is bad but I don't care
		for(Seat s : seats) {
			for(Foundation f : s.getFoundations()) {
				if(f.getTopCard() != null && f.getTopCard().getDistance() != 0) {
					f.getTopCard().draw();
				}
			}
			for(Cascade c : s.getCascades()) {
				if(c.getTopCard() != null && c.getTopCard().getDistance() != 0) {
					c.getTopCard().draw();
				}
			}
		}
	}
	
	public void moveCard(int toMove, int moveTo) {
		getStack(moveTo).addCard(getStack(toMove).removeTopCard());
		
		// Adjust card orientation if need be
		/*if(getStack(moveTo).getRotation() != getStack(toMove).getRotation()) {
			// TODO Fancy shmancy juice rotations
			getStack(moveTo).getTopCard().flip(getStack(moveTo).getRotation());
			getStack(moveTo).getTopCard().updateBox();
		}*/
	}
	
	public CardStack removeCard(Card card) {
		for(CardStack c : cells) {
			if(c.getTopCard() != null && c.getTopCard().equals(card)) {
				c.removeTopCard();
				return c;
			}
		}
		
		for(Seat s : seats) {
			for(CardStack c : s.getCascades()) {
				if(c.getTopCard() != null && c.getTopCard().equals(card)) {
					c.removeTopCard();
					return c;
				}
			}
		}
		
		return null;
	}
	
	public boolean checkWin() {
		for(int s = 0; s<seats.length; s++) {
			if(seats[s].hasFullFoundations()) {
				return true;
			}
		}
		
		return false;
	}
	
	public void processHighlights() {
		for(CardStack c : cells) {
			if(c.getTopCard() != null && c.getTopCard().isHovering()) {
				c.getTopCard().setHighlight(1);
				setupHighlights(c.getTopCard());
				return;
			}
		}
		
		for(Seat s : seats) {
			for(CardStack c : s.getCascades()) {
				if(c.getTopCard() != null) {
					for(int i = c.getCards().size() - 1; i>=0; i--) {
						if(c.getCards().get(i).isHovering()) {
							c.getCards().get(i).setHighlight(1);
							setupHighlights(c.getCards().get(i));
							return;
						}
					}
				}
			}
			
			for(CardStack c : s.getFoundations()) {
				if(c.getTopCard() != null && c.getTopCard().isHovering()) {
					c.getTopCard().setHighlight(1);
					setupHighlights(c.getTopCard());
					return;
				}
			}
		}
		
		setupHighlights(null);
	}
	
	public void setupHighlights(Card card) {
		for(CardStack c : cells) {
			if(c.getTopCard() != null) {
				c.getTopCard().sortHighlight(card);
			}
		}
		
		for(Seat s : seats) {
			for(CardStack cs : s.getCascades()) {
				if(cs.getTopCard() != null) {
					for(Card c : cs.getCards()) {
						c.sortHighlight(card);
					}
				}
			}
			
			for(CardStack c : s.getFoundations()) {
				if(c.getTopCard() != null) {
					c.getTopCard().sortHighlight(card);
				}
			}
		}
	}
	
	public CardStack getStack(int index) {
		// Index is one of the cells
		if(index < 4) {
			return cells[index];
		} else {
			// index - cells / total stacks per seat
			return seats[(index - 4) / 12].getStack(index);
		}
	}
	
	public CardStack[] getCells() {
		return cells;
	}
	
	public CardStack getCell(int cell) {
		return cells[cell];
	}
	
	public Seat[] getSeats() {
		return seats;
	}
	
}
