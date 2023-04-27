#!/usr/bin/env sh
find . -type f \
    -name "*.js" ! -name "*.min.*" ! -name "vfs_fonts*" \
    -exec echo {} \; \
    -exec uglify-js -o {}.min {} \;

find . -type f \
    -name "*.css" ! -name "*.min.*" \
    -exec echo {} \; \
    -exec uglifycss --output {}.min {} \;

find . -type f -name "*.css.min" -exec sh -c 'mv "$1" "${1%.css.min}.min.css"' _ {} \;
find . -type f -name "*.js.min" -exec sh -c 'mv "$1" "${1%.js.min}.min.js"' _ {} \;
