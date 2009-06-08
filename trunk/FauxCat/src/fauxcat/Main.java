/*
Copyright 2007, 2008, 2009 Graylin Trevor Jay

This file is part of CVNICS.

CVNICS is free software: you can redistribute it and/or modify it under the
terms of the GNU General Public License as published by the Free Software
Foundation, either version 3 of the License, or (at your option) any later
version.

CVNICS is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
CVNICS.  If not, see <http://www.gnu.org/licenses/>.
*/



/*
 * Main.java
 *
*/

package fauxcat;

/**
 *
 * @author tjay
 */
public class Main {

    public static void main(String[] args) {
   		if (args.length != 6 && args.length != 7) {
				System.err.println("\tGive me: [the begining depth] [the ending depth] [the spread] [an increment] [number of times to repeat], a shift file, and optionally a base image.");
				System.exit(-1);
			}
			
			int start = Integer.parseInt(args[0]);
			int stop = Integer.parseInt(args[1]);
			int spread = Integer.parseInt(args[2]);
			int increment = Integer.parseInt(args[3]);
			int repeat = Integer.parseInt(args[4]);

			java.io.BufferedReader in = null;
			try {
				in = new java.io.BufferedReader(new java.io.FileReader(args[5]));
			} catch (java.io.FileNotFoundException ioe) {
				System.err.println("Problem opening file: " + ioe.getMessage());
				System.exit(-1);
			}

			boolean color = false;
			java.awt.image.BufferedImage base = null;
			if (args.length == 7) {
				color = true;
				try {
					base = javax.imageio.ImageIO.read(new java.io.File(args[6]));
				} catch (java.io.IOException ioe) {
					System.err.println("Problem opening base image: " + ioe.getMessage());
					System.exit(-1);
				}
			}

			int xMax = Integer.MIN_VALUE;
			int xMin = Integer.MAX_VALUE;
			int yMax = Integer.MIN_VALUE;
			int yMin = Integer.MAX_VALUE;
			double dis;
			double maxDis = Double.MIN_VALUE;
			double minDis = Double.MAX_VALUE;
			int x, xp;
			int y, yp;

			java.util.Hashtable<String,Double> depths = new java.util.Hashtable<String,Double>();

			try {
				String fields[];

  			String line = in.readLine(); //ignore first line
	  		line = in.readLine();
		  	while(line != null) {
					fields = line.split(",");	
					x = Integer.parseInt(fields[0]);
					y = Integer.parseInt(fields[1]);
					xp = Integer.parseInt(fields[2]);
					yp = Integer.parseInt(fields[3]);
					dis = java.lang.Math.sqrt(xp*xp+yp*yp);

					if (x < xMin) xMin = x;
					if (x > xMax) xMax = x;
					if (y < yMin) yMin = y;
					if (y > yMax) yMax = y;
					if (dis < minDis) minDis = dis;
					if (dis > maxDis) maxDis = dis;

					depths.put("" + x + "," + y,dis);

			  	line = in.readLine();
			  }
			} catch (java.io.IOException ioe) {
				System.err.println("Problem reading file: " + ioe.getMessage());
			}

			try {
				in.close();
			} catch (java.io.IOException ioe) {
				//nothing
			}

			if (xMax == Integer.MIN_VALUE || 
			    xMin == Integer.MAX_VALUE ||
					yMax == Integer.MIN_VALUE ||
					yMin == Integer.MAX_VALUE) {
				System.err.println("Ridiculous dimensions!");
				System.exit(-1);
			}

			if (color) {
				xMin = 0;
				xMax = base.getWidth()-1;
				yMin = 0;
				yMax = base.getHeight()-1;
			}

			double scale = (double) WaveToRGB.size/(maxDis - minDis);
			java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(1+xMax - xMin,1+yMax - yMin,java.awt.image.BufferedImage.TYPE_INT_RGB);
			java.awt.Graphics2D g = image.createGraphics();
			g.setColor(java.awt.Color.BLACK);

			double w;
			int n = 0;

			for (int i = start; i <= stop; i = i + increment) {
				System.out.println("\t" + i + ":");
				g.fillRect(0,0,image.getWidth(),image.getHeight());

				for (x = xMin; x <= xMax; x++) {
					for (y = yMin; y <= yMax; y++) {
						String key = "" + x + "," + y;
						if (depths.get(key) == null) continue;
						w = depths.get(key);
						w = java.lang.Math.rint(w*scale)+WaveToRGB.low;
						if (java.lang.Math.abs(w-i) > spread) continue;
						if (color) {
							image.setRGB(x,y,base.getRGB(x,y));
						} else {
							image.setRGB(x-xMin,y-yMin,WaveToRGB.convert((int) w).getRGB());
						}
					}
				}

				for (int j = 0; j < repeat; j++) {
					try {
						String filename = String.format("%05d", n) + ".png";
						System.out.println("\t\tWriting: " + filename);
						javax.imageio.ImageIO.write(image,"png",new java.io.File(filename));
					} catch (java.io.IOException ioe) {
						System.err.println("Problem writing frame: " + ioe.getMessage());
						System.exit(-1);
					}
					n++;
				}
			}

    }

}
