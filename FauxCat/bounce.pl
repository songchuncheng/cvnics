#!/usr/bin/perl 

my $n = 142;
for ($count = 140; $count >= 42; $count--) {
	my $a = sprintf("%05d", $count);
	my $b = sprintf("%05d", $n);
	print "cp $a.bmp $b.bmp\n"; 
	$n++;
}
