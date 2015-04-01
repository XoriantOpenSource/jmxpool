/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xoriant.jmx.pool;
import java.io.IOException;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.log4j.Logger;

import com.xoriant.jmx.pool.exception.ConnectionNotFoundException;
import com.xoriant.jmx.pool.util.ApplicationProperty;

/**
 * This is the factory class which will provide instances to the generic pool.
 * @author asharma
 *
 * @param <Q>
 */
public class JmxPoolFactory<Q> extends BasePooledObjectFactory<Q> {
	final static Logger LOGGER = Logger.getLogger(JmxPoolFactory.class);
	/**	host for jmx connection. */
	private String host = "";
	/**	port for jmx connection. */
	private String port = "";
	/**
	 * constructor for jmx objects.
	 * @param host
	 * @param port
	 */
	public JmxPoolFactory(String host,String port){
		this.host = host;
		this.port = port;
	}
	/**
	 * default constructor
	 */
	public JmxPoolFactory(){
	}
	
	/**
	 * The create method will create the actual JMX connection.
	 */
    @SuppressWarnings("unchecked")
	@Override
    public Q create() throws Exception {
    	if(LOGGER.isDebugEnabled()){
    		LOGGER.debug("In create method");
    	}
    	JMXServiceURL url = null;
    	JMXConnector jmxConnector =  null;
		StringBuilder vUrlString = new StringBuilder(
				ApplicationProperty.JMX_URL_FRONT);
		vUrlString.append(host);
		vUrlString.append(ApplicationProperty.COLON);
		vUrlString.append(port);
		vUrlString.append(ApplicationProperty.JMX_URL_BACK);
		try {
			url = new JMXServiceURL(vUrlString.toString());
			jmxConnector = JMXConnectorFactory.connect(url, null);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new ConnectionNotFoundException(host, port);
		}   
		return (Q)jmxConnector;
    }
	/**
	 *  This method will wrap the pool object.
	 */
    @Override
    public PooledObject<Q> wrap(Q jmxConnector) {
        return new DefaultPooledObject<Q>(jmxConnector);
    }
    /**
     * This method will make the pooled object.
     */
    @Override
    public PooledObject<Q> makeObject()
            throws Exception {
    	return wrap(create());
    }
    /**
     * This method will destroy the JMX connection. 
     */
    @Override
    public void destroyObject(PooledObject<Q> jmxPooledObject)
            throws Exception{
    	JMXConnector jMXConnector = ((JMXConnector)(jmxPooledObject.getObject()));
    	if(LOGGER.isDebugEnabled()){
    		LOGGER.debug("In destroyObject : jMXConnector "+ jMXConnector.getConnectionId());
    	}
    	jMXConnector.close();
    }
    /**
     * This method will validate the pooled object before it's served.
     */
    @Override    
    public boolean validateObject(PooledObject<Q> jmxPooledObject)
    {
    	boolean isValidConnection = Boolean.TRUE;
		try {
			(((JMXConnector)(jmxPooledObject.getObject()))).getMBeanServerConnection();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			isValidConnection = Boolean.FALSE;
		} 
        return isValidConnection;
    }
}