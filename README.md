# laratel.li

This is my blog.

## Build

```shell
mkdir ~/repos
git clone https://github.com/LucianoLaratelli/laratel.li.git
cd ~/repos/laratel.li
```

To run locally for development with hot-reloading etc at `localhost:3000`:

```shell
clj -M:dev
```

To build the release build and serve it locally:

```shell
bb uberjar
cd target
java -jar luciano-standalone.jar
```
