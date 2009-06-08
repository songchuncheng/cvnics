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


class Fill {

  private static java.awt.image.BufferedImage in;

  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Give me: [image file (in)] [image file (out)]");
      System.exit(-1);
    }

    in = null;
    try {
      in = javax.imageio.ImageIO.read(new java.io.File(args[0]));
    } catch (java.io.IOException ioe) {
      System.out.println("Problem opening infile: " + ioe + ioe.getMessage());
    }

    java.awt.image.BufferedImage out = new java.awt.image.BufferedImage(in.getWidth(), in.getHeight(),
                                             java.awt.image.BufferedImage.TYPE_INT_RGB);

    System.out.println("Processing...");
    int work_done = 0;
    int total_work = in.getWidth() * in.getHeight();
    for (int x = 0; x < in.getWidth(); x++) {
      for (int y = 0; y < in.getHeight(); y++) {
        work_done++;
	if (work_done % 7000 == 0) {
	  System.out.println(((double) work_done / (double) total_work * (double) 100) + "%");
	}

        java.awt.Color current = new java.awt.Color(in.getRGB(x,y));
	if (!(current.getRed() == 255 && current.getGreen() == 0 && current.getBlue() == 0)) {
	  out.setRGB(x,y,current.getRGB());
	  continue;
	};
	java.awt.Color[] samples = new java.awt.Color[1024];
	int xs[] = new int[1024];
	int ys[] = new int[1024];
	int size = 0;

	for (int i = -16; i < 16; i++) {
	  for (int j = -16; j < 16; j++) {
	    if (badPlan(x+i,y+j)) continue;
            current = new java.awt.Color(in.getRGB(x+i,y+j));
	    if (current.getRed() == 255 && current.getGreen() == 0 && current.getBlue() == 0) continue;
	    samples[size] = current;
	    xs[size] = x+i;
	    ys[size] = y+j;
	    size++;
	  }
	}

	double total_distance = 0;
	double distance[] = new double[size];
	for (int i = 0; i < size; i++) {
	  distance[i] = Math.pow(x - xs[i],2);
	  distance[i] += Math.pow(y - ys[i],2);
	  distance[i] = 1/Math.sqrt(distance[i]);
	  total_distance += distance[i];
	}

	double RGB[] = {0,0,0};
	for (int i = 0; i < size; i++) {
	  RGB[0] += samples[i].getRed()*distance[i];
	  RGB[1] += samples[i].getGreen()*distance[i];
	  RGB[2] += samples[i].getBlue()*distance[i];
	}
	for (int i = 0; i < 3; i++) {
	  RGB[i] /= total_distance;
	  if (RGB[i] < 0) RGB[i] = 0;
	  if (RGB[i] > 255) RGB[i] = 255;
	}

	out.setRGB(x,y,new java.awt.Color((int) RGB[0], (int) RGB[1], (int) RGB[2]).getRGB());
      }
    }

    System.out.println("Writing file...");
    java.io.File out_file = new java.io.File(args[1]);
    try {
      javax.imageio.ImageIO.write(out, "png", out_file);
    } catch (java.io.IOException ioe) {
      System.out.println("Problem writing out file: " + ioe + " " + ioe.getMessage());
    }
  }

  private static boolean badPlan(int x, int y) {
    if (x < 0 || y < 0) return true;
    if (x >= in.getWidth() || y >= in.getHeight()) return true;
    return false;
  }
}
