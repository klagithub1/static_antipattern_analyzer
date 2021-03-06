/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Responsible for merging an anonymous cart with the currently logged in user's cart
 * 
 * @see {@link BroadleafAuthenticationSuccessHandler}
 * @deprecated this has been replaced by invoking the merge cart service explicitly within the cart state request processor
 */
@Deprecated
public interface MergeCartProcessor {

    /**
     * Convenience method. This will wrap the given <b>request</b> and <b>response</b> inside of a {@link ServletWebRequest}
     * and forward to {@link #execute(WebRequest, Authentication)}
     * 
     * @param request
     * @param response
     * @param authResult
     */
    public void execute(HttpServletRequest request, HttpServletResponse response, Authentication authResult);
    
    /**
     * Merge the cart owned by the anonymous current session {@link Customer} with the {@link Customer} that has just
     * logged in
     * 
     * @param request
     * @param authResult
     */
    public void execute(WebRequest request, Authentication authResult);

}
