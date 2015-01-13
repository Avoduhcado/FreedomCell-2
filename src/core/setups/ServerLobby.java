package core.setups;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.lwjgl.util.vector.Vector4f;

import core.Camera;
import core.Theater;
import core.network.Client;
import core.network.Server;
import core.network.packets.CloseServerPacket;
import core.network.packets.DisconnectClientPacket;
import core.network.packets.GameStartPacket;
import core.render.DrawUtils;
import core.ui.Button;
import core.ui.CheckBox;
import core.ui.ElementGroup;
import core.ui.EmptyFrame;
import core.ui.InputBox;
import core.ui.TextBox;
import core.utilities.Config;
import core.utilities.text.Text;

public class ServerLobby extends GameSetup {
	
	private Button join;
	private InputBox joinIP;
	private InputBox joinPort;
	private CheckBox joinHide;
	private EmptyFrame joinFrame;
	
	private Button hostLobby;
	private CheckBox hostLocal;
	private Button hostGame;
	private TextBox hostIP;
	private InputBox hostPort;
	private CheckBox hostHide;
	private EmptyFrame hostFrame;
	
	private TextBox nameTitle;
	private InputBox name;
	private EmptyFrame nameFrame;
	
	private TextBox connectedUsers;
	
	private TextBox seedTitle;
	private InputBox seed;
	private EmptyFrame seedFrame;
	
	private ElementGroup inputs;
	private Button close;
	
	private Client client;
	private boolean starting;
	private float timeout = 0f;
	private boolean host;
	
	private Vector4f fill = new Vector4f(1f, 1f, 1f, 0f);
	private boolean fading = true;
	private float fadeTimer;
	
	public ServerLobby() {
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
		joinFrame = new EmptyFrame(Camera.get().getDisplayWidth(0.15f), Camera.get().getDisplayHeight(0.075f), "Menu2");
		joinFrame.setBox(joinFrame.getX(), joinFrame.getY(), (float) joinHide.getBox().getWidth() * 2f,
				(float) joinHide.getBox().getMaxY() - join.getY(), false);
		
		hostLobby = new Button("Host Lobby", Camera.get().getDisplayWidth(0.15f), Camera.get().getDisplayHeight(0.275f), 0, null);
		hostLocal = new CheckBox((float) (hostLobby.getBox().getMaxX() + (hostLobby.getBox().getWidth() * 0.5f)), 
				Camera.get().getDisplayHeight(0.275f), null, "Host LAN");
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
		hostFrame = new EmptyFrame(Camera.get().getDisplayWidth(0.15f), hostLobby.getY(), "Menu2");
		hostFrame.setBox(hostFrame.getX(), hostFrame.getY(), (float) hostHide.getBox().getWidth() * 2f,
				(float) hostHide.getBox().getMaxY() - hostLobby.getY(), false);
		
		nameTitle = new TextBox("cwhite>Username:", "SYSTEM",
				Camera.get().getDisplayWidth(0.15f), Camera.get().getDisplayHeight(0.525f), null);
		name = new InputBox(Camera.get().getDisplayWidth(0.15f), (float) nameTitle.getBox().getMaxY(),
				null, 0, Config.joinName, 15);
		name.setCentered(false);
		name.setEnabled(false);
		nameFrame = new EmptyFrame(Camera.get().getDisplayWidth(0.15f), nameTitle.getY(), "Menu2");
		nameFrame.setBox(nameFrame.getX(), nameFrame.getY(), (float) hostFrame.getBox().getWidth(),
				(float) name.getBox().getMaxY() - nameTitle.getY(), false);
		inputs.add(name);
		
		seedTitle = new TextBox("cwhite>Game Seed:", "SYSTEM",
				Camera.get().getDisplayWidth(0.15f), Camera.get().getDisplayHeight(0.65f), null);
		seed = new InputBox(seedTitle.getX(), (float) seedTitle.getBox().getMaxY(), null, 0, "Seed", 15);
		seed.setCentered(false);
		seed.setEnabled(false);
		seedFrame = new EmptyFrame(Camera.get().getDisplayWidth(0.15f), seedTitle.getY(), "Menu2");
		seedFrame.setBox(seedFrame.getX(), seedFrame.getY(), (float) hostFrame.getBox().getWidth(),
				(float) seed.getBox().getMaxY() - seedTitle.getY(), false);
		inputs.add(seed);
		
		close = new Button("Return to Title", Float.NaN, Camera.get().getDisplayHeight(0.833f), 0, "Menu2");
	}
	
