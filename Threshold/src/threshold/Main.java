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

package threshold;

/**
 *
 * @author tjay
 */
public class Main {

    private static java.awt.image.BufferedImage[][] images;
    private static int frames;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      if (args.length != 11) {
        System.out.println("Give me: an in (shift) file, a left triplet, a right triplet, a number of frames, a correlation lower bound, a variance lower bound, and an out (shift) file.");
        System.exit(-1);
      }

      frames = Integer.parseInt(args[7]);
      images = new java.awt.image.BufferedImage[frames][2];

      System.out.println("Loading images...");
      for (int side = 0; side < 2; side++) {
        String prefix = args[1+3*side];
        int number = Integer.parseInt(args[2+3*side]); 
        String postfix = args[3+3*side];
	
        for (int frame = 0; frame < frames; frame++) {
	  java.io.File image_file = new java.io.File(prefix + (number++) + postfix);  
	  java.awt.image.BufferedImage image = null;
          try {
	    image = javax.imageio.ImageIO.read(image_file);
          } catch (java.io.IOException ioe) {
	    System.out.println("Problem loading: " + image_file.getName());
	    System.out.println(ioe + ": " + ioe.getMessage());
          }
          images[frame][side] = image;
	}
      }

      System.out.println("Opening in file...");
      java.io.BufferedReader in = null;
      try {
        in = new java.io.BufferedReader(new java.io.FileReader(args[0]));
      } catch (java.io.FileNotFoundException fnf) {
        System.out.println("Can't open " + args[0]);
	System.exit(-1);
      }

      System.out.println("Opening out file...");
      java.io.PrintWriter out = null;
      try {
        out = new java.io.PrintWriter(new java.io.FileWriter(args[10]));
      } catch (java.io.IOException ioe) {
        System.out.println("Can't open " + args[10]);
	System.exit(-1);
      }

      double correlation_threshold = Double.parseDouble(args[8]);
      double variance_threshold = Double.parseDouble(args[9]);

      System.out.println("Processing...");
      double avg_correlation = 0;
      double avg_variance = 0;
      double min_correlation = Double.MAX_VALUE;
      double max_correlation = Double.MIN_VALUE;
      double min_variance = Double.MAX_VALUE;
      double max_variance = Double.MIN_VALUE;
      int count = 0;
      try {
        String line = in.readLine(); 
	out.println(line); //First line is a header
        line = in.readLine();
        while(line != null) {
	  String[] point = line.split(",");
	  int x = Integer.parseInt(point[0]);
	  int y = Integer.parseInt(point[1]);
	  int offset_x = Integer.parseInt(point[2]);
	  int offset_y = Integer.parseInt(point[3]);
	  double current_correlation = 0;
	  double current_variance = 0;
	  current_correlation = correlation(x,y,offset_x,offset_y);
	  current_variance = variance(x,y);
	  if (current_correlation > correlation_threshold 
	      && current_variance < variance_threshold) {
	      out.println(line);
	  }

	  if (current_correlation >= 0 || current_correlation < 0) {
            min_correlation = Math.min(current_correlation, min_correlation);
	    max_correlation = Math.max(current_correlation, max_correlation);
	    avg_correlation += current_correlation;
	  } else {
	    System.out.println("Record: " + count + " is weird!");
	  }
	  
	  if (current_variance >= 0 || current_variance < 0) {
	    min_variance = Math.min(current_variance, min_variance);
	    max_variance = Math.max(current_variance, max_variance);
	    avg_variance += current_variance;
	  }

	  count++;
          line = in.readLine();
        }
	avg_correlation /= count;
	avg_variance /= count;

      } catch (Exception e) {
        System.out.println("Problem processing: " + e + " " + e.getMessage());
	System.exit(-1);
      }

      System.out.println("Done!");
      System.out.println("Correlation:");
      System.out.println("\tMax: " + max_correlation + " Min: " + min_correlation + " Avg: " + avg_correlation);
      System.out.println("Variance:");
      System.out.println("\tMax: " + max_variance + " Min: " + min_variance + " Avg: " + avg_variance);
      
      try { 
        in.close();
      } catch (java.io.IOException ioe) {
      }
      out.close();

    }

    private static double correlation(int x, int y, int offset_x, int offset_y) {
      //modified from wikipedia (http://en.wikipedia.org/wiki/Correlation)
      double sum_sq_x = 0;
      double sum_sq_y = 0;
      double sum_coproduct = 0;
      java.awt.Color color;
      color = new java.awt.Color(images[0][0].getRGB(x,y));
      double mean_x = grayscale(color.getRed(),color.getGreen(),color.getBlue()); 
      color = new java.awt.Color(images[0][1].getRGB(x+offset_x, y + offset_y));
      double mean_y = grayscale(color.getRed(),color.getGreen(),color.getBlue()); 
      for (int i = 2; i <= frames; i++) {
        double sweep = (double) (i - 1) / (double) i;
        color = new java.awt.Color(images[i-1][0].getRGB(x,y));
	double delta_x = grayscale(color.getRed(),color.getGreen(),color.getBlue()) - mean_x;
        color = new java.awt.Color(images[i-1][1].getRGB(x + offset_x, y + offset_y));
	double delta_y = grayscale(color.getRed(),color.getGreen(),color.getBlue()) - mean_y;
	sum_sq_x += delta_x * delta_x * sweep;
	sum_sq_y += delta_y * delta_y * sweep;
	sum_coproduct += delta_x * delta_y * sweep;
	mean_x += delta_x / i;
	mean_y += delta_y / i;
      }
      double pop_sd_x = Math.sqrt(sum_sq_x / frames);
      double pop_sd_y = Math.sqrt(sum_sq_y / frames);
      double cov_x_y = sum_coproduct / frames;

      return cov_x_y / (pop_sd_x * pop_sd_y);
    }

    private static double variance(int x, int y) {
      double mean = 0;
      java.awt.Color color;
      for (int frame = 0; frame < frames; frame++) {
        color = new java.awt.Color(images[frame][0].getRGB(x,y));
	mean += grayscale(color.getRed(),color.getGreen(),color.getBlue());
      }
      mean /= frames;
      double variance = 0;
      for (int frame = 0; frame < frames; frame++) {
        color = new java.awt.Color(images[frame][0].getRGB(x,y));
	variance += Math.pow(grayscale(color.getRed(),color.getGreen(),color.getBlue()) - mean,2);
      }
      variance /= frames;

      return variance;
    }

    private static double grayscale(int r, int g, int b) {
      return .3*r + .59*g + .11*b;
    }

}
