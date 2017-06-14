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
package com.gluonhq.cloudlink.client.enterprise.javaee;

import com.gluonhq.cloudlink.client.enterprise.CloudLinkClient;
import com.gluonhq.cloudlink.client.enterprise.CloudLinkClientException;
import com.gluonhq.cloudlink.client.enterprise.CloudLinkConfig;
import com.gluonhq.cloudlink.client.enterprise.domain.ObjectData;
import com.gluonhq.cloudlink.client.enterprise.domain.PushNotification;
import com.gluonhq.cloudlink.client.enterprise.domain.PushNotificationTarget;
import com.gluonhq.impl.cloudlink.client.enterprise.javaee.GluonAuthenticationFeature;
import com.gluonhq.impl.cloudlink.client.enterprise.javaee.StringObject;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.json.Json;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
 * An implementation of the {@link CloudLinkClient} using the
 * <a href="https://docs.oracle.com/javaee/7/api/javax/ws/rs/client/package-summary.html">JAX-RS client API</a>.
 */
public class JavaEECloudLinkClient implements CloudLinkClient {

    private CloudLinkConfig config;

    private WebTarget webTarget;

    @Inject
    private Validator validator;

    /**
     * Used for injection.
     */
    JavaEECloudLinkClient() {
    }

    /**
     * Construct a new CloudLinkClient instance with the specified configuration.
     *
     * @param cloudLinkConfig the configuration to use for configuring the CloudLinkClient
     */
    public JavaEECloudLinkClient(CloudLinkConfig cloudLinkConfig) {
        this(cloudLinkConfig, null);
    }

    /**
     * Construct a new CloudLinkClient instance with the specified configurations for both
     * the CloudLinkClient and the JAX-RS client.
     *
     * @param cloudLinkConfig the configuration to use for configuring the CloudLinkClient
     * @param clientConfig the configuration to use for configuring the JAX-RS client
     */
    public JavaEECloudLinkClient(CloudLinkConfig cloudLinkConfig,
            Configuration clientConfig) {
        this.config = Objects.requireNonNull(cloudLinkConfig);
        this.webTarget = buildJaxRSClient(clientConfig);
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public void setCloudLinkConfig(CloudLinkConfig cloudLinkConfig) {
        this.config = Objects.requireNonNull(cloudLinkConfig);
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
     * {@inheritDoc}
     */
    @Override
    public PushNotification sendPushNotification(@Valid @NotNull PushNotification notification) {
        Form form = new Form();
        form.param("title", notification.getTitle())
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
     * {@inheritDoc}
     */
    @Override
    public <T> T getObject(@NotNull @Size(min = 1) String objectId, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectMapper);
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
     * {@inheritDoc}
     */
    @Override
    public <T> T getObject(@NotNull @Size(min = 1) String objectId, @NotNull Class<T> objectType) {
        return getObject(objectId, data -> fromJson(data, objectType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T addObject(@NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectMapper);
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
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T addObject(@NotNull @Size(min = 1) String objectId, @NotNull T target) {
        return addObject(objectId, target, data -> fromJson(data, (Class<T>) target.getClass()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T updateObject(@NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(objectMapper);
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
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T updateObject(@NotNull @Size(min = 1) String objectId, @NotNull T target) {
        return updateObject(objectId, target, data -> fromJson(data, (Class<T>) target.getClass()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObject(@NotNull @Size(min = 1) String objectId) {
        Response response = webTarget.path("3").path("data").path("enterprise").path("object").path(objectId).path("remove")
                .request().post(Entity.form(new Form()));
        if (response.getStatus() != 200) {
            throw handleErrorResponse(response);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> getList(@NotNull @Size(min = 1) String listId, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectMapper);
        Response response = webTarget.path("3").path("data").path("enterprise").path("list").path(listId)
                .request().get();
        if (response.getStatus() == 200) {
            String json = response.readEntity(String.class);
            Jsonb jsonb = JsonbBuilder.create();
            List<ObjectData> objects = jsonb.fromJson(json, new ArrayList<ObjectData>(){}.getClass());
            return objects.stream().map(objectMapper).collect(Collectors.toList());
        } else {
            throw handleErrorResponse(response);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> getList(@NotNull @Size(min = 1) String listId, @NotNull Class<T> objectType) {
        Objects.requireNonNull(objectType);
        Jsonb jsonb = JsonbBuilder.create();
        return getList(listId, data -> jsonb.fromJson(data.getPayload(), objectType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T addToList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectMapper);
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
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T addToList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target) {
        Jsonb jsonb = JsonbBuilder.create();
        return addToList(listId, objectId, target, data -> jsonb.fromJson(data.getPayload(), (Class<T>) target.getClass()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T updateInList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
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
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T updateInList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target) {
        Jsonb jsonb = JsonbBuilder.create();
        return updateInList(listId, objectId, target, data -> jsonb.fromJson(data.getPayload(), (Class<T>) target.getClass()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId) {
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
