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
 * Shifter.java
 * 
*/

package shifter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.imageio.ImageIO;


/**
 *
 * @author Public
 */
public class Shifter {
    final BufferedImage images[][];
    final int frames, horizontalA, horizontalZ, verticalA, verticalZ, win;
    final BufferedWriter output;
    final BufferedImage masks[];
    final int width, height;
    final int total;
    final Overseer overseer;

    public Shifter(String prefixA, int numberA, String postfixA,
                   String prefixB, int numberB, String postfixB,
                   int frames,
                   int horizontalA, int horizontalZ,
                   int verticalA, int verticalZ,
                   String maskAName, String maskBName,
                   int win,
                   String outputFilename) {
        
        this.frames = frames;
        this.horizontalA = horizontalA;
        this.horizontalZ = horizontalZ;
        this.verticalA = verticalA;
        this.verticalZ = verticalZ;
        this.win = win;
        
        Main.log("Opening output file...");
        BufferedWriter temp = null;
        try {
            temp = new BufferedWriter(new FileWriter(outputFilename));
        } catch (Exception e) {
            Main.die("Error opening: " + outputFilename + " " + e.getMessage());
        } finally {
            output = temp;
        }
        Main.log("Opened!");
            
        masks = new BufferedImage[2];
        
        Main.log("\nLoading mask 1...");
        masks[0] = loadImage(maskAName);
        Main.log("Loaded!");
        
        width = masks[0].getWidth();
        height = masks[0].getHeight();
        total = width*height;
        overseer = new Overseer(total);
        
        try {
            output.write(width + "x" + height + "\n");
        } catch (Exception e) {
            Main.die("Problem with output: " + e.getMessage());
        }
        
        Main.log("\nLoading mask 2...");
        masks[1] = loadImage(maskBName);
        Main.log("Loaded!");
        
        images = new BufferedImage[2][frames];
        images[0] = loadImages(prefixA, numberA, postfixA);
        images[1] = loadImages(prefixB, numberB, postfixB);
        
    }
    
    public void main() {
    
        Main.log("\nMatching...");
        final int threadCount = 8;
        final int unit = (total / threadCount) + (total % threadCount);
        
        Correlator[] threads = new Correlator[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Correlator(unit*i,unit*(i+1));
        }
        
        overseer.start();
        for (int i = 0; i < threadCount; i++) threads[i].start();
        try {
            for (int i = 0; i < threadCount; i++) threads[i].join();
        } catch (Exception e) {
            Main.die("Problem with threads: " + e.getMessage());
        }
        overseer.kill();
        try {
            overseer.join();
        } catch (Exception e) {
            Main.die("Problem with counter: " + e.getMessage());
        }
    
        Main.log("Matched!");
        
        Main.log("\nClosing output...");
        try {
            output.close();
        } catch (Exception e) {
            Main.die("Problem closing output: " + e.getMessage());
        }
        Main.log("Closed!");
    }
    
    class Correlator extends Thread {
        int alpha, omega;
        int count;
        
        public Correlator(int alpha, int omega) {
            this.alpha = alpha;
            this.omega = omega;
            count = 0;
        }
        
        @Override public void run() {
            for (int dot = alpha; dot <= omega; dot++) {
              count++;
              if (count % width == 0) {
                  overseer.add(count);
                  count = 0;
              }
                
              int x = dot % width;
              int y = dot / width;
              
              if (x < 0 || x >= width) continue;
              if (y < 0 || y >= height) continue;
              if (!inMask(0,x,y)) continue;
              
              int[] point = closest(x,y);
              
              overseer.report(x + "," + y 
                      + "," + (point[0] - x) + "," + (point[1] - y)
                      + "," + point[2]);
            }
            overseer.add(count);
        }
    }
    
    class Overseer extends Thread {
        int count, total;
        boolean alive = true;
        
        @Override public void run() {
            while (alive) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Main.die("Problem sleeping: " + e.getMessage());
                }
                Main.log("\t" + percent());
            }
        }
        
        public Overseer(int total) {
            count = 0;
            this.total = total;
        }
        
        public synchronized void add(int amount) {
            count += amount;
        }
        
        public synchronized void report(String line) {
            try {
                output.write(line + "\n");
            } catch (Exception e) {
                Main.die("Problem with output: " + e.getMessage());
            }
        }
        
        public String percent() {
            float percent = (float) count / (float) total;
            percent *= 100;
            if (percent > 100) percent = 100;
            return percent + "%";
        }
        
        public void kill() {
            alive = false;
        }
    }
    
    private BufferedImage[] loadImages(
                            String prefix, int number, String postfix) {     
        BufferedImage images[] = new BufferedImage[frames];
        Main.log("\nLoading images...");
        for (int i = 0; i < frames; i++) {
            String filename = prefix + number++ + postfix;
            images[i] = loadImage(filename);
        }
        Main.log("Loaded.");
        
        return images;
    }
    
    private BufferedImage loadImage(String filename) {
        BufferedImage image = null;
        Main.log("\tloading " + filename + "...");
        try {
            File file = new File(filename);
            image = ImageIO.read(file);
        } catch (Exception e) {
            Main.die("Exception: \"" + 
                    e.getMessage() + 
                    "\", encountered loading " + filename + ".");
        }
        
        return image;
    }
    
    private Boolean inMask(int maskNumber, int x, int y) {
        Color color = new Color(masks[maskNumber].getRGB(x, y));
        return color.getRed() == 0;
    }
    
    private double distance(int x, int y, int xp, int yp) {
        double t = 0;
        double u = 0;
        double v = 0;
        
        Color colorA, colorB;
        int red, redp, green, greenp, blue, bluep;
        for (int frame = 0; frame < frames; frame++) {
            for (int i = -win; i <= win; i++) {
            for (int j = -win; j <= win; j++) {
                if (x+i < 0 || y+j < 0 || xp+i < 0 || yp+j < 0) continue;
                if (x+i >= width || y+j >= height || 
                   xp+i >= width || yp+j >= height) continue;
                if (!inMask(0,x+i,y+j) || !inMask(1,xp+i,yp+j)) continue;
                
                colorA = new Color(images[0][frame].getRGB(x+i,y+j));
                colorB = new Color(images[1][frame].getRGB(xp+i,yp+j));
                
                red = colorA.getRed();
                redp = colorB.getRed();
                
                green = colorA.getGreen();
                greenp = colorB.getGreen();

		blue = colorA.getBlue();
		bluep = colorB.getBlue();
                
                t += red*redp;
                t += green*greenp;
                t += blue*bluep;
                
                u += red*red;
                u += green*green;
                u += blue*blue;
                
                v += redp*redp;
                v += greenp*greenp;
                v += bluep*bluep;
            }}
        }
        
        return ((double) 1)-(t/Math.sqrt(u*v));
    }
    
    private int[] closest(int x, int y) {
        double distance, closest = Double.MAX_VALUE;
        int xp = x;
        int yp = y;
        
        for (int i = x + horizontalA; i < x + horizontalZ; i++) {
          if (i < 0) continue;
          if (i >= width) break;
        for (int j = y + verticalA; j < y + verticalZ; j++) {
          if (j < 0) continue;
          if (j >= height) break;
          if (!inMask(1, i, j)) continue;
          
          distance = distance(x,y,i,j);          
          if (distance >= closest) continue;
          
          closest = distance;
          xp = i;
          yp = j;
        }}
        
        int[] point = new int[3];
        point[0] = xp;
        point[1] = yp;
        point[2] = Math.round((float) (closest * 10000));
        return point;
    }
    
}
