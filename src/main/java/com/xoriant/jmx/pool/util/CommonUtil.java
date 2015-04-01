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
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
/**
 * This is common utility class for all modules
 * @author asharma
 *
 */
public class CommonUtil {
		
	final static Logger LOGGER = Logger.getLogger(CommonUtil.class);
		/**
		 * This method return property from JmxPool.properties file.
		 * @param key
		 * @return String
		 */
		public static Properties getJmxPoolProperty() {

			Properties prop = new Properties();

			InputStream inputStr = CommonUtil.class.getClassLoader()
					.getResourceAsStream("jmxpool.properties");

			if(inputStr != null){
				try {
					prop.load(inputStr);
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
					try {
						inputStr.close();
					} catch (IOException e1) {
						LOGGER.error(e.getMessage());
					}
				}
			}
			return prop;
		}
		/**
		 * This method return the exception message for a given key from exceptions.properties file.
		 * @param key
		 * @return String
		 */
		public static String getExceptionProperty(String key) {

			String  value = null;
			Properties prop = new Properties();

			InputStream inputStr = CommonUtil.class.getClassLoader()
					.getResourceAsStream("exceptions.properties");

			if(inputStr != null){

				try {

					prop.load(inputStr);
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}

			value = prop.getProperty(key);

			return value;
		}
}
