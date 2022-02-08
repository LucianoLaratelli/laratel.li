# Projects

## Personal Projects

### [laratel.li](https://github.com/lucianolaratelli/laratel.li)

This site! It's built almost entirely in Clojurescript; some macros in Clojure
land read files off the filesystem, but otherwise there's no backend whatsoever.

I owe a lot to my friend Kiran's excellent
[`CyberMonday`](https://github.com/kiranshila/cybermonday) library. I must admit
I stole his code block CSS and some of the CyberMonday-specific things from his
[blog](https://blog.kiranshila.com/). Thanks Kiran!

I used [Dracula UI](https://draculatheme.com/ui) to style the site.

I host it on a DigitalOcean droplet, using `docker-compose` with these containers:

- [SWAG](https://docs.linuxserver.io/images/docker-swag), the Secure Web
  Application Gateway gives me nginx reverse proxying and automated LetsEncrypt
  certificate renewal for as many domains as I'd like.
- [Matomo](https://github.com/matomo-org/docker) for privacy-focused analytics
- [MariaDB](https://hub.docker.com/_/mariadb/) as the data store for Matomo

## School Projects

### [reljr](https://github.com/reljr/reljr)

A relational algebra evaluator written in Clojure(script). This was a group
project for the advanced databases elective at USF. I contributed a large
portion of the parser and its grammar, portions of the interpreter, the frontend
interface, and the majority of the documentation.

### [dj2ll](https://github.com/lucianolaratelli/dj2ll-public)

An extension of the `dj2dism` compiler. It reuses the front-end components
(lexer, parser, and typechecker) but implements an entirely new backend using
the LLVM C++ API. I wrote this as part of the Independent Study elective at USF.

### dj2dism

A non-optimizing compiler for a Java-like object-oriented language, `diminished Java` (DJ), written for the Compilers elective at USF. The compiler generates a
RISC-like instruction set for a provided virtual machine. This semester-long
project involved interpreting the language specification into the five main
components of a compiler: lexer (using `flex`), parser (using `bison`), AST
generation (using `bison` semantic actions), type-checker, and code generator.
The type-checker and code generator are implemented in C89. Due to the course's
honor code, I can not publicly display the source code.
