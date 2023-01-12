target/luciano-standalone.jar:
	bb uberjar
deploy: target/luciano-standalone.jar
	scp target/luciano-standalone.jar luciano@laratel.li:~/docker/appdata/laratel.li/japp.jar
