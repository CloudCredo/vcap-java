package org.cloudfoundry.runtime.env.scanning;

import com.cloudcredo.cloudfoundry.runtime.env.CassandraServiceInfo;
import junit.framework.Assert;
import org.cloudfoundry.runtime.env.AbstractServiceInfo;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author: chris
 * @date: 22/01/2013
 */
public class ServiceInfoProviderClasspathScannerTest {

    private ServiceInfoProviderClasspathScanner unit;

    @org.junit.Before
    public void before() {
        unit = new ServiceInfoProviderClasspathScanner(new ClassloaderStrategy() {
            @Override
            public ClassLoader getClassLoader() {
                return AbstractServiceInfo.class.getClassLoader();
            }

            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return Class.forName(name);
            }
        });
    }

    @org.junit.AfterClass
    public static void after() throws IOException {
        writeValueIntoMetaServiceInfo("com.cloudcredo.cloudfoundry.runtime.env.CassandraServiceInfo");
    }

    @org.junit.Test
    public void shouldLoadServiceInfoProvider() throws Exception {

        writeValueIntoMetaServiceInfo("com.cloudcredo.cloudfoundry.runtime.env.CassandraServiceInfo");

        Map<String, Class<? extends AbstractServiceInfo>> actual = unit.getServiceInfoProviders();
        Assert.assertEquals(1, actual.size());

        Assert.assertTrue(actual.containsKey("cassandra"));
        Assert.assertEquals(CassandraServiceInfo.class, actual.get("cassandra"));
    }

    @org.junit.Test(expected = RuntimeException.class)
    public void should_Throw_Runtime_Exception_If_ServiceInfo_File_Is_Invalid_Format() throws IOException {
        writeValueIntoMetaServiceInfo("bad file format");
        unit.getServiceInfoProviders();
    }



    private static void writeValueIntoMetaServiceInfo(String valueToWrite) throws IOException {
        boolean doNotAppend = false;
        Resource res = new ClassPathResource(ServiceInfoProviderClasspathScanner.SERVICES_FILE);
        FileWriter writer = new FileWriter(res.getFile(), doNotAppend);
        try {
            writer.write(valueToWrite);
            writer.flush();
        } finally {
            writer.close();
        }
    }
}
