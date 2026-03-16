SHELL := /bin/bash
.PHONY: help up down run check

ifneq ("$(wildcard .env)","")
    include .env
    export $(shell sed 's/=.*//' .env)
endif

help:
	@echo "Available commands:"
	@echo "  make up       - Start MariaDB container (docker-compose up)"
	@echo "  make down     - Stop MariaDB container (docker-compose down)"
	@echo "  make run      - Build and run application (loads .env)"
	@echo "  make check    - Run all application checks"

up:
	docker-compose up

down:
	docker-compose down

run:
	./gradlew build
	./gradlew run

check:
	./gradlew check
