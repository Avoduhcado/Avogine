package core.scripts;

import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import core.Camera;
import core.setups.GameSetup;
import core.setups.Stage;
import core.ui.Button;
import core.ui.InputBox;
import core.ui.TextBox;
import core.ui.UIElement;
import core.ui.overlays.SelectionMenu;
import core.ui.utils.HorizontalAlign;
import core.ui.utils.InputStyle;

public class Interpreter {

	/** TODO
	 * Custom exceptions probably?
	 */

	public static enum InterpretedEventType {
		TEXT,
		CHOICE,
		INPUT,
		LOAD_SCENE,
		DISPLAY_UI,
		SET_ENTITY,
		MOVE_ENTITY;
	}
	
	// TODO Pass along a variable that you can modify for storing something like input?

	private static GameSetup setup;

	public static void registerSetup(GameSetup setup) {
		Interpreter.setup = setup;
	}
	
	public static void interpret(Script script, JsonObject json) {
		System.out.println(json.toString());
		switch(InterpretedEventType.valueOf(json.entrySet().iterator().next().getKey().toUpperCase())) {
		case TEXT:
			doShowText(script, json);
			break;
		case CHOICE:
			doShowChoice(script, json);
			break;
		case INPUT:
			doShowInput(script, json);
			break;
		case LOAD_SCENE:
			doLoadScene(script, json);
			break;
		/*case DISPLAY_UI:
			return new InterpretedEvent(() -> doDisplayUI(json));
		case SET_ENTITY:
			return new InterpretedEvent(() -> doSetEntity(json));
		case MOVE_ENTITY:
			return new InterpretedEvent(() -> doMoveEntity(json));*/
		default:
			System.out.println("Malformed event: " + json);
			script.read();
		}
		
		//return null;
	}

	public static void doShowText(Script script, JsonObject json) {
		TextBox scriptText = new TextBox(json.get("TEXT").getAsString(), json.has("FILL") ? json.get("FILL").getAsBoolean() : false);
		scriptText.addCompleteScriptListener(e -> {
			script.read();
			setup.removeElement(scriptText);
		});
		
		scriptText.setFrame("menu5");
		scriptText.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> Camera.get().getDisplayHeight(0.75f));
		scriptText.setHorizontalAlign(HorizontalAlign.CENTER);
		setup.addUI(scriptText);
		setup.setFocus(scriptText);
	}
	
	public static void doShowChoice(Script script, JsonObject json) {
		JsonObject choiceObject = json.get("CHOICE").getAsJsonObject();

		SelectionMenu scriptMenu = new SelectionMenu(SelectionMenu.HORIZONTAL);
		scriptMenu.setTitle(choiceObject.get("TITLE").getAsString());
		
		JsonArray choiceArray = choiceObject.get("CHOICES").getAsJsonArray();
		JsonObject resultsObject = choiceObject.get("RESULTS").getAsJsonObject();

		Button choiceButton;
		for(int i = 0; i < choiceArray.size(); i++) {
			String index = "" + i;
			choiceButton = new Button(choiceArray.get(i).getAsString());
			choiceButton.addActionListener(e -> {
				script.mergeQueue(script.fillQueue(new LinkedList<JsonObject>(), resultsObject.get(index).getAsJsonArray()));
				script.read();
				setup.removeElement(scriptMenu);
			});
			scriptMenu.addOptionButton(choiceButton);
		}
		setup.addUI(scriptMenu);
		setup.setFocus(scriptMenu);
	}
	
	public static void doShowInput(Script script, JsonObject json) {
		InputStyle style = json.has("STYLE") ? InputStyle.valueOf(json.get("STYLE").getAsString()) : InputStyle.PLAIN_TEXT;
		InputBox scriptInput = new InputBox(json.has("PREFILL") ? json.get("PREFILL").getAsString() : null, style,
				json.has("LIMIT") ? json.get("LIMIT").getAsInt() : 0);
		scriptInput.addCompleteScriptListener(e -> {
			script.read();
			// TODO Support some kind of custom variable saving? Potentially an available variable map to reference and effect?
			// Or just storing an Object in Script that you can retrieve whenever you want, not sure
			//script.saveValue(scriptInput.getText());
			setup.removeElement(scriptInput);
		});
		
		scriptInput.setFrame("menu5");
		scriptInput.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> Camera.get().getDisplayHeight(0.75f));
		scriptInput.setHorizontalAlign(HorizontalAlign.CENTER);
		setup.addUI(scriptInput);
		setup.setFocus(scriptInput);
	}
	
	public static void doLoadScene(Script script, JsonObject json) {
		if(!currentlyOnStage()) {
			return;
		}
	}
	
	public static UIElement doDisplayUI(JsonObject json) {
		return null;
	}
	
	public static void doSetEntity(JsonObject json) {
		
	}
	
	public static void doMoveEntity(JsonObject json) {
		
	}
	
	private static boolean currentlyOnStage() {
		return setup instanceof Stage;
	}
	
}
