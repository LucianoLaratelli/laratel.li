---
title: "Task ordering"
author: ["Luciano Laratelli"]
date: 2023-07-18T09:21:00-04:00
draft: false
---

Lily and I wear [retainers](https://web.archive.org/web/20230717004606/https://www.shutterstock.com/image-photo/two-invisible-dental-teeth-aligners-on-1807717714) every night. We clean them daily using a regular toothbrush and liquid hand soap. Cleaning these retainers at the end of the day, before bed, is one of my least favorite chores. I dealt with that for a long time! I would delay, put it off, getting in to bed fifteen or twenty minutes later than I would have if I would have just washed the damn things as soon as I finished flossing and brushing my teeth. For a while, I tried cleaning the retainers before the flossing and tooth brushing, but that didn't improve things. After a while I figured out a way to resolve my issue: I now clean our retainers _in the morning_, immediately after brushing my teeth. It's the easiest thing in the world at that point in the day, but feels impossible at night.

Though my issue with the nighttime retainer cleaning is probably just a classic ADHD Moment, after I got the morning thing going, it got me thinking about task ordering. It's not a novel idea that tasks have orderings. A simple example is a task where you don't have the knowledge to accomplish it yet. You can't accomplish it without getting the knowledge first. Not every ordering is "strict", though — the retainer cleaning saga involved at least three orderings.

```text
floss -> nighttime tooth brushing -> clean retainers -> get in bed

clean retainers -> floss -> nighttime tooth brushing -> get in bed

wake up -> random morning things -> morning tooth brushing -> clean retainers -> [ .. the whole day .. ] -> floss -> nighttime tooth brushing -> get in bed
```

Once I had noticed that, I realized I'd seen variations of the concept before.

[Structured Procrastination](https://www.structuredprocrastination.com/) is well known, and one of my favorite essays of its kind. It's about ordering your tasks so that there's always something you don't want to do (but have committed to doing) at the "end of the list."

CPUs do [out-of-order execution](https://en.wikipedia.org/wiki/Out-of-order_execution).

[The Engineer/Manager Pendulum](https://charity.wtf/2017/05/11/the-engineer-manager-pendulum/) is about flipping the traditional

```text
Start Career -> IC -> Management -> End Career
```

order into

```text
Start Career -> IC -> Management -> IC -> Management -> End career
                                    ↑         ↓
                                     ↖_______↙
```

There are algorithms that utilize [amortization](https://en.wikipedia.org/wiki/Amortized_analysis) to improve their average performance. C++'s `std::vector` notably [uses this technique](https://stackoverflow.com/a/5232342/5692730) to achieve (amortized) constant cost when growing in capacity. This is a reordering of tasks from this expensive loop:

```text
Make vector with capacity 1 -> add item -> increase capacity
                                    ↑         ↓
                                     ↖_______↙
```

To one that minimizes the number of times we `increase capacity`.

[DFA minimization](https://en.wikipedia.org/wiki/DFA_minimization) is an algorithm for reducing the number of states in a (deterministic and finite) automaton. We remove states if we can show they're equivalent. Now that I think about it, removal is only sort of like reordering. There's a world of difference between "I clean my retainers in the morning instead of at night" and "I don't clean my retainers".

Project management tools are all about task reordering.

Task reordering isn't a magical solution. I despise washing our coffee pot and can't find a good time of day to do it. Part of the problem is that we don't use it every day; some mornings we have tea, yerba mate, or go out for coffee instead. Maybe if I worked the washing of the coffee pot into a fixed place in my daily routine, I would realize it's in the wrong place in the routine, place it correctly, and then be able to wash it as easily as I clean the retainers.

Maybe part of the problem is that reordering tasks only works for low-friction tasks where the cost to start doing them is low. Some tasks are really four or five tasks dressed in a trench coat disguised as a single task, and it's not always easy to tell that from a todo list. Comparing "cleaning the retainers" to "washing the coffee pot" doesn't convey any of this context. But cleaning the retainers takes two minutes at most, while the coffee pot involves making sure there's space on the dish drying rack, ensuring there's space in the sink, putting on the dishwashing gloves, then washing the four separate pieces of the coffee pot ensemble.

Those first two steps (of the single "washing the coffee pot" task!) might create a cascade of new tasks. If the dishes on the drying rack are wet, we'd have to dry them then put them away. Is the sink full because the dishwasher is running? If that's the case, maybe there isn't space in the sink to wash the coffee pot. This can go on and on.

Getting things done is hard, especially with executive function disorders like ADHD. Thinking about the order of tasks has helped me a great deal.
