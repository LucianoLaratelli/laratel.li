# luciano.laratel.li

[![deploy](https://github.com/LucianoLaratelli/laratel.li/actions/workflows/deploy.yml/badge.svg)](https://github.com/LucianoLaratelli/laratel.li/actions/workflows/deploy.yml)
![code:data](https://img.shields.io/badge/code-data-blueviolet)

This is my blog.

## Build

```shell
mkdir ~/repos
git clone https://github.com/LucianoLaratelli/laratel.li.git
cd ~/repos/laratel.li
```

To build the release build and serve it locally:

```shell
make uberjar
java -jar luciano-service.jar
```
