---
title: CV
date: 2023-09-14
layout: layouts/cv.njk
---

## Work Experience

### Dwolla

- Software Engineer
- April 2022 -> September 2023
- Fully remote

I was on the five-person team that built out [Dwolla Connect](https://www.dwolla.com/dwolla-connect/), a new payment-processing API. It’s an event-driven system built using the [Serverless ](https://www.serverless.com/) framework to configure AWS Lambda, SQS/SNS, and DynamoDB. We wrote the system in TypeScript using the [fp-ts](https://gcanti.github.io/fp-ts/) typed functional programming library.

Became a resource for the team on fp-ts. I wrote an internal document on best practices for this library.

Contributed to internal RFCs on topics including improving microservice testing, platform alerting, runtime validation (using [Zod](https://zod.dev/)), and sandbox simulations.

Designed and implemented a load-testing plan for Dwolla Connect, yielding actionable, data-backed performance improvements that lowered response times by 25% for our most critical workloads. I built this system in Clojure and scaled it to 30 transactions per second before switching to generate requests for use with the [vegeta](https://github.com/tsenart/vegeta) load testing tool.

Wrote a visualization tool for the new system that parsed the Serverless YAML files and wrote [D2](https://d2lang.com/) markup to generate diagrams of the new Dwolla Connect system. This resulting diagrams aided design discussions and helped team members in understanding the system. I identified critical subsystems and generated individual diagrams for those in addition to the primary “whole system” diagram.

Maintained and expanded legacy Scala microservices, using the Cats and Cats Effect libraries.

### Flexibits, Inc.

- Software Engineer
- March 2021 -> April 2022
- Fully remote

Wrote code across the stack and for all supported devices using Objective-C, Swift, and SwiftUI.

Owned the implementation and deployment of new API integrations for Microsoft Graph and Google People. - Debugged and resolved complex, multi-platform syncing bugs (Apple Watch/iPhone, iPhone/Mac).

Implemented custom week numbering (and learned just how frustrating date math could be!)

Main developer in charge of new features and bug fixes for the Apple Watch version of Fantastical.

Ported Shortcuts to macOS, extending them with new Shortcuts.

<!-- Some examples of my work on the UI side are fixing the bugs introduced by watchOS8 and iOS 15, adding support for item creation and list refresh on the Watch, and implementing context menus for item templates. -->

<!-- On the business logic side, I implemented new API integrations for Microsoft Graph and Google People, debugged and resolved complex sync bugs between iOS and watchOS, extended Fantastical's parser, dealt with bugs regarding event ordering on the Watch, added new AppleScript functionality, and implemented custom week numbering. -->

## Personal Information

Residence:

- Miami Beach, FL

Citizenships:

- United States
- Italy

Languages spoken:

- English (native)
- Spanish (native)
- Italian (basic, in progress)

## Skills

### Computer Languages

I've worked professionally with SQL, TypeScript, Swift, Objective-C, Scala, and Clojure(Script).

I used C, C++, Python, and Bash when I was in graduate school and when I was doing computational chemistry research.

I can hack around in HTML and CSS, but not professionally. I also have a good bit of LaTeX experience due to my stint in academia.

### Computer Platforms and Frameworks

I spent a good bit of time in school working with CUDA and MPI, but it's been a while.

### Software Engineering Tools

- `docker`
- `emacs` (using my [custom configuration](https://git.sr.ht/~luciano/.home/tree/main/item/doom/.doom.d/config.el) for [Doom Emacs](https://github.com/hlissner/doom-emacs))
- `gdb`
- `git`
- `vim`
- Xcode

### Operating Systems

I'm proficient in the usage and configuration of Arch Linux, the Debian-based Linux distributions, and macOS. I used to be pretty good with Windows, too, but it's been years since I used it. Recently I've been hacking around with GNU Guix System for my home server.

## Projects

### Personal Projects

#### laratel.li

You're looking at it! 

### School Projects

[`reljr`](https://github.com/reljr/reljr) is a relational algebra evaluator written in Clojure and Clojurescript. This was a group project for the advanced databases elective at USF. I contributed a large portion of the parser and its grammar, portions of the interpreter, the frontend interface, and the majority of the documentation.

[`dj2ll`](https://github.com/lucianolaratelli/dj2ll-public) is an extension of the `dj2dism` compiler. It reuses the front-end components (lexer, parser, and typechecker) but implements an entirely new backend using the LLVM C++ API.

`dj2dism` is a non-optimizing compiler for a Java-like object-oriented language, `diminished Java` (DJ). The compiler generates a RISC-like instruction set for a provided virtual machine. This semester-long project involved interpreting the language specification into the five main components of a compiler: lexer (using `flex`), parser (using `bison`), AST generation (using `bison` semantic actions), type-checker, and code generator. The type-checker and code generator are implemented in C89. Due to the course's honor code, I can not publicly display the source code.

## Education

### Master of Science in Computer Science

- May 2021
- University of South Florida
- Tampa, FL

### Bachelor of Science in Chemistry

- May 2018
- University of South Florida
- Tampa, FL

### High School

- May 2013
- Belen Jesuit Preparatory School
- Miami, FL

### Other

I completed eighteen credit hours in the Ph.D. Chemistry program at USF between August 2018 and May 2019, focusing on research in computational chemistry. I decided to transfer into the Computer Science program sometime during the spring of 2019, and began taking classes for that degree that summer.

## Other Work Experience

### Apple (retail)

- Technical Specialist => Technical Expert
- July 2019 -> November 2020
- Tampa, FL

I worked at the Genius Bar, doing assessment and resolution of software issues that occurred with Apple's mobile offerings -- iPhone, iPad, and Apple Watch. In February of 2020 I was promoted to "Technical Expert", which also involved doing physical repairs of iPhones, mostly battery and display repairs. I was consistently in the top five employees for sessions completed per hour and lowest average session duration. I also assisted the most customers out of anyone on the team in the fourth quarter of 2019.

### University of South Florida Department of Chemistry

- Graduate Teaching Assistant
- August 2018 -> May 2019
- Tampa, FL

I worked as a Lab TA for the General Chemistry I lab, where I was responsible for teaching students semester basic and intermediate experimental chemistry techniques, as well as the chemical theory behind the experiments performed. Taught students the basics of data analysis and reporting.Responsible for teaching students proper laboratory safety techniques and ensuring their safety at all times in the lab. This position required the communication of ideas using distinct methods, handling multiple tasks at once, and managing groups of people to reach a common goal.

### University of South Florida Space Lab

- Undergraduate Researcher => Graduate Researcher
- September 2016 -> May 2019
- Tampa, FL

I worked in Dr. Brian Space's lab, where I was responsible for conducting independent computational chemistry research, mentoring undergraduates, and contributing to the lab’s code base, a program called `mpmc`. Designed and implemented a parallel [end-to-end testing suite](https://github.com/mpmccode/mpmc_testing) for the lab’s code base that supports serial and parallel testing. I improved the lab's code base by refactoring important sections, adjusting the build system to achieve cross-platform compilation, increasing output functionality, and constructing and adding a new set of simulation parameters.

### University of South Florida Leahy Lab

- Undergraduate Researcher
- August 2015 -> August 2016
- Tampa, FL

I worked as an undergraduate researcher in Dr. James Leahy's lab, under the direction of Zach Shultz. I was performing basic labwork in organic chemistry, including titrations, spectroscopic analysis, and purification of novel compounds. Eventually I transitioned to also doing more advanced reactions, more difficult separations chemistry, and natural products exctraction.

### University of Miami Miller School of Medicine Podack Lab

- Undergraduate Researcher
- May 2014 -> August 2014
- Miami, FL

I worked as a (very green) undergraduate in the lab of Dr. Eckhard Podack, under the direction of Ryan McCormack. I started with doing basic cell culture, mostly on agar plates, and eventually transitioned to more advanced cultures and working with animal models (particularly mice), where I studied the effects of a novel anticancer drug intended to treat lung cancer.

## Awards and Scholarships

### Experiencing HPC for Undergraduates, SC17

I received full funding from the International Conference for High Performance Computing, Networking, Storage, and Analysis to travel to the SC 17 Conference in Denver, Colorado for a week, where I attended talks on subjects such as the C++ language standard and efficient usage of HPC systems.

### 2nd Place, 15th Raymond N. Castle Student Research Conference

Undergraduate Poster Presentation, Comparison of Many-Body Polarizable Potentials With Lennard-Jones and Stockmayer Potentials in Grand Canonical Monte Carlo Simulation

### National Hispanic Recognition Program Scholar, 2013 - 2017

Four-year scholarship awarded to me by USF for pre-university academic achievement.
