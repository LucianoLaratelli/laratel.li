target/luciano-standalone.jar: $(shell find -E . -type f -regex ".*\.(clj|edn|html|xml|css|js|org|md|js|asc)")
	bb uberjar
deploy: target/luciano-standalone.jar
	scp target/luciano-standalone.jar luciano@laratel.li:~/docker/appdata/laratel.li/japp.jar
