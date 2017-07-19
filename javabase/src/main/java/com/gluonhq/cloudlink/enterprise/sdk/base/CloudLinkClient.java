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
package com.gluonhq.cloudlink.enterprise.sdk.base;

import com.gluonhq.cloudlink.enterprise.sdk.base.domain.ObjectData;
import com.gluonhq.cloudlink.enterprise.sdk.base.domain.PushNotification;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.function.Function;

/**
 * A Java client that acts as a wrapper for the Gluon CloudLink Enterprise REST SDK.
 */
public interface CloudLinkClient {

    /**
     * Send a push notification.
     *
     * @param notification the push notification to send
     * @return the push notification that was sent, with the identifier and creation date set
     * @throws javax.validation.ConstraintViolationException when the provided push notification object fails to
     * validate
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when <code>notification</code> is <code>null</code>
     */
    PushNotification sendPushNotification(@NotNull @Valid PushNotification notification);

    /**
     * Retrieve an object with the specified identifier. If no object with such an identifier exists,
     * <code>null</code> will be returned.
     *
     * @param objectId the identifier of the object to retrieve
     * @param objectMapper a mapper to convert an instance of ObjectData into the defined object type
     * @param <T> the type of the returned object
     * @return the object attached to the specified identifier or <code>null</code> if no such object exists
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> T getObject(@NotNull @Size(min = 1) String objectId, @NotNull Function<ObjectData, T> objectMapper);

    /**
     * Retrieve an object with the specified identifier. If no object with such an identifier exists,
     * <code>null</code> will be returned.
     *
     * @param objectId the identifier of the object to retrieve
     * @param objectType the type of the returned object
     * @param <T> the type of the returned object
     * @return the object attached to the specified identifier or <code>null</code> if no such object exists
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> T getObject(@NotNull @Size(min = 1) String objectId, @NotNull Class<T> objectType);

    /**
     * Adds the object with the specified identifier. If an object already exists with the specified identifier,
     * the existing object will be overwritten with the new value.
     *
     * @param objectId the identifier of the object to add
     * @param target the object to add
     * @param objectMapper a mapper to convert an instance of ObjectData into the defined object type
     * @param <T> the type of the added object
     * @return the newly added object
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> T addObject(@NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper);

    /**
     * Adds the object with the specified identifier. If an object already exists with the specified identifier,
     * the existing object will be overwritten with the new value.
     *
     * @param objectId the identifier of the object to add
     * @param target the object to add
     * @param <T> the type of the added object
     * @return the newly added object
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> T addObject(@NotNull @Size(min = 1) String objectId, @NotNull T target);

    /**
     * Updates the object with the specified identifier. If no object exists with the specified identifier,
     * nothing will happen and <code>null</code> will be returned.
     *
     * @param objectId the identifier of the object to update
     * @param target the object to update
     * @param objectMapper a mapper to convert an instance of ObjectData into the defined object type
     * @param <T> the type of the updated object
     * @return the updated object or <code>null</code> if no object exists with the specified identifier
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> T updateObject(@NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper);

    /**
     * Updates the object with the specified identifier. If no object exists with the specified identifier,
     * nothing will happen and <code>null</code> will be returned.
     *
     * @param objectId the identifier of the object to update
     * @param target the object to update
     * @param <T> the type of the updated object
     * @return the updated object or <code>null</code> if no object exists with the specified identifier
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> T updateObject(@NotNull @Size(min = 1) String objectId, @NotNull T target);

    /**
     * Removes the object with the specified identifier.
     *
     * @param objectId the identifier of the object to remove
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when <code>objectId</code> is <code>null</code>
     */
    void removeObject(@NotNull @Size(min = 1) String objectId);

    /**
     * Retrieve a list with the specified identifier. The returned list contains the list of objects
     * that were added to the list.
     *
     * @param listId the identifier of the list to retrieve
     * @param objectMapper a mapper to convert instances of ObjectData into the defined object type
     * @param <T> the type of the objects in the list
     * @return the list attached to the specified identifier
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> List<T> getList(@NotNull @Size(min = 1) String listId, @NotNull Function<ObjectData, T> objectMapper);

    /**
     * Retrieve a list with the specified identifier. The returned list contains the list of objects
     * that were added to the list.
     *
     * @param listId the identifier of the list to retrieve
     * @param objectType the type of the objects in the list
     * @param <T> the type of the objects in the list
     * @return the list attached to the specified identifier
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> List<T> getList(@NotNull @Size(min = 1) String listId, @NotNull Class<T> objectType);

    /**
     * Adds an object to the list with the specified identifiers.
     *
     * @param listId the identifier of the list to add the object to
     * @param objectId the identifier of the object to add
     * @param target the object to add
     * @param objectMapper a mapper to convert an instance of ObjectData into the defined object type
     * @param <T> the type of the added object
     * @return the newly added object
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> T addToList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper);

    /**
     * Adds an object to the list with the specified identifiers.
     *
     * @param listId the identifier of the list to add the object to
     * @param objectId the identifier of the object to add
     * @param target the object to add
     * @param <T> the type of the added object
     * @return the newly added object
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> T addToList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target);

    /**
     * Updates an existing object in the list with the specified identifiers. When the object with the specified
     * identifier does not exist in the list, nothing will happen and <code>null</code> will be returned.
     *
     * @param listId the identifier of the list to update the object in
     * @param objectId the identifier of the object to update
     * @param target the object to update
     * @param objectMapper a mapper to convert an instance of ObjectData into the defined object type
     * @param <T> the type of the updated object
     * @return the updated object or <code>null</code> if no object exists in the list with the specified identifiers
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> T updateInList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper);

    /**
     * Updates an existing object in the list with the specified identifiers. When the object with the specified
     * identifier does not exist in the list, nothing will happen and <code>null</code> will be returned.
     *
     * @param listId the identifier of the list to update the object in
     * @param objectId the identifier of the object to update
     * @param target the object to update
     * @param <T> the type of the updated object
     * @return the updated object or <code>null</code> if no object exists in the list with the specified identifiers
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    <T> T updateInList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target);

    /**
     * Removes the object from the list with the specified identifiers.
     *
     * @param listId the identifier of the list to remove the object from
     * @param objectId the identifier of the object to remove
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    void removeFromList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId);

}
