#!/bin/bash

rm data/*
: > Pages.ino
cd uncompressed
for f in *; do
  echo "Processing $f file..";
  gzip -9 -c "$f" > "../data/$f.gz"
done

