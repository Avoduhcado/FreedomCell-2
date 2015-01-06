package core.ui;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.Camera;

public class ButtonGroup extends UIElement {

	private ArrayList<Button> buttons = new ArrayList<Button>();
	private boolean centered;
	
	public ButtonGroup(float x, float y, String image, boolean centered) {
		super(x, y, image);
		
		this.centered = centered;
	}
	
	public void update() {
		for(Button b : buttons) {
			b.update();
		}
	}
	
	public void draw() {
		super.draw();
		for(int i = 0; i<buttons.size(); i++) {
			buttons.get(i).draw();
		}
	}
	
	public void centerItems() {
		for(int i = 0; i<buttons.size(); i++) {
			buttons.get(i).setPosition(x + (float)(box.getWidth() / 2f) - (float)(buttons.get(i).getBox().getWidth() / 2f),
					y + getCurrentHeight(i));
		}
	}
	
	@Override
	public void updateBox() {
		if(centered) {
			if(getMaxWidth() > box.getWidth()) {
				box = new Rectangle2D.Double((float) (box.getCenterX() - (buttons.get(getWidestElement()).getBox().getWidth() / 2f)),
						y, getMaxWidth(), getTotalHeight());
			} else {
				box = new Rectangle2D.Double(buttons.get(getWidestElement()).getX(), y, getMaxWidth(), getTotalHeight());
			}
			this.x = (float) box.getX();
		} else {
			box = new Rectangle2D.Double(x, y, getMaxWidth(), getTotalHeight());
		}
	}
	
	@Override
	public void setPosition(float x, float y) {
		if(Float.isNaN(x))
			this.x = (float) (Camera.get().getDisplayWidth(0.5f) - (box.getWidth() / 2f));
		else
			this.x = x;
		this.y = y;
		updateBox();
		
		if(isCentered())
			centerItems();
	}
	
	public float getMaxWidth() {
		float width = 0;
		for(Button b : buttons) {
			if(b.getBox().getWidth() > width) {
				width = (float) b.getBox().getWidth();
			}
		}
		
		return width;
	}
	
	public int getWidestElement() {
		int index = 0;
		for(int x = 0; x<buttons.size(); x++) {
			if(buttons.get(x).getBox().getWidth() > buttons.get(index).getBox().getWidth()) {
				index = x;
			}
		}
		
		return index;
	}
	
	public float getTotalHeight() {
		float height = 0;
		for(Button b : buttons) {
			height += b.getBox().getHeight() + 2f;
		}
		
		return height;
	}
	
	public float getCurrentHeight(int index) {
		float height = 0;
		for(int x = 0; x<index; x++) {
			height += buttons.get(x).getBox().getHeight() + 2f;
		}
		
		return height;
	}
	
	public ArrayList<Button> getButtons() {
		return buttons;
	}
	
	public Button getButton(int index) {
		return buttons.get(index);
	}
	
	public void addButton(Button button) {
		buttons.add(button);
		updateBox();
		if(centered) {
			button.setPosition((float) (box.getCenterX() - (button.getBox().getWidth() / 2f)), y + getCurrentHeight(buttons.size() - 1));
		} else {
			button.setPosition(x, y + getCurrentHeight(buttons.size() - 1));
		}
	}

	public boolean isCentered() {
		return centered;
	}

	public void setCentered(boolean centered) {
		this.centered = centered;
		if(centered)
			centerItems();
	}

}
