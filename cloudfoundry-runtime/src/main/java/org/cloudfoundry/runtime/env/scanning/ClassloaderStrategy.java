package org.cloudfoundry.runtime.env.scanning;

/**
 * @author: chris
 * @date: 22/01/2013
 */
public interface ClassloaderStrategy {

    /**
     * @return the class loader for the implementation
     */
    ClassLoader getClassLoader();

    /**
     *
     * @param name   Fully qualified name of the class to load
     * @return the class instance of name.
     */
    Class<?> loadClass(String name) throws ClassNotFoundException;

}
