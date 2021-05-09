---
title: "Closing Pull Requests (and Issues) on Github Without Using Github's Web UI"
date: 2021-04-04
---

## Motivation

I like to use [magit](https://magit.vc/) to interact with git. I wanted to
figure out how exactly the command-line version of git (which `magit` is running
under the hood) interacts with pull requests on GitHub, because the workflow at
my job is to make commits on feature branches, then merge them onto `develop`
following a PR (which provides time for code review.)

## Closing a PR (but not issues)

Assuming you have a PR to merge a branch `test` onto a branch `develop`, with
some linked issues, this `magit` workflow will close the PR when you merge, but
_not_ the associated issues:

- checkout your local copy of `develop`, and pull any changes from the remote
  (`b c develop` followed by `F origin/develop`)
- merge `test` into `develop` (`m m test`)
- push the changes to the remote (`p origin/develop`)

## Closing the PR and the issues

To get the issues to close, you must edit the commit message _for the merge_
with `m e test` after checking out `develop`. The merge commit must mention each
of the issues you wish to close. I usually go for a commit message like this:

```text
Merge branch 'test'

* test:
  commit 1

Closes #14
Closes #16
```

## What doesn't work

I tried a few things that I thought would be more convenient but that sadly didn't work:

- Using the PR number in the `Closes` string sadly does _not_ close the
  associated issues.
- You can't do `Closes #1, #2, #3`. It appears each issue you want to close must
  have its own "closing word" associated with it.
