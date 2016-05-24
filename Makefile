compile:
	mkdir dist
	javac -cp ./src -d ./dist ./src/App.java

jar:
	mkdir build
	jar cvfe ./build/build.jar App -C dist/ .

clean:
	rm -rf dist

build: compile jar clean

build-downloader:
	mkdir dist
	javac -cp ./src -d ./dist ./src/DownloadManager.java
	jar cvfe ./build/Downloader.jar DownloadManager -C dist/ .
	make clean

run:
	java -jar ./build/build.jar
