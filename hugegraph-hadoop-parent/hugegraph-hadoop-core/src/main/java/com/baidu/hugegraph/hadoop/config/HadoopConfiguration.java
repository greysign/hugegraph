// Copyright 2017 hugegraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.baidu.hugegraph.hadoop.config;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Map.Entry;
import com.google.common.base.Predicate;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.baidu.hugegraph.diskstorage.configuration.WriteConfiguration;
import com.baidu.hugegraph.diskstorage.util.time.Durations;


import javax.annotation.Nullable;

public class HadoopConfiguration implements WriteConfiguration {

    private static final Logger log =
            LoggerFactory.getLogger(HadoopConfiguration.class);

    private final Configuration config;
    private final String prefix;

    public HadoopConfiguration(Configuration config) {
        this(config, null);
    }

    public HadoopConfiguration(Configuration config, String prefix) {
        this.config = config;
        this.prefix = prefix;
    }

    @Override
    public <O> O get(String key, Class<O> datatype) {

        final String internalKey = getInternalKey(key);

        if (null == config.get(internalKey))
            return null;

        if (datatype.isArray()) {
            Preconditions.checkArgument(datatype.getComponentType()==String.class,"Only string arrays are supported: %s",datatype);
            return (O)config.getStrings(internalKey);
        } else if (Number.class.isAssignableFrom(datatype)) {
            String s = config.get(internalKey);
            return constructFromStringArgument(datatype, s);
        } else if (datatype==String.class) {
            return (O)config.get(internalKey);
        } else if (datatype==Boolean.class) {
            return (O)Boolean.valueOf(config.get(internalKey));
        } else if (datatype.isEnum()) {
            O[] constants = datatype.getEnumConstants();
            Preconditions.checkState(null != constants && 0 < constants.length, "Zero-length or undefined enum");
            String estr = config.get(internalKey);
            for (O c : constants)
                if (c.toString().equals(estr))
                    return c;
            throw new IllegalArgumentException("No match for string \"" + estr + "\" in enum " + datatype);
        } else if (datatype==Object.class) {
            // Return String when an Object is requested
            // Object.class must be supported for the sake of AbstractConfiguration's getSubset impl
            return (O)config.get(internalKey);
        } else if (Duration.class.isAssignableFrom(datatype)) {
            // This is a conceptual leak; the config layer should ideally only handle standard library types
            String s = config.get(internalKey);
            String[] comps = s.split("\\s");
            TemporalUnit unit = null;
            if (comps.length == 1) {
                //By default, times are in milli seconds
                unit = ChronoUnit.MILLIS;
            } else if (comps.length == 2) {
                unit = Durations.parse(comps[1]);
            } else {
                throw new IllegalArgumentException("Cannot parse time duration from: " + s);
            }
            return (O) Duration.of(Long.valueOf(comps[0]), unit);
        } else throw new IllegalArgumentException("Unsupported data type: " + datatype);
    }

    @Override
    public Iterable<String> getKeys(final String userPrefix) {
        /*
         * Is there a way to iterate over just the keys of a Hadoop Configuration?
         * Iterating over Map.Entry is needlessly wasteful since we don't need the values.
         */
        Iterable<String> internalKeys = Iterables.transform(config, new Function<Entry<String, String>, String>() {
            @Override
            public String apply(Entry<String, String> internalEntry) {
                return internalEntry.getKey();
            }
        });

        Iterable<String> prefixedKeys = Iterables.filter(internalKeys, new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String internalKey) {
                String k = internalKey;
                if (null != prefix) {
                    if (k.startsWith(prefix)) {
                        k = getUserKey(k);
                    } else {
                        return false; // does not have the prefix
                    }
                }
                return k.startsWith(userPrefix);
            }
        });

        return Iterables.transform(prefixedKeys, new Function<String, String>() {

            @Nullable
            @Override
            public String apply(@Nullable String internalKey) {
                String userKey = getUserKey(internalKey);
                Preconditions.checkState(userKey.startsWith(userPrefix));
                return userKey;
            }
        });
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public <O> void set(String key, O value) {

        final String internalKey = getInternalKey(key);

        Class<?> datatype = value.getClass();

        if (datatype.isArray()) {
            Preconditions.checkArgument(datatype.getComponentType()==String.class,"Only string arrays are supported: %s",datatype);
            config.setStrings(internalKey, (String[])value);
        } else if (Number.class.isAssignableFrom(datatype)) {
            config.set(internalKey, value.toString());
        } else if (datatype==String.class) {
            config.set(internalKey, value.toString());
        } else if (datatype==Boolean.class) {
            config.setBoolean(internalKey, (Boolean)value);
        } else if (datatype.isEnum()) {
            config.set(internalKey, value.toString());
        } else if (datatype==Object.class) {
            config.set(internalKey, value.toString());
        } else if (Duration.class.isAssignableFrom(datatype)) {
            // This is a conceptual leak; the config layer should ideally only handle standard library types
            String millis = String.valueOf(((Duration)value).toMillis());
            config.set(internalKey, millis);
        } else throw new IllegalArgumentException("Unsupported data type: " + datatype);
    }

    @Override
    public void remove(String key) {
        config.unset(getInternalKey(key));
    }

    @Override
    public WriteConfiguration copy() {
        return new HadoopConfiguration(new Configuration(config), prefix);
    }

    private <O> O constructFromStringArgument(Class<O> datatype, String arg) {
        try {
            Constructor<O> ctor = datatype.getConstructor(String.class);
            return ctor.newInstance(arg);
        // ReflectiveOperationException is narrower and more appropriate than Exception, but only @since 1.7
        //} catch (ReflectiveOperationException e) {
        } catch (Exception e) {
            log.error("Failed to parse configuration string \"{}\" into type {} due to the following reflection exception", arg, datatype, e);
            throw new RuntimeException(e);
        }
    }

    private String getInternalKey(String userKey) {
        return null == prefix ? userKey : prefix + userKey;
    }

    private String getUserKey(String internalKey) {
        String k = internalKey;

        if (null != prefix) {
            Preconditions.checkState(k.startsWith(prefix), "key %s does not start with prefix %s", internalKey, prefix);
            Preconditions.checkState(internalKey.length() > prefix.length());
            k = internalKey.substring(prefix.length());
        }

        return k;
    }
}