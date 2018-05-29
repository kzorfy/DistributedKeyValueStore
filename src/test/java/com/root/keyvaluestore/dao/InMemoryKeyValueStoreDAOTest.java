package com.root.keyvaluestore.dao;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.root.keyvaluestore.db.KeyValueStore;
import com.root.keyvaluestore.exception.DataNotFoundException;
import com.root.keyvaluestore.exception.DataStoreException;
import com.root.keyvaluestore.model.KeyValuePair;

/**
 * Unit test for {@link InMemoryKeyValueStoreDAO}
 */

public class InMemoryKeyValueStoreDAOTest {
    
    @Mock
    KeyValueStore<String, String> mockKeyValueStore;
    
    @Captor
    private ArgumentCaptor<String> stringCaptor;
    
    @Captor
    private ArgumentCaptor<KeyValuePair> keyValuePairCaptor;
    
    private InMemoryKeyValueStoreDAO target;
    
    @BeforeMethod
    public void initSetup() {
        MockitoAnnotations.initMocks(this);
        this.target = new InMemoryKeyValueStoreDAO(mockKeyValueStore);
    }
    
    @Test(dataProvider = "happyTestCaseForInMemoryKeyValueStoreDAOCreateOperation")
    public void testCreateMethod(final KeyValuePair keyValuePair,
            final Class<? extends Exception> throwException,
            final Class<? extends Exception> exception) throws Exception {
        
        
        if(throwException != null) {
            final Exception error = throwException.getConstructor(String.class).newInstance("some exception occurred");
            doThrow(error).when(mockKeyValueStore).put(stringCaptor.capture(), stringCaptor.capture());
        } else {
            doNothing().when(mockKeyValueStore).put(stringCaptor.capture(), stringCaptor.capture());
        }
        
        try {
            target.create(keyValuePair);
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
        
        assertEquals(stringCaptor.getAllValues().get(0), keyValuePair.getKey());
        assertEquals(stringCaptor.getAllValues().get(1), keyValuePair.getValue());
    }
    
    @Test(dataProvider = "testCaseForInMemoryKeyValueStoreDAOReadOperation")
    public void testReadMethod(final String key, final String value,
            final Class<? extends Exception> throwException, 
            final Class<? extends Exception> exception) throws Exception {
        
        if(throwException != null) {
            final Exception error = throwException.getConstructor(String.class).newInstance("some exception occurred");
            when(mockKeyValueStore.get(stringCaptor.capture())).thenThrow(error);
        } else {
            when(mockKeyValueStore.get(stringCaptor.capture())).thenReturn(value);
        }
        
        try {
            
            target.read(key);
            assertEquals(stringCaptor.getAllValues().get(0), key);
            
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

    }
    
    @DataProvider
    public Object[][] happyTestCaseForInMemoryKeyValueStoreDAOCreateOperation() {
        
        final KeyValuePair keyValuePair = new KeyValuePair("key", "value");
        return new Object[][] {
            {keyValuePair, null, null},
            {keyValuePair, DataStoreException.class, DataStoreException.class}
        };
        
    }
    
    @DataProvider
    public Object[][] testCaseForInMemoryKeyValueStoreDAOReadOperation() {
        
        return new Object[][] {
            {"key", "value", null, null},
            {"key", null, null, DataNotFoundException.class},
            {"key", null, DataNotFoundException.class, DataNotFoundException.class}
        };
        
    }

}
