package com.kennycason.struktural.data.web

import org.apache.http.Header

/**
 * Created by kenny on 5/25/17.
 */
data class Request(
    val uri: String, // may be path or full uri. Depends on whether base_url is set or not.
    val method: HttpMethod = HttpMethod.GET,
    val parameters: List<String> = listOf(),
    val body: String? = null,
    val headers: List<Header> = listOf()
)
