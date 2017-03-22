// Copyright 2017 HugeGraph Authors
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

package com.baidu.hugegraph.graphdb.types.system;

import com.google.common.collect.ImmutableSet;
import com.baidu.hugegraph.graphdb.internal.Order;
import com.baidu.hugegraph.graphdb.internal.InternalRelationType;
import com.baidu.hugegraph.graphdb.types.IndexType;
import com.baidu.hugegraph.core.schema.SchemaStatus;

import java.util.Collections;

/**
 */
public abstract class EmptyRelationType extends EmptyVertex implements InternalRelationType {

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public long[] getSignature() {
        return new long[0];
    }

    @Override
    public long[] getSortKey() {
        return new long[0];
    }

    @Override
    public Order getSortOrder() {
        return Order.ASC;
    }

    @Override
    public InternalRelationType getBaseType() {
        return null;
    }

    @Override
    public Iterable<InternalRelationType> getRelationIndexes() {
        return ImmutableSet.of((InternalRelationType)this);
    }

    @Override
    public SchemaStatus getStatus() {
        return SchemaStatus.ENABLED;
    }

    @Override
    public Iterable<IndexType> getKeyIndexes() {
        return Collections.EMPTY_LIST;
    }

    public Integer getTTL() {
        return 0;
    }

    @Override
    public String toString() {
        return name();
    }
}