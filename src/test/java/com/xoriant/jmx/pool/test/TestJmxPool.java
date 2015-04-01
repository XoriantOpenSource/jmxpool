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
package com.xoriant.jmx.pool.test;
import static org.junit.Assert.fail;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.xoriant.jmx.pool.JmxHost;
import com.xoriant.jmx.pool.JmxPoolManager;
/**
 * This is the test class for the JMX connection pool.
 * @author asharma
 *
 * @param <Q>
 */
public class TestJmxPool {
    private AtomicInteger count = new AtomicInteger(0);
    private final static Logger LOGGER = Logger.getLogger(TestJmxPool.class);

    @Test
    public void test() {
        try {
            int limit = 50;
            final JmxPoolManager jmxPoolHelper = new JmxPoolManager();
            ExecutorService es = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(limit));
            
            for (int i=0; i<limit; i++) {
            	Runnable r = new Runnable() {
                    @Override
                    public void run() {
                    	JmxHost host = new JmxHost("10.20.3.23","7199");
                    	JMXConnector jmxConnector = null;
                        try {
                        	jmxConnector = jmxPoolHelper.getConnectionFromPool(host);
                            MBeanServerConnection  mBeanServerConnection  = jmxConnector.getMBeanServerConnection();
                            mBeanServerConnection.getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "TotalPhysicalMemorySize");
                            if(LOGGER.isDebugEnabled()){
                        		LOGGER.debug("Value : " + mBeanServerConnection.getAttribute(new ObjectName("java.lang:type=OperatingSystem"), "TotalPhysicalMemorySize"));
                        	}
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                            LOGGER.error(e.getMessage());
                        } finally {
                            if (jmxConnector != null) {
                            	 if(LOGGER.isDebugEnabled()){
                            		 LOGGER.debug("connection returned" );
                            	 }
                                jmxPoolHelper.returnConnectionToPool(host, jmxConnector);
                            }
                        }
                        
                    }
                };
                es.submit(r); 
                count.incrementAndGet();
            }
            es.shutdown();
            
            try {
                es.awaitTermination(5, TimeUnit.MINUTES);
                /**
                 * The pool invalidate functionality.
                 */
                jmxPoolHelper.cleanup();
            } catch (InterruptedException e) {
            	LOGGER.error(e.getMessage());
            }
            Assert.assertEquals(limit, count.get());
            
        } catch (Exception ex) {
            fail("Exception:" + ex);
        }
    }

}