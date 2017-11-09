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
package com.gluonhq.cloudlink.enterprise.sdk.javaee;

import com.gluonhq.cloudlink.enterprise.sdk.javaee.domain.ObjectData;
import com.gluonhq.cloudlink.enterprise.sdk.javaee.domain.PushNotification;
import com.gluonhq.cloudlink.enterprise.sdk.javaee.domain.PushNotificationTarget;
import com.gluonhq.impl.cloudlink.enterprise.sdk.javaee.GluonAuthenticationFeature;
import com.gluonhq.impl.cloudlink.enterprise.sdk.javaee.StringObject;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.json.Json;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * A Java client that acts as a wrapper for the Gluon CloudLink Enterprise REST SDK. Internally it makes use of the
 * <a href="https://docs.oracle.com/javaee/7/api/javax/ws/rs/client/package-summary.html">JAX-RS client API</a>.
 */
public class CloudLinkClient {

    private CloudLinkClientConfig config;

    private WebTarget webTarget;

    @Inject
    private Validator validator;

    /**
     * Used for injection.
     */
    CloudLinkClient() {
    }

    /**
     * Construct a new CloudLinkClient instance with the specified configuration.
     *
     * @param cloudLinkConfig the configuration to use for configuring the CloudLinkClient
     */
    public CloudLinkClient(CloudLinkClientConfig cloudLinkConfig) {
        this(cloudLinkConfig, null);
    }

    /**
     * Construct a new CloudLinkClient instance with the specified configurations for both
     * the CloudLinkClient and the JAX-RS client.
     *
     * @param cloudLinkClientConfig the configuration to use for configuring the CloudLinkClient
     * @param clientConfig the configuration to use for configuring the JAX-RS client
     */
    public CloudLinkClient(CloudLinkClientConfig cloudLinkClientConfig,
            Configuration clientConfig) {
        this.config = Objects.requireNonNull(cloudLinkClientConfig);
        this.webTarget = buildJaxRSClient(clientConfig);
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public void setCloudLinkClientConfig(CloudLinkClientConfig cloudLinkClientConfig) {
        this.config = Objects.requireNonNull(cloudLinkClientConfig);
        this.webTarget = buildJaxRSClient(null);
    }

    private WebTarget buildJaxRSClient(Configuration clientConfig) {
        String cloudLinkUrl = config.getHostname();

        //TODO Safer protocol check
        if (! cloudLinkUrl.startsWith("http")) {
            cloudLinkUrl = "https://" + cloudLinkUrl;
        }

        ClientBuilder builder = ClientBuilder.newBuilder();
        builder.register(new GluonAuthenticationFeature(config.getServerKey()));

        if (clientConfig != null) {
            builder.withConfig(clientConfig);
        }

        return builder.build().target(cloudLinkUrl);
    }

    @AroundInvoke
    private Object aroundInvoke(InvocationContext invocationContext) throws Exception {
        validator.forExecutables().validateParameters(this, invocationContext.getMethod(), invocationContext.getParameters());
        return invocationContext.proceed();
    }

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
    public PushNotification sendPushNotification(@Valid @NotNull PushNotification notification) {
        Objects.requireNonNull(notification, "notification may not be null");

        Form form = new Form();
        form.param("customIdentifier", notification.getCustomIdentifier())
                .param("title", notification.getTitle())
                .param("body", notification.getBody())
                .param("deliveryDate", "0")
                .param("priority", notification.getPriority().name())
                .param("expirationType", notification.getExpirationType().name())
                .param("expirationAmount", String.valueOf(notification.getExpirationAmount()))
                .param("targetType", notification.getTarget().getType().name())
                .param("invisible", String.valueOf(notification.isInvisible()));

        if (notification.getTarget().getType() == PushNotificationTarget.Type.SINGLE_DEVICE) {
            form.param("targetDeviceToken", notification.getTarget().getDeviceToken());
        } else if (notification.getTarget().getType() == PushNotificationTarget.Type.TOPIC) {
            form.param("targetTopic", notification.getTarget().getTopic());
        }

        Response response = webTarget.path("3").path("push").path("enterprise").path("notification").request()
                .post(Entity.form(form));
        if (response.getStatus() == 200) {
            String json = response.readEntity(String.class);
            Jsonb jsonb = JsonbBuilder.create();
            return jsonb.fromJson(json, PushNotification.class);
        } else {
            throw handleErrorResponse(response);
        }
    }

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
    public <T> T getObject(@NotEmpty String objectId, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        Response response = webTarget.path("3").path("data").path("enterprise").path("object").path(objectId)
                .request().get();
        if (response.getStatus() == 200) {
            String json = response.readEntity(String.class);
            Jsonb jsonb = JsonbBuilder.create();
            ObjectData object = jsonb.fromJson(json, ObjectData.class);
            if (object.getUid() == null) {
                return null;
            } else {
                return objectMapper.apply(object);
            }
        } else {
            throw handleErrorResponse(response);
        }
    }

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
    public <T> T getObject(@NotEmpty String objectId, @NotNull Class<T> objectType) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(objectType, "objectType may not be null");

        return getObject(objectId, data -> fromJson(data, objectType));
    }

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
    public <T> T addObject(@NotEmpty String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        Response response = webTarget.path("3").path("data").path("enterprise").path("object").path(objectId).path("add")
                .request().post(Entity.json(toJson(target)));
        if (response.getStatus() == 200) {
            String json = response.readEntity(String.class);
            ObjectData object = JsonbBuilder.create().fromJson(json, ObjectData.class);
            return objectMapper.apply(object);
        } else {
            throw handleErrorResponse(response);
        }
    }

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
    @SuppressWarnings("unchecked")
    public <T> T addObject(@NotEmpty String objectId, @NotNull T target) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");

