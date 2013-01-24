package org.cloudfoundry.runtime.env.scanning;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.runtime.env.AbstractServiceInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

import static java.lang.Class.forName;

/**
 * @author: chris
 * @date: 22/01/2013
 */
public class ServiceInfoProviderClasspathScanner {

    private final Map<ClassLoader, Map<String, Class<? extends AbstractServiceInfo>>> providersPerClassloader = new HashMap<ClassLoader, Map<String, Class<? extends AbstractServiceInfo>>>();

    /**
     * The file to search the classpath for, It is expected each file will contain a single key value property with the
     * key being the service name as represented within CloudFoundry and the value being a fully qualified class name of
     * AbstractServiceInfo representing the service. E.g
     * <pre>
     *     cassandra=foo.bar.CassandraServiceInfo
     * </pre>
     * <p>>
     */
    public static final String SERVICES_FILE = "META-INF/cloud/services/ServiceInfo";

    private static final Log log = LogFactory.getLog(ServiceInfoProviderClasspathScanner.class);

    private ClassloaderStrategy classloaderStrategy;

    public ServiceInfoProviderClasspathScanner(ClassloaderStrategy classloaderStrategy) {
        this.classloaderStrategy = classloaderStrategy;
    }

    /**
     * @return a collection of AbstractServiceInfo provider objects found on the classpath.
     */
    public Map<String, Class<? extends AbstractServiceInfo>> getServiceInfoProviders() {

        ClassLoader classloader = classloaderStrategy.getClassLoader();
        Map<String, Class<? extends AbstractServiceInfo>> providers = providersPerClassloader.get(classloader);

        if (providers == null) {

            providers = new HashMap<String, Class<? extends AbstractServiceInfo>>();

            try {
                Enumeration<URL> providerDefinitions = classloader.getResources(SERVICES_FILE);
                while (providerDefinitions.hasMoreElements()) {
                    URL url = providerDefinitions.nextElement();
                    log.info(String.format("Found ServiceInfo provider to read: %s", url));
                    InputStream stream = url.openStream();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 100);
                        String name = reader.readLine().trim();
                        log.info(String.format("Found ServiceInfo provider class: %s in file %s", name, url));
                        while (name != null) {
                            Class<? extends AbstractServiceInfo> clazz = (Class<? extends AbstractServiceInfo>) forName(name);
                            providers.put("cassandra", clazz);
                            name = reader.readLine();
                        }
                    } catch (Exception e) {
                        String msg = String.format("An exception has occurred while initializing CloudFoundry runtime. Bad ServiceInfo provider: %s.", url);
                        throw new RuntimeException(msg);
                    } finally {
                        stream.close();
                        log.info(String.format("%s closed for reading", url));
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("There has been an error looking for ServiceInfo providers", e);
            }

            providersPerClassloader.put(classloader, providers);
        }

        return providers;
    }

    public static class ProductionClassLoaderStrategy implements ClassloaderStrategy {

        @Override
        public ClassLoader getClassLoader() {
            return getClass().getClassLoader();
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            return Class.forName(name);
        }
    }
}
