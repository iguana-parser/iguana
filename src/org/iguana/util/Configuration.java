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

package org.iguana.util;

import org.iguana.utils.logging.LogLevel;
import org.iguana.util.config.XMLConfigFileParser;

public class Configuration {

    public static final int DEFAULT_LOOKAHEAD = 1;

    private final LookupImpl gssLookupImpl;

    private final MatcherType matcherType;

    private final int lookAheadCount;

    private final HashMapImpl hashmapImpl;

    private final EnvironmentImpl envImpl;

    private final LogLevel logLevel;

    public static Configuration load() {
        Configuration configuration;
        try {
            XMLConfigFileParser parser = XMLConfigFileParser.create("config.xml");
            configuration = parser.getConfiguration();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return configuration;
    }

    private Configuration(Builder builder) {
        this.gssLookupImpl = builder.gssLookupImpl;
        this.lookAheadCount = builder.lookaheadCount;
        this.matcherType = builder.matcherType;
        this.hashmapImpl = builder.hashmapImpl;
        this.envImpl = builder.envImpl;
        this.logLevel = builder.logLevel;
    }

    public LookupImpl getGSSLookupImpl() {
        return gssLookupImpl;
    }

    public int getLookAheadCount() {
        return lookAheadCount;
    }

    public MatcherType getMatcherType() {
        return matcherType;
    }

    public HashMapImpl getHashmapImpl() {
        return hashmapImpl;
    }

    public EnvironmentImpl getEnvImpl() {
        return envImpl;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public enum MatcherType {
        DFA,
        JAVA_REGEX
    }

    public enum LookupImpl {
        ARRAY,
        HASH_MAP
    }

    public enum HashMapImpl {
        JAVA,
        INT_OPEN_ADDRESSING
    }

    public enum EnvironmentImpl {
        ARRAY,
        INT_ARRAY,
        HASH_MAP,
        TRIE
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(MatcherType.class.getName()).append(": ").append(matcherType)
          .append(LookupImpl.class.getName()).append(": ").append(gssLookupImpl)
          .append(HashMapImpl.class.getName()).append(": ").append(hashmapImpl)
          .append("LookaheadCount").append(": ").append(lookAheadCount);

        return sb.toString();
    }

    public static class Builder {

        private LookupImpl gssLookupImpl = LookupImpl.HASH_MAP;
        private MatcherType matcherType = MatcherType.JAVA_REGEX;
        private HashMapImpl hashmapImpl = HashMapImpl.JAVA; // HashMapImpl.INT_OPEN_ADDRESSING;
        private int lookaheadCount = DEFAULT_LOOKAHEAD;
        private LogLevel logLevel = LogLevel.NONE;

        private EnvironmentImpl envImpl = EnvironmentImpl.TRIE;

        public Configuration build() {
            return new Configuration(this);
        }

        public Builder setGSSLookupImpl(LookupImpl gssLookupImpl) {
            this.gssLookupImpl = gssLookupImpl;
            return this;
        }

        public Builder setMatcherType(MatcherType matcherType) {
            this.matcherType = matcherType;
            return this;
        }

        public Builder setHashmapImpl(HashMapImpl hashmapImpl) {
            this.hashmapImpl = hashmapImpl;
            return this;
        }

        public Builder setLookaheadCount(int lookaheadCount) {
            this.lookaheadCount = lookaheadCount;
            return this;
        }

        public Builder setEnvironmentImpl(EnvironmentImpl impl) {
            this.envImpl = impl;
            return this;
        }

        public Builder setLogLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

    }
}
