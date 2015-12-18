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

package iguana.utils.collections.hash;

import iguana.utils.function.*;

public class MurmurHash2 {

	private final static int m = 0x5bd1e995;
	private final static int r = 24;

    public static IntFunction5 hash5() {
        return hash5(19);
    }

    public static IntFunction5 hash5(int seed) {
        return (a, b, c, d, e) -> {

            int h = seed ^ 4;

            // a
            int k = a;
            k = mixK(k);
            h = mixH(h, k);

            // b
            k = b;
            k = mixK(k);
            h = mixH(h, k);

            // c
            k = c;
            k = mixK(k);
            h = mixH(h, k);

            // d
            k = d;
            k = mixK(k);
            h = mixH(h, k);

            // last mix
            h *= m;
            h ^= h >>> 13;
            h *= m;
            h ^= h >>> 15;

            return h;
        };
    }

    public static IntFunction4 hash4() {
        return hash4(19);
    }

	public static IntFunction4 hash4(int seed) {
        return (a, b, c, d) -> {

            int h = seed ^ 4;

            // a
            int k = a;
            k = mixK(k);
            h = mixH(h, k);

            // b
            k = b;
            k = mixK(k);
            h = mixH(h, k);

            // c
            k = c;
            k = mixK(k);
            h = mixH(h, k);

            // d
            k = d;
            k = mixK(k);
            h = mixH(h, k);

            // last mix
            h *= m;
            h ^= h >>> 13;
            h *= m;
            h ^= h >>> 15;

            return h;
        };
	}

    public static IntFunction3 hash3() {
        return hash3(19);
    }

	public static IntFunction3 hash3(int seed) {
        return (a, b, c) -> {

            int h = seed ^ 4;

            // a
            int k = a;
            k = mixK(k);
            h = mixH(h, k);

            // b
            k = b;
            k = mixK(k);
            h = mixH(h, k);

            // c
            k = c;
            k = mixK(k);
            h = mixH(h, k);

            // last mix
            h *= m;
            h ^= h >>> 13;
            h *= m;
            h ^= h >>> 15;

            return h;
        };
    }

    public static IntFunction2 hash2() {
        return hash2(19);
    }

    public static IntFunction2 hash2(int seed) {
        return (a, b) -> {

            int h = seed ^ 4;

            // a
            int k = a;
            k = mixK(k);
            h = mixH(h, k);

            // b
            k = b;
            k = mixK(k);
            h = mixH(h, k);

            // last mix
            h *= m;
            h ^= h >>> 13;
            h *= m;
            h ^= h >>> 15;

            return h;
        };
    }

    public static IntFunctionAny hashN() {
        return hashN(19);
    }

    public static IntFunctionAny hashN(int seed) {
        return (Integer...elements) -> {

            int h = seed ^ 4;

            for (int element : elements) {
                int k = element;
                k = mixK(k);
                h = mixH(h, k);
            }

            // last mix
            h *= m;
            h ^= h >>> 13;
            h *= m;
            h ^= h >>> 15;

            return h;
        };
    }

    private static int mixK(int k) {
        k *= m;
        k ^= k >>> r;
        k *= m;
        return k;
    }

    private static int mixH(int h, int k) {
        h *= m;
        h ^= k;
        return h;
    }

}