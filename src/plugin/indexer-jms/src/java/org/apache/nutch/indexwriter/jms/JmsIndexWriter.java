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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobConf;
import org.apache.nutch.indexer.IndexWriter;
import org.apache.nutch.indexer.IndexerMapReduce;
import org.apache.nutch.indexer.NutchDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JmsIndexWriter. This pluggable indexer writes <action>\t<url>\n lines to a
 * plain text file for debugging purposes. Possible actions are delete, update
 * and add.
 */
public class JmsIndexWriter implements IndexWriter {
    public static final Logger LOG = LoggerFactory
            .getLogger(JmsIndexWriter.class);
    private Configuration config;
    private Writer writer;
    private boolean delete = false;

    public void open(JobConf job, String name) throws IOException {
        LOG.warn("GASTON: open job: '{}' with name: '{}'", job, name);
        delete = job.getBoolean(IndexerMapReduce.INDEXER_DELETE, false);
    }

    @Override
    public void delete(String key) throws IOException {
        LOG.warn("GASTON: delete using key: '{}'", key);
        if (delete) {
            writer.write("delete\t" + key + "\n");
        }
    }

    @Override
    public void update(NutchDocument doc) throws IOException {
        LOG.warn("Gaston: update nutch doc: '{}'", doc);
        writer.write("update\t" + doc.getFieldValue("id") + "\n");
    }

    @Override
    public void write(NutchDocument doc) throws IOException {
        LOG.warn("Gaston: write nutch doc: '{}'", doc);
        writer.write("add\t" + doc.getFieldValue("id") + "\n");
    }

    public void close() throws IOException {
        LOG.warn("Gaston: close()");
        writer.flush();
        writer.close();
    }

    @Override
    public void commit() throws IOException {
        LOG.warn("Gaston: commit()");
        writer.write("commit\n");
    }

    @Override
    public Configuration getConf() {
        return config;
    }

    @Override
    public void setConf(Configuration conf) {
        LOG.warn("Gaston: setConf() with conf: '{}'", conf);
        config = conf;
        String path = conf.get("dummy.path");
        if (path == null) {
            String message = "Missing path. Should be set via -Ddummy.path";
            message += "\n" + describe();
            LOG.error(message);
            throw new RuntimeException(message);
        }

        try {
            writer = new BufferedWriter(new FileWriter(conf.get("dummy.path")));
        } catch (IOException e) {
        }
    }

    public String describe() {
        LOG.warn("Gaston: describe()");
        StringBuffer sb = new StringBuffer("JmsIndexWriter\n");
        sb.append("\t").append(
                "dummy.path : Path of the file to write to (mandatory)\n");
        return sb.toString();
    }

    public static void main (String args[]) {
        System.out.println("Initial main method for indexer-jms.");
    }
}
