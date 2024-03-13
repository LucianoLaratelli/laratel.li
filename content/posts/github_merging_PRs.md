---
title: "Closing Pull Requests (and Issues) on Github Without Using Github's Web UI"
date: 2021-04-04
---

## Motivation

I like to use [magit](https://magit.vc/) to interact with git. I wanted to figure out how exactly the command-line version of git (which `magit` is running under the hood) interacts with pull requests on GitHub, because the workflow at my job is to make commits on feature branches, then merge them onto `develop` following a PR (which provides time for code review.)

## Closing a PR (but not issues)

I'm making the assumption you have a PR to merge a branch `test` onto a branch `develop`, with some linked issues. Here's how you close PR when you merge. Note that this doesn't close the associated issues. First, checkout your local copy of `develop`: `b c develop`, then and pull any changes from your remote with `F origin/develop`. Merge `test` into `develop` with `m m test`. Finally, push the changes to the remote: `p origin/develop`.

## Closing the PR and the issues

To get the issues to close, you must edit the commit message _for the merge_ with `m e test` after checking out `develop`. The merge commit must mention each of the issues you wish to close. I usually go for a commit message like this:

```text
Merge branch 'test'

* test:
  commit 1

Closes #14
Closes #16
```

## What doesn't work

Sadly, you can't use the PR number in the `Closes` string to close the associated issues. You have to enumerate the issues explicitly. You also can't do `Closes #1, #2, #3`. Each issue has to have its own closing keyword.
