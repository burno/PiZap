#!/bin/bash
[ -z "$1" ] && echo usage: sudo $0 [http://ton_flux_mp3:port] [freq] && exit
[ -z "$2" ] && echo usage: sudo $0 [http://ton_flux_mp3:port] [freq] && exit
[ -z "$PIZAP_HOME" ] && echo set PIZAP_HOME path first && exit
[ -z "$PIZAP_RDSNAME" ] && echo set PIZAP_RDSNAME path first && exit
cd $PIZAP_HOME/PiFmRds/src
date
/opt/vc/bin/vcgencmd measure_temp
sox -v .7 -t mp3 $1 -t wav --input-buffer 80000 -r 44100 -c 2 - | ./pi_fm_rds -freq $2 -ps "$PIZAP_RDSNAME" -rt "" -pi FFFF -ctl rds_ctl -audio - 
