/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.indexwriter.jms;

/**
 * @author Gaston Gonzalez
 */
public interface JmsIndexerConstants {

    /** JMS broker URL property name */
    public static final String BROKER_URL = "jms.index.broker.url";
    /** JMS index topic property name */
    public static final String TOPIC = "jms.index.topic";
    /** JMS Nutch metadat name prefix */
    public static final String JMS_NUTCH_METADATA_PREFIX = "nutch.metadata.";
    /** JMS Nutch index field key prefix */
    public static final String JMS_NUTCH_FIELD_PREFIX = "nutch.field.";
    /** Nutch operation (e.g., add, update, delete) prefix */
    public static final String JMS_NUTCH_OP_TYPE = "nutch.op";
    /** Indicates a request to add a document to the index */
    public static final String JMS_NUTCH_OP_ADD = "ADD_DOC";
    /** Indicates a request to update a document in the index */
    public static final String JMS_NUTCH_OP_UPDATE = "UPDATE_DOC";
    /** Indicates a request to delete a document from the index */
    public static final String JMS_NUTCH_OP_DELETE = "DELETE_DOC";
}
