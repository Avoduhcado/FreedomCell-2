package core.ui.overlays;

import java.awt.geom.Rectangle2D;

import core.Camera;
import core.Theater;
import core.network.packets.DisconnectClientPacket;
import core.setups.ServerLobby;
import core.setups.Stage;
import core.ui.Button;
import core.ui.TextBox;

public class GameOverMenu extends MenuOverlay {

	private TextBox title;
	private Button exitToLobby;
	private Button quitGame;
	
	public GameOverMenu(float x, float y, String image, String winnerName) {
		super(x, y, image);
		
		title = new TextBox("corange>" + winnerName + " <cwhite>has won!", "SYSTEM", Float.NaN, y, null);
		title.setPosition(Camera.get().getDisplayWidth(0.5f) - (title.getWidth() / 2f), title.getY());
		
		exitToLobby = new Button("Exit to Lobby", Float.NaN, (float) (title.getBox().getMaxY() + title.getBox().getHeight()), 0, null);
		quitGame = new Button("Quit Game", Float.NaN, (float) exitToLobby.getBox().getMaxY(), 0, null);
		
		this.x = title.getX();
		this.box = new Rectangle2D.Double(title.getX(), y, title.getBox().getWidth(), quitGame.getBox().getMaxY() - y);
	}
	
	@Override
	public void update() {
		exitToLobby.update();
		if(exitToLobby.isClicked()) {
			((Stage) Theater.get().getSetup()).getClient().sendData(new DisconnectClientPacket());
			Theater.get().swapSetup(new ServerLobby());
		}
		
		quitGame.update();
		if(quitGame.isClicked()) {
			Theater.get().close();
		}
	}
	
	@Override
	public void draw() {
		super.draw();
		
		title.draw();
		exitToLobby.draw();
		quitGame.draw();
	}

	@Override
	public boolean isCloseRequest() {
		return false;
	}

}
