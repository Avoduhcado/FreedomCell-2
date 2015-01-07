package core.setups;

import org.lwjgl.util.vector.Vector3f;

import core.Theater;
import core.cards.Card;
import core.cards.CardStack;
import core.cards.Table;
import core.network.Client;
import core.network.packets.CardMovePacket;
import core.render.DrawUtils;
import core.ui.overlays.GameMenu;
import core.utilities.keyboard.Keybinds;
import core.utilities.mouse.MouseInput;

public class Stage extends GameSetup {
	
	private GameMenu gameMenu;
	
	private int seat;
	private Table table;
	
	private Card selectedCard;
	
	private Client client;
	//private boolean starting;
	//private float timeout = 0f;
	
	public Stage(Client client) {
		this.client = client;
		/*client = new Client("76.240.44.178", 34336, "Client", this);
		client.start();
		starting = true;*/
	}
	
	@Override
	public void update() {
		if(gameMenu != null) {
			gameMenu.update();
			if(gameMenu.isCloseRequest())
				gameMenu = null;
		} else {
			if(table != null) {
				table.update();
				
				if(selectedCard == null) {
					table.processHighlights();
				} else {
					table.setupHighlights(selectedCard);
				}
			}
			
			if(Keybinds.EXIT.clicked()) {
				gameMenu = new GameMenu(20f, 20f, "Menu2");
			}
		}
		
		if(!client.isConnected()) {
			Theater.get().swapSetup(new TitleMenu());
		}
		/*if(!client.isConnected() && !starting) {
			Theater.get().swapSetup(new TitleMenu());
		} else if(client.isConnected() && starting) {
			starting = false;
		} else if(!client.isConnected() && starting) {
			timeout += Theater.getDeltaSpeed(0.025f);
			if(timeout > 5f) {
				Theater.get().swapSetup(new TitleMenu());
			}
		}*/
	}

	@Override
	public void draw() {
		DrawUtils.fillColor(0f, 0.5f, 0f, 1f);
		
		if(table != null)
			table.draw(seat);
		
		if(selectedCard != null) {
			if(selectedCard.getSuit().isBlack())
				DrawUtils.setColor(new Vector3f(1f, 1f, 1f));
			else
				DrawUtils.setColor(new Vector3f(0f, 0f, 0f));
			DrawUtils.drawRect(selectedCard.getX(), selectedCard.getY(), selectedCard.getBox());
		}
		
		if(gameMenu != null)
			gameMenu.draw();
	}

	@Override
	public void resizeRefresh() {
	}
	
	public void selectCard() {
		if(table != null) {
			for(CardStack c : table.getCells()) {
				if(c.getTopCard() != null && c.getTopCard().getBox().contains(MouseInput.getMouse())) {
					selectedCard = c.getTopCard();
					return;
				}
			}
			
			for(CardStack c : table.getSeats()[seat].getCascades()) {
				if(c.getTopCard() != null && c.getTopCard().getBox().contains(MouseInput.getMouse())) {
					selectedCard = c.getTopCard();
					return;
				}
			}
		}
		
		selectedCard = null;
	}
	
	public void placeCard() {
		for(CardStack c : table.getCells()) {
			if(c.getTopCard() == null && c.getBox().contains(MouseInput.getMouse())) {
				client.sendData(new CardMovePacket(table.removeCard(selectedCard).getTableIndex(), c.getTableIndex()));
				c.addCard(selectedCard);
				selectedCard = null;
				return;
			}
		}
		
		for(CardStack c : table.getSeats()[seat].getCascades()) {
			if(c.getTopCard() == null && c.getBox().contains(MouseInput.getMouse())) {
				client.sendData(new CardMovePacket(table.removeCard(selectedCard).getTableIndex(), c.getTableIndex()));
				c.addCard(selectedCard);
				selectedCard = null;
				return;
			} else if(c.getTopCard() != null && c.getTopCard().getBox().contains(MouseInput.getMouse()) 
					&& selectedCard.canBePlacedOn(c.getTopCard(), false)) {
				client.sendData(new CardMovePacket(table.removeCard(selectedCard).getTableIndex(), c.getTableIndex()));
				c.addCard(selectedCard);
				selectedCard = null;
				return;
			}
		}
		
		for(CardStack c : table.getSeats()[seat].getFoundations()) {
			if(c.getTopCard() == null && c.getBox().contains(MouseInput.getMouse()) 
					&& table.getSeats()[seat].canStartNewFoundation(selectedCard)) {
				client.sendData(new CardMovePacket(table.removeCard(selectedCard).getTableIndex(), c.getTableIndex()));
				c.addCard(selectedCard);
				selectedCard = null;
				return;
			} else if(c.getTopCard() != null && c.getTopCard().getBox().contains(MouseInput.getMouse()) 
					&& selectedCard.canBePlacedOn(c.getTopCard(), true)) {
				client.sendData(new CardMovePacket(table.removeCard(selectedCard).getTableIndex(), c.getTableIndex()));
				c.addCard(selectedCard);
				selectedCard = null;
				return;
			}
		}
		
		selectedCard = null;
	}

	public int getSeat() {
		return seat;
	}
	
	public void setSeat(int seat) {
		this.seat = seat;
	}
	
	public Table getTable() {
		return table;
	}
	
	public void setTable(Table table) {
		this.table = table;
		this.table.arrangeSeats(getSeat());
	}
	
	public Card getSelectedCard() {
		return selectedCard;
	}

	public Client getClient() {
		return client;
	}
	
}
