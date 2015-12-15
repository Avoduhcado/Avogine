package core.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.newdawn.slick.openal.SoundStore;

import core.utilities.keyboard.Keybind;

public class Config {
	
	public static void loadConfig() {
		try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/config"))) {

	    	String line;
	    	while((line = reader.readLine()) != null) {
	    		if(line.matches("<AUDIO>")) {
	    			while((line = reader.readLine()) != null && !line.matches("<END>")) {
		    			String[] temp = line.split("=");
		    			if(temp[0].matches("bgmvolume")) {
		    				SoundStore.get().setMusicVolume(Float.parseFloat(temp[1]));
		    				SoundStore.get().setCurrentMusicVolume(Float.parseFloat(temp[1]));
		    			} else if(temp[1].matches("sfxvolume")) {
		    				SoundStore.get().setSoundVolume(Float.parseFloat(temp[1]));
		    			}
	    			}
	    		} else if(line.matches("<VIDEO>")) {
	    			// TODO Custom video settings
	    		} else if(line.matches("<KEYS>")) {
	    			while((line = reader.readLine()) != null && !line.matches("<END>")) {
	    				Keybind.changeBind(line);
	    			}
	    		}
	    	}

	    } catch (FileNotFoundException e) {
	    	System.out.println("Creating config file.");
	    	createConfig();
	    } catch (IOException e) {
	    	System.out.println("Config file failed to load!");
	    	e.printStackTrace();
	    }
	}
	
	public static void createConfig() {
		File configFile = new File(System.getProperty("user.dir") + "/config");
		
		if(configFile.exists()) {
			configFile.delete();
		}
		
		try {
			if(configFile.createNewFile()) {
				saveConfig(configFile);
			} else {
				System.err.println("Config file was not created");
			}
		} catch(IOException e) {
			System.err.println("Config file failed to be created.");
			e.fillInStackTrace();
		}
	}
	
	public static void saveConfig(File current) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(current))) {

			writer.write("<AUDIO>");
			writer.newLine();
			writer.write("bgmvolume=" + SoundStore.get().getMusicVolume());
			writer.newLine();
			writer.write("sfxvolume=" + SoundStore.get().getSoundVolume());
			writer.newLine();
			writer.write("<END>");
			writer.newLine();
			writer.newLine();
			
			writer.write("<KEYS>");
			writer.newLine();
			for(int x = 0; x<Keybind.values().length; x++) {
				writer.write(Keybind.values()[x].name() + "=" + Keybind.values()[x].getKey());
				writer.newLine();
			}
			writer.write("<END>");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
