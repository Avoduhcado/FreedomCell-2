package core.cards;

public class Cascade extends CardStack {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Cascade() {
		super();
	}
	
	@Override
	public void addCard(Card card) {
		super.addCard(card);
		getTopCard().setStackType(1);
	}
}
