alias i := install
alias s := serve
alias d := deploy

install:
    source ${HOME}/.nvm/nvm.sh && nvm use --reinstall-packages-from=current
    source ${HOME}/.nvm/nvm.sh && nvm use && yarn install

serve:
    npx @11ty/eleventy --serve

deploy:
  npx @11ty/eleventy
  fly deploy --local-only
