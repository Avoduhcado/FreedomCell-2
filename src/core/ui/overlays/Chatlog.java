package core.ui.overlays;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import core.Camera;
import core.Theater;
import core.network.packets.ChatPacket;
import core.setups.Stage;
import core.ui.InputBox;
import core.utilities.keyboard.Keybinds;
import core.utilities.text.TextLine;

public class Chatlog extends MenuOverlay {

	private float displayTimer;
	private LinkedList<TextLine> chat = new LinkedList<TextLine>();
	private InputBox chatBox;
	private String entry;
	
	public Chatlog(float x, float y, String image) {
		super(x, y, image);
		
		this.box = new Rectangle2D.Double(x, y, Camera.get().getDisplayWidth(0.9f), Camera.get().getDisplayHeight(0.175f));
		
		chatBox = new InputBox(Camera.get().getDisplayWidth(0.05f), Camera.get().getDisplayHeight(0.92f), null, 0, "", 50);
		chatBox.setCentered(false);
		chatBox.setEnabled(false);
	}

	public void update() {
		if(displayTimer > 0) {
			displayTimer -= Theater.getDeltaSpeed(0.025f);
		}
			
		if(chatBox.isEnabled()) {
			chatBox.update();
			if(chatBox.input() != null) {
				entry = chatBox.getText();
				if(!entry.trim().matches("")) {
					((Stage) Theater.get().getSetup()).getClient().sendData(new ChatPacket(entry));
				} else {
					displayTimer = 0f;
				}
				
				chatBox.setEnabled(false);
				chatBox.setText("");
			}
		} else if(isActive() && Keybinds.CONFIRM.clicked()) {
			open();
		}
	}
	
	public void draw() {
		super.draw();
		
		//for(int x = 0; (displayTimer == -1 ? x<chat.size() : x < (chat.size() > 5 ? 5 : chat.size())); x++) {
		for(int x = 0; x < (chat.size() > 5 ? 5 : chat.size()); x++) {
			chat.get(x).draw(chatBox.getX(), chatBox.getY() - (22 * (x + 1)), "SYSTEM");
		}
		
		chatBox.draw();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();

		GL11.glTranslatef(chatBox.getX(), chatBox.getY(), 0);
		GL11.glLineWidth(2f);
		GL11.glColor4f(0f, 0f, 0f, 0.8f);

		GL11.glBegin(GL11.GL_LINES);
		{
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f((float) getBox().getMaxX() * 0.945f, 0);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public void addMessage(String message) {
		chat.addFirst(new TextLine(message));
		if(chat.size() > 25)
			chat.removeLast();
		if(!chatBox.isEnabled())
			displayTimer = 5f;
	}
	
	public boolean isActive() {
		return chatBox.isEnabled() || displayTimer > 0 || displayTimer == -1;
	}
	
	public void open() {
		displayTimer = -1;
		chatBox.setText("");
		chatBox.setEnabled(true);
		entry = null;
	}

	public void close() {
		displayTimer = 5f;
	}

	@Override
	public boolean isCloseRequest() {
		return !chatBox.isEnabled() && Keybinds.CONFIRM.clicked();
	}
	
}
