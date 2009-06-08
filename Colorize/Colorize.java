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


class Colorize {
	public static void main(String args[]) {
		if (args.length != 2) {
			System.out.println("Give me: [an input ply] [an image]. I will print the resulting ply to stdout.");
			System.exit(-1);
		}

		java.io.BufferedReader in = null;
		try {
			in = new java.io.BufferedReader(new java.io.FileReader(args[0]));
		} catch (java.io.FileNotFoundException fnfe) {
			System.err.println("Couldn't find input file!");
			System.exit(-1);
		}

		java.awt.image.BufferedImage image = null;
		try {
			image = javax.imageio.ImageIO.read(new java.io.File(args[1]));
		} catch (java.io.IOException ioe) {
			System.err.println("Can't read in image file.");
			System.exit(-1);
		}

		try {
			java.util.regex.Pattern element = java.util.regex.Pattern.compile("element face.*");
			java.util.regex.Pattern point = java.util.regex.Pattern.compile("(\\d+) (\\d+) (\\d+) ");

			String line = in.readLine();
			while(line != null) {
				if (element.matcher(line).matches()) {
					System.out.println("property uchar red");
					System.out.println("property uchar green");
					System.out.println("property uchar blue");
				}
				java.util.regex.Matcher points = point.matcher(line);
				if (points.matches()) {
					System.out.print(line);
					java.awt.Color color = new java.awt.Color(image.getRGB(Integer.parseInt(points.group(1)),Integer.parseInt(points.group(2))));
					System.out.print(color.getRed());
					System.out.print(" ");
					System.out.print(color.getGreen());
					System.out.print(" ");
					System.out.print(color.getBlue());
					System.out.println(" ");
				} else {
					System.out.println(line);
				}

				line = in.readLine();
			}

		} catch (java.io.IOException ioe) {
			System.err.println("Problem reading input!");
			System.exit(-1);
		}

	}
}
