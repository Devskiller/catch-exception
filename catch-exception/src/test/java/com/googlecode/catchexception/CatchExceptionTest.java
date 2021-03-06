/**
 * Copyright (C) 2011 rwoo@gmx.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.catchexception;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.CatchException.verifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link CatchException}.
 *
 * @author rwoo
 * @since 16.09.2011
 *
 */
@SuppressWarnings("javadoc")
public class CatchExceptionTest {

    private final List<String> list = new ArrayList<>();

    /**
     * The message of the exception thrown by new ArrayList<String>().get(0).
     */
    private final String expectedMessage = "Index: 0, Size: 0";

    /**
     * The message of the exception thrown by new ArrayList<String>().get(0) for jdk9on.
     */
    private final String expectedMessageJdk9on = "Index 0 out of bounds for length 0";

    @Before
    public void setUp() {
        // set any exception so that we have clear state before the test
        ExceptionHolder.set(new HttpRetryException("detail", 0));
    }

    @Test
    public void testCatchException_ObjExc_noExceptionThrown()  {

        catchException(list::size, IndexOutOfBoundsException.class);
        assertNull(caughtException());
    }

    @Test
    public void testCatchException_ObjExc_actualClassThrown()  {

        // test for actual class
        catchException(() -> list.get(0), IndexOutOfBoundsException.class);
        if (!expectedMessage.equals(caughtException().getMessage())) {
            assertEquals(expectedMessageJdk9on, caughtException().getMessage());
        }
    }

    @Test
    public void testCatchException_ObjExc_subClassOfExpectedThrown()  {

        // test for super class
        catchException(() -> list.get(0), RuntimeException.class);
        if (!expectedMessage.equals(caughtException().getMessage())) {
            assertEquals(expectedMessageJdk9on, caughtException().getMessage());
        }
    }

    @Test
    public void testCatchException_ObjExc_superClassOfExpectedThrown()  {

        try {
            catchException(() -> list.get(0), ArrayIndexOutOfBoundsException.class);
            fail("IndexOutOfBoundsException is expected (shall not be caught)");
        } catch (IndexOutOfBoundsException e) {
            assertNull(caughtException());
        }
    }

    @Test
    public void testCatchException_ObjExc_otherClassThanExpectedThrown()  {

        try {
            catchException(() -> list.get(0), IllegalArgumentException.class);
            fail("IndexOutOfBoundsException is expected (shall not be caught)");
        } catch (IndexOutOfBoundsException e) {
            assertNull(caughtException());
        }
    }

    @Test
    public void testCatchException_ObjExc_missingArgument_Exception()  {

        // test validation of the arguments
        try {
            catchException(() -> list.get(0), null);
            fail("IllegalArgumentException is expected");
        } catch (IllegalArgumentException e) {
            assertEquals("exceptionClazz must not be null", e.getMessage());
        }
    }

    @Test
    public void testCatchException_ObjExc_missingArgument_Object()  {

        try {
            catchException(null, IllegalArgumentException.class);
            fail("IllegalArgumentException is expected");
        } catch (IllegalArgumentException e) {
            assertEquals("obj must not be null", e.getMessage());
        }
    }

    @Test
    public void testVerifyException_ObjExc_noExceptionThrown() {

        try {
            verifyException(list::size, IndexOutOfBoundsException.class);
            fail("ExceptionNotThrownAssertionError is expected");
        } catch (ExceptionNotThrownAssertionError e) {
            assertNull(caughtException());
            assertEquals("Neither an exception of type "
                    + IndexOutOfBoundsException.class.getName()
                    + " nor another exception was thrown", e.getMessage());
        }

    }

    @Test
    public void testVerifyException_ObjExc_actualClassThrown() {

        // test for actual class
        verifyException(() -> list.get(0), IndexOutOfBoundsException.class);
        if (!expectedMessage.equals(caughtException().getMessage())) {
            assertEquals(expectedMessageJdk9on, caughtException().getMessage());
        }
    }

    @Test
    public void testVerifyException_ObjExc_subClassOfExpectedThrown() {

        // test for super class
        verifyException(() -> list.get(0), RuntimeException.class);
        if (!expectedMessage.equals(caughtException().getMessage())) {
            assertEquals(expectedMessageJdk9on, caughtException().getMessage());
        }
    }

