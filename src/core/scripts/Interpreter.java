package core.scripts;

import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import core.Camera;
import core.setups.GameSetup;
import core.setups.Stage;
import core.ui.Button;
import core.ui.TextBox;
import core.ui.UIElement;
import core.ui.compoundui.SelectionBox;

public class Interpreter {

	/** TODO
	 * Custom exceptions probably?
	 */

	public static enum InterpretedEventType {
		TEXT,
		CHOICE,
		LOAD_SCENE,
		DISPLAY_UI,
		SET_ENTITY,
		MOVE_ENTITY;
	}

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
		/*case LOAD_SCENE:
			return new InterpretedEvent(() -> doLoadScene(json));
		case DISPLAY_UI:
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

	public static JsonObject interpretConditional(JsonObject json) {
		return null;
	}
	
	public static void doShowText(Script script, JsonObject json) {
		TextBox scriptText = new TextBox(json.get("TEXT").getAsString(), json.has("FILL") ? json.get("FILL").getAsBoolean() : false);
		scriptText.addCompleteScriptListener(e -> {
			script.read();
			setup.removeElement(scriptText);
		});
		
		scriptText.setFrame("menu5");
		scriptText.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> Camera.get().getDisplayHeight(0.75f));
		setup.addUI(scriptText);
		setup.setFocus(scriptText);
	}
	
	public static void doShowChoice(Script script, JsonObject json) {
		JsonObject choiceObject = json.get("CHOICE").getAsJsonObject();
		
		SelectionBox scriptSelection = new SelectionBox();
		scriptSelection.setFrame("menu5");
		scriptSelection.setPosition(() -> Camera.get().getDisplayWidth(0.5f), () -> Camera.get().getDisplayHeight(0.75f));
		scriptSelection.getTitle().setText(choiceObject.get("TITLE").getAsString());
		
		Button choice;
		//InterpretedEvent choiceEvent;
		JsonArray choiceArray = choiceObject.get("CHOICES").getAsJsonArray();
		JsonObject resultsObject = choiceObject.get("RESULTS").getAsJsonObject();
		for(int i = 0; i < choiceArray.size(); i++) {
			String index = "" + i;
			choice = new Button(choiceArray.get(i).getAsString());
			choice.addActionListener(e -> {
				script.mergeQueue(script.fillQueue(new LinkedList<JsonObject>(), resultsObject.get(index).getAsJsonArray()));
				/*script.mergeQueue(script.fillQueue(new LinkedList<JsonObject>(), 
						choiceObject.get("RESULTS").getAsJsonObject()
						.get("" + (int) (Math.random() * choiceObject.get("CHOICES").getAsJsonArray().size())).getAsJsonArray()));
				*/
				setup.removeElement(scriptSelection);
			});
			scriptSelection.addChoice(choice);
		}
		setup.addUI(scriptSelection);
		// InterpretedEvent should look at scriptSelection object and grab whatever it's selection is. Perhaps beyond some weird keylistener on the
		// element itself and just a basic function like "what's being looked at"
		
		
		/*ElementGroup optionGroup = new ElementGroup();
		TextBox scriptText = new TextBox(choiceObject.get("TITLE").getAsString(), choiceObject.has("FILL"));
		optionGroup.addUI(scriptText);
		Button choice;
		for(JsonElement element : choiceObject.get("CHOICES").getAsJsonArray()) {
			choice = new Button(element.getAsString());
			optionGroup.addUI(choice);
		}
		optionGroup.setFrame("menu5");
		if(script.getSource().hasBody()) {
			scriptText.setPosition(() -> Camera.get().convertXCoordinateToScreenX(script.getSource().getBody().getPosition().x),
					() -> Camera.get().convertYCoordinateToScreenY(script.getSource().getBody().getPosition().y));
		}
		InterpretedEvent choiceEvent = new InterpretedEvent(() -> setup.addUI(optionGroup), () -> {
			script.mergeQueue(script.fillQueue(new LinkedList<JsonObject>(), 
					choiceObject.get("RESULTS").getAsJsonObject()
					.get("" + (int) (Math.random() * choiceObject.get("CHOICES").getAsJsonArray().size())).getAsJsonArray()));
			
			setup.removeElement(scriptText);
		});*/
		//return choiceEvent;
	}
	
	public static void doLoadScene(JsonObject json) {
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
	
	private static void processConditional(JsonObject json) {
		
	}
	
}
