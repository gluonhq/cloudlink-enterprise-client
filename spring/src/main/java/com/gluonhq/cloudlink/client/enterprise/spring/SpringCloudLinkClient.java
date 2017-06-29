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
package com.gluonhq.cloudlink.client.enterprise.spring;

import com.gluonhq.cloudlink.client.enterprise.CloudLinkClient;
import com.gluonhq.cloudlink.client.enterprise.CloudLinkConfig;
import com.gluonhq.cloudlink.client.enterprise.domain.ObjectData;
import com.gluonhq.cloudlink.client.enterprise.domain.PushNotification;
import com.gluonhq.impl.cloudlink.client.enterprise.spring.CloudLinkAuthRequestInterceptor;
import com.gluonhq.impl.cloudlink.client.enterprise.spring.CloudLinkErrorDecoder;
import com.gluonhq.impl.cloudlink.client.enterprise.spring.FeignClient;
import com.gluonhq.impl.cloudlink.client.enterprise.spring.StringObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import feign.Feign;
import feign.Logger;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import feign.jaxrs.JAXRSContract;
import feign.okhttp.OkHttpClient;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link CloudLinkClient} using <a href="https://github.com/OpenFeign/feign">Feign</a>.
 */
@Validated
public class SpringCloudLinkClient implements CloudLinkClient {

    private static final Logger LOG = new Logger.ErrorLogger();

    private static final Gson gson = new Gson();

    private final CloudLinkConfig config;

    private final FeignClient feignClient;

    /**
     * Construct a new CloudLinkClient instance with the specified configuration.
     *
     * @param cloudLinkConfig the configuration to use for configuring the CloudLinkClient
     */
    public SpringCloudLinkClient(CloudLinkConfig cloudLinkConfig) {
        this.config = Objects.requireNonNull(cloudLinkConfig);
        feignClient = buildFeignClient();
    }

    private FeignClient buildFeignClient() {
        String cloudLinkUrl = config.getHostname();

        //TODO Safer protocol check
        if (! cloudLinkUrl.startsWith("http")) {
            cloudLinkUrl = "https://" + cloudLinkUrl;
        }

        return Feign.builder()
                .logger(LOG)
                .logLevel(getLogLevel(config.getLogLevel()))
                .contract(new JAXRSContract())
                .client(new OkHttpClient())
                .encoder(new FormEncoder())
                .decoder(new GsonDecoder())
                .errorDecoder(new CloudLinkErrorDecoder(config))
                .requestInterceptor(new CloudLinkAuthRequestInterceptor(config.getServerKey()))
                .target(FeignClient.class, cloudLinkUrl + "/3");
    }

    private Logger.Level getLogLevel(Level level) {
        if (level.intValue() <= Level.FINE.intValue()) {
            return Logger.Level.FULL;
        } else if (level.intValue() <= Level.INFO.intValue()) {
            return Logger.Level.HEADERS;
        } else if (level.intValue() <= Level.SEVERE.intValue()) {
            return Logger.Level.BASIC;
        } else {
            return Logger.Level.NONE;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T fromJson(ObjectData objData, Class<T> objectType) {
        if (String.class.equals(objectType)) {
            return (T) gson.fromJson(objData.getPayload(), StringObject.class).getV();
        } else {
            return gson.fromJson(objData.getPayload(), objectType);
        }
    }

    private static <T> String toJson(T target) {
        if (String.class.equals(target.getClass())) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("v", (String) target);
            return jsonObject.toString();
        } else {
            return gson.toJson(target);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PushNotification sendPushNotification(@NotNull @Valid PushNotification notification) {
        Objects.requireNonNull(notification, "notification may not be null");

        return feignClient.sendPushNotification(
                notification.getTitle(),
                notification.getBody(),
                notification.getDeliveryDate(),
                notification.getPriority(),
                notification.getExpirationType(),
                notification.getExpirationAmount(),
                notification.getTarget().getType(),
                notification.getTarget().getTopic(),
                notification.getTarget().getDeviceToken(),
                notification.isInvisible());
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getObject(@NotNull @Size(min = 1) String objectId, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        ObjectData objData = feignClient.getObject(objectId);
        if (objData.getUid() == null) {
            return null;
        } else {
            return objectMapper.apply(objData);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getObject(@NotNull @Size(min = 1) String objectId, @NotNull Class<T> objectType) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(objectType, "objectType may not be null");

        ObjectData objData = feignClient.getObject(objectId);
        if (objData.getUid() == null) {
            return null;
        } else {
            return fromJson(objData, objectType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T addObject(@NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        ObjectData objData = feignClient.addObject(objectId, toJson(target));
        return objectMapper.apply(objData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T addObject(@NotNull @Size(min = 1) String objectId, @NotNull T target) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");

        ObjectData objData = feignClient.addObject(objectId, toJson(target));
        return fromJson(objData, (Class<T>) target.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T updateObject(@NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        ObjectData objData = feignClient.updateObject(objectId, toJson(target));
        if (objData.getUid() == null) {
            return null;
        } else {
            return objectMapper.apply(objData);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T updateObject(@NotNull @Size(min = 1) String objectId, @NotNull T target) {
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");

        ObjectData objData = feignClient.updateObject(objectId, toJson(target));
        if (objData.getUid() == null) {
            return null;
        } else {
            return fromJson(objData, (Class<T>) target.getClass());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeObject(@NotNull @Size(min = 1) String objectId) {
        Objects.requireNonNull(objectId, "objectId may not be null");

        feignClient.removeObject(objectId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> getList(@NotNull @Size(min = 1) String listId, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        List<ObjectData> objDataList = feignClient.getList(listId);
        return objDataList.stream().map(objectMapper).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<T> getList(@NotNull @Size(min = 1) String listId, @NotNull Class<T> objectType) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectType, "objectType may not be null");

        List<ObjectData> objDataList = feignClient.getList(listId);
        return objDataList.stream().map(objData -> fromJson(objData, objectType)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T addToList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        ObjectData objData = feignClient.addToList(listId, objectId, gson.toJson(target));
        return objectMapper.apply(objData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T addToList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");

        ObjectData objData = feignClient.addToList(listId, objectId, gson.toJson(target));
        return fromJson(objData, (Class<T>) target.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T updateInList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target, @NotNull Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");
        Objects.requireNonNull(objectMapper, "objectMapper may not be null");

        ObjectData objData = feignClient.updateInList(listId, objectId, gson.toJson(target));
        if (objData.getUid() == null) {
            return null;
        } else {
            return objectMapper.apply(objData);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T updateInList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId, @NotNull T target) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectId, "objectId may not be null");
        Objects.requireNonNull(target, "target may not be null");

        ObjectData objData = feignClient.updateInList(listId, objectId, gson.toJson(target));
        if (objData.getUid() == null) {
            return null;
        } else {
            return fromJson(objData, (Class<T>) target.getClass());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromList(@NotNull @Size(min = 1) String listId, @NotNull @Size(min = 1) String objectId) {
        Objects.requireNonNull(listId, "listId may not be null");
        Objects.requireNonNull(objectId, "objectId may not be null");

        feignClient.removeFromList(listId, objectId);
    }

}
