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

import com.gluonhq.cloudlink.client.enterprise.domain.ObjectData;
import com.gluonhq.cloudlink.client.enterprise.domain.PushNotification;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.function.Function;

/**
 * Implementation of {@link CloudLinkClient} that throws a RuntimeException on
 * each method call. This implementations is solely used for testing the bean
 * validation of the CloudLinkClient methods.
 */
public class NotImplementedCloudLinkClient implements CloudLinkClient {
    @Override
    public PushNotification sendPushNotification(@NotNull @Valid PushNotification notification) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> T getObject(@NotNull @Size(min = 1) String objectId, @NotNull Function<ObjectData, T> objectMapper) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> T getObject(@NotNull @Size(min = 1) String objectId, @NotNull Class<T> objectType) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> T addObject(@NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> T addObject(@NotNull @Size(min = 1) String objectId, @NotNull T target) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> T updateObject(@NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> T updateObject(@NotNull @Size(min = 1) String objectId, @NotNull T target) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public void removeObject(@NotNull @Size(min = 1) String objectId) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> List<T> getList(@NotNull @Size(min = 1) String listId, @NotNull Function<ObjectData, T> objectMapper) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> List<T> getList(@NotNull @Size(min = 1) String listId, @NotNull Class<T> objectType) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> T addToList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> T addToList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> T updateInList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public <T> T updateInList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Override
    public void removeFromList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId) {
        throw new RuntimeException("Not yet implemented.");
    }
}
