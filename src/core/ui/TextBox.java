package core.ui;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.utilities.text.TextLine;

public class TextBox extends UIElement {

	private ArrayList<TextLine> text = new ArrayList<TextLine>();
	private String fontFace;
	
	public TextBox(String text, String fontFace, float x, float y, String image) {
		super(x, y, image);
		
		this.fontFace = fontFace;
		String[] temp = text.split(";");
		for(String t : temp)
			this.text.add(new TextLine(t));
		this.box = new Rectangle2D.Double(x, y, getWidth(), getHeight());
	}
	
	@Override
	public void draw() {
		if(frame != null)
			frame.draw(x, y, box);
		for(int i = 0; i<text.size(); i++)
			text.get(i).draw(x, y + getCurrentHeight(i), fontFace);
	}
	
	@Override
	public void draw(float x, float y) {
		if(frame != null)
			frame.draw(x, y, box);
		for(int i = 0; i<text.size(); i++)
			text.get(i).draw(x, y + getCurrentHeight(i), fontFace);
	}
	
	@Override
	public void updateBox() {
		box = new Rectangle2D.Double(x, y, getWidth(), getHeight());
	}
	
	public float getWidth() {
		float width = 0;
		for(TextLine t : text) {
			if(t.getWidth() > width) {
				width = t.getWidth();
			}
		}
		
		return width;
	}
	
	public float getHeight() {
		float height = 0;
		for(TextLine t : text) {
			height += t.getHeight();
		}
		
		return height;
	}
	
	public float getCurrentHeight(int index) {
		float height = 0;
		for(int x = 0; x<index; x++) {
			height += text.get(x).getHeight();
		}
		
		return height;
	}
	
	public String getText() {
		String fullText = "";
		for(TextLine t : text) {
			fullText += t.getText();
		}
		return fullText;
	}
	
	public String[] getTextByLine() {
		String[] lineText = new String[text.size()];
		for(int t = 0; t<lineText.length; t++) {
			lineText[t] = text.get(t).getText();
		}
		
		return lineText;
	}
	
	public void setText(String text) {
		this.text.clear();
		
		String[] temp = text.split(";");
		for(String t : temp)
			this.text.add(new TextLine(t));
		
		updateBox();
	}
	
	public void addText(String text) {
		String[] temp = text.split(";");
		for(String t : temp)
			this.text.add(new TextLine(t));
		
		updateBox();
	}
}
