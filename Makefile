.PHONY: help deps build run test clean

help: ## tampilkan daftar perintah
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2}'

deps: ## download dependencies
	./mvnw dependency:resolve

build: ## build jar (skip test biar cepat)
	./mvnw clean package -DskipTests

run: ## jalanin dev mode (pengganti ./mvnw spring-boot:run)
	./mvnw spring-boot:run

test: ## jalanin unit test
	./mvnw test

clean: ## bersihkan target/
	./mvnw clean
