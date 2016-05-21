#!/bin/bash

: > Pages.ino
for f in html/*; do
  echo "Processing $f file..";

  #name=$(echo "$f" | sed 's/\./_/g' | sed 's/\//_/g')
  #echo "unsigned char $name[] = {" >> Pages.ino
  #cat "$f" | gzip -n | xxd -i >> Pages.ino
  #echo "};" >> Pages.ino

  xxd -i "$f" | sed ':a;N;$!ba;s/\n}/, 0x00\n}/g' >> Pages.ino
done

