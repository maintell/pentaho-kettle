#!/bin/sh
# ******************************************************************************
#
# Pentaho
#
# Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
#
# Use of this software is governed by the Business Source License included
# in the LICENSE.TXT file.
#
# Change Date: 2029-07-20
# ******************************************************************************


echo "SpoonDebug is to support you in finding unusual errors and start problems."
echo "-"

echo "Set logging level to Debug? (default: Basic logging)"
echo "Debug? (Y=Yes, N=No, C=Cancel)"
while true ; do
    read ync
    case $ync in
        Y* ) SPOON_OPTIONS="-level=Debug";export SPOON_OPTIONS;break;;
        N* ) break;;
        C* ) exit;;
        * ) exit;;
    esac
done

echo "-"
echo "Redirect console output to SpoonDebug.txt in the actual Spoon directory?"
echo "Redirect to SpoonDebug.txt? (Y=Yes, N=No, C=Cancel)"
while true ; do
    read ync
    case $ync in
        Y* ) SPOON_REDIRECT="1";unset SPOON_PAUSE;break;;
        N* ) break;;
        C* ) exit;;
        * ) exit;;
    esac
done

echo "-"
SPOONDIR=$(dirname "$(readlink -f "$0")")
echo Launching Spoon: "$SPOONDIR/spoon.sh $SPOON_OPTIONS"
if [ "$SPOON_REDIRECT" != "1" ] ; then
    exec ./spoon.sh $SPOON_OPTIONS
else
    echo "Console output gets redirected to $SPOONDIR/SpoonDebug.txt"
    exec ./spoon.sh $SPOON_OPTIONS >> "$SPOONDIR/SpoonDebug.txt" 2>&1
fi
