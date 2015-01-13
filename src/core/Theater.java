package core;

import java.io.IOException;

import core.audio.Ensemble;
import core.network.packets.CloseServerPacket;
import core.network.packets.DisconnectClientPacket;
import core.setups.GameSetup;
import core.setups.ServerLobby;
import core.setups.SplashScreen;
import core.setups.Stage;
import core.utilities.Config;
import core.utilities.text.Text;

public class Theater {
	
	/** TODO
	 * Comment
	 */
	
	/** Current Game Setup */
	private GameSetup setup;

	/** Main game loop */
	private boolean playing;
	/** Game pause */
	public boolean paused;
	/** Game Debug */
	public boolean debug;

	/** Total time a game loop takes */
	private float delta;
	/** Determines visual FPS */
	private final float deltaMax = 25f;
	/** Used in calculating FPS */
	private long currentTime;
	/** Used in calculating FPS */
	private long lastLoopTime;
	/** Current FPS */
	public static int fps = 0;
	/** Doubly current FPS? */
	private int currentfps = Camera.TARGET_FPS;
	/** Game version, appears in Window Title */
	public static String version = "v0.9";
	/** Game name, appears in Window Title */
	public static String title = "FreedomCell";
	/** Current engine framework version */
	public static final String AVOGINE_VERSION = "~0.7.12";
	
	/** Theater singleton */
	private static Theater theater;

	/** Creates a new Theater */
	public static void init() {
		theater = new Theater();
	}

	/** Returns Theater singleton */
	public static Theater get() {
		return theater;
	}

	/**	Initialize Screen, create System font, initialize Ensemble, 
	 * load any preset Configurations, and create Splash Screen.
	 */
	public Theater() {
		Camera.init();
		Text.loadFont("SYSTEM", "Modenine");
		Ensemble.init();
		Config.loadConfig();
	
		setup = new SplashScreen();
	}

	/**
	 * Core update function to handle FPS, display, ensemble, and input.
	 */
	public void update() {
		getFps();

		Camera.get().draw(getSetup());
		Camera.get().update();

		Ensemble.get().update();

		if(!paused)
			getSetup().update();

		Input.checkInput(getSetup());

		if(Camera.get().toBeClosed()) {
			close();
		}
	}

	/**
	 * Main game loop.
	 */
	public void play() {
		currentTime = getTime();

		playing = true;

		while(playing) {
			update();
		}
	}

	/**
	 * Pause or unpause the game.
	 */
	public void pause() {
		paused = !paused;
	}

	/**
	 * Save configuration, close ensemble, close screen, and exit game.
	 */
	public void close() {
		try {
			Config.saveConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(getSetup() instanceof Stage && ((Stage) getSetup()).getClient().isConnected()) {
			((Stage) getSetup()).getClient().sendData(new CloseServerPacket());
		} else if(getSetup() instanceof ServerLobby && ((ServerLobby) getSetup()).getClient() != null
				&& ((ServerLobby) getSetup()).getClient().isConnected()) {
			if(((ServerLobby) getSetup()).isHost())
				((ServerLobby) getSetup()).getClient().sendData(new CloseServerPacket());
			else
				((ServerLobby) getSetup()).getClient().sendData(new DisconnectClientPacket());
		}
		Ensemble.get().close();
		Camera.get().close();
		System.exit(0);
	}

	/**
	 * Calculate the current FPS.
	 */
	public void getFps() {
		delta = getTime() - currentTime;
		currentTime = getTime();
		lastLoopTime += delta;
		fps++;
		if(lastLoopTime >= 1000) {
			Camera.get().updateHeader();
			fps = 0;
			lastLoopTime = 0;
		}
	}

	/**
	 * @return Current game setup
	 */
	public GameSetup getSetup() {		
		return setup;
	}
	
	/**
	 * Swap to a new Game Setup.
	 * @param setup New GameSetup to swap to
	 */
	public void swapSetup(GameSetup setup) {
		this.setup = setup;
	}

	/**
	 * @return Current time in nanoseconds divided by 1,000,000
	 */
	public static long getTime() {
		return System.nanoTime() / 1000000;
	}

	/**
	 * Useful in determining delta time updates.
	 * @param speed 0.025f will give closest value to incrementing by 1 every second.
	 * @return A value scaled to current delta time of running application
	 */
	public static float getDeltaSpeed(float speed) {
		return ((1000f / Theater.get().currentfps) * speed) / Theater.get().deltaMax;
	}
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		// Determine OS and set natives path accordingly
		if(System.getProperty("os.name").startsWith("Windows")) {
			System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + "/native/windows");
		} else if(System.getProperty("os.name").startsWith("Mac")) {
			System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + "/native/macosx");
		} else if(System.getProperty("os.name").startsWith("Linux")) {
			System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + "/native/linux");
		} else {
			System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + "/native/solaris");
		}
		System.setProperty("resources", System.getProperty("user.dir") + "/resources");
		
		Theater.init();
		theater.play();
	}

}
