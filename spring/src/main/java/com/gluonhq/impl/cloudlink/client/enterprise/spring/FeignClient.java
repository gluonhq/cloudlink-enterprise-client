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
package com.gluonhq.impl.cloudlink.client.enterprise.spring;

import com.gluonhq.cloudlink.client.enterprise.domain.ObjectData;
import com.gluonhq.cloudlink.client.enterprise.domain.PushNotification;
import com.gluonhq.cloudlink.client.enterprise.domain.PushNotificationTarget;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

public interface FeignClient {

    String CHARSET = "charset=UTF-8";

    @POST
    @Path("push/enterprise/notification")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    PushNotification sendPushNotification(@FormParam("title") String title,
            @FormParam("body") String body,
            @FormParam("deliveryDate") long deliveryDate,
            @FormParam("priority") PushNotification.Priority priority,
            @FormParam("expirationType") PushNotification.ExpirationType expirationType,
            @FormParam("expirationAmount") int expirationAmount,
            @FormParam("targetType") PushNotificationTarget.Type targetType,
            @FormParam("targetTopic") String targetTopic,
            @FormParam("targetDeviceToken") String targetDeviceToken,
            @FormParam("invisible") @DefaultValue("false") boolean invisible);


    @GET
    @Path("data/enterprise/object/{objectIdentifier}")
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    ObjectData getObject(@PathParam("objectIdentifier") String objectIdentifier);

    @POST //TODO Should should enterprise handler use PUT request internally instead of POST?
    @Consumes(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Path("data/enterprise/object/{objectIdentifier}/add")
    ObjectData addObject(@PathParam("objectIdentifier") String objectIdentifier, String target);


    @POST //TODO should enterprise handler use DELETE request internally instead of POST?
    @Path("data/enterprise/object/{objectIdentifier}/remove")
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    ObjectData removeObject(@PathParam("objectIdentifier") String objectIdentifier);


    @POST
    @Consumes(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Path("data/enterprise/object/{objectIdentifier}/update")
    ObjectData updateObject(@PathParam("objectIdentifier") String objectIdentifier, String target);


    @GET
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Path("data/enterprise/list/{listIdentifier}")
    List<ObjectData> getList(@PathParam("listIdentifier") String listIdentifier);


    @POST
    @Consumes(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Path("data/enterprise/list/{listIdentifier}/add/{objectIdentifier}")
    ObjectData addToList(@PathParam("listIdentifier") String listIdentifier,
            @PathParam("objectIdentifier") String objectIdentifier, String target);


    @POST
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Path("data/enterprise/list/{listIdentifier}/remove/{objectIdentifier}")
    ObjectData removeFromList(@PathParam("listIdentifier") String listIdentifier,
            @PathParam("objectIdentifier") String objectIdentifier);


    @POST
    @Consumes(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Produces(MediaType.APPLICATION_JSON + "; " + CHARSET)
    @Path("data/enterprise/list/{listIdentifier}/update/{objectIdentifier}")
    ObjectData updateInList(@PathParam("listIdentifier") String listIdentifier,
            @PathParam("objectIdentifier") String objectIdentifier,
            String target);
}
