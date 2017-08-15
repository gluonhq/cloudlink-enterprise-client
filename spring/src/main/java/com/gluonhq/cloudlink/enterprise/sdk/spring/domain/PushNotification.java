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

import com.gluonhq.impl.cloudlink.enterprise.sdk.spring.validation.MaxPushNotificationExpiration;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Contains the information to use when sending a push notification.
 */
@MaxPushNotificationExpiration
public class PushNotification {

    private String identifier;
    private long creationDate;
    private String customIdentifier = "";
    private String title = "";
    private String body = "";
    private long deliveryDate = 0;
    private Priority priority = Priority.NORMAL;
    private ExpirationType expirationType = ExpirationType.WEEKS;
    private int expirationAmount = 4;
    private PushNotificationTarget target = new PushNotificationTarget();
    private boolean invisible = false;

    /**
     * Returns <code>true</code> to send a silent push notification.
     *
     * @return true if the push notification should be silent
     */
    public boolean isInvisible() {
        return invisible;
    }

    /**
     * Set the value to <code>true</code> to enable silent push notifications. Silent push
     * notifications will not trigger a visual notification on the device when the push
     * notification arrives.
     *
     * @param invisible <code>true</code> for enabling, <code>false</code> for disabling silent push notifications
     */
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    /**
     * Returns the internal unique identifier for this push notification.
     *
     * @return the unique identifier for the push notification
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the internal unique identifier for this push notification. The identifier is not needed when sending
     * the initial push notification. The value will be set automatically by Gluon CloudLink in the returned push
     * notification after sending has completed.
     *
     * @param identifier the unique identifier for the push notification
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the timestamp when the push notification is created as an epoch time in milliseconds.
     *
     * @return the creation date of the push notification
     */
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the timestamp when the push notification is created as an epoch time in milliseconds. The creation
     * date will be set automatically by Gluon CloudLink in the returned push notification after sending has
     * completed.
     *
     * @param creationDate the creation date of the push notification
     */
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Returns the custom identifier for the push notification.
     *
     * @return the custom identifier for the push notification
     */
    @NotNull
    public String getCustomIdentifier() {
        return customIdentifier;
    }

    /**
     * Sets the custom identifier for the push notification.
     *
     * @param customIdentifier the custom identifier of the push notification
     */
    public void setCustomIdentifier(String customIdentifier) {
        this.customIdentifier = customIdentifier;
    }

    /**
     * Returns the title for the push notification.
     *
     * @return the title of the push notification
     */
    @NotNull
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title for the push notification.
     *
     * @param title the title of the push notification
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the body for the push notification.
     *
     * @return the body of the push notification
     */
    @NotNull
    public String getBody() {
        return body;
    }

    /**
     * Sets the body for the push notification.
     *
     * @param body the body of the push notification
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Returns the timestamp when the push notification must be delivered as an epoch time in milliseconds.
     * <p><b>Note:</b> this is currently not supported.</p>
     *
     * @return the timestamp when the push notification must be delivered
     */
    @Min(0)
    public long getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * Sets the timestamp when the push notification must be delivered as an epoch time in milliseconds
     * <p><b>Note:</b> this is currently not supported.</p>
     *
     * @param deliveryDate the timestamp when the push notification must be delivered
     */
    public void setDeliveryDate(long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * Returns the priority for the push notification.
     *
     * @return the priority of the push notification
     */
    @NotNull
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the priority for the push notification.
     *
     * @param priority the priority of the push notification
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Returns the expiration type of the push notification.
     *
     * @return the expiration type
     */
    @NotNull
    public ExpirationType getExpirationType() {
        return expirationType;
    }

    /**
     * Sets the expiration type of the push notification that is used in combination with the expiration amount
     * to calculate the expiration time.
     *
     * @param expirationType the expiration type of the push notification
     */
    public void setExpirationType(ExpirationType expirationType) {
        this.expirationType = expirationType;
    }

    /**
     * Returns the expiration amount of the push notification.
     *
     * @return the expiration amount
     */
    public int getExpirationAmount() {
        return expirationAmount;
    }

    /**
     * Sets the expiration amount of the push notification that is used in combination with the expiration type
     * to calculate the expiration time.
     *
     * @param expirationAmount the expiration amount of the push notification
     */
    public void setExpirationAmount(int expirationAmount) {
        this.expirationAmount = expirationAmount;
    }

    /**
     * Returns the targets where the push notification needs to be sent to.
     *
     * @return the targets to send the push notification to
     */
    @Valid @NotNull
    public PushNotificationTarget getTarget() {
        return target;
    }

    /**
     * Sets the targets where the push notification needs to be sent to.
     *
     * @param target the targets to send the push notification to
     */
    public void setTarget(PushNotificationTarget target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PushNotification that = (PushNotification) o;

        return identifier != null ? identifier.equals(that.identifier) : that.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    /**
     * The type of the push notification priority.
     */
    public enum Priority {
        /**
         * Send the push notification with high priority. The underlying push notification service will try
         * to deliver the push notification immediately. This can result in higher battery drain.
         */
        HIGH,
        /**
         * Send the push notification with normal priority. The underlying push notification service might delay
         * the delivery to conserve battery. This priority should be used for less time-sensitive messages.
         */
        NORMAL
    }

    /**
     * The type of the push notification expiration.
     */
    public enum ExpirationType {
        /**
         * The expiration amount is defined in weeks. The maximum value for expiration amount is 4.
         */
        WEEKS(4),
        /**
         * The expiration amount is defined in days. The maximum value for expiration amount is 7.
         */
        DAYS(7),
        /**
         * The expiration amount is defined in hours. The maximum value for expiration amount is 24.
         */
        HOURS(24),
        /**
         * The expiration amount is defined in minutes. The maximum value for expiration amount is 60.
         */
        MINUTES(60);

        private int maxAmount;

        ExpirationType(int maxAmount) {
            this.maxAmount = maxAmount;
        }

        public int getMaxAmount() {
            return maxAmount;
        }
    }

    @Override
    public String toString() {
        return "PushNotification{" +
                "identifier='" + identifier + '\'' +
                ", customIdentifier='" + customIdentifier + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", deliveryDate=" + deliveryDate +
                ", priority=" + priority +
                ", expirationType=" + expirationType +
                ", expirationAmount=" + expirationAmount +
                ", target=" + target +
                ", invisible=" + invisible +
                '}';
    }
}
