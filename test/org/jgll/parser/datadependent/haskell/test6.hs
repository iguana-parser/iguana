module Main where

main = do print 1
          let x = 1
           in print x
          print 2
          do {
    print 4; print 5
     }
          print 3

main = do print 2
           + 3