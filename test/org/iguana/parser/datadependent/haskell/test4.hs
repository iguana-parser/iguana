module Main where

h x = case x of
         1 -> 1 + z
         2 -> do 
                let y = z
                do let zz = z
                   x + y + zz 
           where z = 4