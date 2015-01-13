package core.ui.overlays;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import core.Camera;
import core.network.Client;
import core.network.Server;
import core.ui.Button;
import core.ui.CheckBox;
import core.ui.ElementGroup;
import core.ui.InputBox;
import core.ui.TextBox;
import core.utilities.Config;
import core.utilities.text.Text;

public class ServerMenu extends MenuOverlay {

	private Button join;
	private InputBox joinIP;
	private InputBox joinPort;
	private CheckBox joinHide;
	private Button hostLobby;
	private Button hostGame;
	private TextBox hostIP;
	private InputBox hostPort;
	private CheckBox hostHide;
	private InputBox name;
	private TextBox connectedUsers;
	
	private ElementGroup inputs;
	
	private Button close;
	
	public ServerMenu(float x, float y, String image) {
		super(x, y, image);
		
		this.x -= (frame.getWidth() / 3f);
		this.y -= (frame.getHeight() / 3f);
		this.box = new Rectangle2D.Double(x, y, Camera.get().getDisplayWidth(0.5f), Camera.get().getDisplayHeight(0.6f));
		inputs = new ElementGroup();
		
		join = new Button("Join Server", Camera.get().getDisplayWidth(0.15f), Camera.get().getDisplayHeight(0.075f), 0, null);
		joinIP = new InputBox(Camera.get().getDisplayWidth(0.15f), (float) join.getBox().getMaxY(),
				null, 0, Config.joinIP, 15);
		joinIP.setCentered(false);
		joinIP.setEnabled(false);
		inputs.add(joinIP);
		joinPort = new InputBox(Camera.get().getDisplayWidth(0.15f), (float) joinIP.getBox().getMaxY(),
				null, 1, Config.joinPort + "", 5);
		joinPort.setCentered(false);
		joinPort.setEnabled(false);
		inputs.add(joinPort);
		joinHide = new CheckBox(Camera.get().getDisplayWidth(0.15f), 
				(float) joinPort.getBox().getMaxY(), null, "Hide Details");
		joinHide.setChecked(true);
		
		hostLobby = new Button("Host Lobby", Camera.get().getDisplayWidth(0.15f), Camera.get().getDisplayHeight(0.275f), 0, null);
		hostGame = new Button("Start Game", Camera.get().getDisplayWidth(0.15f), (float) hostLobby.getBox().getMaxY(), 0, null);
		hostGame.setEnabled(false);
		hostIP = new TextBox("127.0.0.1", "SYSTEM", Camera.get().getDisplayWidth(0.15f), (float) hostGame.getBox().getMaxY(), null);
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine(); //you get the IP as a String
			hostIP.setText("cgray>" + ip);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		hostPort = new InputBox(Camera.get().getDisplayWidth(0.15f), (float) hostIP.getBox().getMaxY(),
				null, 1, Config.hostPort + "", 5);
		hostPort.setCentered(false);
		hostPort.setEnabled(false);
		inputs.add(hostPort);
		hostHide = new CheckBox(Camera.get().getDisplayWidth(0.15f), (float) hostPort.getBox().getMaxY(), null, "Hide Details");
		hostHide.setChecked(true);
		
		name = new InputBox(Camera.get().getDisplayWidth(0.15f), Camera.get().getDisplayHeight(0.525f),
				null, 0, Config.joinName, 15);
		name.setCentered(false);
		name.setEnabled(false);
		inputs.add(name);
		connectedUsers = new TextBox("", "SYSTEM", Camera.get().getDisplayWidth(0.4f), hostLobby.getY(), null);
		
		close = new Button("Close", Float.NaN, Camera.get().getDisplayHeight(0.833f), 0, null);
	}
	
	@Override
	public void update() {
		join.update();
		if(join.isClicked()) {
			
		}
		
		if(!joinHide.isChecked()) {
			joinIP.update();
			if(joinIP.isClicked()) {
				inputs.setEnabledAllExcept(false, joinIP);
			}
			if(joinIP.isEnabled() && joinIP.input() != null) {
				Config.joinIP = joinIP.getText();
				joinIP.setEnabled(false);
			}
			joinPort.update();
			if(joinPort.isClicked()) {
				inputs.setEnabledAllExcept(false, joinPort);
			}
			if(joinPort.isEnabled() && joinPort.input() != null) {
				Config.joinPort = Integer.parseInt(joinPort.getText());
				joinPort.setEnabled(false);
			}
		}
		
		joinHide.update();
		if(joinHide.isClicked()) {
			joinIP.setEnabled(false);
			joinPort.setEnabled(false);
		}
		
		hostLobby.update();
		if(hostLobby.isClicked()) {
			hostGame.setEnabled(!hostGame.isEnabled());
			if(hostGame.isEnabled()) {
				connectedUsers.setText("c#FFFFFF>Current Users:;" + "clightGray>" + name.getText());
				Server server = new Server(Integer.parseInt(hostPort.getText()));
				server.start();
				
				Client client = new Client(hostIP.getText(), Integer.parseInt(hostPort.getText()), name.getText(), null);
				client.start();
			} else {
				connectedUsers.setText("");
			}
		}
		hostGame.update();
		
		if(!hostHide.isChecked()) {
			hostPort.update();
			if(hostPort.isClicked()) {
				inputs.setEnabledAllExcept(false, hostPort);
			}
			if(hostPort.isEnabled() && hostPort.input() != null) {
				Config.hostPort = Integer.parseInt(hostPort.getText());
				hostPort.setEnabled(false);
			}
		}
		
		hostHide.update();
		if(hostHide.isClicked()) {
			hostPort.setEnabled(false);
		}
		
		name.update();
		if(name.isClicked()) {
			inputs.setEnabledAllExcept(false, name);
		}
		if(name.isEnabled() && name.input() != null) {
			Config.joinName = name.getText();
			name.setEnabled(false);
		}
		
		close.update();
	}
	
	@Override
	public void draw() {
		super.draw();
		
		join.draw();
		if(!joinHide.isChecked()) {
			joinIP.draw();
			joinPort.draw();
		} else {
			Text.drawString("Join IP", joinIP.getX(), joinIP.getY(), Color.lightGray);
			Text.drawString("Join Port", joinPort.getX(), joinPort.getY(), Color.lightGray);
		}
		joinHide.draw();
		
		hostLobby.draw();
		hostGame.draw();
		if(!hostHide.isChecked()) {
			hostIP.draw();
			hostPort.draw();
		} else {
			Text.drawString("Server IP", hostIP.getX(), hostIP.getY(), Color.lightGray);
			Text.drawString("Server Port", hostPort.getX(), hostPort.getY(), Color.lightGray);
		}
		hostHide.draw();
		
		name.draw();
		connectedUsers.draw();
		
		close.draw();
	}

	@Override
	public boolean isCloseRequest() {
		return close.isClicked();
	}

}
