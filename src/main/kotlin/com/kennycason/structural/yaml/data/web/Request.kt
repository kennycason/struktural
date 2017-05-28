package com.kennycason.structural.yaml.data.web

import org.apache.http.Header
import org.apache.http.NameValuePair
import java.net.URI

/**
 * Created by kenny on 5/25/17.
 */
data class Request(val uri: String,
                   val parameters: List<String> = emptyList<String>(),
                   val body: String?,
                   val headers: List<Header> = emptyList<Header>())