/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lidroid.xutils.http.client;

import com.lidroid.xutils.http.client.callback.RequestCallBackHandler;
import com.lidroid.xutils.http.client.entity.UploadEntity;
import com.lidroid.xutils.http.client.util.URIBuilder;
import com.lidroid.xutils.util.LogUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.CloneUtils;
import org.apache.http.protocol.HTTP;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Author: wyouflf
 * Date: 13-7-26
 * Time: 下午2:49
 */
public class HttpRequest extends HttpRequestBase implements HttpEntityEnclosingRequest {

    private HttpEntity entity;

    private HttpMethod method;

    private URIBuilder uriBuilder;

    public HttpRequest(HttpMethod method) {
        super();
        this.method = method;
    }

    public HttpRequest(HttpMethod method, String uri) {
        super();
        this.method = method;
        setURI(URI.create(uri));
    }

    public HttpRequest(HttpMethod method, URI uri) {
        super();
        this.method = method;
        setURI(uri);
    }

    public HttpRequest addQueryStringParameter(String name, String value) {
        uriBuilder.addParameter(name, value);
        return this;
    }

    public HttpRequest addQueryStringParameter(NameValuePair nameValuePair) {
        uriBuilder.addParameter(nameValuePair.getName(), nameValuePair.getValue());
        return this;
    }

    public HttpRequest addQueryStringParams(List<NameValuePair> nameValuePairs) {
        if (nameValuePairs != null) {
            for (NameValuePair nameValuePair : nameValuePairs) {
                uriBuilder.addParameter(nameValuePair.getName(), nameValuePair.getValue());
            }
        }
        return this;
    }

    public void addHeaders(List<Header> headers) {
        if (headers != null) {
            for (Header header : headers) {
                this.addHeader(header);
            }
        }
    }

    public void setRequestParams(RequestParams param) {
        if (param != null) {
            this.addHeaders(param.getHeaders());
            this.addQueryStringParams(param.getQueryStringParams());
            this.setEntity(param.getEntity());
        }
    }

    public void setRequestParams(RequestParams param, RequestCallBackHandler callBackHandler) {
        if (param != null) {
            this.addHeaders(param.getHeaders());
            this.addQueryStringParams(param.getQueryStringParams());
            HttpEntity entity = param.getEntity();
            if (entity != null) {
                if (entity instanceof UploadEntity) {
                    ((UploadEntity) entity).setCallBackHandler(callBackHandler);
                }
                this.setEntity(entity);
            }
        }
    }

    @Override
    public URI getURI() {
        try {
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            LogUtils.e(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void setURI(URI uri) {
        this.uriBuilder = new URIBuilder(uri);
    }

    @Override
    public String getMethod() {
        return this.method.toString();
    }

    public HttpEntity getEntity() {
        return this.entity;
    }

    public void setEntity(final HttpEntity entity) {
        this.entity = entity;
    }

    public boolean expectContinue() {
        Header expect = getFirstHeader(HTTP.EXPECT_DIRECTIVE);
        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        HttpRequest clone = (HttpRequest) super.clone();
        if (this.entity != null) {
            clone.entity = (HttpEntity) CloneUtils.clone(this.entity);
        }
        return clone;
    }

    public static enum HttpMethod {
        GET("GET"), POST("POST"), PUT("PUT"), HEAD("HEAD"), MOVE("MOVE"), COPY("COPY"), DELETE("DELETE");

        private final String value;

        HttpMethod(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
