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

public class MurmurHash3 {

	private final static int C1 = 0xcc9e2d51;
	private final static int C2 = 0x1b873593;
	private final static int M = 5;
	private final static int N = 0xe6546b64;

    public static IntFunction2 f2() {
        return f2(19);
    }

    public static IntFunction2 f2(int seed) {
        return (a, b) -> {
            int h = seed;

            int k = a;
            k = mixK(k);
            h = mixH(h, k);

            k = b;
            k = mixK(k);
            h = mixH(h, k);

            // finalizing
            h ^= 2;

            h ^= h >>> 16;
            h *= 0x85ebca6b;
            h ^= h >>> 13;
            h *= 0xc2b2ae35;
            h ^= h >>> 16;

            return h;
        };
	}

    public static IntFunction3 f3() {
        return f3(19);
    }

	public static IntFunction3 f3(int seed) {
        return (a, b, c) -> {
            int h = seed;

            int k = a;
            k = mixK(k);
            h = mixH(h, k);

            k = b;
            k = mixK(k);
            h = mixH(h, k);

            k = c;
            k = mixK(k);
            h = mixH(h, k);

            // finalizing
            h ^= 3;

            h ^= h >>> 16;
            h *= 0x85ebca6b;
            h ^= h >>> 13;
            h *= 0xc2b2ae35;
            h ^= h >>> 16;

            return h;
        };
	}

    public static IntFunction4 f4() {
        return f4(19);
    }

    public static IntFunction4 f4(int seed) {
        return (a, b, c, d) -> {
            int h = seed;

            int k = a;
            k = mixK(k);
            h = mixH(h, k);

            k = b;
            k = mixK(k);
            h = mixH(h, k);

            k = c;
            k = mixK(k);
            h = mixH(h, k);

            k = d;
            k = mixK(k);
            h = mixH(h, k);

            // finalizing
            h ^= 4;

            h ^= h >>> 16;
            h *= 0x85ebca6b;
            h ^= h >>> 13;
            h *= 0xc2b2ae35;
            h ^= h >>> 16;

            return h;
        };
    }

    public static IntFunction5 f5() {
        return f5(19);
    }

	public static IntFunction5 f5(int seed) {
        return (a, b, c, d, e) -> {
            int h = seed;

            int k = a;
            k = mixK(k);
            h = mixH(h, k);

            k = b;
            k = mixK(k);
            h = mixH(h, k);

            k = c;
            k = mixK(k);
            h = mixH(h, k);

            k = d;
            k = mixK(k);
            h = mixH(h, k);

            k = e;
            k = mixK(k);
            h = mixH(h, k);

            // finalizing
            h ^= 5;

            h ^= h >>> 16;
            h *= 0x85ebca6b;
            h ^= h >>> 13;
            h *= 0xc2b2ae35;
            h ^= h >>> 16;

            return h;
        };
	}

    public static IntFunctionAny fn() {
        return fn(19);
    }

    public static IntFunctionAny fn(int seed) {
        return (Integer...elements) -> {
            int h = seed;

            int k = 0;
            for (int element : elements) {
                k = element;
                k = mixK(k);
                h = mixH(h, k);
            }

            h ^= elements.length;

            h ^= h >>> 16;
            h *= 0x85ebca6b;
            h ^= h >>> 13;
            h *= 0xc2b2ae35;
            h ^= h >>> 16;

            return h;
        };
    }

    private final static int mixK(int k) {
		k *= C1;
		k = Integer.rotateLeft(k, 15);
		k = k * C2;
		return k;
	}

	private final static int mixH(int h, int k) {
		h ^= k;
		h = Integer.rotateLeft(h, 13);
		h = h * M + N;
		return h;
	}
}
