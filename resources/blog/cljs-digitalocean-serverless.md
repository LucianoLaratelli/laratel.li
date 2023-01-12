---
title: "Using CLJS and shadow-cljs for serverless DigitalOcean Functions"
date: 2023-01-11T06:57:00-05:00
description: ""
---

DigitalOcean (DO) [Functions](https://www.digitalocean.com/products/functions): "a serverless computing solution that runs on-demand, enabling you to focus on your code, scale instantly with confidence, and save costs by eliminating the need to maintain servers." Since I'm a fanatic, I would like to write some Clojure for my serverless use case. Because DO offers Node as a runtime, we're able to use ClojureScript to write code and deploy it to the serverless, er, server.

Source code for this blog post is [available](https://git.sr.ht/~luciano/cljs-digitalocean-serverless-function).

You'll need a DO account. Log in, select the appropriate team, then select `Functions` on the left-hand column. Create a function namespace and you're ready to go.

Next we need the `doctl` binary. Here's what I did:

```bash
brew install doctl
doctl auth init
doctl serverless install
doctl serverless connect
```

This gets you authenticated with DO so you can deploy from the command line.

```bash
npx create-cljs-project do_serverless
cd do_serverless
```

Now, let's edit the generated `shadow.cljs` a bit. Add this map as the value under `:builds`:

```clojure
{:core {:target :node-script
         :main core/main
         :output-to "packages/do-serverless/core/core.js"}}
```

Create `src/main/core.cljs` and define `main` in it:

```clojure
(ns core)

(defn main [])
```

Create `packages/do-serverless/core/package.json` with this in it:

```json
{
  "name": "core",
  "version": "1.0.0",
  "description": "CLJS on DO!",
  "main": "core.js",
  "dependencies": {
    "source-map-support": "^0.5.21"
  },
  "devDependencies": {}
}
```

Lastly, create `project.yml`:

```yaml
packages:
  - name: do-serverless
    actions:
      - name: core
        runtime: nodejs:default
```

OK! Let's see where we're at:

```bash
shadow-cljs release core
doctl serverless deploy .
```

Now we can go to the Functions tab on DO's site and run our function by first going to the function namespace, clicking on the name of the function, and hitting Run. I get this error:

```txt
2023-01-12T11:14:08.172732642Z stdout: Action entrypoint 'main' is not a function.
```

What tha...

At this point, I dug around and found that DO maintains a bunch of sample functions. Going to the [Node one](https://web.archive.org/web/20220728083446/https://github.com/digitalocean/sample-functions-nodejs-qrcode/blob/main/packages/qr/qr/qr.js), we see this:

```javascript
exports.main = (args) => { ... }
```

Huh. OK, so let's do that in our example, `src/main/core.cljs`:

```clojurescript
(ns core)

(defn main [& args]
  (println "hello!")
  (println "args: " args))

(set! js/exports.main main)
```

And re-build and deploy.

```
2023-01-12T11:22:14.933096349Z stdout: hello!
2023-01-12T11:22:14.933797937Z stdout: args:  nil
2023-01-12T11:22:14.961195498Z stdout: hello!
2023-01-12T11:22:14.982016323Z stdout: args:  (#js {} ... // output truncated
```

Ok, so when our function executes, our `main` gets executed twice. I don't know why this happens. If I run our compiled javascript file locally with `node`, I only see one execution:

```
$ node packages/do-serverless/core/core.js
hello!
args:  nil
```

So, OK, some detail that's above my head. My use case for serverless would, uh, not do well with running everything twice. So, what to do?

Well, we know whatever we tell `shadow` our `main` is will get run. And we also know whatever we tell DO our main is (the `js/exports.main` bit) will also run. Well, I only care about the DO side of things!

```clojurescript
(ns core)

(defn my-actual-function [& args]
  (println "hello!")
  (println "args: " args))

(defn main [])

(set! js/exports.main my-actual-function)
```

```
2023-01-12T11:28:57.786063804Z stdout: hello!
2023-01-12T11:28:57.793552189Z stdout: args:  (#js {} ... // output truncated
```

Neat!
