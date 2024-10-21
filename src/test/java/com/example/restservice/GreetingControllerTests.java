/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.restservice;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application.properties") 
public class GreetingControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@InjectMocks
    private GreetingController greetingController;

    @Spy
    private Logger mockLogger = LoggerFactory.getLogger(GreetingController.class);;
	
	@Test
    void testGreetingWithTraceEnabled() throws NoSuchFieldException, IllegalAccessException {
		
		System.out.println(mockLogger.toString());
		 // Simulate trace logging being enabled
        Mockito.when(mockLogger.isTraceEnabled()).thenReturn(true);

        greetingController.greeting("John");

        // Verify that the trace message was logged
        verify(mockLogger).trace("This is traceable message");
    }

//    @Test
    void testGreetingWithTraceDisabled() throws NoSuchFieldException, IllegalAccessException {
        GreetingController greetingController = new GreetingController();

        // Access the private log field using reflection
        Field logField = GreetingController.class.getDeclaredField("log");
        logField.setAccessible(true);
        Logger mockLogger = Mockito.mock(Logger.class);
        logField.set(greetingController, mockLogger);

        // Simulate trace logging being disabled
        Mockito.when(mockLogger.isTraceEnabled()).thenReturn(false);

        greetingController.greeting("Jane");

        // Verify that the trace message was not logged
        verify(mockLogger, Mockito.never()).trace("This is traceable message");
    }	    
	@Test
	public void noParamGreetingShouldReturnDefaultMessage() throws Exception {

		this.mockMvc.perform(get("/greeting")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").value("Hello, World!"));
	}

	@Test
	public void paramGreetingShouldReturnTailoredMessage() throws Exception {

		this.mockMvc.perform(get("/greeting").param("name", "Spring Community"))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").value("Hello, Spring Community!"));
	}

}
