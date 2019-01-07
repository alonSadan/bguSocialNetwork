CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude

LDFLAGS:=-lboost_system -pthread



all: assignment3

	g++ -o bin/BGSclient bin/connectionHandler.o bin/main.o bin/ReceiveMessages.o $(LDFLAGS)



assignment3: bin/connectionHandler.o bin/main.o bin/ReceiveMessages.o

	

bin/connectionHandler.o: src/connectionHandler.cpp

	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp



bin/main.o: src/main.cpp

	g++ $(CFLAGS) -o bin/main.o src/main.cpp



bin/ReceiveMessages.o: src/ReceiveMessages.cpp

	g++ $(CFLAGS) -o bin/ReceiveMessages.o src/ReceiveMessages.cpp 



.PHONY: clean

clean:

	rm -f bin/*
