package com.root.keyvaluestore.resources;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.root.keyvaluestore.dao.KeyValueStoreDAO;
import com.root.keyvaluestore.exception.DataNotFoundException;
import com.root.keyvaluestore.exception.DataStoreException;
import com.root.keyvaluestore.model.KeyValuePair;


/**
 * Unit test for {@link KeyValueStoreResource}
 */
public class KeyValueStoreResourceTest {
    
    @Mock
    KeyValueStoreDAO<String, KeyValuePair> mockKeyValueStoreDAO;
    
    @Mock
    UriInfo mockUriInfo;
    
    @Mock
    UriBuilder mockUriBuilder;
    
    @Captor
    private ArgumentCaptor<KeyValuePair> keyValuePairCaptor;
    
    @Captor
    private ArgumentCaptor<String> stringCaptor;
    
    private KeyValueStoreResource target;
    
    @BeforeMethod
    public void initSetup() {
        MockitoAnnotations.initMocks(this);
        this.target = new KeyValueStoreResource(mockKeyValueStoreDAO);
    }
    
    @Test(dataProvider = "testCaseForKeyValueStoreResourcePutKeyValuePairMethod")
    public void testPutKeyValuePairMethod(final KeyValuePair keyValuePair,
            final Class<? extends Exception> throwException, 
            final Class<? extends Exception> exception) throws Exception {
        
        if(throwException != null) {
            final Exception error = throwException.getConstructor(String.class).newInstance("some exception occurred");
            when(mockKeyValueStoreDAO.create(keyValuePairCaptor.capture())).thenThrow(error);
        } else {
            when(mockKeyValueStoreDAO.create(keyValuePairCaptor.capture())).thenReturn(keyValuePair);
        }
        final URI uri = URI.create("http://localhost:8080/keyvaluestore/webapi/keyvaluedatastore");
        when(mockUriInfo.getAbsolutePathBuilder()).thenReturn(mockUriBuilder);
        when(mockUriBuilder.path(stringCaptor.capture())).thenReturn(mockUriBuilder);
        when(mockUriBuilder.build()).thenReturn(uri);
        
        try {
            target.putKeyValuePair(keyValuePair, mockUriInfo);
        } catch (Exception e) {
            if (exception == null) {
                fail("Unexpected error occurred");
            } else {
                if (!e.getClass().getSimpleName().equals(exception.getSimpleName())) {
                    fail(String.format("Expected %s but found %s", exception.getSimpleName(),
                            e.getClass().getSimpleName()));
                }
            }
        }
        
        assertEquals(keyValuePairCaptor.getAllValues().get(0), keyValuePair);
        
    }
    
    @Test(dataProvider = "testCaseForKeyValueStoreResourceGetKeyValuePairMethod")
    public void testGetKeyValuePairMethod(final String key, final KeyValuePair value,
            final Class<? extends Exception> throwException, 
            final Class<? extends Exception> exception) throws Exception {
        
        if(throwException != null) {
            final Exception error = throwException.getConstructor(String.class).newInstance("some exception occurred");
            when(mockKeyValueStoreDAO.read(stringCaptor.capture())).thenThrow(error);
        } else {
            when(mockKeyValueStoreDAO.read(stringCaptor.capture())).thenReturn(value);
        }
        
        try {
            
            target.getKeyValuePair(key);
        } catch (Exception e) {
            if (exception == null) {
                fail("Unexpected error occurred");
            } else {
                if (!e.getClass().getSimpleName().equals(exception.getSimpleName())) {
                    fail(String.format("Expected %s but found %s", exception.getSimpleName(),
                            e.getClass().getSimpleName()));
                }
            }
        }
        
        assertEquals(stringCaptor.getAllValues().get(0), key);
        
    }
    
    
    @DataProvider
    public Object[][] testCaseForKeyValueStoreResourceGetKeyValuePairMethod() {
        
        final KeyValuePair keyValuePair = new KeyValuePair("key", "value");
        return new Object[][] {
            {"key", keyValuePair, null, null},
            {"key", null, DataNotFoundException.class, DataNotFoundException.class}
        };
        
    }
    
    @DataProvider
    public Object[][] testCaseForKeyValueStoreResourcePutKeyValuePairMethod() {
        
        final KeyValuePair keyValuePair = new KeyValuePair("key", "value");
        return new Object[][] {
            {keyValuePair, null, null},
            {keyValuePair, DataStoreException.class, DataStoreException.class}
        };
        
    }

}
