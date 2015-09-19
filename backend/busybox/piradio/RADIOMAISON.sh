#!/bin/bash
[ -z "$1" ] && echo usage: sudo $0 [http://ton_flux_mp3:port] [freq] && exit
[ -z "$2" ] && echo usage: sudo $0 [http://ton_flux_mp3:port] [freq] && exit
cd /home/pi/PiFmRds/src
date
/opt/vc/bin/vcgencmd measure_temp
sox -v .7 -t mp3 $1 -t wav --input-buffer 80000 -r 44100 -c 2 - | ./pi_fm_rds -freq $2 -ps "Burn'FM" -rt "" -pi FFFF -ctl rds_ctl -audio - 
sox -v .7 -t mp3 $1 -t wav --input-buffer 80000 -r 44100 -c 2 - | ./pi_fm_rds -freq 87.5 -ps "Burn'FM" -rt "" -pi FFFF -ctl rds_ctl -audio - 
