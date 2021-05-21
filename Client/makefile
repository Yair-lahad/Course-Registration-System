CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread

all: BGRSclient
	g++ -o bin/BGRSclient bin/connectionHandler.o bin/BGRSclient.o $(LDFLAGS)

BGRSclient: bin/connectionHandler.o bin/BGRSclient.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGRSclient.o: src/BGRSclient.cpp
	g++ $(CFLAGS) -o bin/BGRSclient.o src/BGRSclient.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
