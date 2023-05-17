clean:
	rm -rf target

uberjar:
	clojure -T:build uberjar
