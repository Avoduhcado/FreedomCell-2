package core.cards;

import java.awt.geom.Point2D;

import core.Camera;
import core.render.SpriteIndex;

public class Foundation extends CardStack {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Foundation() {
		super();
	}
	
	@Override
	public void draw() {
		if(!cards.isEmpty()) {
			if(cards.size() > 1)
				cards.get(cards.size() - 2).draw();
			getTopCard().draw();
				
		} else {
			switch(rotation) {
			case -1:
			case 0:
				SpriteIndex.getSprite(blankSpace).set2DRotation(0f, 0f);
				break;
			case 1:
				SpriteIndex.getSprite(blankSpace).set2DRotation(180f, 0f);
				break;
			case 2:
				SpriteIndex.getSprite(blankSpace).set2DRotation(90f, 0f);
				break;
			case 3:
				SpriteIndex.getSprite(blankSpace).set2DRotation(270f, 0f);
			}
			
			SpriteIndex.getSprite(blankSpace).draw((float) position.getX(), (float) position.getY());
		}
	}
	
	@Override
	public void arrange(int position, int stack) {
		setRotation(position);
		switch(position) {
		case 0:
			setPosition(Camera.get().getDisplayWidth(0.335f) - (((Card.size.width + 2) * 2)
					- ((Card.size.width + 2) * (stack % 2))),
					(stack < 2 ? Camera.get().getDisplayHeight(0.625f) : Camera.get().getDisplayHeight(0.775f)));
			break;
		case 1:
			setPosition(Camera.get().getDisplayWidth(0.665f) + (((Card.size.width + 2) * 2)
					- ((Card.size.width + 2) * (stack % 2))),
					(stack < 2 ? Camera.get().getDisplayHeight(0.375f) : Camera.get().getDisplayHeight(0.225f)));
			break;
		case 2:
			setPosition((stack < 2 ? Camera.get().getDisplayWidth(0.215f) : Camera.get().getDisplayWidth(0.13f)), 
					Camera.get().getDisplayHeight(0.205f) - (((Card.size.width + 2) * 2) - ((Card.size.width + 2) * (stack % 2))));
			break;
		case 3:
			setPosition((stack < 2 ? Camera.get().getDisplayWidth(0.785f) : Camera.get().getDisplayWidth(0.87f)), 
					Camera.get().getDisplayHeight(0.795f) + (((Card.size.width + 2) * 2) - ((Card.size.width + 2) * (stack % 2))));
			break;
		}

		for(Card c : cards) {
			c.setPosition((float) getPosition().getX(), (float) getPosition().getY());
			c.flip(position);
		}
	}
	
	@Override
	public void addCard(Card card) {
		card.setMovement(new Point2D.Double(position.getX(), position.getY()));
		card.flip(rotation);
		
		if(!cards.contains(card))
			cards.add(card);
		
		getTopCard().setStackType(2);
	}
	
	@Override
	public void align(Card card) {
		card.setPosition((float) position.getX(), (float) position.getY());
		card.flip(rotation);
	}
	
}
