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
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link CloudLinkClient} using <a href="https://github.com/OpenFeign/feign">Feign</a>.
 */
public class SpringCloudLinkClient implements CloudLinkClient {

    private static final Logger LOG = new Logger.ErrorLogger();

    private static final Gson gson = new Gson();

    private final CloudLinkConfig config;

    private final FeignClient feignClient;

    @Autowired
    private Validator validator;

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
    private static <T> T fromJson( ObjectData objData, Class<T> objectType ) {
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

    private Validator getValidator() {
        if (validator == null) {
            this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        }
        return validator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PushNotification sendPushNotification(final PushNotification notification) {

        Set<ConstraintViolation<PushNotification>> violations = getValidator().validate(notification);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

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
    public final <T> T getObject(final String objectId, final Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectMapper);
        ObjectData objData = feignClient.getObject(Objects.requireNonNull(objectId));
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
    public final <T> T getObject(final String objectId, final Class<T> objectType) {
        Objects.requireNonNull(objectType);
        ObjectData objData = feignClient.getObject(Objects.requireNonNull(objectId));
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
    public final <T> T addObject(final String objectId, final T target, final Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(objectMapper);
        ObjectData objData = feignClient.addObject(objectId, toJson(target));
        return objectMapper.apply(objData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public final <T> T addObject(final String objectId, final T target) {
        Objects.requireNonNull(target);
        ObjectData objData = feignClient.addObject(objectId, toJson(target));
        return fromJson(objData, (Class<T>) target.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> T updateObject(final String objectId, final T target, final Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(target);
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
    public final <T> T updateObject(final String objectId, final T target) {
        Objects.requireNonNull(target);
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
    public final void removeObject(final String objectId) {
        feignClient.removeObject(Objects.requireNonNull(objectId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> List<T> getList(final String listId, final Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectMapper);
        List<ObjectData> objDataList = feignClient.getList(Objects.requireNonNull(listId));
        return objDataList.stream().map(objectMapper).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> List<T> getList(final String listId, final Class<T> objectType) {
        Objects.requireNonNull(objectType);
        List<ObjectData> objDataList = feignClient.getList(Objects.requireNonNull(listId));
        return objDataList.stream().map(objData -> fromJson(objData, objectType)).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> T addToList(final String listId, final String objectId, final T target, final Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectMapper);
        ObjectData objData = feignClient.addToList(Objects.requireNonNull(listId),
                Objects.requireNonNull(objectId), gson.toJson(target));
        return objectMapper.apply(objData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public final <T> T addToList(final String listId, final String objectId, final T target) {
        ObjectData objData = feignClient.addToList(Objects.requireNonNull(listId),
                Objects.requireNonNull(objectId), gson.toJson(target));
        return fromJson(objData, (Class<T>) target.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> T updateInList(final String listId, final String objectId, final T target, final Function<ObjectData, T> objectMapper) {
        Objects.requireNonNull(objectMapper);
        ObjectData objData = feignClient.updateInList(Objects.requireNonNull(listId),
                Objects.requireNonNull(objectId), gson.toJson(target));
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
    public final <T> T updateInList(final String listId, final String objectId, final T target) {
        ObjectData objData = feignClient.updateInList(Objects.requireNonNull(listId),
                Objects.requireNonNull(objectId), gson.toJson(target));
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
    public final void removeFromList(final String listId, final String objectId) {
        feignClient.removeFromList(Objects.requireNonNull(listId),
                Objects.requireNonNull(objectId));
    }

}
