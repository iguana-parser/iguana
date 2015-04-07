Diguana
======

Diguana is a data-dependent extension of the [Iguana] (https://github.com/cwi-swat/iguana) parsing framewrok.

To run Diguana, you need the [Iguana branch of Rascal] (https://github.com/cwi-swat/rascal/tree/iguana). For instructions
how to set up Rascal, see [here] (https://github.com/cwi-swat/rascal/wiki/Rascal-Developers-Setup---Step-by-Step).

At the moment, Diguana is a prototype and you can only generate ATN grammars from a Rascal grammar.
To do so, `import` a grammar and then execute the `save(#startSymbol, path)` method in the Rascal console. 
Examples of generated ATN grammars are in the [grammars] 
(https://github.com/afroozeh/diguana/tree/master/grammars) directory, and exmaples of how to run them are in the [test] 
(https://github.com/afroozeh/diguana/tree/master/test/org/jgll/grammar) and [benchmark] (https://github.com/afroozeh/diguana/tree/master/test/org/jgll/benchmark) directories.). 

Contributers
------
- Ali Afroozeh
- Anastasia Izmaylova
