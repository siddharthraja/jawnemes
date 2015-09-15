#!/bin/bash

shuffle() {
   local i tmp size max rand

   # $RANDOM % (i+1) is biased because of the limited range of $RANDOM
   # Compensate by using a range which is a multiple of the array size.
   size=${#array[*]}
   max=$(( 32768 / size * size ))

   for ((i=size-1; i>0; i--)); do
      while (( (rand=$RANDOM) >= max )); do :; done
      rand=$(( rand % (i+1) ))
      tmp=${array[i]} array[i]=${array[rand]} array[rand]=$tmp
   done
}

while true; do
    array=(  'SNAPSHOT' 'RECORD_VIDEO' 'TEXT' 'O_K_GLASS' )
    #array=( 'SEND_A_MESSAGE_TO' )
    shuffle

    i=0
    while [[ -e ${array[0]}_$i.log ]] ; do
        let i++
    done

    clear

    for x in "${array[@]}"
    do
        echo ${x}_$i.log
        read -n 1 key

        cat /dev/cu.usbmodem485911 | tee ${x}_$i.log &
        PID=$!

	echo "..."
        read -n 1 key
        kill -INT $PID
	killall tee
	clear
    done
done
