package org.vafer.jmx;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public final class Query {

    public void run(String url,String user, String pass, String expression, Filter filter, Output output) throws IOException, MalformedObjectNameException, InstanceNotFoundException, ReflectionException, IntrospectionException, AttributeNotFoundException, MBeanException {
        JMXConnector connector = null;
        try {
            Map<String, Object> env = new HashMap<String, Object>();
            if (user != null) {
                env.put(JMXConnector.CREDENTIALS, new String[] { user, pass });
                env.put("username", user);
                env.put("password", pass);
            }else{
                System.err.println("no username or password found");
            }
            connector = JMXConnectorFactory.connect(new JMXServiceURL(url), env);
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            final Collection<ObjectInstance> mbeans = connection.queryMBeans(new ObjectName(expression), null);

            for(ObjectInstance mbean : mbeans) {
                final ObjectName mbeanName = mbean.getObjectName();
                final MBeanInfo mbeanInfo = connection.getMBeanInfo(mbeanName);
                final MBeanAttributeInfo[] attributes = mbeanInfo.getAttributes();
                for (final MBeanAttributeInfo attribute : attributes) {
                    if (attribute.isReadable()) {
                        if (filter.include(mbeanName, attribute.getName())) {
                            final String attributeName = attribute.getName();
                            try {
                                output.output(
                                        mbean.getObjectName(),
                                        attributeName,
                                        connection.getAttribute(mbeanName, attributeName)
                                        );
                            } catch(Exception e) {
                                // System.err.println("Failed to read " + mbeanName + "." + attributeName);
                            }
                        }
                    }
                }
            }
        } finally {
            if (connector != null) {
                connector.close();
            }
        }
    }
}