        return addObject(objectId, target, data -> fromJson(data, (Class<T>) target.getClass()));
    }

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
    public <T> T updateObject(@NotEmpty String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        Response response = webTarget.path("3").path("data").path("enterprise").path("object").path(objectId).path("update")
                .request().post(Entity.json(toJson(target)));
        if (response.getStatus() == 200) {
            String json = response.readEntity(String.class);
            ObjectData object = JsonbBuilder.create().fromJson(json, ObjectData.class);
            if (object.getUid() == null) {
                return null;
            } else {
                return objectMapper.apply(object);
            }
        } else {
            throw handleErrorResponse(response);
        }
    }

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
    @SuppressWarnings("unchecked")
    public <T> T updateObject(@NotEmpty String objectId, @NotNull T target) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");

        return updateObject(objectId, target, data -> fromJson(data, (Class<T>) target.getClass()));
    }

    /**
     * Removes the object with the specified identifier.
     *
     * @param objectId the identifier of the object to remove
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when <code>objectId</code> is <code>null</code>
     */
    public void removeObject(@NotEmpty String objectId) {
        Objects.requireNonNull(objectId, "objectId may not be null");

        Response response = webTarget.path("3").path("data").path("enterprise").path("object").path(objectId).path("remove")
                .request().post(Entity.form(new Form()));
        if (response.getStatus() != 200) {
            throw handleErrorResponse(response);
        }
    }

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
    public <T> List<T> getList(@NotEmpty String listId, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        Response response = webTarget.path("3").path("data").path("enterprise").path("list").path(listId)
                .request().get();
        if (response.getStatus() == 200) {
            String json = response.readEntity(String.class);
            Jsonb jsonb = JsonbBuilder.create();
            List<ObjectData> objects = jsonb.fromJson(json, new ArrayList<ObjectData>(){}.getClass().getGenericSuperclass());
            return objects.stream().map(objectMapper).collect(Collectors.toList());
        } else {
            throw handleErrorResponse(response);
        }
    }

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
    public <T> List<T> getList(@NotEmpty String listId, @NotNull Class<T> objectType) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectType, "objectType may not be null");

        Jsonb jsonb = JsonbBuilder.create();
        return getList(listId, data -> jsonb.fromJson(data.getPayload(), objectType));
    }

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
    public <T> T addToList(@NotEmpty String listId, @NotEmpty String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        Jsonb jsonb = JsonbBuilder.create();
        Response response = webTarget.path("3").path("data").path("enterprise").path("list").path(listId).path("add").path(objectId)
                .request().post(Entity.json(jsonb.toJson(target)));
        if (response.getStatus() == 200) {
            String json = response.readEntity(String.class);
            ObjectData object = jsonb.fromJson(json, ObjectData.class);
            return objectMapper.apply(object);
        } else {
            throw handleErrorResponse(response);
        }
    }

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
    @SuppressWarnings("unchecked")
    public <T> T addToList(@NotEmpty String listId, @NotEmpty String objectId, @NotNull T target) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");

        Jsonb jsonb = JsonbBuilder.create();
        return addToList(listId, objectId, target, data -> jsonb.fromJson(data.getPayload(), (Class<T>) target.getClass()));
    }

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
    public <T> T updateInList(@NotEmpty String listId, @NotEmpty String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        Objects.requireNonNull(objectMapper);
        Jsonb jsonb = JsonbBuilder.create();
        Response response = webTarget.path("3").path("data").path("enterprise").path("list").path(listId).path("update").path(objectId)
                .request().post(Entity.json(jsonb.toJson(target)));
        if (response.getStatus() == 200) {
            String json = response.readEntity(String.class);
            ObjectData object = jsonb.fromJson(json, ObjectData.class);
            if (object.getUid() == null) {
                return null;
            } else {
                return objectMapper.apply(object);
            }
        } else {
            throw handleErrorResponse(response);
        }
    }

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
    @SuppressWarnings("unchecked")
    public <T> T updateInList(@NotEmpty String listId, @NotEmpty String objectId, @NotNull T target) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");

        Jsonb jsonb = JsonbBuilder.create();
        return updateInList(listId, objectId, target, data -> jsonb.fromJson(data.getPayload(), (Class<T>) target.getClass()));
    }

    /**
     * Removes the object from the list with the specified identifiers.
     *
     * @param listId the identifier of the list to remove the object from
     * @param objectId the identifier of the object to remove
     * @throws CloudLinkClientException when an invalid HTTP response is returned from the request to Gluon CloudLink
     * @throws NullPointerException when any of the parameters is <code>null</code>
     */
    public void removeFromList(@NotEmpty String listId, @NotEmpty String objectId) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectId, "objectId may not be null");

        Response response = webTarget.path("3").path("data").path("enterprise").path("list").path(listId).path("remove").path(objectId)
                .request().post(Entity.form(new Form()));
        if (response.getStatus() != 200) {
            throw handleErrorResponse(response);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T fromJson(ObjectData data, Class<T> objectType) {
        if (String.class.equals(objectType)) {
            return (T) JsonbBuilder.create().fromJson(data.getPayload(), StringObject.class).getV();
        } else {
            return JsonbBuilder.create().fromJson(data.getPayload(), objectType);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> String toJson(T target) {
        if (String.class.equals(target.getClass())) {
            return Json.createObjectBuilder().add("v", (String) target).build().toString();
        } else {
            return JsonbBuilder.create().toJson(target);
        }
    }

    private CloudLinkClientException handleErrorResponse(Response response) {
        if (config.getLogLevel().intValue() <= Level.FINE.intValue()) {
            return new CloudLinkClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase(),
                    response.readEntity(String.class));
        } else {
            return new CloudLinkClientException(response.getStatus(), response.getStatusInfo().getReasonPhrase());
        }
    }
}
