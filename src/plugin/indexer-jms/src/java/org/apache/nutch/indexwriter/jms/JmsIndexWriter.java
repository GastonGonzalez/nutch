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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.indexer.IndexWriter;
import org.apache.nutch.indexer.NutchDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.jms.*;

/**
 * JmsIndexWriter is responsible for converting NutchDocuments to a framework agnostic POJO to a JMS topic.
 */
public class JmsIndexWriter implements IndexWriter {
    public static final Logger LOG = LoggerFactory.getLogger(JmsIndexWriter.class);

    private ActiveMQConnectionFactory activeMQConnectionFactory;
    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private String jmsBrokerUrl;
    private String jmsTopic;

    private Configuration config;
    private boolean delete = false;

    @Override
    public void open(Configuration conf) throws IOException {

        activeMQConnectionFactory = new ActiveMQConnectionFactory(jmsBrokerUrl);
        try {
            LOG.info("Creating connection factory for broker: '{}'", jmsBrokerUrl);
            connection = activeMQConnectionFactory.createConnection();

            LOG.info("Starting connection");
            connection.start();

            LOG.info("Creating session");
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            LOG.info("Creating topic: '{}'", jmsTopic);
            Topic topic = session.createTopic(jmsTopic);

            LOG.info("Creating message producer");
            producer = session.createProducer(topic);

        } catch (JMSException e) {
            LOG.error("Unable to create ActiveMQ connection factory for broker: '{}'", jmsBrokerUrl, e);
        }

    }

    @Override
    public void delete(String key) throws IOException {
        if (delete) {
            LOG.debug("Deleting document using key: '{}'", key);
        }
    }

    @Override
    public void update(NutchDocument doc) throws IOException {
        LOG.debug("Updating document: '{}'", doc);
        handleAddOrUpdate(doc);
    }

    @Override
    public void write(NutchDocument doc) throws IOException {
        LOG.debug("Adding document: '{}'", doc);
        handleAddOrUpdate(doc);
    }

    private void handleAddOrUpdate(NutchDocument doc) {

        TextMessage message = null;
        try {
            final String docId = doc.getFieldValue("id");
            // TODO: change message type to body
            LOG.info("Sending message for document with id: '{}'", docId);
            message = session.createTextMessage(docId);
            producer.send(message);
        } catch (JMSException e) {
            LOG.error("Unable to to send JMS message for document: '{}'", doc, e);
        }
    }

    @Override
    public void commit() throws IOException {
        // noop
    }

    @Override
    public void close() throws IOException {
        try {
            LOG.info("Closing connection to broker: '{}'", jmsBrokerUrl);
            connection.stop();
        } catch (JMSException e) {
            LOG.error("Unable to close connector to broker: '{}'", jmsBrokerUrl);
        }
    }

    @Override
    public Configuration getConf() {
        return config;
    }

    @Override
    public void setConf(Configuration conf) {
        config = conf;

        jmsBrokerUrl = conf.get(JmsIndexerConstants.BROKER_URL);
        jmsTopic = conf.get(JmsIndexerConstants.TOPIC);

        if (null == jmsBrokerUrl || null == jmsTopic) {
            final String msg = String.format("Missing JMS configuration. Ensure that '%s' and '%s' are defined.",
                    JmsIndexerConstants.BROKER_URL, JmsIndexerConstants.TOPIC);
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }

    public String describe() {
        StringBuffer sb = new StringBuffer("JmsIndexWriter\n");
        return sb.toString();
    }

    public static void main (String args[]) throws  Exception {

        final String endpoint = "tcp://localhost:61616";
        final String topicName = "nutch-index-topic-test";

        System.out.println(String.format("Sending test JMS message to topic '%s' at '%s'",
            topicName, endpoint));

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(endpoint);
        Connection connection = factory.createConnection();

        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Topic topic = session.createTopic(topicName);

        MessageProducer producer = session.createProducer(topic);

        TextMessage message = session.createTextMessage("!");
        producer.send(message);

        connection.stop();

        System.exit(0);
    }
}
