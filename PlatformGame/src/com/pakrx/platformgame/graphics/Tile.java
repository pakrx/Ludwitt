package com.pakrx.platformgame.graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.pakrx.platformgame.resources.ResourceManager;

public class Tile {

	public final static int NW = 0;
	public final static int N = 1;
	public final static int NE = 2;
	public final static int E = 3;
	public final static int SE = 4;
	public final static int S = 5;
	public final static int SW = 6;
	public final static int W = 7;
	public final static int C = 8;
	public final static String CENTER = "center";
	public final static String CORNER = "corner";
	public final static String SIDE = "side";
	public final static String FILLED = "filled";
	public final static String EDGE = "edge";
	public boolean deadly;
	
	
	private Image fullImage;	
	private HashMap<ImageSet, Integer> imageSets = new HashMap<Tile.ImageSet, Integer>();
	
	private boolean walkthrough = false;

	
	
	private static class ImageSet{
		private Image centerImage;
		private Image[] filledImages = new Image[8];
		private Image[] edgeImages = new Image[8];
	}
	
	
	public Image getImage() {
		return fullImage;
	}
	
	public void getImages(Image[] images, Tile[] neighbours, long randomSeed) {
		Random random = new Random(randomSeed);
		int r = random.nextInt(100) + 1;
		int x = 0;
		ImageSet pickedImageSet = null;
		for (ImageSet imageSet : imageSets.keySet()) {
			x += imageSets.get(imageSet).intValue();
			if (r <= x) {
				pickedImageSet = imageSet;
				break;
			}
		}
		Image centerImage = pickedImageSet.centerImage;
		Image[] filledImages = pickedImageSet.filledImages;
		Image[] edgeImages = pickedImageSet.edgeImages;
		images[C] = centerImage;
		images[NW] = (equals(neighbours[NW]) && equals(neighbours[N]) && equals(neighbours[W])) ? filledImages[NW] : edgeImages[NW];
		images[SW] = (equals(neighbours[SW]) && equals(neighbours[S]) && equals(neighbours[W])) ? filledImages[SW] : edgeImages[SW];
		images[NE] = (equals(neighbours[NE]) && equals(neighbours[N]) && equals(neighbours[E])) ? filledImages[NE] : edgeImages[NE];
		images[SE] = (equals(neighbours[SE]) && equals(neighbours[S]) && equals(neighbours[E])) ? filledImages[SE] : edgeImages[SE];
		images[N] = equals(neighbours[N]) ? filledImages[N] : edgeImages[N];
		images[W] = equals(neighbours[W]) ? filledImages[W] : edgeImages[W];
		images[E] = equals(neighbours[E]) ? filledImages[E] : edgeImages[E];
		images[S] = equals(neighbours[S]) ? filledImages[S] : edgeImages[S];
	}
	
	public Tile(String id) {
		setImage(id);
	}
	
	public Tile() {
		
	}
	
	public void setImage(Image image) {
		this.fullImage = image;
	}
	
	public void setImage(String id) {
		String filename = name(id);
		fullImage = ResourceManager.readImage(filename);
	}
	
	public void addImageSet(String id, int probability) {
		BufferedImage boundaryImage = ResourceManager.readImage(nameBoundary(id));
		BufferedImage betweenImage = ResourceManager.readImage(nameBetween(id));
		
		if (boundaryImage == null || betweenImage == null) {
			setImage(id);
			return;
		}
		
		Image centerImage = boundaryImage.getSubimage(9, 9, 14, 14);
		Image[] filledImages = new Image[8];
		Image[] edgeImages = new Image[8];
		
		filledImages[NW] = betweenImage.getSubimage(0, 0, 9, 9);
		filledImages[SW] = betweenImage.getSubimage(0, 23, 9, 9);
		filledImages[NE] = betweenImage.getSubimage(23, 0, 9, 9);
		filledImages[SE] = betweenImage.getSubimage(23, 23, 9, 9);
		filledImages[N] = betweenImage.getSubimage(9, 0, 14, 9);
		filledImages[W] = betweenImage.getSubimage(0, 9, 9, 14);
		filledImages[E] = betweenImage.getSubimage(23, 9, 9, 14);
		filledImages[S] = betweenImage.getSubimage(9, 23, 14, 9);
		
		edgeImages[NW] = boundaryImage.getSubimage(0, 0, 9, 9);
		edgeImages[SW] = boundaryImage.getSubimage(0, 23, 9, 9);
		edgeImages[NE] = boundaryImage.getSubimage(23, 0, 9, 9);
		edgeImages[SE] = boundaryImage.getSubimage(23, 23, 9, 9);
		edgeImages[N] = boundaryImage.getSubimage(9, 0, 14, 9);
		edgeImages[W] = boundaryImage.getSubimage(0, 9, 9, 14);
		edgeImages[E] = boundaryImage.getSubimage(23, 9, 9, 14);
		edgeImages[S] = boundaryImage.getSubimage(9, 23, 14, 9);
		
		ImageSet imageSet = new ImageSet();
		imageSet.centerImage = centerImage;
		imageSet.filledImages = filledImages;
		imageSet.edgeImages = edgeImages;
		imageSets.put(imageSet, probability);
	}
	
	public static String name(String id) {
		return String.format("tiles/%s.png", id);
	}
	
	public static String name(String id, String center) {
		return String.format("tiles/%s-%s.png", id, center);
	}
	
	public static String nameBetween(String id) {
		return String.format("tiles/%s-between.png", id);
	}
	
	public static String nameBoundary(String id) {
		return String.format("tiles/%s-boundary.png", id);
	}
	
	public static String name(String id, String cornerSide, String pos, String edgeFilled) {
		return String.format("tiles/%s-%s-%s-%s.png", id, cornerSide, pos, edgeFilled);
	}

	public void readFromFile(String filename) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (lines != null) {
			String line;
			for (int i = 0; i < lines.size(); i++) {
				line = lines.get(i);
				if (line.trim().length() == 0) {
					continue;
				}
				String[] splitLine;
				if (line.startsWith("IMAGES:")) {
					String id;
					int p;
					line = lines.get(++i);
					while (line.trim().length() > 0) {
						splitLine = line.trim().split(ResourceManager.SEPARATOR);
						id = splitLine[1];
						p = Integer.parseInt(splitLine[2]);
						addImageSet(id, p);
						if (i < lines.size() - 1) {
							line = lines.get(++i);
						} else {
							break;
						}
					}
				}
				if (line.startsWith("WALKTHROUGH:")) {
					walkthrough = Boolean.parseBoolean(line.split(ResourceManager.SEPARATOR)[1]);
				}
				if (line.startsWith("DEADLY:")) {
					deadly = Boolean.parseBoolean(line.split(ResourceManager.SEPARATOR)[1]);
				}
			}
		}
	}
	
	public boolean collides() {
		return !walkthrough;
	}
}
