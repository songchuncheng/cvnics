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

package shifter;

/**
 *
 * @author Public
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 15) {
            System.out.println("Give me: two triplets, a number of frames, " +
                               "a horizontal window (a,z), " +
                               "a vertical window (a,z), " +
                               "two masks, a blur window size, " + 
                               "and an output file.");
            System.exit(-1);
        }
        
        int o = 0;
        Shifter shifter = new Shifter(
                args[o++],Integer.parseInt(args[o++]),args[o++],
                args[o++],Integer.parseInt(args[o++]),args[o++],
                Integer.parseInt(args[o++]),
                Integer.parseInt(args[o++]), Integer.parseInt(args[o++]),
                Integer.parseInt(args[o++]), Integer.parseInt(args[o++]),
                args[o++],args[o++],
                Integer.parseInt(args[o++]),
                args[o++]);
        shifter.main();
    }
    
    public static void log(String msg) {
        System.out.println(msg);
    }
    
    public static void die(String msg) {
        log(msg);
        System.exit(-1);
    }

}
