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
package com.gluonhq.cloudlink.client.enterprise.domain;

/**
 * Represents a single Gluon CloudLink object. The object can be a standalone object or an object that is linked
 * with a list.
 */
public class ObjectData {

    private String uid;
    private String payload;

    /**
     * The unique identifier of the object within your Gluon Application.
     *
     * @return the unique identifier of the object
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the unique identifier of the object.
     *
     * @param uid the unique identifier of the object
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * The payload of the object which is the object serialized into a JSON string.
     *
     * @return the payload of the object
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Sets the payload of the object as a serialized JSON string.
     *
     * @param payload the payload of the object
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectData that = (ObjectData) o;

        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
        return payload != null ? payload.equals(that.payload) : that.payload == null;
    }

    @Override
    public int hashCode() {
        int result = 31 *  (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "ObjectData{" +
                ", uid='" + uid + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
