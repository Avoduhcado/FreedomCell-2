package core.ui.overlays;

import java.awt.geom.Rectangle2D;
import org.lwjgl.input.Keyboard;

import core.Camera;
import core.audio.Ensemble;
import core.ui.Button;
import core.ui.ElementGroup;
import core.ui.InputBox;
import core.ui.Slider;
import core.utilities.keyboard.Keybinds;
import core.utilities.text.Text;

public class OptionsMenu extends MenuOverlay {

	//private DisplayMode[] displayModes;
	//private String modes = "";
	private Slider volumeSlider;
	private Button close;
	private ElementGroup keybinds = new ElementGroup();
	
	public OptionsMenu(float x, float y, String image) {
		super(x, y, image);
		
		this.box = new Rectangle2D.Double(x, y, Camera.get().getDisplayWidth() - (this.frame.getWidth() / 1.5f), Camera.get().getDisplayHeight() - (this.frame.getWidth() / 1.5f));
		
		/*try {
			displayModes = Display.getAvailableDisplayModes();
			for(DisplayMode d : displayModes)
				modes += d.toString() + "\n";
		} catch (LWJGLException e) {
			e.printStackTrace();
		}*/
		
		volumeSlider = new Slider(Camera.get().getDisplayWidth(0.5f), Camera.get().getDisplayHeight(0.1667f), 1f,
				Ensemble.get().getMasterVolume(), "SliderBG", "SliderValue");
		volumeSlider.setPosition((float) (volumeSlider.getX() - (volumeSlider.getBox().getWidth() / 2f)), volumeSlider.getY());
		
		float keyX = Camera.get().getDisplayWidth(0.25f);
		float keyY = 0;
		for(int i = 0; i<Keybinds.values().length; i++) {
			if(!keybinds.isEmpty())
				keyY += keybinds.get(keybinds.size() - 1).getBox().getHeight();
			if(Camera.get().getDisplayHeight(0.285f) + keyY > this.getBox().getHeight() / 1.2f) {
				keyX *= 3f;
				keyY = 0;
			}
				
			keybinds.add(new InputBox(keyX, Camera.get().getDisplayHeight(0.285f) + keyY, null, -1, Keybinds.values()[i].getKey(), 0));
			keybinds.get(keybinds.size() - 1).setEnabled(false);
			((InputBox) keybinds.get(keybinds.size() - 1)).setCentered(false);
		}
		
		close = new Button("Close", Float.NaN, Camera.get().getDisplayHeight(0.833f), 0, null);
	}
	
	@Override
	public void update() {
		// Update close button
		close.update();
		// Cycle through each keybind button
		for(int i = 0; i<keybinds.size(); i++) {
			keybinds.get(i).update();
			// When one keybind is clicked, disable all others
			if(keybinds.get(i).isClicked()) {
				keybinds.setEnabledAllExcept(false, keybinds.get(i));
			}
			// Set changed keybind equal to new value and disable updating
			if(keybinds.get(i).isEnabled() && ((InputBox) keybinds.get(i)).input() != null) {
				Keybinds.values()[i].setKey(Keyboard.getKeyIndex(((InputBox) keybinds.get(i)).getText()));
				keybinds.get(i).setEnabled(false);
			}
		}
		
		// Update volume slider
		volumeSlider.update();
		// Set Ensemble volume equal to value of slider
		if(Ensemble.get().getMasterVolume() != volumeSlider.getValue()) {
			Ensemble.get().setMasterVolume(volumeSlider.getValue());
		}
	}
	
	@Override
	public void draw() {
		super.draw();
		
		Text.drawCenteredString("Options", Camera.get().getDisplayWidth(0.5f), y, null, "SYSTEM");
		Text.drawCenteredString("Volume", (float) volumeSlider.getBox().getCenterX(),
				volumeSlider.getY() - (Text.getHeight("Volume", "SYSTEM") * 1.5f), null, "SYSTEM");
		volumeSlider.draw();
		for(int i = 0; i<keybinds.size(); i++) {
			Text.drawString(Keybinds.values()[i].toString() + ":", 
					keybinds.get(i).getX() - Text.getWidth(Keybinds.values()[i].toString() + ": ", "SYSTEM"),
					keybinds.get(i).getY(), null, "SYSTEM");
			keybinds.get(i).draw();
		}
		
		close.draw();
	}
	
	@Override
	public boolean isCloseRequest() {
		return close.isClicked();
	}

}
