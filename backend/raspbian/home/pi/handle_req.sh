read req file proto
echo `date` $req $file $proto >> /home/pi/piradiod.log
res1=`echo $file | cut -d '/' -f 2 | cut -d '?' -f 1`
params=`echo $file | cut -d '?' -f 2`
url=`echo $params | cut -d '&' -f 1`
freq=`echo $params | cut -d '&' -f 2`
okstring="HTTP/1.1 202 OK\r\nContent-Type: text/html\r\nConnection:close\r\nContent-Length:0\r\n"
okstringwithcontent="HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nConnection:close\r\nContent-Length:"
kostring="HTTP/1.1 400 OK\r\nContent-Type: text/html\r\nConnection:close\r\nContent-Length:0\r\n"
echo "res1:" $res1 " url:" $url >> /home/pi/piradiod.log 2>&1
case $res1 in
	current)
                url=`ps -eaf | grep sox | grep -v grep | sed 's#.*mp3 \([^ ]*\) -t.*#\1#'`
                length=`expr 3 + length $url`
		echo "$okstringwithcontent$length\r\n\r\n$url\r\n"
		exit;;
	listen)
		sudo /etc/init.d/piradio start $url $freq >> /home/pi/piradiod.log 2>&1
		echo $okstring
		exit;;
	stop)
		sudo /etc/init.d/piradio stop >> /home/pi/piradiod.log 2>&1
		echo $okstring
		exit;;
	cat)
                toto=`find /home/pi/catalogue -name "*.m3u" -exec head -1 {} \; | sed -e 's/\(.*\)\/\(.*\)/{"name":"\2","url":"\1\/\2"}/' | tr '\n' ',' | sed -e 's/^/{"catalogue":[/' -e 's/$/]}/' | sed 's/},]}$/}]}$/'`
                toto_length=${#toto}
                length=`expr 3 + $toto_length`
                echo 'catalogue length:' $length >> /home/pi/piradiod.log
		echo "$okstringwithcontent$length\r\n\r\n$toto\r\n"
		exit;;
	*)
		echo $kostring;;
esac
