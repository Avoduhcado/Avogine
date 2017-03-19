package core;

import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;

import core.scripts.Interpreter;
import core.setups.GameSetup;
import core.setups.SplashScreen;
import core.ui.event.TimeEvent;
import core.utilities.Config;
import core.utilities.text.Text;

public class Theater {

	/** TODO
	 * Comment
	 * 
	 * Options for resource loading and interface to manage it
	 * 
	 * Save classes to manage save files
	 * 
	 * Default UI assets
	 * 
	 * Camera functions and stuff
	 * 
	 * Redo audio implementation to use openAL
	 * 
	 * UI Consumable events
	 */

	/** Current Game Setup */
	private GameSetup setup;

	/** Main game loop */
	private boolean playing;
	/** Game Debug */
	public static boolean debug;

	/** Total time a game loop takes */
	private float delta;
	/** Determines visual FPS */
	private final float deltaMax = 25f;
	/** Used in calculating FPS */
	private long currentTime;
	/** Used in calculating FPS */
	private long lastLoopTime;
	/** Displayed FPS */
	public static int fps = 0;
	/** Second-by-second FPS */
	private int currentfps = Camera.TARGET_FPS;
	/** Game version, appears in Window Title */
	public static String version = "v0.0";
	/** Game name, appears in Window Title */
	public static String title = "Avogine";
	/** Current engine framework version */
	public static final String AVOGINE_VERSION = "0.8.12";
	
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
		
		Camera.init();
		SoundStore.get().init();
		Text.loadFont("DEFAULT", "Benegraphic");
		Text.loadFont("DEBUG", "Pixeled");
		Config.loadConfig();
		
		setSetup(new SplashScreen());
	}

	/**
	 * Core update function to handle FPS, display, ensemble, and input.
	 */
	public void update() {
		getFps();
		
		Camera.get().draw(getSetup());
		Camera.get().update();

		AudioLoader.update();
		
		getSetup().update();
		
		Input.get().checkInput(getSetup());

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
	 * Save configuration, destroy audio, destroy display, and exit game.
	 */
	public void close() {
		Config.createConfig();
		AL.destroy();
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
		
		setup.fireEvent(new TimeEvent(delta / 1000));
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
	public void setSetup(GameSetup setup) {
		this.setup = setup;
		Interpreter.registerSetup(this.setup);
	}

	/**
	 * @return Current time in nanoseconds divided by 1,000,000
	 */
	public static long getTime() {
		return System.nanoTime() / 1_000_000L;
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
		Theater.init();
		theater.play();
	}

}
