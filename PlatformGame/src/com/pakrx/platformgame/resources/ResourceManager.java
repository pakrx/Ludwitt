package com.pakrx.platformgame.resources;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.pakrx.platformgame.Game;
import com.pakrx.platformgame.gameplay.Level;
import com.pakrx.platformgame.gameplay.characters.EnemyCharacter;
import com.pakrx.platformgame.gameplay.characters.PlayerCharacter;
import com.pakrx.platformgame.gameplay.objects.GameObject;
import com.pakrx.platformgame.graphics.Tile;
import com.pakrx.platformgame.graphics.TileMap;
import com.pakrx.platformgame.sound.SoundFile;

public class ResourceManager {
	
	public final static int ENGLISH = 1;
	
	public final static String MAIN_MODULE = "main";
	public final static String DEFAULT_LEVEL = "01-tutorial-01";
	public final static String SEPARATOR = ": ";
	public final static String FILE_EXTENSION = ".pkr";
	
	private static String moduleName = MAIN_MODULE;
	private static String levelName = DEFAULT_LEVEL;
	
	private static int language = ENGLISH;
	private static Map<String, String> strings = new HashMap<String, String>();
	private static Map<String, Integer> languages = new HashMap<String, Integer>();
	
	private static Map<String, List<String>> objectsFilesCache = new HashMap<String, List<String>>();
	
	public static void readStrings() {
		readLanguages();
		readStringsData();	
	}
	
	public static void readLevel(Level level) {
		readTilesIntoLevel(level);
		readObjectsIntoLevel(level);
		readEnemiesIntoLevel(level);
	}
	
	private static void readTilesIntoLevel(Level level) {
		ArrayList<String> files = getFiles("tiles");
		Map<Integer, String> colorsFilenames = getColorsFilenames(files);
		Map<Integer, Tile> colorsTiles = getColorsTiles(colorsFilenames);
		BufferedImage tileMapImage = readTileMapLayoutImage();
		TileMap tileMap = new TileMap();
		tileMap.setSize(tileMapImage.getWidth(), tileMapImage.getHeight());
		for (int x = 0; x < tileMapImage.getWidth(); x++) {
			for (int y = 0; y < tileMapImage.getHeight(); y++) {
				int rgb = tileMapImage.getRGB(x, y);
				if (colorsTiles.containsKey(rgb)) {
					tileMap.getTiles()[x][y] = colorsTiles.get(rgb);
				}
			}
		}
		level.setTileMap(tileMap);
	}
	
	private static void readObjectsIntoLevel(Level level) {
		ArrayList<String> objectFiles = getFiles("objects");
		Map<Integer, String> colorsFilenames = getColorsFilenames(objectFiles);
		Map<Integer, Dimension> colorsSizes = getColorsSizes(colorsFilenames);
		BufferedImage tileMapImage = readTileMapItemsImage();
		int tilex;
		int tiley;
		for (int x = 0; x < tileMapImage.getWidth(); x++) {
			for (int y = 0; y < tileMapImage.getHeight(); y++) {
				int rgb = tileMapImage.getRGB(x, y);
				if (colorsFilenames.containsKey(rgb)) {
					int width = colorsSizes.get(rgb).width;
					int height = colorsSizes.get(rgb).height;
					boolean isLeftTopCorner = true;
					for (int sx = x; sx < x + width ; sx++) {
						for (int sy = y; sy < y + height; sy++) {
							if (tileMapImage.getRGB(sx, sy) != rgb) {
								isLeftTopCorner = false;
								break;
							}
						}
					}
					if (!isLeftTopCorner) {
						continue;
					}
					GameObject gameObject = createGameObjectFromFile(colorsFilenames.get(rgb));
					if (gameObject != null) {
						tilex = x * TileMap.TILE_WIDTH - 1;
						tiley = y * TileMap.TILE_HEIGHT - 1;
						gameObject.setLocation(tilex, tiley);
						gameObject.setInitialPosition(tilex, tiley);
						level.addGameObject(gameObject);
					}
				}
			}
		}
	}
	
