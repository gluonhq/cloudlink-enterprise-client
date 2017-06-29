/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2017, Gluon Software
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.cloudlink.client.enterprise;

import com.gluonhq.cloudlink.client.enterprise.domain.PushNotification;
import junit.framework.AssertionFailedError;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class CloudLinkClientValidationTest {

    private Validator validator;

    @Before
    public void before() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void validateSendPushNotification() throws NoSuchMethodException {
        String methodName = "sendPushNotification";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, PushNotification.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");

        PushNotification pushNotification = new PushNotification();
        pushNotification.setExpirationType(PushNotification.ExpirationType.WEEKS);
        pushNotification.setExpirationAmount(5);

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {pushNotification});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "value must be between 0 and 4 when using expiration type WEEKS", methodName + ".arg0.expirationAmount");

        pushNotification.setExpirationAmount(3);
        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {pushNotification});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateGetObjectByMapper() throws NoSuchMethodException {
        String methodName = "getObject";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, Function.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");

        Function mapper = (obj) -> obj;

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", mapper});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", mapper});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"objectIdentifier", mapper});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateGetObjectByClass() throws NoSuchMethodException {
        String methodName = "getObject";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, Class.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", String.class});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", String.class});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"objectIdentifier", String.class});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateAddObject() throws NoSuchMethodException {
        String methodName = "addObject";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, Object.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", "sample"});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", "sample"});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"objectIdentifier", "sample"});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateAddObjectWithMapper() throws NoSuchMethodException {
        String methodName = "addObject";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, Object.class, Function.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null, null});
        assertEquals(3, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg2");

        Function mapper = (obj) -> obj;

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", "sample", mapper});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", "sample", mapper});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"objectIdentifier", "sample", mapper});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateUpdateObject() throws NoSuchMethodException {
        String methodName = "updateObject";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, Object.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", "sample"});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", "sample"});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"objectIdentifier", "sample"});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateUpdateObjectWithMapper() throws NoSuchMethodException {
        String methodName = "updateObject";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, Object.class, Function.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null, null});
        assertEquals(3, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg2");

        Function mapper = (obj) -> obj;

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", "sample", mapper});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", "sample", mapper});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"objectIdentifier", "sample", mapper});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateRemoveObject() throws NoSuchMethodException {
        String methodName = "removeObject";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {""});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  "});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"objectIdentifier"});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateGetListByMapper() throws NoSuchMethodException {
        String methodName = "getList";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, Function.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");

        Function mapper = (obj) -> obj;

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", mapper});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", mapper});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"objectIdentifier", mapper});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateGetListByClass() throws NoSuchMethodException {
        String methodName = "getList";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, Class.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", String.class});
        assertEquals(1, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", String.class});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"objectIdentifier", String.class});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateAddToList() throws NoSuchMethodException {
        String methodName = "addToList";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, String.class, Object.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null, null});
        assertEquals(3, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg2");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", "", "sample"});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg1");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", "  ", "sample"});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"listIdentifier", "objectIdentifier", "sample"});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateAddToListWithMapper() throws NoSuchMethodException {
        String methodName = "addToList";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, String.class, Object.class, Function.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null, null, null});
        assertEquals(4, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg2");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg3");

        Function mapper = (obj) -> obj;

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", "", "sample", mapper});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg1");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", " ", "sample", mapper});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"listIdentifier", "objectIdentifier", "sample", mapper});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateUpdateInList() throws NoSuchMethodException {
        String methodName = "updateInList";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, String.class, Object.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null, null});
        assertEquals(3, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg2");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", "", "sample"});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg1");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", "  ", "sample"});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"listIdentifier", "objectIdentifier", "sample"});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateUpdateInListWithMapper() throws NoSuchMethodException {
        String methodName = "updateInList";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, String.class, Object.class, Function.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null, null, null, null});
        assertEquals(4, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg2");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg3");

        Function mapper = (obj) -> obj;

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", "", "sample", mapper});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg1");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", "  ", "sample", mapper});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"listIdentifier", "objectIdentifier", "sample", mapper});
        assertEquals(0, violations.size());
    }

    @Test
    public void validateRemoveFromList() throws NoSuchMethodException {
        String methodName = "removeFromList";

        CloudLinkClient cloudLinkClient = new NotImplementedCloudLinkClient();
        Method method = CloudLinkClient.class.getMethod(methodName, String.class, String.class);

        Set<ConstraintViolation<CloudLinkClient>> violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {null,null});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "may not be null", methodName + ".arg0");
        assertMessageForProperty(violations, "may not be null", methodName + ".arg1");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"", ""});
        assertEquals(2, violations.size());
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg0");
        assertMessageForProperty(violations, "size must be between 1 and " + Integer.MAX_VALUE, methodName + ".arg1");

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[] {"  ", "  "});
        assertEquals(0, violations.size());

        violations = validator.forExecutables().validateParameters(cloudLinkClient, method, new Object[]{"listIdentifier", "objectIdentifier"});
        assertEquals(0, violations.size());
    }

    private <T> void assertMessageForProperty(Set<ConstraintViolation<T>> violations, String message, String propertyPath) {
        ConstraintViolation violation = violations.stream()
                .filter(v -> propertyPath.equals(v.getPropertyPath().toString()))
                .findFirst().orElseThrow(() -> new AssertionFailedError("No constraint violation found for property path " + propertyPath));
        assertEquals(message, violation.getMessage());
    }
}
