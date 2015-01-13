package core.cards;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import core.Theater;
import core.entities.Mobile;
import core.render.DrawUtils;
import core.render.SpriteIndex;

public class Card extends Mobile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Dimension size = new Dimension(60, 99);
	private int rank;
	private Suit suit;
	
	private float rotation = 0f;
	private float rotateSpeed = 0f;
	private float rotateEnd = 0f;
	private float scale = 1f;
	
	private int stackType = 0;
	private int highlight;
	private float fade;
	private boolean fader;
	
	public Card(Suit suit, int rank) {
		this.setRank(rank);
		this.setSuit(suit);
		this.box = new Rectangle2D.Double(0, 0, size.getWidth(), size.getHeight());
		this.sprite = suit.toString() + rank;
		this.speed = 15f;
		try {
			if(Display.isCurrent()) {
				SpriteIndex.getSprite(sprite);
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update() {
		if(fade != -1f) {
			if(fader) {
				fade -= Theater.getDeltaSpeed(0.025f);
				if(fade <= 0.3f) {
					fade = 0.3f;
					fader = false;
				}
			} else {
				fade += Theater.getDeltaSpeed(0.025f);
				if(fade >= 0.7f) {
					fade = 0.7f;
					fader = true;
				}
			}
		}
		
		/*if(rotateSpeed != 0) {
			rotate();
		}*/
	}
	
	@Override
	public void draw() {
		SpriteIndex.getSprite(sprite).setFixedSize(size.width, size.height);
		SpriteIndex.getSprite(sprite).set2DRotation(rotation, 0f);
		SpriteIndex.getSprite(sprite).set2DScale(scale);
		SpriteIndex.getSprite(sprite).draw(x, y);
		
		switch(highlight) {
		case 1:
			// White - Match
			DrawUtils.fillRect(0.9f, 0.9f, 0.9f, fade, box);
			break;
		case 2:
			// Green - Can place on - red 9 on black 10 - 3 of Clubs on 2 of Clubs
			DrawUtils.fillRect(0.2f, 0.9f, 0.5f, fade, box);
			break;
		case 3:
			// Blue - Can be placed on - black 10 supports red 9 - 2 of clubs supports 3 of clubs
			DrawUtils.fillRect(0.2f, 0.5f, 0.9f, fade, box);
			break;
		}
		
		if(Theater.get().debug) {
			DrawUtils.setColor(new Vector3f(1f, 0f, 1f));
			DrawUtils.drawRect((float)box.getX(), (float)box.getY(), box);
		}
	}
	
	@Override
	public void updateBox() {
		box = new Rectangle2D.Double(x, y, box.getWidth(), box.getHeight());
		if(rotation != 0) {
			//AffineTransform at = AffineTransform.getTranslateInstance(x, y);
			//at.rotate(Math.toRadians(rotation), box.getX(), box.getY());
			AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(rotation), box.getX(), box.getY());
		
			this.box = at.createTransformedShape(box).getBounds2D();
		}
	}
	
	public void flip(int rotation) {
		switch(rotation) {
		case -1:
		case 0:
			scale = 1f;
			this.rotation = 0f;
			break;
		case 1:
			scale = 1f;
			this.rotation = 180f;
			break;
		case 2:
			scale = 1f;
			this.rotation = 90f;
			break;
		case 3:
			scale = 1f;
			this.rotation = 270f;
			break;
		}
		this.rotateSpeed = 0f;
		
		updateBox();
	}
	
	public float getRotation() {
		return rotation;
	}
	
	public void setRotation(int rotation) {
		switch(rotation) {
		case -1:
		case 0:
			this.rotateEnd = 0f;
			break;
		case 1:
			this.rotateEnd = 180f;
			break;
		case 2:
			this.rotateEnd = 90f;
			break;
		case 3:
			this.rotateEnd = 270f;
			break;
		}
		
		this.rotateSpeed = 4.5f;
	}
	
	public void rotate() {
		if(rotation != rotateEnd) {
			rotation += Theater.getDeltaSpeed(rotateSpeed);
			if(rotateSpeed > 0 && rotation >= rotateEnd) {
				rotateSpeed = 0f;
			} else if(rotateSpeed < 0 && rotation <= rotateEnd) {
				rotateSpeed = 0f;
			}
		}
	}
	
	public boolean matches(Card card) {
		return this.rank == card.rank && this.suit.equals(card.suit);
	}
	
	/**
	 * Determine if this card can be placed on top of another card.
	 * @param card to be placed on
	 * @param isFoundation true if the card is part of a foundation
	 * @return True if this card can be placed on card
	 */
	public boolean canBePlacedOn(Card card, boolean isFoundation) {
		if(isFoundation) {
			return this.rank == card.rank + 1 && this.suit.equals(card.getSuit());
		} else {
			return this.rank == card.rank - 1 && this.suit.areOppositeSuits(card.suit);
		}
	}

	public void sortHighlight(Card card) {
		if(card == null) {
			// No card selected, disable highlighting
			setHighlight(0);
			return;
		}
		
		if(this.matches(card)) {
			// Other occurrences of the same card
			setHighlight(1);
		} else if(this.getStackType() != 2 && card.getStackType() != 0
				&& (((this.getStackType() == 1 || this.getStackType() == 0) && this.canBePlacedOn(card, false) && card.getStackType() != 2)
				|| (card.getStackType() == 2 && this.canBePlacedOn(card, true)))) {
			// This card can be placed on top of the selected card
			setHighlight(2);
		} else if((this.getStackType() != 0 && card.getStackType() != 2) 
				&& ((this.getStackType() != 2 && card.canBePlacedOn(this, false)) 
						|| (this.getStackType() == 2 && card.canBePlacedOn(this, true)))) {
			// The selected card can be placed on top of this card
			setHighlight(3);
		} else {
			// No relation, disable highlight
			setHighlight(0);
		}
	}
	
	@Override
	public String toString() {
		switch(rank) {
		case(0):
			return "Ace of " + suit;
		case(10):
			return "Jack of " + suit;
		case(11):
			return "Queen of " + suit;
		case(12):
			return "King of " + suit;
		default:
			return (rank + 1) + " of " + suit;
		}
	}
	
	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public Suit getSuit() {
		return suit;
	}

	public void setSuit(Suit suit) {
		this.suit = suit;
	}
	
	public float getWidth() {
		return size.width;
	}
	
	public float getHeight() {
		return size.height;
	}
	
	public int getStackType() {
		return stackType;
	}
	
	public void setStackType(int stackType) {
		this.stackType = stackType;
	}
	
	public int getHighlight() {
		return highlight;
	}
	
	public void setHighlight(int highlight) {
		if(this.highlight != highlight) {
			this.highlight = highlight;
			if(highlight != 0) {
				fade = 0.7f;
				fader = true;
			} else
				fade = -1f;
		}
	}
	
}