	private static void readEnemiesIntoLevel(Level level) {
		ArrayList<String> files = getFiles("enemies");
		Map<Integer, String> colorsFilenames = getColorsFilenames(files);
		Map<Integer, Dimension> colorsSizes = getColorsSizes(colorsFilenames);
		BufferedImage tileMapImage = readTileMapItemsImage();
		int tilex;
		int tiley;
		for (int x = 0; x < tileMapImage.getWidth(); x++) {
			for (int y = 0; y < tileMapImage.getHeight(); y++) {
				int rgb = tileMapImage.getRGB(x, y);
				if (colorsFilenames.containsKey(rgb)) {
					int width = colorsSizes.get(rgb).width;
					int height = colorsSizes.get(rgb).height;
					boolean isLeftTopCorner = true;
					for (int sx = x; sx < x + width; sx++) {
						for (int sy = y; sy < y + height; sy++) {
							if (tileMapImage.getRGB(sx, sy) != rgb) {
								isLeftTopCorner = false;
								break;
							}
						}
					}
					if (!isLeftTopCorner) {
						continue;
					}
					EnemyCharacter enemyCharacter = createEnemyCharacterFromFile(colorsFilenames.get(rgb));
					if (enemyCharacter != null) {
						tilex = x * TileMap.TILE_WIDTH - 1;
						tiley = y * TileMap.TILE_HEIGHT - 1;
						enemyCharacter.setLocation(tilex, tiley);
						level.addEnemyCharacter(enemyCharacter);
					}
				}
			}
		}
	}
	
	public static PlayerCharacter readPlayerCharacter() {
		ArrayList<String> files = getFiles("player");
		Map<Integer, String> colorsFilenames = getColorsFilenames(files);
		Map<Integer, Dimension> colorsSizes = getColorsSizes(colorsFilenames);
		BufferedImage tileMapImage = readTileMapItemsImage();
		int tilex;
		int tiley;
		
		for (int x = 0; x < tileMapImage.getWidth(); x++) {
			for (int y = 0; y < tileMapImage.getHeight(); y++) {
				int rgb = tileMapImage.getRGB(x, y);
				if (colorsFilenames.containsKey(rgb)) {
					int width = colorsSizes.get(rgb).width;
					int height = colorsSizes.get(rgb).height;
					boolean isLeftTopCorner = true;
					for (int sx = x; sx < x + width; sx++) {
						for (int sy = y; sy < y + height; sy++) {
							if (tileMapImage.getRGB(sx, sy) != rgb) {
								isLeftTopCorner = false;
								break;
							}
						}
					}
					if (!isLeftTopCorner) {
						continue;
					}
					PlayerCharacter playerCharacter = createPlayerCharacterFromFile(colorsFilenames.get(rgb));
					if (playerCharacter != null) {
						tilex = x * TileMap.TILE_WIDTH - 1;
						tiley = y * TileMap.TILE_HEIGHT - 1;
						playerCharacter.setLocation(tilex, tiley);
						return playerCharacter;
					}
				}
			}
		}
		return null;
	}
	
	private static Map<Integer, Dimension> getColorsSizes(Map<Integer, String> colorsObjects) {
		Map<Integer, Dimension> colorsSizes = new HashMap<Integer, Dimension>();
		String filename;
		for (int color : colorsObjects.keySet()) {
			filename = colorsObjects.get(color);
			colorsSizes.put(color, getSizeFromGameObjectFile(filename));
		}
		return colorsSizes;
	}

