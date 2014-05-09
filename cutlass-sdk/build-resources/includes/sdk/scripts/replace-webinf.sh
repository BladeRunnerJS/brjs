#!/bin/sh

if [  -z "$1" ]; then
  echo "Please provide an application name";
  exit;
fi

SCRIPT_PATH=$(cd `dirname ${0}`; pwd)
APP_TO_CHECK=$1
APP_TO_CHECK_PATH=$SCRIPT_PATH/../../apps/$APP_TO_CHECK

echo "Checking app $APP_TO_CHECK at $APP_TO_CHECK_PATH"

# create a temporary app
TMP_APP_NAME=`mktemp tmpXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX`
rm $TMP_APP_NAME
TMP_APP_PATH=$SCRIPT_PATH/../../apps/$TMP_APP_NAME
$SCRIPT_PATH/../brjs create-app $TMP_APP_NAME

# delete old WEB-INF
rm -rf $APP_TO_CHECK_PATH/WEB-INF

# move tmp app WEB-INF over
mv $TMP_APP_PATH/WEB-INF $APP_TO_CHECK_PATH/

# delete tmp app
rm -rf $TMP_APP_PATH

echo "WEB-INF directory replaced for $APP_TO_CHECK"
