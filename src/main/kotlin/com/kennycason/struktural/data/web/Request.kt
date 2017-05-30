package com.kennycason.struktural.data.web

import org.apache.http.Header
import org.apache.http.HttpRequest
import org.apache.http.NameValuePair
import java.net.URI

/**
 * Created by kenny on 5/25/17.
 */
data class Request(val uri: String, // may be path or full uri. Depends on whether or not base_url is set or not.
                   val method: HttpMethod = HttpMethod.GET,
                   val parameters: List<String> = emptyList<String>(),
                   val body: String? = null,
                   val headers: List<Header> = emptyList<Header>())