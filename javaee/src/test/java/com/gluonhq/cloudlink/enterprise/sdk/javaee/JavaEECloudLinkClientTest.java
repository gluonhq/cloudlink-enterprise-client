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

import com.gluonhq.cloudlink.enterprise.sdk.base.CloudLinkClient;
import com.gluonhq.cloudlink.enterprise.sdk.base.CloudLinkClientException;
import com.gluonhq.cloudlink.enterprise.sdk.base.CloudLinkConfig;
import com.gluonhq.cloudlink.enterprise.sdk.base.domain.PushNotification;
import com.gluonhq.cloudlink.enterprise.sdk.base.domain.PushNotificationTarget;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class JavaEECloudLinkClientTest {

    @Test
    public void testValidAuthentication() {
        String identifier = UUID.randomUUID().toString();
        String serverKey = UUID.randomUUID().toString();

        HttpServer httpServer = null;
        try {
            httpServer = startHttpServer(request -> {
                String authorization = request.getHeader("authorization");
                if ( ("Gluon " + serverKey).equals(authorization) ) {
                    request.response().setStatusCode(200)
                            .end("{\"identifier\":\"" + identifier + "\"}");
                } else {
                    request.response().setStatusCode(401).end();
                }
            });

            CloudLinkConfig config = new CloudLinkConfig("http://localhost:45010", serverKey);
            config.setLogLevel(Level.INFO);
            CloudLinkClient client = new JavaEECloudLinkClient(config);

            PushNotification notification = client.sendPushNotification(buildPushNotification());

            Assert.assertNotNull(notification);
            Assert.assertEquals(identifier, notification.getIdentifier());
        } finally {
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }

    private PushNotification buildPushNotification() {
        PushNotification pushNotification = new PushNotification();
        pushNotification.setTitle("Title");
        pushNotification.setBody("Body");
        pushNotification.setPriority(PushNotification.Priority.HIGH);
        pushNotification.setExpirationType(PushNotification.ExpirationType.DAYS);
        pushNotification.setExpirationAmount(5);
        pushNotification.getTarget().setType(PushNotificationTarget.Type.ALL_DEVICES);
        return pushNotification;
    }

    @Test
    public void testInvalidAuthentication() {
        String serverKey = UUID.randomUUID().toString();

        HttpServer httpServer = null;
        try {
            httpServer = startHttpServer(request -> {
                request.response()
                        .putHeader("Content-Type", "application/json; charset=UTF-8")
                        .setStatusCode(401)
                        .end();
            });

            CloudLinkConfig config = new CloudLinkConfig("http://localhost:45010", serverKey);
            config.setLogLevel(Level.INFO);
            CloudLinkClient client = new JavaEECloudLinkClient(config);

            try {
                client.sendPushNotification(buildPushNotification());
                Assert.fail("CloudLinkException must be thrown.");
            } catch (CloudLinkClientException e) {
                Assert.assertEquals(401, e.getStatus());
                Assert.assertEquals("Unauthorized", e.getMessage());
            }
        } finally {
            if (httpServer != null) {
                httpServer.close();
            }
        }
    }

    private HttpServer startHttpServer(Handler<HttpServerRequest> requestHandler) {
        HttpServerResponse httpServerResponse = new HttpServerResponse();

        CountDownLatch latch = new CountDownLatch(1);
        HttpServer httpServer = Vertx.vertx().createHttpServer();
        httpServer.requestHandler(requestHandler);
        httpServer.listen(45010, asyncResult -> {
            httpServerResponse.httpServer = asyncResult.result();
            if (asyncResult.failed()) {
                asyncResult.cause().printStackTrace();
            }
            latch.countDown();
        });

        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }

        return httpServerResponse.httpServer;
    }

    private static class HttpServerResponse {
        HttpServer httpServer;
    }
}
