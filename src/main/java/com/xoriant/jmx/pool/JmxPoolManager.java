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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.remote.JMXConnector;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;

import com.xoriant.jmx.pool.exception.ConnectionNotFoundException;
import com.xoriant.jmx.pool.util.ApplicationProperty;
import com.xoriant.jmx.pool.util.CommonUtil;
/**
 * This class provides helper methods for JMX connection pool.
 * @author asharma
 *
 */
public class JmxPoolManager {
	private static GenericObjectPoolConfig config ;
	private Map<String, JmxPool<JMXConnector>> map = new HashMap<String, JmxPool<JMXConnector>>();
	private static Properties prop  = CommonUtil.getJmxPoolProperty();
	private final static Logger LOGGER = Logger.getLogger(JmxPoolManager.class);
	static {
		config = new GenericObjectPoolConfig();
		/**	Needs to shift these in the configuration file.*/
		config.setMaxIdle(Integer.parseInt(prop.getProperty(ApplicationProperty.JMX_POOL_MAX_IDLE)));
        config.setMinIdle(Integer.parseInt(prop.getProperty(ApplicationProperty.JMX_POOL_MIN_IDLE)));
        config.setMaxTotal(Integer.parseInt(prop.getProperty(ApplicationProperty.JMX_POOL_MAX_TOTAL)));
        config.setJmxEnabled(Boolean.TRUE);
        config.setTestOnBorrow(Boolean.TRUE);
        config.setTestOnReturn(Boolean.TRUE);
        config.setTestOnCreate(Boolean.TRUE);
	}
	/**	Default constructor */	
	public JmxPoolManager(){
	}
	
	/**
	 * Provides connection from the pool.
	 * @param host
	 * @param port
	 * @throws ConnectionNotFoundException 
	 */
	public JMXConnector getConnectionFromPool(JmxHost host) throws ConnectionNotFoundException{
		JmxPool<JMXConnector> pool = null;
		synchronized (map) {
			pool = map.get(host.getHost());
			if(pool == null){
				pool = new JmxPool<JMXConnector>(new JmxPoolFactory<JMXConnector>(host.getHost(),host.getPort()), config);
				map.put(host.getHost(), pool);
			}
		}	
		JMXConnector jmxConnector = null;
		try{
			jmxConnector = (JMXConnector) pool.borrowObject();
			LOGGER.info("Pool Stats:\n Created:[" + pool.getCreatedCount() + "], Borrowed:[" + pool.getBorrowedCount() + "]");
			if(LOGGER.isDebugEnabled()){
        		LOGGER.debug("Pool Stats:\n Created:[" + pool.getCreatedCount() + "], Borrowed:[" + pool.getBorrowedCount() + "]");
        	}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ConnectionNotFoundException(host.getHost(), host.getPort());
	    }
		return jmxConnector;
	} 
	/**
	 * Returns the connection to the pool.
	 * @param host
	 * @param connector
	 * @throws Exception
	 */
	public void returnConnectionToPool(JmxHost host, JMXConnector connector) {
		JmxPool<JMXConnector> pool = null;
		synchronized (map) {
			pool = map.get(host.getHost());
		}
		try{
			if(pool == null){
				 if(LOGGER.isDebugEnabled()){
             		LOGGER.debug("Invalid pool state");
             	}
			}
			pool.returnObject(connector);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
	/**
	 * This method will invalidate the pool.
	 * @param host
	 */
	public void cleanup(){
		if(LOGGER.isDebugEnabled()){
     		LOGGER.debug("inside invalidatePool()");
     	}
		Set<String> hostSet= map.keySet();
		for(String host: hostSet){
			JmxPool<JMXConnector> pool = map.get(host);
			LOGGER.info("In cleanup removed pool : " + pool);
			pool.close();
		}
		if(LOGGER.isDebugEnabled()){
     		LOGGER.debug("out invalidatePool()");
     	}
	}
}
