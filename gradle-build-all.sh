#! /bin/bash -e

for dir in voting-* ; do
	(cd $dir ; ./gradlew build $*)
done