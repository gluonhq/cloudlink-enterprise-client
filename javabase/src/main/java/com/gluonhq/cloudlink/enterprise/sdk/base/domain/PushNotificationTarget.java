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
package com.gluonhq.cloudlink.enterprise.sdk.base.domain;

import com.gluonhq.impl.cloudlink.enterprise.sdk.base.validation.ValidPushNotificationTarget;

import javax.validation.constraints.NotNull;

/**
 * Defines the devices that the push notification will be sent to.
 */
@ValidPushNotificationTarget
public class PushNotificationTarget {

    private Type type = Type.ALL_DEVICES;
    private String topic = "";
    private String deviceToken = "";

    /**
     * Returns the type of the target.
     *
     * @return the type of the target
     */
    @NotNull
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of the target.
     *
     * @param type the type of the target.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the name of the topic where a device must be subscribed to in order to
     * receive the push notification.
     *
     * @return the name of the topic to set as the target
     */
    @NotNull
    public String getTopic() {
        return topic;
    }

    /**
     * Sets the name of the topic where a device must be subscribed to in order to
     * receive the push notification. The value is ignored if the target type doesn't match
     * {@link Type#TOPIC}.
     *
     * @param topic the name of the topic to set as the target
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Returns the device token of the device where the push notification must be sent to.
     *
     * @return the device token to set as the target
     */
    @NotNull
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     * Sets the device token of the device where the push notification must be sent to. The value must be
     * the unique identifier of the device as is retrieved with the Charm Down device service, using the
     * <a href="http://docs.gluonhq.com/charm/javadoc/4.3.5/com/gluonhq/charm/down/plugins/DeviceService.html#getUuid--">getUuid()</a>
     * method. The value is ignored if the target type doesn't match {@link Type#SINGLE_DEVICE}.
     *
     * @param deviceToken the device token to set as the target
     */
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    /**
     * The type of the push notification target.
     */
    public enum Type {
        /**
         * Targets all devices.
         */
        ALL_DEVICES,
        /**
         * Target all devices that are subscribed to a defined topic.
         */
        TOPIC,
        /**
         * Target a single device that matches a defined device token.
         */
        SINGLE_DEVICE
    }
}
