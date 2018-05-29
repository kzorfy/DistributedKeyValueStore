package com.root.keyvaluestore.db;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Map;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.root.keyvaluestore.exception.DataStoreException;

/**
 * Unit test for {@link InMemoryKeyValueStore}
 */
public class InMemoryKeyValueStoreTest {
    
    @Mock
    Map<String, String> mockKeyValueStore;
    
    @Captor
    private ArgumentCaptor<String> stringCaptor;
    
    private InMemoryKeyValueStore target;
    
    @BeforeMethod
    public void initSetup() {
        MockitoAnnotations.initMocks(this);
        this.target = new InMemoryKeyValueStore(mockKeyValueStore);
    }
    
    @Test(dataProvider = "happyTestCaseForInMemoryKeyValueStore")
    public void testPutMethod(final String key, final String value, 
            final Class<? extends Exception> throwException,
            final Class<? extends Exception> exception) throws Exception {
        
        if(throwException != null) {
            final Exception error = throwException.getConstructor(String.class).newInstance("some exception occurred");
            when(mockKeyValueStore.put(stringCaptor.capture(), stringCaptor.capture())).thenThrow(error);
        } else {
            when(mockKeyValueStore.put(stringCaptor.capture(), stringCaptor.capture())).thenReturn(null);
        }
        
        try {
            target.put(key, value);
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
        assertEquals(stringCaptor.getAllValues().get(1), value);
    }
    
    @Test(dataProvider = "happyTestCaseForInMemoryKeyValueStore")
    public void testGetMethod(final String key, final String value,
            final Class<? extends Exception> throwException,
            final Class<? extends Exception> exception) throws Exception {
        
        if(throwException != null) {
            final Exception error = throwException.getConstructor(String.class).newInstance("some exception occurred");
            when(mockKeyValueStore.get(stringCaptor.capture())).thenThrow(error);
        } else {
            when(mockKeyValueStore.get(stringCaptor.capture())).thenReturn(value);
        }
        
        try {
            target.get(key);
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
    public Object[][] happyTestCaseForInMemoryKeyValueStore() {
        
        return new Object[][] {
            {"key", "value", null, null},
            {"key", "value", RuntimeException.class, DataStoreException.class}
        };
        
    }

}
