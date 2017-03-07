package core.scripts;

import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import core.entities.Entity;

public class Script {
	private static final String OPTIONS = "OPTIONS";
	private static final String START_EVENT = "EVENT";
	
	private static final JsonObject DEFAULT_SCRIPT = new JsonParser().parse("{" + START_EVENT + ": null}").getAsJsonObject();
	public static final String testScript = "{\"EVENT\":[{\"TEXT\":\"Hello friends\"},{\"CHOICE\":{\"TITLE\":\"How are you?\",\"CHOICES\":[\"Good\",\"Bad\"],\"RESULTS\":{\"0\":[{\"TEXT\":\"Dope\"}],\"1\":[{\"TEXT\":\"Darn\"},{\"TEXT\":\"That sucks\"}]}}},{\"TEXT\":\"Well, see ya later.\"}]}";
	
	private Entity source;
	private Entity reader;
	
	private JsonObject parsedData;
	private Queue<JsonObject> eventQueue = new LinkedList<>();
	
	private boolean reading;
		
	public Script(Entity source, String data) {
		this.source = source;
		parseData(data);
		fillQueue();
	}

	private void parseData(String data) {
		try {
			parsedData = new JsonParser().parse(data).getAsJsonObject();
		} catch(JsonSyntaxException | IllegalStateException e) {
			System.err.println("Failed to parse script for: " + data);
			e.printStackTrace();
			parsedData = DEFAULT_SCRIPT.getAsJsonObject();
		}
	}
	
	public Queue<JsonObject> fillQueue(Queue<JsonObject> queue, JsonArray array) {
		queue.clear();
		for(JsonElement e : array) {
			queue.add(e.getAsJsonObject());
		}
		return queue;
	}
	
	public void fillQueue() {
		fillQueue(eventQueue, parsedData.get(START_EVENT).getAsJsonArray());
	}
	
	public void mergeQueue(Queue<JsonObject> newQueue) {
		newQueue.addAll(eventQueue);
		eventQueue = newQueue;
	}

	public void startReading(Entity reader) {
		setReading(true);
		setReader(reader);
		resetEvent();
		
		read();
	}

	public void read() {
		// If the event is over entirely, cancel the reading
		if(eventQueue.isEmpty()) {
			endReading();
			return;
		}
		
		// Interpret the next event and perform it
		/*Scriptable scriptEvent = */Interpreter.interpret(this, eventQueue.poll());
		//scriptEvent.perform();
		
		
		// Perform close action on previous event
		/*if(scriptEvent != null && scriptEvent.getCloseAction() != null) {
			scriptEvent.getCloseAction().perform();
		}
		
		// If the event is over entirely, cancel the reading
		if(eventQueue.isEmpty()) {
			endReading();
			return;
		}
		
		// Interpret the next event and perform it
		scriptEvent = Interpreter.interpret(this, eventQueue.poll());
		if(scriptEvent.getAction() != null) {
			scriptEvent.getAction().perform();
		}
		
		// If an event has an auto complete action, run it and read the next event
		if(scriptEvent.getAutoCompleteAction() != null) {
			scriptEvent.getAutoCompleteAction().perform();
			read();
		}*/
	}

	public void endReading() {
		setReading(false);
	}

	public void interrupt() {
		setReading(false);
		setReader(null);
	}

	public boolean isBusyReading() {
		return reading;
	}
	
	private void resetEvent() {
		fillQueue();
	}
	
	private void setReading(boolean reading) {
		this.reading = reading;
	}

	private void setReader(Entity reader) {
		this.reader = reader;
	}
	
	public Entity getSource() {
		return source;
	}
	
	public Entity getReader() {
		return reader;
	}

}
