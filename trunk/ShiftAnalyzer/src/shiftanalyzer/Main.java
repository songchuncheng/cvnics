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

package shiftanalyzer;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author tjay
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Give me: a filename and a threshold.\n");
            System.exit(-1);
        }
        
        int threshold = Integer.parseInt(args[1]);
        
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(args[0]));
        } catch (Exception e) {
            System.out.println("Problem opening " + args[0] + e.getMessage());
            System.exit(-1);
        }
        
        int width = 0;
        int height = 0;
        int x = 0;
        int y = 0;
        int xDis = 0;
        int yDis = 0;
	int eucDis = 0;
        int currentThreshold = 0;
        int count = 0;
        
        int xMin = Integer.MAX_VALUE;
        int xMax = 0;
        int xTotal = 0;
        int yMin = Integer.MAX_VALUE;
        int yMax = 0;
        int yTotal = 0;
        int xDisMin = Integer.MAX_VALUE;
        int xDisMax = Integer.MIN_VALUE;
        int xDisTotal = 0;
        int yDisMin = Integer.MAX_VALUE;
        int yDisMax = Integer.MIN_VALUE;
        int yDisTotal = 0;
	int eucDisMin = Integer.MAX_VALUE;
	int eucDisMax = Integer.MIN_VALUE;
	int eucDisTotal = 0;
        int thresholdMin = Integer.MAX_VALUE;
        int thresholdMax = 0;
        int thresholdTotal = 0;
        
        System.out.println("\nAnalyzing...");
        try {
            
            String line = input.readLine();
            line = line.replace("\n","");
            
            String parts[] = line.split("x");
            width = Integer.parseInt(parts[0]);
            height = Integer.parseInt(parts[1]);
            
            line = input.readLine();
            while(line != null) {
                line.replace("\n","");
                parts = line.split(",");
                
                x = Integer.parseInt(parts[0]);
                y = Integer.parseInt(parts[1]);
                xDis = Integer.parseInt(parts[2]);
                yDis = Integer.parseInt(parts[3]);
                currentThreshold = Integer.parseInt(parts[4]);
                
                if (currentThreshold > threshold) {
                    line = input.readLine();
                    continue;
                }

		eucDis = (int) (java.lang.Math.sqrt(xDis*xDis+yDis*yDis));
                
                if (x < xMin) xMin = x;
                if (x > xMax) xMax = x;
                if (y < yMin) yMin = y;
                if (y > yMax) yMax = y;
                if (xDis < xDisMin) xDisMin = xDis;
                if (xDis > xDisMax) xDisMax = xDis;
                if (yDis < yDisMin) yDisMin = yDis;
                if (yDis > yDisMax) yDisMax = yDis;
                if (currentThreshold < thresholdMin) 
                    thresholdMin = currentThreshold;
                if (currentThreshold > thresholdMax) 
                    thresholdMax = currentThreshold;
		if (eucDis < eucDisMin) eucDisMin = eucDis;
		if (eucDis > eucDisMax) eucDisMax = eucDis;
                
                xTotal += x;
                yTotal += y;
                xDisTotal += xDis;
                yDisTotal += yDis;
                thresholdTotal += currentThreshold;
		eucDisTotal += eucDis;
                
                count++;
                
                line = input.readLine();
            }
            
        } catch (Exception e) {
            System.out.println("Problem analyzing: " + e + e.getMessage());
            System.exit(-1);
        }
        System.out.println("Analyzed!");
        
        
        System.out.println("\n" + args[0] + " (" + width + "x" + height + "):");
        System.out.println("\tpoints:\t" + count);
        System.out.println("\tx:");
        System.out.println("\t\tMin: " + xMin + "\tMax: " + xMax + "\tAvg: " 
                + xTotal / count + "\tMedian: " + (xMax + xMin)/2);
        System.out.println("\ty:");
        System.out.println("\t\tMin: " + yMin + "\tMax: " + yMax + "\tAvg: " 
                + yTotal / count + "\tMedian: " + (yMax + yMin)/2);
        System.out.println("\txDis:");
        System.out.println("\t\tMin: " + xDisMin + "\tMax: " + xDisMax + "\tAvg: " 
                + xDisTotal / count + "\tMedian: " + (xDisMax + xDisMin)/2);
        System.out.println("\tyDis:");
        System.out.println("\t\tMin: " + yDisMin + "\tMax: " + yDisMax + "\tAvg: " 
                + yDisTotal / count + "\tMedian: " + (yDisMax + yDisMin)/2);
	System.out.println("\teucDis:");
	System.out.println("\t\tMin: " + eucDisMin + "\tMax: " + eucDisMax + "\tAvg: "
		+ eucDisTotal / count + "\tMedian: " + (eucDisMax + eucDisMin)/2);
        System.out.println("\tthreshold:");
        System.out.println("\t\tMin: " + thresholdMin + "\tMax: " + thresholdMax
                + "\tAvg: " + thresholdTotal / count 
                + "\tMedian: " + (thresholdMax + thresholdMin)/2);
        
    }

}
