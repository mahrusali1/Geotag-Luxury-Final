#!/bin/bash

# Download library appinventor
git submodule update --init --recursive

# Pindah ke folder lib
cd lib/appinventor
git checkout master
git pull origin master

echo "Library updated successfully without signing."
