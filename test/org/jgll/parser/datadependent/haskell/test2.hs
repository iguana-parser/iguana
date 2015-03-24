module Main where

g = do x <- [1,2];
       y <- [3,4]; z 
          <- [5,5]
       return x