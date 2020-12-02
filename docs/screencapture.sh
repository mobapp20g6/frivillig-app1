#!/bin/sh
if [ $# -eq 1 ]
then
	echo "GOT DEVICE $1"
	export ANDROID_SERIAL=$1
	exec scrcpy
else
	echo "PLEASE SET DEVICE ID WITH 'adb devices'."
fi
