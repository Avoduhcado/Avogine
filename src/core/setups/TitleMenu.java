package core.setups;

import java.io.IOException;

import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

import core.Camera;
import core.Theater;
import core.render.effects.TintEffect;
import core.render.effects.Tween;
import core.ui.Button;
import core.ui.ElementGroup;
import core.ui.Icon;
import core.ui.overlays.OptionsMenu;
import core.ui.utils.HorizontalAlign;
import core.ui.utils.VerticalAlign;

public class TitleMenu extends GameSetup {
	
	private Audio menuTheme;
	
	/**
	 * Title Menu
	 * Set up buttons for game operation.
	 */
	public TitleMenu() {
		// Ensure fading has reset
		Camera.get().addScreenEffect(new TintEffect(new Vector4f(0f, 0f, 0f, 0f), 0f, true, Tween.IN));
		
		// Load title logo
		Icon logo = new Icon("Avogine Title");
		logo.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> Camera.get().getDisplayHeight(0.2667f));
		logo.setAlignments(VerticalAlign.CENTER, HorizontalAlign.CENTER);
		addUI(logo);

		// Temp to test world gen stuff
		Button worldGen = new Button("World Gen (Temp)");
		worldGen.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> Camera.get().getDisplayHeight(0.55f));
		worldGen.setHorizontalAlign(HorizontalAlign.CENTER);
		worldGen.addActionListener(e -> Theater.get().setSetup(new TestWorldGenerator()));
		
		// Initialize game buttons
		Button newGame = new Button("New Game");
		newGame.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> worldGen.getBounds().getMaxY());
		newGame.setHorizontalAlign(HorizontalAlign.CENTER);
		newGame.addActionListener(e -> Theater.get().setSetup(new Stage()));
		
		Button options = new Button("Options");
		options.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> newGame.getBounds().getMaxY());
		options.setHorizontalAlign(HorizontalAlign.CENTER);
		options.addActionListener(e -> {
			OptionsMenu optionsMenu = new OptionsMenu();
			addUI(optionsMenu);
			setFocus(optionsMenu);
		});
		
		Button exit = new Button("Exit");
		exit.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> options.getBounds().getMaxY());
		exit.setHorizontalAlign(HorizontalAlign.CENTER);
		exit.addActionListener(e -> Theater.get().close());

		// Initialize game buttons
		worldGen.setSurrounding(3, newGame);
		newGame.setSurrounding(3, options);
		options.setSurrounding(3, exit);
		exit.setSurrounding(3, worldGen);
		
		ElementGroup buttons = new ElementGroup();
		buttons.addUI(worldGen);
		buttons.addUI(newGame);
		buttons.addUI(options);
		buttons.addUI(exit);
		buttons.setKeyboardNavigable(true, worldGen);
		//buttons.setSelectionPointer("screen ui/Pointer");
		buttons.setFrame("Menu2");
		addUI(buttons);
		setFocus(buttons);
						
		// Play title track
		try {
			menuTheme = AudioLoader.getStreamingAudio("OGG", ResourceLoader.getResource(
					System.getProperty("resources") + "/music/" + "Menu" + ".ogg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		menuTheme.playAsMusic(1f, 1f, true);
	}
	
	@Override
	public void update() {
	}
	
	@Override
	public void draw() {
	}

}
