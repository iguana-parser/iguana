module Main where

k x = do let y = 1
         let z = do {
    let l = 2
      ; -- this comma is funny
                     let n = do let i = 1
                                let h = 2
                                i + h
                     ; -- this one as well
            l + n; 
       }
         y + z