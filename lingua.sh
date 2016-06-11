#!/bin/bash
MAINJAR=lingua.jar
CLASSPATH=.:lib/hamcrest-core-1.3.jar:lib/junit-4.12.jar:lib/swingx-0.8.0.jar:$MAINJAR:../htmlparser1_6/lib/htmlparser.jar:..htmlparser1_6/lib/htmllexer.jar
MAIN=de.lingua.gui.Main
java -classpath $CLASSPATH $MAIN
