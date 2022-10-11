#!/usr/bin/bash
echo ":: Downloading ANTLR4 complete binary >> antlr-4.11.1-complete.jar"
curl -L https://www.antlr.org/download/antlr-4.11.1-complete.jar --output antlr-4.11.1-complete.jar
echo ":: Downloading StringTemplate binary >> ST-4.3.4.jar"
curl -L http://www.stringtemplate.org/download/ST-4.3.4.jar --output ST-4.3.4.jar