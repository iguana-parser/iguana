Diguana
======

Diguana is a data-dependent extension of the [Iguana] (https://github.com/cwi-swat/iguana) parsing framewrok.

To run Diguana, you need the [Iguana branch or Rascal] (https://github.com/cwi-swat/rascal/tree/iguana). For the instructions
how to set up Rascal see [here] (https://github.com/cwi-swat/rascal/wiki/Rascal-Developers-Setup---Step-by-Step).

At the moment, Diguana is a prototype and you can only generate ATN grammars from a Rascal grammar.
To do so, `import` a grammar and thenexecute `save(#startSymbol, path)` method in the Rascal console. 
Examples of generated ATN grammars and how to run them in Java are in the [grammar] 
(https://github.com/afroozeh/diguana/tree/master/grammars) and the [test] 
(https://github.com/afroozeh/diguana/tree/master/test/org/jgll/grammar) directories, respectively). 

Contributers
------
- Ali Afroozeh
- Anastasia Izmaylova