    @Test
    public void testVerifyException_ObjExc_superClassOfExpectedThrown() {

        // test for sub class
        try {
            verifyException(() -> list.get(0), ArrayIndexOutOfBoundsException.class);
            fail("ExceptionNotThrownAssertionError is expected");
        } catch (ExceptionNotThrownAssertionError e) {
            assertNull(caughtException());
            if (!e.getMessage().contains(expectedMessageJdk9on)) {
              assertEquals("Exception of type "
                    + ArrayIndexOutOfBoundsException.class.getName()
                    + " expected but was not thrown."
                    + " Instead an exception of type "
                    + IndexOutOfBoundsException.class + " with message '"
                    + expectedMessage + "' was thrown.", e.getMessage());
            }
        }
    }

    @Test
    public void testVerifyException_ObjExc_otherClassThanExpectedThrown()
             {

        // test for other exception type
        try {
            verifyException(() -> list.get(0), IllegalArgumentException.class);
            fail("ExceptionNotThrownAssertionError is expected");
        } catch (ExceptionNotThrownAssertionError e) {
            assertNull(caughtException());
            if (!e.getMessage().contains(expectedMessageJdk9on)) {
                assertEquals(
                    "Exception of type "
                            + IllegalArgumentException.class.getName()
                            + " expected but was not thrown."
                            + " Instead an exception of type "
                            + IndexOutOfBoundsException.class
                            + " with message '" + expectedMessage
                            + "' was thrown.", e.getMessage());
            }
        }
    }

    //fixme
    @Test
    public void testVerifyException_ObjExc_missingArgument_Exception()
             {

        // test validation of the arguments
        try {
            verifyException(() -> list.get(0), null);
            fail("IllegalArgumentException is expected");
        } catch (IllegalArgumentException e) {
            assertEquals("exceptionClazz must not be null", e.getMessage());
        }
    }

    @Test
    public void testVerifyException_ObjExc_missingArgument_Object()
             {

        try {
            verifyException(null, IllegalArgumentException.class);
            fail("IllegalArgumentException is expected");
        } catch (IllegalArgumentException e) {
            assertEquals("obj must not be null", e.getMessage());
        }
    }

    @Test
    public void testVerifyException_Obj_noExceptionThrown()  {

        List<String> list = new ArrayList<>();

        // no exception thrown by size()
        try {
            verifyException(list::size);
            fail("ExceptionNotThrownAssertionError is expected");
        } catch (ExceptionNotThrownAssertionError e) {
            assertNull(caughtException());
            assertEquals("Exception expected but not thrown", e.getMessage());
        }
    }

    @Test
    public void testVerifyException_Obj_exceptionThrown()  {

        List<String> list = new ArrayList<>();

        verifyException(() -> list.get(0));
        if (!expectedMessage.equals(caughtException().getMessage())) {
            assertEquals(expectedMessageJdk9on, caughtException().getMessage());
        }
    }

    @Test
    public void testVerifyException_Obj_missingArgument_Object()
             {

        // test validation of the arguments
        try {
            verifyException(null);
            fail("IllegalArgumentException is expected");
        } catch (IllegalArgumentException e) {
            assertEquals("obj must not be null", e.getMessage());
        }
    }

    @Test
    public void testCatchException_Obj_noExceptionThrown()  {

        List<String> list = new ArrayList<>();

        // no exception thrown by size()
        catchException(list::size);
        assertNull(caughtException());
    }

    @Test
    public void testCatchException_Obj_exceptionThrown()  {

        List<String> list = new ArrayList<>();

        catchException(() -> list.get(0));
        if (!expectedMessage.equals(caughtException().getMessage())) {
            assertEquals(expectedMessageJdk9on, caughtException().getMessage());
        }
    }

    @Test
    public void testCatchException_Obj_missingArgument_Object()
             {

        // test validation of the arguments
        try {
            catchException(null);
            fail("IllegalArgumentException is expected");
        } catch (IllegalArgumentException e) {
            assertEquals("obj must not be null", e.getMessage());
        }
    }

    @Test
    public void testProtected()  {
        PublicSomethingImpl obj = new PublicSomethingImpl();
        catchException(obj::dooo);
        assertTrue(caughtException() instanceof MyException);
    }

}
