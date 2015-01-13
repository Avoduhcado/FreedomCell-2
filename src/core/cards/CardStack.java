package core.cards;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.LinkedList;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import core.Camera;
import core.render.SpriteIndex;

public class CardStack implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String blankSpace = "Blank0";
	protected LinkedList<Card> cards;
	protected Point2D position;
	protected int tableIndex;
	protected int rotation = -1;
	protected float compression = 5f;
	
	public CardStack() {
		this.cards = new LinkedList<Card>();
		position = new Point2D.Float();
		try {
			if(Display.isCurrent())
				SpriteIndex.getSprite(blankSpace);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public CardStack(float x, float y) {
		this.cards = new LinkedList<Card>();
		position = new Point2D.Float(x, y);
		try {
			if(Display.isCurrent())
				SpriteIndex.getSprite(blankSpace);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		for(Card c : cards) {
			c.update();
			if(c.getDistance() > 0) {
				c.move();
				if(c.getDistance() <= 0) {
					align(c);
				}
			}
		}
	}
	
	public void draw() {
		if(!cards.isEmpty()) {
			for(Card c : cards) {
				c.draw();
			}
		} else {
			// TODO Potentially add a blank card to utilize drawing foundation moves and keep consistent with movements
			SpriteIndex.getSprite(blankSpace).setFixedSize(Card.size.width, Card.size.height);
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
	
	public void draw(float x, float y) {
		if(!cards.isEmpty()) {
			for(Card c : cards) {
				c.draw();
			}
		} else {
			SpriteIndex.getSprite(blankSpace).draw(x, y);
		}
	}
	
	public void arrange(int position, int stack) {
		setRotation(position);
		switch(position) {
		case 0:
			setPosition(Camera.get().getDisplayWidth(0.55f) - (((Card.size.width + 2) * 4) - ((Card.size.width + 2) * stack)),
					Camera.get().getDisplayHeight(0.625f));
			for(int c = 0; c<cards.size(); c++) {
				cards.get(c).setPosition((float) this.position.getX(), (float) this.position.getY() + ((Card.size.height / 5f) * c));
				cards.get(c).flip(position);
			}
			break;
		case 1:
			setPosition(Camera.get().getDisplayWidth(0.45f) + (((Card.size.width + 2) * 4) - ((Card.size.width + 2) * stack)),
					Camera.get().getDisplayHeight(0.375f));
			for(int c = 0; c<cards.size(); c++) {
				cards.get(c).setPosition((float) this.position.getX(), (float) this.position.getY() - ((Card.size.height / 5f) * c));
				cards.get(c).flip(position);
			}
			break;
		case 2:
			setPosition(Camera.get().getDisplayWidth(0.215f), 
					Camera.get().getDisplayHeight(0.575f) - (((Card.size.width + 2) * 4) - ((Card.size.width + 2) * stack)));
			for(int c = 0; c<cards.size(); c++) {
				cards.get(c).setPosition((float) this.position.getX() - ((Card.size.height / 5f) * c), (float) this.position.getY());
				cards.get(c).flip(position);
			}
			break;
		case 3:
			setPosition(Camera.get().getDisplayWidth(0.785f), 
					Camera.get().getDisplayHeight(0.425f) + (((Card.size.width + 2) * 4) - ((Card.size.width + 2) * stack)));
			for(int c = 0; c<cards.size(); c++) {
				cards.get(c).setPosition((float) this.position.getX() + ((Card.size.height / 5f) * c), (float) this.position.getY());
				cards.get(c).flip(position);
			}
			break;
		}
	}
	
	public LinkedList<Card> getCards() {
		return cards;
	}
	
	public Card getTopCard() {
		if(cards.isEmpty())
			return null;
		return cards.getLast();
	}
	
	public void addCard(Card card) {
		card.setStackType(0);
		
		switch(rotation) {
		case -1:
			card.setMovement(new Point2D.Double(position.getX(), position.getY()));
			//card.setPosition((float) position.getX(), (float) position.getY());
			break;
		case 0:
			//card.setPosition((float) position.getX(), (float) position.getY() + ((Card.size.height / 5f) * cards.size()));
			card.setMovement(new Point2D.Double(position.getX(),
					position.getY() + ((Card.size.height / compression) * cards.size())));
			break;
		case 1:
			//card.setPosition((float) position.getX(), (float) position.getY() - ((Card.size.height / 5f) * cards.size()));
			card.setMovement(new Point2D.Double(position.getX(),
					position.getY() - ((Card.size.height / compression) * cards.size())));
			break;
		case 2:
			//card.setPosition((float) position.getX() - ((Card.size.height / 5f) * cards.size()), (float) position.getY());
			card.setMovement(new Point2D.Double(position.getX() - ((Card.size.height / compression) * cards.size()),
					position.getY()));
			break;
		case 3:
			//card.setPosition((float) position.getX() + ((Card.size.height / 5f) * cards.size()), (float) position.getY());
			card.setMovement(new Point2D.Double(position.getX() + ((Card.size.height / compression) * cards.size()),
					position.getY()));
			break;
		}
		
		//if(card.getRotation() != rotation)
			//card.setRotation(rotation);
		card.flip(rotation);
		
		if(!cards.contains(card)) {
			cards.add(card);
			setCompression();
			if(cards.size() > 9) {
				compress();
			}
		}
	}
	
	// Potentially can fuck up compressions?
	public void align(Card card) {
		switch(rotation) {
		case -1:
			card.setPosition((float) position.getX(), (float) position.getY());
			break;
		case 0:
			card.setPosition((float) position.getX(),
					(float) position.getY() + ((Card.size.height / compression) * cards.indexOf(card)));
			break;
		case 1:
			card.setPosition((float) position.getX(),
					(float) position.getY() - ((Card.size.height / compression) * cards.indexOf(card)));
			break;
		case 2:
			card.setPosition((float) position.getX() - ((Card.size.height / compression) * cards.indexOf(card)),
					(float) position.getY());
			break;
		case 3:
			card.setPosition((float) position.getX() + ((Card.size.height / compression) * cards.indexOf(card)),
					(float) position.getY());
			break;
		}
		
		//card.flip(rotation);
	}
	
	public Card removeTopCard() {
		Card temp = cards.removeLast();
		setCompression();
		if(cards.size() >= 9) {
			compress();
		}
		
		return temp;
	}
	
	public void setCompression() {
		compression = 5f + (cards.size() > 9 ? (cards.size() % 9) : 0f);
	}
	
	public void compress() {
		//float compression = 5f + (cards.size() % 9);
		switch(rotation) {
		case 0:
			for(int c = 1; c<cards.size(); c++) {
				//cards.get(c).setPosition((float) this.position.getX(), 
						//(float) this.position.getY() + ((Card.size.height / compression) * c));
				cards.get(c).setMovement(new Point2D.Double(position.getX(),
						this.position.getY() + ((Card.size.height / compression) * c)));
			}
			break;
		case 1:
			for(int c = 1; c<cards.size(); c++) {
				cards.get(c).setMovement(new Point2D.Double(position.getX(),
						this.position.getY() - ((Card.size.height / compression) * c)));
			}
			break;
		case 2:
			for(int c = 1; c<cards.size(); c++) {
				cards.get(c).setMovement(new Point2D.Double(position.getX() + ((Card.size.height / compression) * c),
						this.position.getY()));
			}
			break;
		case 3:
			for(int c = 1; c<cards.size(); c++) {
				cards.get(c).setMovement(new Point2D.Double(position.getX() - ((Card.size.height / compression) * c),
						this.position.getY()));
			}
			break;
		}
	}

	public int getRotation() {
		return rotation;
	}
	
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
	
	public Point2D getPosition() {
		return position;
	}
	
	public void setPosition(Point2D position) {
		this.position = position;
	}

	public void setPosition(float x, float y) {
		this.position = new Point2D.Float(x, y);
	}
	
	public Rectangle2D getBox() {
		return new Rectangle2D.Double(position.getX(), position.getY(), Card.size.getWidth(), Card.size.getHeight());
	}
	
	public int getTableIndex() {
		return tableIndex;
	}
	
	public void setTableIndex(int tableIndex) {
		this.tableIndex = tableIndex;
	}

	
}
