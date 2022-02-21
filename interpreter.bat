@echo off

javac -encoding UTF-8 .\mua\Main.java
java mua.Main
del .\mua\*.class