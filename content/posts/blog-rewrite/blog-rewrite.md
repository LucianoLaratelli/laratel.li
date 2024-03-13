---
title: "I rewrote this site (again [again {again (again"
author: ["Luciano Laratelli"]
date: 2024-03-12
draft: false
description: I rewrote my site to use the eleventy static site generator.
---

I rewrote my site again. 

{% image "./i-think-i-will-start-a-blog.png", "A two-panel comic titled 'Developer's side projects'. The first panel has the developer sitting at their computer, saying 'I think I will start a blog...' The second panel says 'looks like I will have to develop a blogging platform from scratch first.'"%}


It has gone through at least five or six lives: first it was a
[Hugo](https://gohugo.io/) site, then it was a [Jekyll](https://jekyllrb.com/)
site (or maybe Jekyll was first), then I [wrote
it](https://github.com/LucianoLaratelli/laratel.li/tree/99fc8027e135f1288ae90da98bf6958338411486)
as a [reagent](https://reagent-project.github.io/) SPA, then I wrote it as a
server-rendered custom Clojure thing
([first](https://github.com/LucianoLaratelli/laratel.li/tree/d2d5b63d40a52d603f2b6ddcde778168db3b9123)
with [Kit](https://kit-clj.github.io/), then [another
rewrite](https://github.com/LucianoLaratelli/laratel.li/tree/2afad3b8dedc2f2e2a9773d5bff64588c4c0b755)
with some lessons learned from [Biff](https://biffweb.com/)). This latest (and,
dear god, hopefully the last) is with [eleventy](https://www.11ty.dev/).

It's hard to retrace all of my motivations for these rewrites. The initial
iterations used static site generators because I didn't know how to make a
website from HTML and CSS. I did the SPA because a) Clojure and b) my friend
Kiran had written his blog in a similar fashion. Then I switched to Kit because
of the slowness of SPAs and general difficulties authoring posts. That was bad,
too, though, so I rewrote again with a stripped-down version of Biff. That was
*fine*, but when I went to add [NamedTab](/programs/named-tab), I found it not
to my tastes again. It took me way too long to get a simple one-page site up!

I've been following [Zach Leatherman](https://fediverse.zachleat.com/@zachleat),
the creator and maintainer of eleventy, on Mastodon for some time. I liked what
I was seeing, loved the documentation, so I figured I'd give it a go. After four
or five hours of pleasant work spread out over about a week, the new site is
ready! You're reading this post on it, in fact. I still have a few things to do
(cleaning up the image pipeline, holding the CSS stuff correctly) but I like
where it's at.

This switch simplified the deployment story for this site a lot. In the most
recent iteration, publishing a new post meant:
1. Writing the post in org-mode
2. Exporting it to markdown
3. Committing and pushing to GitHub
4. [CI](https://github.com/LucianoLaratelli/laratel.li/blob/main/.github/workflows/deploy.yml)
   runs to build a
   [Docker](https://github.com/LucianoLaratelli/laratel.li/blob/main/Dockerfile)
   image that gets deployed to [Fly.io](https://fly.io/).

This takes between a minute and 45 seconds and three minutes. You may notice
that it is also overkill for what amounts to a dumb pipe serving up some HTML
and CSS.
   
Now it's: write blog post in either org-mode (then export it to markdown) or in markdown[^1]. Then, `just deploy`
to [sourcehut pages](https://srht.site/):
```make
install:
    source ${HOME}/.nvm/nvm.sh && nvm use --reinstall-packages-from=current
    source ${HOME}/.nvm/nvm.sh && nvm use && yarn install
    
process:
    npx @11ty/eleventy
    
tar: 
  tar -C _site -cvz . > site.tar.gz

publish: 
  hut pages publish -d luciano.laratel.li site.tar.gz
  
deploy:
  just install
  just process
  just tar
  just publish
```

It's simpler, is nearly instant, and is a lot more flexible.

One downside is that sourcehut [disallows](https://srht.site/limitations)
tracking scripts, so I can no longer use [plausible](https://plausible.io/) on
this site. That is entirely a vanity thing that I may be able to live without.
For the meantime, the convenience of this deployment strategy outweighs my
desire for seeing my 17 visitors a month on a slick dashboard.


[^1]: Some things (e.g. images) are just easier in markdown than trying to
    wrangle [ox-hugo](https://ox-hugo.scripter.co/).
