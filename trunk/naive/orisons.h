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
#include <stdarg.h>

#ifndef ORISONS_H
#define ORISONSD_H

void l_open(char *name, FILE **file_p_p, char *mode) {
	*file_p_p = fopen(name, mode);

	if (*file_p_p == NULL) {
		printf("The program could not open: %s.\n",name);
		exit(-1);
	}
}

/*LINESIZE must be defined elsewhere */
int l_scanline(FILE *input, char *format, ...) {
	va_list va;
	char line[LINESIZE + 1];
	int scanErr;

	clearerr(input);
	fgets(line, LINESIZE, input);
	if (feof(input) || ferror(input)) return -1;

	va_start(va, format);

	scanErr = vsscanf(line, format, va);

	va_end( va );

	if (scanErr == 0 || scanErr == EOF) return -1;

	return 0;
}

#endif
