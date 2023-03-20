/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.utils.collections.hash;

import org.iguana.utils.function.IntFunction2;
import org.iguana.utils.function.IntFunction3;
import org.iguana.utils.function.IntFunction4;
import org.iguana.utils.function.IntFunction5;
import org.iguana.utils.function.IntFunctionAny;

public class PrimeMultiplication {

    private static final int P1 = 17;
    private static final int P2 = 31;

    public static IntFunction2 f2() {
        return (k1, k2) -> {
            int result = P1;
            result = P2 * result + k1;
            result = P2 * result + k2;
            return result;
        };
    }

    public static IntFunction3 f3() {
        return (k1, k2, k3) -> {
            int result = P1;
            result = P2 * result + k1;
            result = P2 * result + k2;
            result = P2 * result + k3;
            return result;
        };
    }

    public static IntFunction4 f4() {
        return (k1, k2, k3, k4) -> {
            int result = P1;
            result = P2 * result + k1;
            result = P2 * result + k2;
            result = P2 * result + k3;
            result = P2 * result + k4;
            return result;
        };
    }

    public static IntFunction5 f5() {
        return (k1, k2, k3, k4, k5) -> {
            int result = P1;
            result = P2 * result + k1;
            result = P2 * result + k2;
            result = P2 * result + k3;
            result = P2 * result + k4;
            result = P2 * result + k5;
            return result;
        };
    }

    public static IntFunctionAny fn() {
        return (Integer...elements) -> {
            int result = P1;
            for (int k : elements) {
                result = P2 * result + k;
            }
            return result;
        };
    }

}
