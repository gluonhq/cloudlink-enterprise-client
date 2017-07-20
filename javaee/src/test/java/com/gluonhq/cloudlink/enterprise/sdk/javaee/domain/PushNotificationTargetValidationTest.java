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
package com.gluonhq.cloudlink.enterprise.sdk.javaee.domain;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class PushNotificationTargetValidationTest {

    private Validator validator;

    @Before
    public void before() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void validateTargetBasic() {
        PushNotificationTarget target = new PushNotificationTarget();

        Set<ConstraintViolation<PushNotificationTarget>> violations = validator.validate(target);
        assertEquals(0, violations.size());
    }

    @Test
    public void validateTargetAllDevices() {
        PushNotificationTarget target = new PushNotificationTarget();
        target.setType(PushNotificationTarget.Type.ALL_DEVICES);

        Set<ConstraintViolation<PushNotificationTarget>> violations = validator.validate(target);
        assertEquals(0, violations.size());

        target.setTopic("sample_topic");

        violations = validator.validate(target);
        assertEquals(0, violations.size());

        target.setTopic("");
        target.setDeviceToken(UUID.randomUUID().toString());

        violations = validator.validate(target);
        assertEquals(0, violations.size());
    }

    @Test
    public void validateTargetSingleDevice() {
        PushNotificationTarget target = new PushNotificationTarget();
        target.setType(PushNotificationTarget.Type.SINGLE_DEVICE);

        Set<ConstraintViolation<PushNotificationTarget>> violations = validator.validate(target);
        assertEquals(1, violations.size());
        assertEquals("must not be blank when using target type " + target.getType().name(), violations.iterator().next().getMessage());

        target.setDeviceToken(UUID.randomUUID().toString());
        violations = validator.validate(target);
        assertEquals(0, violations.size());
    }

    @Test
    public void validateTargetTopic() {
        PushNotificationTarget target = new PushNotificationTarget();
        target.setType(PushNotificationTarget.Type.TOPIC);

        Set<ConstraintViolation<PushNotificationTarget>> violations = validator.validate(target);
        assertEquals(1, violations.size());
        assertEquals("must not be blank when using target type " + target.getType().name(), violations.iterator().next().getMessage());

        target.setTopic("sample_topic");
        violations = validator.validate(target);
        assertEquals(0, violations.size());
    }
}
