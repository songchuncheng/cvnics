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

package fauxcat;

public class WaveToRGB {
	public static int low = 380;
	public static int high = 645;
	public static int size = 265;

	public static void main(String[] args) {
		int width = 265;
		int height = 73;

		java.awt.image.BufferedImage spectrum = new java.awt.image.BufferedImage(width,height,java.awt.image.BufferedImage.TYPE_INT_RGB );
		java.awt.Graphics2D pen = spectrum.createGraphics();

		for (int i = low; i <= high; i++) {
			pen.setColor(convert(i));
			pen.draw(new java.awt.Rectangle(i-380,0,1,height));
		}

		try {
			java.io.File out = new java.io.File("spectrum.png");
			javax.imageio.ImageIO.write(spectrum, "png", out);
		} catch (java.io.IOException ioe) {
			System.err.println("Problem writing file.");
		}
	}

	public static java.awt.Color convert(int wL) {
		double waveLength = (double) wL;
		double red,green,blue;

		/* if (waveLength) < 440) {  */
			red = -1.*(waveLength-440.)/(440.-380.);
			green = 0;
			blue = 1;
		/* } */
		if (waveLength >= 440 && waveLength < 490) {
			red = 0;
			green = (waveLength-440.)/(490.-440.);
			blue = 1;
		}
		if (waveLength >= 490 && waveLength < 510) {
			red = 0;
			green = 1;
			blue = -1.*(waveLength-510.)/(510.-490.);
		}
		if (waveLength >= 510 && waveLength < 580) {
			red = (waveLength-510.)/(580.-510.);
			green = 1;
			blue = 0;
		}
		if (waveLength >= 580 && waveLength < 645) {
			red = 1;
			green = -1.*(waveLength-645.)/(645.-580.);
			blue = 0;
		}
		if (waveLength >= 645) {
			red = 1;
			green = 0;
			blue = 0;
		}

		red = 255*red;
		green = 255*green;
		blue = 255*blue;

		if (red>255) red = 255;
		if (green>255) green = 255;
		if (blue>255) blue = 255;

		if (red<0) red = 0;
		if (green<0) green = 0;
		if (blue<0) blue = 0;

		return new java.awt.Color((int) red, (int) green, (int) blue);
	}

}
