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

package com.baidu.hugegraph.graphdb.query.graph;

import com.google.common.base.Preconditions;
import com.baidu.hugegraph.diskstorage.BackendTransaction;
import com.baidu.hugegraph.diskstorage.EntryList;
import com.baidu.hugegraph.diskstorage.keycolumnvalue.KeySliceQuery;
import com.baidu.hugegraph.graphdb.query.BackendQuery;
import com.baidu.hugegraph.graphdb.query.BaseQuery;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class MultiKeySliceQuery extends BaseQuery implements BackendQuery<MultiKeySliceQuery>  {

    private final List<KeySliceQuery> queries;

    public MultiKeySliceQuery(List<KeySliceQuery> queries) {
        Preconditions.checkArgument(queries!=null && !queries.isEmpty());
        this.queries = queries;
    }

    @Override
    public MultiKeySliceQuery updateLimit(int newLimit) {
        MultiKeySliceQuery newQuery = new MultiKeySliceQuery(queries);
        newQuery.setLimit(newLimit);
        return newQuery;
    }

    public List<EntryList> execute(final BackendTransaction tx) {
        int total = 0;
        List<EntryList> result = new ArrayList<EntryList>(4);
        for (KeySliceQuery ksq : queries) {
            EntryList next =tx.indexQuery(ksq.updateLimit(getLimit()-total));
            result.add(next);
            total+=next.size();
            if (total>=getLimit()) break;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(queries).append(getLimit()).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        else if (other == null) return false;
        else if (!getClass().isInstance(other)) return false;
        MultiKeySliceQuery oth = (MultiKeySliceQuery) other;
        return getLimit()==oth.getLimit() && queries.equals(oth.queries);
    }

    @Override
    public String toString() {
        return "multiKSQ["+queries.size()+"]@"+getLimit();
    }

}