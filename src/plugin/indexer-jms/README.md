# JMS Indexer Plugin

The indexer-jms plugin is responsible for converting Nutch documents to a generic, serializable object model
(a hash map) and sending the object model to a user-defined ActiveMQ topic.

## Running indexer-jms

1. Edit `$NUTCH_HOME/ivy/ivy.xml` and add the following ActiveMQ dependencies.


        <!-- Start of indexer-jms dependencies -->
        <dependency org="org.apache.activemq" name="activemq-client"
                    rev="5.13.3" conf="*->default" />
        <dependency org="org.apache.geronimo.specs" name="geronimo-jms_1.1_spec"
                    rev="1.1.1" conf="*->default" />
        <dependency org="org.apache.geronimo.specs" name="geronimo-j2ee-management_1.1_spec"
                    rev="1.0.1" conf="*->default" />
        <!-- End of indexer-jms dependencies -->

2. Add `indexer-jms` to `$NUTCH_HOME/conf/nutch-site.xml`. As well as define your ActiveMQ topic and broker URL.


        <property>
            <name>plugin.includes</name>
            <value>protocol-http|urlfilter-regex|parse-(html|tika)|index-(basic|anchor)|urlnormalizer-(pass|regex|basic)|scoring-opic|indexer-jms</value>
        </property>

        <property>
            <name>jms.index.broker.url</name>
            <value>tcp://localhost:61616</value>
        </property>
        <property>
            <name>jms.index.topic</name>
            <value>nutch-index-topic</value>
        </property>


3. Rebuild Nutch.


        $ cd $NUTCH_HOME
        $ ant runtime

4. Test the plugin. Send a message to rquest and add, then request a delete.


        $ nutch plugin indexer-jms org.apache.nutch.indexwriter.jms.JmsIndexWriter \
            tcp://localhost:61616 nutch-index-topic ADD_DOC test-doc-id

        $ nutch plugin indexer-jms org.apache.nutch.indexwriter.jms.JmsIndexWriter \
            tcp://localhost:61616 nutch-index-topic DELETE_DOC test-doc-id