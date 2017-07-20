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
package com.gluonhq.cloudlink.enterprise.sdk.spring.domain;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class PushNotificationValidationTest {

    private Validator validator;

    @Before
    public void before() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void validatePushNotificationBasic() {
        PushNotification pushNotification = new PushNotification();

        Set<ConstraintViolation<PushNotification>> violations = validator.validate(pushNotification);
        assertEquals(0, violations.size());
    }

    @Test
    public void validatePushNotificationExpiration() {
        PushNotification pushNotification = new PushNotification();
        pushNotification.setExpirationType(PushNotification.ExpirationType.DAYS);
        pushNotification.setExpirationAmount(8);

        Set<ConstraintViolation<PushNotification>> violations = validator.validate(pushNotification);
        assertEquals(1, violations.size());
        assertEquals("value must be between 0 and " + pushNotification.getExpirationType().getMaxAmount() + " when using expiration type " + pushNotification.getExpirationType().name(), violations.iterator().next().getMessage());

        pushNotification.setExpirationType(PushNotification.ExpirationType.WEEKS);
        pushNotification.setExpirationAmount(-1);

        violations = validator.validate(pushNotification);
        assertEquals(1, violations.size());
        assertEquals("value must be between 0 and " + pushNotification.getExpirationType().getMaxAmount() + " when using expiration type " + pushNotification.getExpirationType().name(), violations.iterator().next().getMessage());

        pushNotification.setExpirationAmount(3);

        violations = validator.validate(pushNotification);
        assertEquals(0, violations.size());
    }

    @Test
    public void validatePushNotificationDelivery() {
        PushNotification pushNotification = new PushNotification();
        pushNotification.setDeliveryDate(-189L);

        Set<ConstraintViolation<PushNotification>> violations = validator.validate(pushNotification);
        assertEquals(1, violations.size());
        assertEquals("must be greater than or equal to 0", violations.iterator().next().getMessage());

        pushNotification.setDeliveryDate(System.currentTimeMillis());

        violations = validator.validate(pushNotification);
        assertEquals(0, violations.size());
    }
}
