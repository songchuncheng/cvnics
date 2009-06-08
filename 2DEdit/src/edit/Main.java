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

package edit;

/**
 *
 * @author tjay
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	if (args.length != 3) {
		System.out.println("Give me: [in shift file] [in image] [out shift file]");
		System.exit(-1);
	}

	java.awt.image.BufferedImage image = null;
	try {
		java.io.File file = new java.io.File(args[1]);
		image = javax.imageio.ImageIO.read(file);
	} catch (Exception e) {
		System.out.println("Problem reading image file!");
		System.exit(-1);
	}

	
	java.io.BufferedReader in = null;
	try {
		in = new java.io.BufferedReader(new java.io.FileReader(args[0]));
	} catch (Exception e) {
		System.out.println("Problem reading in file!");
		System.exit(-1);
	}

	java.io.PrintWriter out = null;
	try {
	out = new java.io.PrintWriter(new java.io.FileOutputStream(args[2]));
	} catch (Exception e) {
		System.out.println("Problem opening out file!");
		System.exit(-1);
		
	}

	try {
		for (String line; (line = in.readLine()) != null;) {
			String[] props = line.split(",");
			if (props.length == 1) { //first line
				out.println(line);
				continue;
			}

			if (image.getRGB(Integer.parseInt(props[0]),Integer.parseInt(props[1])) != 
			    	java.awt.Color.BLACK.getRGB()) {
				out.println(line);
			}
		}
	} catch (Exception e) {
		System.out.println("Problem processing: " + e + e.getMessage());
		System.exit(-1);
	}

	out.close();
	try {
		in.close();
	} catch (Exception e) {
	}

    }

}
