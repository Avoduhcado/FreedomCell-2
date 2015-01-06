package core.cards;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Deck {

	public static final int size = 52;
	private Card[] cards = new Card[size];
	
	public Deck() {
		int i = 0;
		for(int x = 0; x<4; x++) {
			for(int y = 0; y<13; y++) {
				cards[i] = new Card(Suit.values()[x], y);
				i++;
			}
		}
	}
	
	public Deck(int totalDecks) {
		cards = new Card[size * totalDecks];
		
		int i = 0;
		for(int z = 0; z<totalDecks; z++) {
			for(int x = 0; x<4; x++) {
				for(int y = 0; y<13; y++) {
					cards[i] = new Card(Suit.values()[x], y);
					i++;
				}
			}
		}
	}
	
	public void shuffle(long seed) {
		Card[] newCards = new Card[cards.length];
		Random gen = new Random(seed);
		int randomCard;
		List<Integer> usedInts = new LinkedList<Integer>();
		int x = 0;
		
		while(x < cards.length) {
			randomCard = (int)(gen.nextFloat() * cards.length);
			if(!usedInts.contains(randomCard)) {
				usedInts.add(randomCard);
				newCards[x] = cards[randomCard];
				x++;
			}
		}
		
		cards = newCards;
	}
	
	public Card[] getCards() {
		return cards;
	}
	
	public List<Card> getCards(int start, int end) {
		List<Card> subCards = new LinkedList<Card>();
		for(int x = start; x<end; x++) {
			subCards.add(cards[x]);
		}
		
		return subCards;
	}
	
}
