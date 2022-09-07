# laratel.li

[![Deploy laratel.li](https://github.com/LucianoLaratelli/laratel.li/actions/workflows/main.yml/badge.svg)](https://github.com/LucianoLaratelli/laratel.li/actions/workflows/main.yml)
![code:data](https://img.shields.io/badge/code-data-blueviolet)

This is my blog.

## Build

```shell
mkdir -p ~/repos
cd ~/repos
git clone https://github.com/LucianoLaratelli/laratel.li.git
cd ~/laratel.li
yarn install
```

To run locally for development with hot-reloading etc at `localhost:3000`:

```shell
yarn shadow-cljs watch laratelli
```

To build the release build and serve it locally:

```shell
yarn shadow-cljs release laratelli
cd public
python -m http.server
```

## Project particulars

Some of my lowering functions are not functioning properly, so paragraphs in
markdown all have to be on one line, as opposed to re-flowed how I like them.

To hot-reload the frontend after making changes to a blog post, save the blog
post, go to `parsed_posts.clj`, make some change to the file (I usually just add
a newline between `defn`s) and save it. Then, refresh the page in the browser.

## Layout

Blog markdown is in `resources/blog`.
