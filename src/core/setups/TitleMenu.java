package core.setups;

import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.Theater;
import core.audio.Ensemble;
import core.audio.Track;
import core.render.DrawUtils;
import core.render.textured.Image;
import core.ui.Button;
import core.ui.ButtonGroup;
import core.ui.overlays.OptionsMenu;
import core.ui.overlays.ServerMenu;

public class TitleMenu extends GameSetup {

	/** Title logo */
	private Image logo;
	/** A button group contain New Game, Options, and Exit */
	private ButtonGroup buttonGroup;
	/** The options menu */
	private OptionsMenu optionsMenu;
	/** Server menu */
	private ServerMenu serverMenu;
	
	private Vector4f fill = new Vector4f(1f, 1f, 1f, 0f);
	private boolean fading = true;
	private float fadeTimer;
		
	/**
	 * Title Menu
	 * Set up buttons for game operation.
	 */
	public TitleMenu() {
		// Ensure fading has reset
		Camera.get().setFadeTimer(-0.1f);
		
		// Load title logo
		logo = new Image("logo");
		
		// Initialize game buttons
		buttonGroup = new ButtonGroup(Float.NaN, Camera.get().getDisplayHeight(0.625f), "Menu2", true);
		buttonGroup.addButton(new Button("Play"));
		buttonGroup.addButton(new Button("Options"));
		buttonGroup.addButton(new Button("Exit"));
		
		// Play title track
		//Ensemble.get().setBackground(new Track("Menu"));
		//Ensemble.get().getBackground().play();
	}
	
	@Override
	public void update() {
		// Update the background fade animation
		fade();
		
		// Update options instead of main screen if it's open
		if(optionsMenu != null) {
			optionsMenu.update();
			// Close options if user chooses to close
			if(optionsMenu.isCloseRequest())
				optionsMenu = null;
		} else if(serverMenu != null) {
			serverMenu.update();
			if(serverMenu.isCloseRequest())
				serverMenu = null;
		} else {
			// Update buttons
			buttonGroup.update();
			if(buttonGroup.getButton(0).isClicked()) {
				// Start game, proceed with state swap
				Theater.get().swapSetup(new ServerLobby());
			} else if(buttonGroup.getButton(1).isClicked()) {
				// Open options menu
				optionsMenu = new OptionsMenu(20, 20, "Menu2");
			} else if(buttonGroup.getButton(2).isClicked()) {
				// Exit game
				Theater.get().close();
			}
		}
	}
	
	@Override
	public void draw() {
		DrawUtils.fillColor(fill.x, fill.y, fill.z, fill.w);
		
		if(serverMenu != null) {
			serverMenu.draw();
		} else {
			// Draw logo
			logo.draw(Float.NaN, Camera.get().getDisplayHeight(0.1667f));
			
			// Draw buttons
			buttonGroup.draw();
		}
		
		// If options menu is open, draw it
		if(optionsMenu != null)
			optionsMenu.draw();
	}

	public void fade() {
		if(fading) {
			if(fadeTimer < 5f) {
				fadeTimer += Theater.getDeltaSpeed(0.025f);
				if(fill.w < 0.95f) {
					fill.w += Theater.getDeltaSpeed(0.025f);
					if(fill.w > 0.95f)
						fill.w = 0.95f;
				}
			} else {
				fadeTimer = 2f;
				fading = false;
			}
		} else {
			if(fadeTimer > 0f) {
				fadeTimer -= Theater.getDeltaSpeed(0.025f);
				if(fill.w > 0f) {
					fill.w -= Theater.getDeltaSpeed(0.025f) / 2f;
					if(fill.w < 0f)
						fill.w = 0f;
				}
			} else {
				fadeTimer = 0f;
				fading = true;
				switch((int)(Math.random() * 3)) {
				case(0):
					fill = new Vector4f(0.95f, 0.95f, 0.95f, 0f);
					break;
				case(1):
					fill = new Vector4f(0.85f, 0.1f, 0.25f, 0f);
					break;
				case(2):
					fill = new Vector4f(0.2f, 0.15f, 0.85f, 0f);
					break;
				}
			}
		}
	}
	
	@Override
	public void resizeRefresh() {
		// Reposition and center
		buttonGroup.setPosition(Float.NaN, Camera.get().getDisplayHeight(0.625f));
	}

}
