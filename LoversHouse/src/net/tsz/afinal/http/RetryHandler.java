/**
 * Copyright (c) 2012-2013, Michael Yang 鏉ㄧ娴�(www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tsz.afinal.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.os.SystemClock;

public class RetryHandler implements HttpRequestRetryHandler {
    private static final int RETRY_SLEEP_TIME_MILLIS = 1000;
    
    //缃戠粶寮傚父锛岀户缁�    
    private static HashSet<Class<?>> exceptionWhitelist = new HashSet<Class<?>>();
    
    //鐢ㄦ埛寮傚父锛屼笉缁х画锛堝锛岀敤鎴蜂腑鏂嚎绋嬶級
    private static HashSet<Class<?>> exceptionBlacklist = new HashSet<Class<?>>();

    static {
    	exceptionWhitelist.add(NoHttpResponseException.class);
        exceptionWhitelist.add(UnknownHostException.class);
        exceptionWhitelist.add(SocketException.class);

        exceptionBlacklist.add(InterruptedIOException.class);
        exceptionBlacklist.add(SSLHandshakeException.class);
    }

    private final int maxRetries;

    public RetryHandler(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        boolean retry = true;

        Boolean b = (Boolean) context.getAttribute(ExecutionContext.HTTP_REQ_SENT);
        boolean sent = (b != null && b.booleanValue());

        if(executionCount > maxRetries) {
            // 灏濊瘯娆℃暟瓒呰繃鐢ㄦ埛瀹氫箟鐨勬祴璇曪紝榛樿5娆�            retry = false;
        } else if (exceptionBlacklist.contains(exception.getClass())) {
            // 绾跨▼琚敤鎴蜂腑鏂紝鍒欎笉缁х画灏濊瘯
            retry = false;
        } else if (exceptionWhitelist.contains(exception.getClass())) {
            retry = true;
        } else if (!sent) {
            retry = true;
        }

        if(retry) {
            HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute( ExecutionContext.HTTP_REQUEST );
            retry = currentReq!=null && !"POST".equals(currentReq.getMethod());
        }

        if(retry) {
        	//浼戠湢1绉掗挓鍚庡啀缁х画灏濊瘯
            SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
        } else {
            exception.printStackTrace();
        }

        return retry;
    }
    
}