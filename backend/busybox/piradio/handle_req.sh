#! /bin/sh
read req file proto
homedir=/mnt/HD_a2/scripts/piradio
logfile=$homedir/piradiod.log
echo `date` $req $file $proto >> $logfile
res1=`echo $file | cut -d '/' -f 2 | cut -d '?' -f 1`
params=`echo $file | cut -d '?' -f 2`
param=`echo $params | cut -d '&' -f 1`
freq=`echo $params | cut -d '&' -f 2`
okstring="HTTP/1.1 202 OK\r\nContent-Type: text/html\r\nConnection:close\r\nContent-Length:0\r\n"
okstringwithcontent="HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nConnection:close\r\nContent-Length:"
kostring="HTTP/1.1 400 OK\r\nContent-Type: text/html\r\nConnection:close\r\nContent-Length:0\r\n"
echo "res1:" $res1 " param:" $param >> $logfile 2>&1
case $res1 in
	current)
                url=`ps -eaf | grep sox | grep -v grep | sed 's#.*mp3 \([^ ]*\) -t.*#\1#'`
	        echo $url >> $logfile
                length=`expr 3 + length "$url"`
		echo "$okstringwithcontent$length\r\n\r\n$url\r\n"
		exit;;
	listen)
		sudo /etc/init.d/piradio start $param $freq >> $logfile 2>&1
		echo $okstring
		exit;;
	stop)
		sudo /etc/init.d/piradio stop >> $logfile 2>&1
		echo $okstring
		exit;;
	cat)
                toto=`find $homedir/catalogue -name "*.m3u" -exec head -1 {} \; | sed -e 's/\(.*\)\/\(.*\)/{"name":"\2","url":"\1\/\2"}/' | tr '\n' ',' | sed -e 's/^/{"catalogue":[/' -e 's/$/]}/' | sed 's/},]}$/}]}$/'`
                toto_length=${#toto}
                length=`expr 3 + $toto_length`
                echo 'catalogue length:' $length >> $logfile
		echo "$okstringwithcontent$length\r\n\r\n$toto\r\n"
		exit;;
	record)
	        sudo /mnt/HD_a2/scripts/piradio/piradio start $param >> $logfile 2>&1
	        echo -e $okstring
	        exit;;
	recording)
	        url=`ps -eaf | grep streamripper | grep -v grep | head -1 | sed 's#.*ripper \([^ ]*\) --quiet.*#\1#'`
	        echo $url >> $logfile
	        length=`expr 3 + length "$url"`
	        echo -e "$okstringwithcontent$length\r\n\r\n$url\r\n"
	        exit;;
	*)
		echo $kostring;;
esac
