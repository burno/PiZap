# PiZap
Raspeberry Pi as a FM radio transmitter and ripper. Remote controled with Android phone.

Little explanation:
- the FM transmission was performed following this tutorial http://www.jacquet80.eu/blog/post/2014/04/Emetteur-FM-RDS-Raspberry-Pi
It uses PiFmRds Git project and Sound eXchange (sox) linux tool to play the webradio stream to the FM band.

- a shell daemon listens for http commands and send the commands to a linux service that start/stop the streaming

- the android app sends http commands to the daemon:
  * start/stop FM transmission
  * choose url of webradio to listen to
  * start/stop ripping
  * manage list of webradios
