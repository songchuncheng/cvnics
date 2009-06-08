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

package shift2pic;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.imageio.ImageIO;

/**
 *
 * @author tjay
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 9) {
            System.out.println("Give me: the minimum and maximum x and y" + 
                    "values, the maximum distance, a threshold, a background setting (1 = red, 0 = black), a euclidian setting (1 = yes, 0 = no)," +
                    "and an infile.");
            System.exit(-1);
        }
        
        int o = 0;
        int xMin = Integer.parseInt(args[o++]); 
        int xMax = Integer.parseInt(args[o++]); 
        int yMin = Integer.parseInt(args[o++]); 
        int yMax = Integer.parseInt(args[o++]); 
        int disMax = Integer.parseInt(args[o++]); 
        int threshold = Integer.parseInt(args[o++]); 
	boolean red = false; 
	boolean euc = false;
	if (Integer.parseInt(args[o++]) == 1) {
	  red = true;
	}
	if (Integer.parseInt(args[o++]) == 1) {
	  euc = true;
	}
        float grayscale;
        String infileName = args[o++];
        
        BufferedImage output = new BufferedImage(1 + xMax - xMin, 1 + yMax - yMin, 
                BufferedImage.TYPE_INT_RGB);
	
	java.awt.Graphics2D pen = output.createGraphics();
	if (red) {
		pen.setColor(java.awt.Color.RED);
		pen.fillRect(0,0,output.getWidth(),output.getHeight());
		System.out.println("Drawing red background...");
	}
        
        float scale = 255 / disMax;
        
        System.out.println("Opening input file...");
        BufferedReader infile = null;
        try {
            infile = new BufferedReader(new FileReader(infileName));
        } catch (Exception e) {
            System.out.println("Error opening: " + infileName + " " 
                    + e.getMessage());
            System.exit(-1);
        }
        System.out.println("Opened!");
        
        int x, y, xDis, yDis, currentThreshold;
        
        String line;
        String[] parts;
        try {
             line = infile.readLine(); //skip size
             line = infile.readLine(); 
             while (line != null) {
                 line.replace("\n","");
                 parts = line.split(",");
                 x = Integer.parseInt(parts[0]) - xMin;
                 y = Integer.parseInt(parts[1]) - yMin;
                 xDis = Integer.parseInt(parts[2]);
                 yDis = Integer.parseInt(parts[3]);
                 currentThreshold = Integer.parseInt(parts[4]);
                 
                 line = infile.readLine();
                 if (currentThreshold > threshold) continue;
                 
		 if (euc) {
		 	grayscale = (float) java.lang.Math.sqrt(xDis*xDis+yDis*yDis);
		 } else {
		 	grayscale = (float) xDis;
		 }
		 grayscale = grayscale * scale;
                 grayscale = 255 - grayscale;
                 if (grayscale > 255) grayscale = 255;
                 if (grayscale < 0) grayscale = 0;
                 
                 Color color = new Color(
                         (int) grayscale, (int) grayscale, (int) grayscale);
                 
                 output.setRGB(x, y, color.getRGB());
             }
        } catch (Exception e) {
            System.out.println("Problem reading data:" + e.getMessage());
            System.exit(-1);
        }
        
        try {
            File file = new File("output.png");
            ImageIO.write(output, "png", file);
        } catch (Exception e) {
            System.out.println("Couldn't save output file: " + e.getMessage());
            System.exit(-1);
        }
        
    }

}
