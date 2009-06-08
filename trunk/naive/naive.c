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


#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define LINESIZE 80
#include "orisons.h"

#define TMPFILE "tmp.faces"

typedef struct {
	int exist;
	int id;
	int xDis;
	int yDis;
	int accuracy;
	int level;
} record;

int main(int argc, char * argv[]) {
	int x, y, xDis, yDis, accuracy, level;
	int vertices, faces;
	record *exist;
	
	FILE *input, *output, *tmp;
	int accThreshold, maxDis, zThreshold;
	double scale;
	int depth;
	int width, height, total;

	int xOffsets[3] = {-1,-1,0};
	int yOffsets[3] = {0,-1,-1};

	int i, j;
	int points[2*2];
	int valid;
	int id1, id2, id3;
	
	if (argc != 7) {
		printf("usage:\n\t%s [input file] [accuracy threshold] ",argv[0]);
		printf("[maximum distance] [height change threshold (\%)] [depth] [output file]\n");
		return -1;
	}
	accThreshold = atoi(argv[2]);
	maxDis = atoi(argv[3]);
	zThreshold = atoi(argv[4]);
	depth = atoi(argv[5]);
	scale = (double) depth / (double) maxDis;

	l_open(argv[1],&input,"r");
	l_open(argv[6],&output,"w");
	l_open(TMPFILE,&tmp,"w");

	if (l_scanline(input, "%dx%d\n", &width, &height) == -1) {
		printf("Can't get width and height!\n");
		return -1;
	}
	total = width*height;

	exist = malloc(sizeof(record)*total);
	if (exist == NULL) {
		printf("Problem allocating memory for existance array!\n");
		return -1;
	}
	for (x = 0; x < width; x++) for (y = 0; y < height; y++) exist[x+y*width].exist = 0;

	vertices = 0;
	while(l_scanline(input, "%d,%d,%d,%d,%d\n", &x, &y, &xDis, &yDis, &accuracy) != -1) {
		if (accuracy > accThreshold) continue;

		exist[x+y*width].exist = 1;
		exist[x+y*width].id = vertices++;
		exist[x+y*width].xDis = xDis;
		exist[x+y*width].yDis = yDis;
		exist[x+y*width].accuracy = accuracy;

		level = depth - (int) (scale * fabs((double) xDis));
		if (level > depth) level = depth;
		if (level < 0) level = 0;
		exist[x+y*width].level = level;
	}
	fclose(input);

	faces = 0;
	for (x = 0; x < width; x++) for (y = 0; y < height; y++) {
		if (exist[x+y*width].exist != 1) continue;

		level = exist[x+y*width].level;

		for (i = 0; i < 2; i++) {
			points[0+0*2] = xOffsets[i] + x;
			points[0+1*2] = yOffsets[i] + y;

			points[1+0*2] = xOffsets[i + 1] + x;
			points[1+1*2] = yOffsets[i + 1] + y;

			valid = 0;
			for (j = 0; j < 2; j++) { 
			  if (points[j+0*2] >= width || points[j+0*2] < 0) continue;
			  if (points[j+1*2] >= height || points[j+1*2] < 0) continue;
			  if (exist[points[j+0*2]+points[j+1*2]*width].exist != 1)
			  	continue;
			  if (abs(exist[points[j+0*2]+points[j+1*2]*width].level  - level) > zThreshold) 
			    	continue;
			  
			  valid++;
			}
			if (valid != 2) continue;

			faces++;

			fprintf(tmp,"%d %d %d \n",
				exist[x+y*width].id,
				exist[points[0+0*2]+points[0+1*2]*width].id,
				exist[points[1+0*2]+points[1+1*2]*width].id);
		}
	}
	fclose(tmp);

	fprintf(output,"ply\nformat ascii 1.0\n");
	fprintf(output,"comment naive mesh for %s\n",argv[1]);
	fprintf(output,"element vertex %d\n",vertices);
	fprintf(output,"property float32 x\nproperty float32 y\nproperty float32 z\n");
	fprintf(output,"element face %d\n",faces);
	fprintf(output,"property list uint8 int32 vertex_indices\n");
	fprintf(output,"end_header\n");

	l_open(argv[1],&input,"r");
	/*need to read to skip*/
	if (l_scanline(input, "%dx%d\n", &width, &height) == -1) {
		printf("Can't get width and height!\n");
		return -1;
	}
	while(l_scanline(input, "%d,%d,%d,%d,%d\n", &x, &y, &xDis, &yDis, &accuracy) != -1) {
		if (exist[x+y*width].exist != 1) continue;

		level = exist[x+y*width].level;
		fprintf(output,"%d %d %d \n", x, y, level);
	}
	fclose(input);

	l_open(TMPFILE,&tmp,"r");
	while(l_scanline(tmp,"%d %d %d \n",&id1,&id2,&id3) != -1) {
		fprintf(output,"3 %d %d %d \n",id1,id2,id3);
	}
	fclose(tmp);
	remove(TMPFILE);
	fclose(output);

}
