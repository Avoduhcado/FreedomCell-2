package core.utilities.text;

import java.util.ArrayList;

public class TextLine {

	private ArrayList<TextSegment> line = new ArrayList<TextSegment>();
	
	public TextLine(String line) {
		String[] temp = line.split("<");
		for(int x = 0; x<temp.length; x++) {
			this.line.add(new TextSegment(temp[x]));
		}
	}
	
	public void draw(float x, float y, String font) {
		for(int i = 0; i<line.size(); i++) {
			line.get(i).draw(x, y, font);
			x += line.get(i).getWidth();
		}
	}
	
	public int getCurrentIndex(int index) {
		int temp = 0;
		
		if(index > 0) {
			for(int x = 0; x<index; x++) {
				temp += line.get(x).getSegment().length();
			}
		}
		
		return temp;
	}
	
	public float getHeight() {
		float height = 0f;
		for(TextSegment t : line) {
			if(t.getHeight() > height)
				height = t.getHeight();
		}
		
		return height;
	}
	
	public float getWidth() {
		float width = 0f;
		for(TextSegment t : line) {
			width += t.getWidth();
		}
		
		return width;
	}
	
	public String getText() {
		String text = "";
		for(TextSegment s : line) {
			text += s.getSegment();
		}
		return text;
	}
	
}
