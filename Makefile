clean:
	rm -rf target

run:
	clojure -M:dev

repl:
	clojure -M:dev:nrepl

uberjar:
	clojure -T:build all
