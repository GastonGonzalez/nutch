# JMS Indexer Plugin

TODO

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

4. Test the plugin.


        $ nutch plugin indexer-jms org.apache.nutch.indexwriter.jms.JmsIndexWriter