	@Override
	public void update() {
		fade();
		
		join.update();
		if(join.isClicked() && join.isEnabled()) {
			hostLobby.setEnabled(!hostLobby.isEnabled());
			if(!hostLobby.isEnabled()) {
				client = new Client(joinIP.getText(), Integer.parseInt(joinPort.getText()), name.getText(), this);
				client.start();
				
				timeout = 0f;
				starting = true;
				inputs.setEnabledAll(false);
			} else {
				connectedUsers = null;
				if(client.isConnected()) {
					client.sendData(new DisconnectClientPacket());
				}
				client = null;
			}
		}
		
		if(!joinHide.isChecked()) {
			joinIP.update();
			if(joinIP.isClicked() && connectedUsers == null) {
				inputs.setEnabledAllExcept(false, joinIP);
			} else if(connectedUsers != null) {
				joinIP.setEnabled(false);
			}
			if(joinIP.isEnabled() && joinIP.input() != null) {
				Config.joinIP = joinIP.getText();
				joinIP.setEnabled(false);
			}
			joinPort.update();
			if(joinPort.isClicked() && connectedUsers == null) {
				inputs.setEnabledAllExcept(false, joinPort);
			} else if(connectedUsers != null) {
				joinPort.setEnabled(false);
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
		if(hostLobby.isClicked() && hostLobby.isEnabled()) {
			hostGame.setEnabled(!hostGame.isEnabled());
			if(hostGame.isEnabled()) {
				join.setEnabled(false);
				connectedUsers = new TextBox("c#FFFFFF>Current Users:;" + "clightGray>" + name.getText(), "SYSTEM",
						Camera.get().getDisplayWidth(0.5f), hostLobby.getY(), "Menu2");
				host = true;
				if(hostLocal.isChecked()) {
					Server server = new Server(8080);
					server.start();
					
					client = new Client("localhost", 8080, name.getText(), this);
					client.start();
				} else {
					Server server = new Server(Integer.parseInt(hostPort.getText()));
					server.start();
					
					client = new Client(hostIP.getText(), Integer.parseInt(hostPort.getText()), name.getText(), this);
					client.start();
				}
				
				timeout = 0f;
				starting = true;
				inputs.setEnabledAll(false);
			} else {
				join.setEnabled(true);
				connectedUsers = null;
				client.sendData(new CloseServerPacket());
				client = null;
				host = false;
			}
		}
		hostLocal.update();
		hostGame.update();
		if(hostGame.isClicked() && hostGame.isEnabled()) {
			if(client.isConnected()) {
				client.sendData(new GameStartPacket(getSeed()));
			}
		}
		
		if(!hostHide.isChecked()) {
			hostPort.update();
			if(hostPort.isClicked() && connectedUsers == null) {
				inputs.setEnabledAllExcept(false, hostPort);
			} else if(connectedUsers != null) {
				hostPort.setEnabled(false);
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
		if(name.isClicked() && connectedUsers == null) {
			inputs.setEnabledAllExcept(false, name);
		} else if(connectedUsers != null) {
			name.setEnabled(false);
		}
		if(name.isEnabled() && name.input() != null) {
			Config.joinName = name.getText();
			name.setEnabled(false);
		}
		
		seed.update();
		if(seed.isClicked() && connectedUsers == null) {
			inputs.setEnabledAllExcept(false, seed);
		} else if(connectedUsers != null) {
			seed.setEnabled(false);
		}
		if(seed.isEnabled() && seed.input() != null) {
			seed.setEnabled(false);
		}
		
		close.update();
		if(close.isClicked()) {
			if(client != null && client.isConnected()) {
				if(host)
					client.sendData(new CloseServerPacket());
				else
					client.sendData(new DisconnectClientPacket());
			}
			client = null;
			Theater.get().swapSetup(new TitleMenu());
		}
		
		if(client != null && !client.isConnected()) {
			if(!client.isConnected() && !starting) {
				client = null;
				System.out.println("Client failed to connect");
				connectedUsers = null;
				if(join.isEnabled()) {
					hostLobby.setEnabled(true);
				} else if(hostLobby.isEnabled()) {
					join.setEnabled(true);
				}
			} else if(client.isConnected() && starting) {
				starting = false;
			} else if(!client.isConnected() && starting) {
				timeout += Theater.getDeltaSpeed(0.025f);
				if(timeout > 5f) {
					starting = false;
				}
			}
		}
	}

	@Override
	public void draw() {
		DrawUtils.fillColor(fill.x, fill.y, fill.z, fill.w);
		
		joinFrame.draw();
		join.draw();
		if(!joinHide.isChecked()) {
			joinIP.draw();
			joinPort.draw();
		} else {
			Text.drawString("Join IP", joinIP.getX(), joinIP.getY(), Color.lightGray);
			Text.drawString("Join Port", joinPort.getX(), joinPort.getY(), Color.lightGray);
		}
		joinHide.draw();
		
		hostFrame.draw();
		hostLobby.draw();
		hostLocal.draw();
		hostGame.draw();
		if(!hostHide.isChecked()) {
			hostIP.draw();
			hostPort.draw();
		} else {
			Text.drawString("Server IP", hostIP.getX(), hostIP.getY(), Color.lightGray);
			Text.drawString("Server Port", hostPort.getX(), hostPort.getY(), Color.lightGray);
		}
		hostHide.draw();
		
		nameFrame.draw();
		nameTitle.draw();
		name.draw();
		
		seedFrame.draw();
		seedTitle.draw();
		seed.draw();
		
		if(connectedUsers != null) {
			connectedUsers.draw();
		}
		
		close.draw();
	}

	@Override
	public void resizeRefresh() {

	}
	
	public void fade() {
		if(fading) {
			if(fadeTimer < 5f) {
				fadeTimer += Theater.getDeltaSpeed(0.025f);
				if(fill.w < 1f) {
					fill.w += Theater.getDeltaSpeed(0.025f);
					if(fill.w > 1f)
						fill.w = 1f;
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
	
	public void setConnectedUsers(String[] users) {
		if(connectedUsers == null)
			connectedUsers = new TextBox("", "SYSTEM", Camera.get().getDisplayWidth(0.5f), join.getY(), "Menu2");
		connectedUsers.setText("c#FFFFFF>Current Users:");
		for(String s : users) {
			if(s != null) {
				connectedUsers.addText(";" + s);
			}
		}
	}
	
	public long getSeed() {
		long gameSeed = System.currentTimeMillis();
		if(!seed.getText().matches("") && !seed.getText().matches("Seed")) {
			try {
				gameSeed = Long.parseLong(seed.getText());
			} catch(NumberFormatException e) {
				gameSeed = 0;
				char[] characterSeed = seed.getText().toCharArray();
				for(char s : characterSeed) {
					gameSeed += (int)s * 34;
				}
			}
		}
		
		return gameSeed;
	}

	public Client getClient() {
		return client;
	}
	
	public boolean isHost() {
		return host;
	}

}
