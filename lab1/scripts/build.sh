#!/bin/bash
mkdir -p ./build
mvn clean install;
cp ./target/mathmod-lab1-1.0-SNAPSHOT.jar ./build/mathmod-lab1-1.0-SNAPSHOT.jar
