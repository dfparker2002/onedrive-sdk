/**
 * Copyright 2015 Rob Sessink
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.yucca.microsoft.onedrive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.WebTarget;

import io.yucca.microsoft.onedrive.filter.Filter;
import io.yucca.microsoft.onedrive.resources.Order;
import io.yucca.microsoft.onedrive.resources.Relationship;
import io.yucca.microsoft.onedrive.util.URLHelper;

/**
 * QueryParameters is be used to influence the way item results are returned.
 * The parameters are configured invoking {@code Builder#newQueryParameters()},
 * building a QueryParameters instance using {@code Builder#build()} and the
 * applied to a WebTarget using {@code QueryParameters#configure(WebTarget)}
 * <p>
 * TODO: move configure() to AbstractAction cuts dependency on WebTarget and
 * move this class to io.yucca.microsoft.onedrive.resources package
 * <p>
 * @author yucca.io
 */
public class QueryParameters {

    public static final String EXPAND = "expand";
    public static final String SELECT = "select";
    public static final String ORDERBY = "orderby";
    public static final String TOP = "top";
    public static final String TOKEN = "token";
    public static final String FILTER = "filter";
    public static final String CONFLICT_BEHAVIOR = "@name.conflictBehavior";

    private static final String[] MANDATORY_FIELDS = new String[] { "id",
                                                                    "file",
                                                                    "folder",
                                                                    "eTag",
                                                                    "parentReference" };

    private final Map<String, String> parameters;

    private QueryParameters() {
        this.parameters = new HashMap<>();
    }

    /**
     * Create a new WebTarget instance by configuring the query parameters on
     * the URI of the current target instance.
     * 
     * @param target WebTarget instance
     * @param unAllowedQueryParameters String[] query parameters that are not
     *            allowed for the API call and must be removed
     * @return WebTarget a new target instance.
     * @throws OneDriveException if encoding of a parameter fails
     */
    public WebTarget configure(WebTarget target,
                               String... unAllowedQueryParameters) {
        for (String unallowed : unAllowedQueryParameters) {
            parameters.remove(unallowed);
        }
        WebTarget expanded = target;
        for (Entry<String, String> parameter : parameters.entrySet()) {
            expanded = expanded
                .queryParam(parameter.getKey(),
                            URLHelper.encodeURIComponent(parameter.getValue()));
        }
        return expanded;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Entry<String, String> parameter : parameters.entrySet()) {
            if ("".equals(b.toString())) {
                b.append("?");
            } else {
                b.append("&");
            }
            b.append(parameter.getKey() + "=" + parameter.getValue());
        }
        return b.toString();
    }

    /**
     * Builder for QueryParameters
     * 
     * @author yucca.io
     */
    public static class Builder {

        private final QueryParameters qp;

        private Builder() {
            this.qp = new QueryParameters();
        }

        private Builder(QueryParameters qp) {
            this.qp = qp;
        }

        public static Builder expandQueryParameters(QueryParameters qp) {
            return new Builder(qp);
        }

        public static Builder newQueryParameters() {
            return new Builder();
        }

        /**
         * Defines the relationships to expand and include in the response
         * 
         * @param relationship Relationship relationship
         * @return Builder
         */
        public Builder expand(Relationship relationship) {
            this.qp.parameters.put(EXPAND, relationship.getRelationship());
            return this;
        }

        /**
         * Defines the relationships to expand and include in the response
         * 
         * @param relationships List<Relationship> relationships
         * @return Builder
         */
        public Builder expand(List<Relationship> relationships) {
            this.qp.parameters.put(EXPAND,
                                   Relationship.commaSeperated(relationships));
            return this;
        }

        /**
         * Defines the list of properties to include in the response. Field "id"
         * is added if not provided
         * 
         * @param select String property
         * @return Builder
         */
        public Builder select(String select) {
            String s = "id".equals(select) ? select : "id," + select;
            this.qp.parameters.put(SELECT, s);
            return this;
        }

        /**
         * Defines the list of properties to include in the response. Field "id"
         * is added if not provided
         * 
         * @param select String[] properties
         * @return Builder
         */
        public Builder select(String... select) {
            this.qp.parameters
                .put(SELECT,
                     commaSeperated(addMandatory(select, MANDATORY_FIELDS)));
            return this;
        }

        /**
         * Paging token that is used to get the next set of results.
         * 
         * @param token String
         * @return Builder
         */
        public Builder skipToken(String token) {
            this.qp.parameters.put(TOKEN, token);
            return this;
        }

        /**
         * The number of items to return in a result set.
         * 
         * @param top Integer
         * @return Builder
         */
        public Builder top(Integer top) {
            this.qp.parameters.put(TOP, String.valueOf(top));
            return this;
        }

        /**
         * Sort the returned item result by field
         * 
         * @param orderby String field to sort on
         * @return Builder
         */
        public Builder orderby(String orderby) {
            this.qp.parameters.put(ORDERBY, orderby);
            return this;
        }

        /**
         * Sort the returned item result by fields
         * 
         * @param orderby String[] fields to sort on
         * @return Builder
         */
        public Builder orderby(String[] orderby) {
            this.qp.parameters.put(ORDERBY, commaSeperated(orderby));
            return this;
        }

        /**
         * Sort the returned item result by field
         * 
         * @param orderby String field to sort on
         * @param order Order in which to sort
         * @return Builder
         */
        public Builder orderby(String orderby, Order order) {
            this.qp.parameters.put(ORDERBY, orderby + " " + order.getOrder());
            return this;
        }

        /**
         * Sorts the returned item result by field
         * 
         * @param orderby String[] fields to sort on
         * @param order Order in which to sort
         * @return Builder
         */
        public Builder orderby(String[] orderby, Order order) {
            this.qp.parameters
                .put(ORDERBY, commaSeperated(orderby) + " " + order.getOrder());
            return this;
        }

        public Builder filter(Filter filter) {
            this.qp.parameters.put(FILTER, filter.toString());
            return this;
        }

        /**
         * Build the QueryParameters instance
         * 
         * @return QueryParameters
         */
        public QueryParameters build() {
            return qp;
        }

        /**
         * Configure a WebTarget with the QueryParameters
         * 
         * @return WebTarget
         */
        public WebTarget configure(WebTarget target) {
            return qp.configure(target);
        }

        /**
         * Create comma seperated String
         * 
         * @return String
         */
        private String commaSeperated(String[] values) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                b.append(values[i]);
                if (i != (values.length - 1)) {
                    b.append(",");
                }
            }
            return b.toString();
        }

        /**
         * Append mandatory values if not provided
         * 
         * @param values String[]
         * @param mandatory String[]
         * @return String[]
         */
        private String[] addMandatory(String[] values, String[] mandatory) {
            List<String> t = new ArrayList<String>(Arrays.asList(values));
            for (int i = 0; i < mandatory.length; i++) {
                if (!t.contains(mandatory[i])) {
                    t.add(mandatory[i]);
                }
            }
            return (String[])t.toArray(new String[t.size()]);
        }
    }
}
