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
package com.xoriant.jmx.pool.util;
/**
 * This interface is used to capture Application constants.
 * @author asharma
 *
 */
public interface ApplicationProperty {

	String JMX_POOL_MAX_IDLE = "maxidle";
	String JMX_POOL_MIN_IDLE = "minidle";
	String JMX_POOL_MAX_TOTAL = "maxtotal";
	String JMX_URL_FRONT = "service:jmx:rmi:///jndi/rmi://";
	String COLON = ":";
	String JMX_URL_BACK = "/jmxrmi";
	String CONN_NOT_ESTABLISHED = "connection_not_established";
}