	private static GameObject createGameObjectFromFile(String filename) {
		String objectClassName = null;
		List<String> lines = null;
		if (objectsFilesCache.containsKey(filename)) {
			lines = objectsFilesCache.get(filename);
		} else {
			try {
				lines = Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8"));
				objectsFilesCache.put(filename, lines);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (lines == null) {
			return null;
		}
		for (String line : lines) {
			if (line.trim().length() > 0 && line.startsWith("TYPE: ")) {
				objectClassName = line.split(": ")[1];
			}
		}
		try {
			Class<?> objectClass = Class.forName(String.format("com.pakrx.platformgame.gameplay.objects.%s", objectClassName));
			GameObject gameObject = (GameObject)objectClass.newInstance();
			gameObject.readFromFile(filename);
			return gameObject;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static GameObject createGameObjectFromSaveFile(String filename, int linenumber) {
		String objectClassName = null;
		try {
			List<String> lines = Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8"));
			String line = null;
			line = lines.get(linenumber);
			objectClassName = line.split(": ")[1];
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (objectClassName == null) {
			return null;
		}
		try {
			Class<?> objectClass = Class.forName(String.format("com.pakrx.platformgame.gameplay.objects.%s", objectClassName));
			GameObject gameObject = (GameObject)objectClass.newInstance();
			gameObject.readFromSaveFile(filename, linenumber);
			return gameObject;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
			return null;
		}
	}
	
	private static PlayerCharacter createPlayerCharacterFromFile(String filename) {
		String objectClassName = null;
		try {
			for (String line : Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8"))) {
				if (line.trim().length() > 0 && line.startsWith("TYPE: ")) {
					objectClassName = line.split(": ")[1];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (objectClassName == null) {
			return null;
		}
		try {
			Class<?> objectClass = Class.forName(String.format("com.pakrx.platformgame.gameplay.characters.%s", objectClassName));
			PlayerCharacter playerCharacter = (PlayerCharacter)objectClass.newInstance();
			playerCharacter.readFromFile(filename);
			return playerCharacter;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
			return null;
		}
	}
	
	private static EnemyCharacter createEnemyCharacterFromFile(String filename) {
		String objectClassName = null;
		try {
			for (String line : Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8"))) {
				if (line.trim().length() > 0 && line.startsWith("TYPE: ")) {
					objectClassName = line.split(": ")[1];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (objectClassName == null) {
			return null;
		}
		try {
			Class<?> objectClass = Class.forName(String.format("com.pakrx.platformgame.gameplay.characters.%s", objectClassName));
			EnemyCharacter enemyCharacter = (EnemyCharacter)objectClass.newInstance();
			enemyCharacter.readFromFile(filename);
			return enemyCharacter;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
			return null;
		}
	}
	
	private static Map<Integer, Tile> getColorsTiles(Map<Integer, String> colorsFilenames) {
		Map<Integer, Tile> colorsTiles = new HashMap<Integer, Tile>();
		for (Integer rgb : colorsFilenames.keySet()) {
			Tile tile = new Tile();
			tile.readFromFile(colorsFilenames.get(rgb));
			colorsTiles.put(rgb, tile);
		}
		return colorsTiles;
	}
	
	private static int getRGBFromFile(String filename) {
		try {
			for (String line : Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8"))) {
				if (line.trim().length() > 0 && line.startsWith("COLOR: ")) {
					String colorString = line.split(": ")[1];
					return (int)Long.parseLong(colorString, 16);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private static Dimension getSizeFromGameObjectFile(String filename) {
		try {
			for (String line : Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8"))) {
				if (line.trim().length() > 0 && line.startsWith("SIZE: ")) {
					String sizeString = line.split(": ")[1];
					int x = Integer.parseInt(sizeString.split("x")[0]);
					int y = Integer.parseInt(sizeString.split("x")[1]);
					return new Dimension(x, y);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Dimension(1, 1);
	}
	
	private static Map<Integer, String> getColorsFilenames(ArrayList<String> files) {
		Map<Integer, String> colorsFilenames = new HashMap<Integer, String>();
		for (String filename : files) {
			colorsFilenames.put(getRGBFromFile(filename), filename);
		}
		return colorsFilenames;
	}
	
	private static ArrayList<String> getFiles(String folderName) {
		ArrayList<String> filenames = new ArrayList<String>();
		File mainModuleFolder = new File(String.format("modules/%s/%s", MAIN_MODULE, folderName));
		if (mainModuleFolder.listFiles() == null) {
			return filenames;
		}
		for (File f : mainModuleFolder.listFiles()) {
			if (f.isFile() && f.getName().substring(f.getName().length() - 4, f.getName().length()).equals(FILE_EXTENSION)) {
				filenames.add(f.getPath());
			}
		}
		if (!moduleName.equals(MAIN_MODULE)) {
			File moduleFolder = new File(String.format("modules/%s/%s", moduleName, folderName));
			if (moduleFolder.isDirectory()) {
				for (File f : moduleFolder.listFiles()) {
					if (f.isFile() && f.getName().substring(f.getName().length() - FILE_EXTENSION.length(), f.getName().length()).equals(FILE_EXTENSION)) {
						filenames.add(f.getPath());
					}
				}
			}
		}
		File levelFolder = new File(String.format("modules/%s/levels/%s/%s", moduleName, levelName, folderName));
		if (levelFolder.isDirectory()) {
			for (File f : levelFolder.listFiles()) {
				if (f.isFile() && f.getName().substring(f.getName().length() - FILE_EXTENSION.length(), f.getName().length()).equals(FILE_EXTENSION)) {
					filenames.add(f.getPath());
				}
			}
		}
		return filenames;
	}
	
	public static BufferedImage readImage(String imgPath) {
		String path;
		File imgFile;
		path = String.format("modules/%s/levels/%s/images/%s", moduleName, levelName, imgPath);
		imgFile = new File(path);
		
		if (!imgFile.exists()) {
			path = String.format("modules/%s/images/%s", moduleName, imgPath);
			imgFile = new File(path);
		}
		if (!imgFile.exists()) {
			path = String.format("modules/main/images/%s", moduleName, imgPath);
			imgFile = new File(path);
		}
		FileInputStream imgStream = null;
		BufferedImage img = null;
		try {
			imgStream = new FileInputStream(imgFile);
		} catch (FileNotFoundException e) {
			return null;
		}
		try {
			img = ImageIO.read(imgStream);
		} catch (IOException e) {
			return null;
		}
		try {
			imgStream.close();
		} catch (IOException e) {
			return null;
		}
		return img;
	}
	
	public static BufferedImage readTileMapLayoutImage() {
		String path;
		File imgFile;
		path = String.format("modules/%s/levels/%s/layout.png", moduleName, levelName);
		imgFile = new File(path);
		if (!imgFile.exists()) {
			path = String.format("modules/main/levels/%s/layout.png", levelName);
			imgFile = new File(path);
		}
		FileInputStream imgStream = null;
		BufferedImage img = null;
		try {
			imgStream = new FileInputStream(imgFile);
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
		if (imgStream == null) {
			return null;
		}
		try {
			img = ImageIO.read(imgStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			imgStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	public static BufferedImage readTileMapItemsImage() {
		String path;
		File imgFile;
		path = String.format("modules/%s/levels/%s/items.png", moduleName, levelName);
		imgFile = new File(path);
		if (!imgFile.exists()) {
			path = String.format("modules/main/levels/%s/items.png", moduleName, levelName);
			imgFile = new File(path);
		}
		FileInputStream imgStream = null;
		BufferedImage img = null;
		try {
			imgStream = new FileInputStream(imgFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (imgStream == null) {
			return null;
		}
		try {
			img = ImageIO.read(imgStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			imgStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
	
	public static SoundFile readSoundFile(String soundPath) {
		String path;
		File soundFile;
		path = String.format("modules/%s/levels/%s/sounds/%s", moduleName, levelName, soundPath);
		soundFile = new File(path);
		if (!soundFile.exists()) {
			path = String.format("modules/%s/sounds/%s", moduleName, soundPath);
			soundFile = new File(path);
		}
		if (!soundFile.exists()) {
			path = String.format("modules/main/sounds/%s",soundPath);
			soundFile = new File(path);
		}
		SoundFile sndFile = new SoundFile(path);
		return sndFile;
	}
	
	public static void setModule(String moduleName) {
		ResourceManager.moduleName = moduleName;
	}
	
	private static void readLanguages() {
		languages = new HashMap<String, Integer>();
		readLanguagesFromFile(String.format("modules/%s/strings.pkr", MAIN_MODULE));
		if (!moduleName.equals(MAIN_MODULE)) {
			readLanguagesFromFile(String.format("modules/%s/strings.pkr", moduleName));
		}
	}
	
	private static void readLanguagesFromFile(String filename) {
		ArrayList<String> lines = null;
		try {
			lines = new ArrayList<String>(Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (lines != null) {
			String line = lines.get(0);
			if (line.trim().length() > 0) {
				String[] entries = line.trim().split(SEPARATOR);
				for (int i = 0; i < entries.length; i++) {
					if (!languages.keySet().contains(entries[i])) {
						languages.put(entries[i], i + 1);
					}
				}
				
			}
		}
	}
	
	public static void saveGame(Game game) {
		String filename = String.format("modules/%s/saves/save.pkr", moduleName);
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (writer == null) {
			return;
		}
		// level 
		writer.write("LEVEL: ");
		writer.write(levelName);
		writer.write("\n");

		// character energy
		writer.write("ENERGY: ");
		writer.write(String.valueOf(game.getPlatformWorld().getPlayerCharacter().getEnergy()));
		writer.write("\n");
		
		// character speed
		writer.write("VELOCITY: ");
		writer.write(String.valueOf(game.getPlatformWorld().getPlayerCharacter().getVelocityX()));
		writer.write(": ");
		writer.write(String.valueOf(game.getPlatformWorld().getPlayerCharacter().getVelocityY()));
		writer.write("\n");
		
		// character position
		writer.write("POSITION: ");
		writer.write(String.valueOf(game.getPlatformWorld().getPlayerCharacter().getMapX()));
		writer.write(": ");
		writer.write(String.valueOf(game.getPlatformWorld().getPlayerCharacter().getMapY()));
		writer.write("\n");
		
		// items in level
		for (GameObject gameObject : game.getPlatformWorld().getLevel().getGameObjects()) {
			gameObject.writeToSaveFile(writer);
		}
		writer.close();
		
	}
	
	public static boolean loadGame(Game game) {
		String filename = String.format("modules/%s/saves/save.pkr", moduleName);
		ArrayList<String> lines = null;
		try {
			lines = new ArrayList<String>(Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8")));
		} catch (IOException e) {
			return false;
		}
		String readLevelName = null;
		int readEnergy = 100;
		float readVX = 0.0f;
		float readVY = 0.0f;
		float readX = 100.0f;
		float readY = 100.0f;
		ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
		if (lines != null) {
			String line = null;
			int i = 0;
			for (i = 0; i < lines.size(); i++) {
				line = lines.get(i);
				String[] splitLine = line.split(": ");
				if (line.trim().length() > 0) {
					if (line.startsWith("LEVEL:") && splitLine.length > 1) {
						readLevelName = splitLine[1];
						levelName = readLevelName;
					}
					if (line.startsWith("ENERGY:") && splitLine.length > 1) {
						readEnergy = Integer.valueOf(splitLine[1]);
					}
					if (line.startsWith("VELOCITY: ") && splitLine.length > 2) {
						readVX = Float.valueOf(splitLine[1]);
						readVY = Float.valueOf(splitLine[2]);
					}
					if (line.startsWith("POSITION: ") && splitLine.length > 2) {
						readX = Float.valueOf(splitLine[1]);
						readY = Float.valueOf(splitLine[2]);
					}
					if (line.startsWith("OBJECT: ")) {
						GameObject gameObject = createGameObjectFromSaveFile(filename, i);
						if (gameObject != null) {
							gameObjects.add(gameObject);	
						}	
					}
				}
			}
		}
		
		Level level = new Level(readLevelName);
		levelName = readLevelName;
		readTilesIntoLevel(level);
		game.getPlatformWorld().setLevel(level);
		game.getPlatformWorld().getPlayerCharacter().setEnergy(readEnergy);
		game.getPlatformWorld().getPlayerCharacter().setVelocity(readVX, readVY);
		game.getPlatformWorld().getPlayerCharacter().setLocation(readX, readY);
		for (GameObject gameObject : gameObjects) {
			game.getPlatformWorld().getLevel().addGameObject(gameObject);
		}
		return true;
	}
	
	public static void setLevel(String levelName) {
		ResourceManager.levelName = levelName;
	}
	
	private static void readStringsData() {
		strings = new HashMap<String, String>();
		readStringsFromFile(String.format("modules/%s/strings.pkr", MAIN_MODULE));
		if (!moduleName.equals(MAIN_MODULE)) {
			readStringsFromFile(String.format("modules/%s/strings.pkr", moduleName));
		}
	}
	
	private static void readStringsFromFile(String filename) {
		if (languages.values().contains(language)) {
			ArrayList<String> lines = null;
			try {
				lines = new ArrayList<String>(Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (lines != null) {
				lines.remove(0);
				for (String line : lines) {
					if (line.trim().length() > 0) {
						String[] entries = line.trim().split(SEPARATOR);
						if (strings.containsKey(entries[0])) {
							strings.remove(entries[0]);
						}
						strings.put(entries[0], entries[language]);
					}
				}
			}
		}
	}
	
	public static String getString(String id) {
		String s = strings.get(id);
		if (s != null) {
			return strings.get(id);
		} else {
			return id;
		}
	}
}

