/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.baidu.hugegraph.structure;

import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import com.baidu.hugegraph.HugeException;
import com.baidu.hugegraph.type.schema.PropertyKey;

public class HugeVertexProperty<V> extends HugeProperty<V>
        implements VertexProperty<V> {

    public HugeVertexProperty(HugeElement owner, PropertyKey key, V value) {
        super(owner, key, value);
    }

    @Override
    public Object id() {
        return this.key();
    }

    @Override
    public <U> Property<U> property(String key, U value) {
        throw new HugeException("Not support nested property");
    }

    @Override
    public Vertex element() {
        return (Vertex) super.element();
    }

    @Override
    public <U> Iterator<Property<U>> properties(String... propertyKeys) {
        throw new HugeException("Not support nested property");
    }
}
