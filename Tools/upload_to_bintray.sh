#!/bin/sh
# --

modules=(
  RoxieMobile.NetworkingApi
)

# --
# Reliable way for a bash script to get the full path to itself?
# @link http://stackoverflow.com/a/4774063

pushd `dirname $0` > /dev/null
prefix=`pwd -P`
popd > /dev/null

# --
echo
for module in "${modules[@]}" ; do
  folder=`pwd` ; cd "${prefix}/../Modules/${module}"

  echo "\033[0;31m>> ${module}\033[0m"
  ./gradlew clean && ./gradlew install && ./gradlew bintrayUpload

  echo ; cd $folder
done
