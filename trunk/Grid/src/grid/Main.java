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

package grid;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import java.awt.image.BufferStrategy;


import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 *
 * @author tjay
 */
public class Main implements KeyListener {
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;

    int stage;
    int offsetX;
    int offsetY;
    float scale;
    public GraphicsDevice graphicsDevice;
    public JFrame frame;
    public BufferedImage canvas;
    BufferStrategy strategy;
    public int[][][][] beamlets;
    
    /** Creates a new instance of Main */
    public Main(Boolean minmax) {
        frame = new JFrame("Grid");
        frame.setUndecorated(true);
        frame.addKeyListener(this);
        
        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        if (!graphicsDevice.getDefaultConfiguration().getBufferCapabilities().isPageFlipping()) {
            System.out.println("Your card must support page flipping!");
            System.exit(-1);
        }
        
        if (!graphicsDevice.isFullScreenSupported()) {
            System.out.println("Your card must support fullscreen!");
            System.exit(-1);
        }

        stage  = 0;
        offsetX = 0;
        offsetY = 0;
        scale = 100;

        java.util.Random random = new java.util.Random(1990);
        beamlets = new int[WIDTH][HEIGHT][10][3];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
	    	for (int frame = 0; frame < 10; frame++) {
			for (int color = 0; color < 3; color++) {
				if (minmax) {
					beamlets[x][y][frame][color] = random.nextInt(2)*255;
				} else {
					beamlets[x][y][frame][color] = random.nextInt(256);
				}

			}
		}

            }
        }
 
        graphicsDevice.setFullScreenWindow(frame);
        
        frame.createBufferStrategy(2);
        strategy = frame.getBufferStrategy();
        
    }

    public void paintFrame() {
            if (frame.getWidth() == 0) return;
            
            //System.out.println("rendering");
            
            float scale = this.scale / 100.0f;
            
            Graphics g = strategy.getDrawGraphics();
            
            canvas = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);

            int[] RGB = new int[3];
            int base = stage * 3;
            int value;
            float xScale = (float) frame.getWidth()/((float) WIDTH) * scale;
            float yScale = (float) frame.getHeight()/((float) HEIGHT);
            int width = (int) ((float) frame.getWidth()/((float) WIDTH) * scale);
            int height = (int) ((float) frame.getHeight()/((float) HEIGHT) * scale);
            int maxX = frame.getWidth();
            int maxY = frame.getHeight();
            int xStart, xFinish, yStart, yFinish;          
	    Graphics c = null;

	    if (stage == 11 || stage == 12 || stage == 13) {
	      c = canvas.createGraphics();
	      c.setColor(java.awt.Color.BLACK);
	      c.fillRect(0,0,frame.getWidth(),frame.getHeight());
	    }

	    if (stage == 11) {
	      c.setColor(java.awt.Color.WHITE);
	      c.fillRect(offsetX,offsetY,(int) (frame.getWidth()*scale),(int) (frame.getHeight()*scale));
	      final int line_size = 3;
	      final int adjustment = -1;
	      final int line_number = 15;
	      float interval;
	      interval = (float) (frame.getWidth()) 
	      			/ (float) line_number;
	      interval *= scale;
	      c.setColor(java.awt.Color.GREEN);
	      for (int vertical_line = 0; vertical_line <= 15; vertical_line++) {
	        c.fillRect(offsetX + (int) (vertical_line*interval) + adjustment, offsetY,
			line_size,(int) (frame.getHeight()*scale));
	      }
	      interval = (float) (frame.getHeight()) 
	      			/ (float) line_number;
	      interval *= scale;
	      for (int horizontal_line = 0; horizontal_line <= 15; horizontal_line++) {
	        c.fillRect(offsetX, offsetY + (int) (horizontal_line*interval) + adjustment, 
			(int) (frame.getWidth()*scale), line_size);
	      }
	    }

	    if (stage == 13) {
	    	c.setColor(java.awt.Color.GREEN);
		int corner = -49;
		int size = 99;
		while(size > 0) {
			if (corner % 2 == 0) {
				c.setColor(java.awt.Color.GREEN);
			} else {
				c.setColor(java.awt.Color.RED);
			}
			c.fillRect(offsetX+corner,offsetY+corner,size,size);
			size -= 2;
			corner++;
		}
	    }

	    if (stage == 12) {
		c.setColor(java.awt.Color.GREEN);
		c.fillRect(offsetX,offsetY,1,1);
	    }
	    
	    if (stage != 11 && stage != 12 && stage != 13) {
		    for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
			    
			    Color color = new Color(beamlets[x][y][stage % 10][0],
			    			    beamlets[x][y][stage % 10][1],
					      	    beamlets[x][y][stage % 10][2]);
			    
			    xStart = (int) (offsetX + x * xScale);
			    if (xStart < 0) xStart = 0;
			    xFinish = xStart + width;
			    if (xFinish >= maxX) xFinish = maxX - 1;
			    yStart = (int) (offsetY + y * yScale);
			    if (yStart < 0) yStart = 0;
			    yFinish = yStart + height;
			    if (yFinish >= maxY) yFinish = maxY - 1;
			    for (int i = xStart; i <= xFinish; i++) {
				for (int j = yStart; j <= yFinish; j++) {
				    canvas.setRGB(i, j, color.getRGB());
				}
			    }
			    
			    
			}
		    }

	    }
            
            g.drawImage(canvas, 0,0, null);
                    
            g.dispose();
            strategy.show();
    }
    
    public void keyReleased(KeyEvent e) {
        //nothing
    }
    
    public void keyTyped(KeyEvent e) {
        int ascii = Character.getNumericValue(e.getKeyChar());
        
        int code = ascii - 10;
        if (code >= 0 && code <= 13) {
            stage = code;
        }
        
	paintFrame();
    }
    
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        switch (key) {
            case KeyEvent.VK_Z: cleanUp(); break;
            
            case KeyEvent.VK_UP: offsetY--; break;
            case KeyEvent.VK_DOWN: offsetY++; break;
            case KeyEvent.VK_LEFT: offsetX--; break;
            case KeyEvent.VK_RIGHT: offsetX++; break;
            case KeyEvent.VK_0: offsetX = 0; offsetY = 0; scale = 100; break;
            
            case KeyEvent.VK_EQUALS: scale++; break;
            case KeyEvent.VK_MINUS: scale--; break;
            
            default:
        }
        
        if (scale <= 0) scale = 1;
        if (scale >= 100) scale = 100;
        
	paintFrame();
    }
    
    public void cleanUp() {
        //graphicsDevice.setFullScreenWindow(null);
        System.exit(0);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	Boolean minmax = false;
	for (String arg : args) {
		if (arg.equals("-minmax")) {
			minmax = true;
			continue;
		}
	}
        Main main = new Main(minmax);
    }
    
}
