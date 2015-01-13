package core.setups;

import java.awt.Color;

import org.lwjgl.util.vector.Vector3f;

import core.Camera;
import core.Theater;
import core.cards.Card;
import core.cards.CardStack;
import core.cards.Table;
import core.network.Client;
import core.network.packets.CardMovePacket;
import core.render.DrawUtils;
import core.ui.overlays.Chatlog;
import core.ui.overlays.GameMenu;
import core.utilities.keyboard.Keybinds;
import core.utilities.mouse.MouseInput;
import core.utilities.text.Text;

public class Stage extends GameSetup {
	
	private GameMenu gameMenu;
	
	private int seat;
	private Table table;
	
	private Card selectedCard;
	
	private Client client;
	private Chatlog chat;
	private String[] players;
	
	public Stage(Client client) {
		this.client = client;
		this.players = client.getPlayerList().split(";");
		this.chat = new Chatlog(Camera.get().getDisplayWidth(0.05f), Camera.get().getDisplayHeight(0.775f), "Menu2");
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
			
			if(chat.isActive()) {
				if(chat.isCloseRequest()) {
					chat.close();
				}
				chat.update();
			} else if(Keybinds.CONFIRM.clicked()) {
				chat.open();
			}
			
			if(Keybinds.EXIT.clicked()) {
				gameMenu = new GameMenu(20f, 20f, "Menu2");
			}
		}
		
		if(!client.isConnected()) {
			Theater.get().swapSetup(new TitleMenu());
		}
	}

	@Override
	public void draw() {
		DrawUtils.fillColor(0f, 0.5f, 0f, 1f);
		
		if(table != null)
			table.draw();
		
		for(int x = 0; x<players.length; x++) {
			switch((x % players.length) - seat < 0 ? ((x % players.length) - seat) + players.length : (x % players.length) - seat) {
			case 0:
				Text.drawString(players[x], Camera.get().getDisplayWidth(0.35f),
						Camera.get().getDisplayHeight(0.625f) - Text.getHeight(players[x]), Color.white);
				break;
			case 1:
				Text.drawString(players[x], Camera.get().getDisplayWidth(0.55f),
						Camera.get().getDisplayHeight(0.375f), Color.white);
				break;
			case 2:
				Text.drawString(players[x], Camera.get().getDisplayWidth(0.05f),
						Camera.get().getDisplayHeight(0.925f), Color.white);
				break;
			case 3:
				Text.drawString(players[x], Camera.get().getDisplayWidth(0.85f),
						Camera.get().getDisplayHeight(0.075f) - Text.getHeight(players[x]), Color.white);
				break;
			}
		}
		
		Text.drawString(players[seat], Camera.get().getDisplayWidth(0.35f),
				Camera.get().getDisplayHeight(0.625f) - Text.getHeight(players[seat]), Color.white);
		
		if(selectedCard != null) {
			if(selectedCard.getSuit().isBlack())
				DrawUtils.setColor(new Vector3f(1f, 1f, 1f));
			else
				DrawUtils.setColor(new Vector3f(0f, 0f, 0f));
			DrawUtils.drawRect(selectedCard.getX(), selectedCard.getY(), selectedCard.getBox());
		}
		
		if(chat.isActive()) {
			chat.draw();
		}
		
		if(gameMenu != null) {
			DrawUtils.fillColor(0f, 0f, 0f, 0.45f);
			gameMenu.draw();
		}
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
	
	public Chatlog getChat() {
		return chat;
	}
	
}
