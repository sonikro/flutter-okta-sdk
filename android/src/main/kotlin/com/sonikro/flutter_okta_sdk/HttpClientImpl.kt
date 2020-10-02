package com.sonikro.flutter_okta_sdk

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import com.okta.oidc.BuildConfig
import com.okta.oidc.net.ConnectionParameters
import com.okta.oidc.net.OktaHttpClient
import com.okta.oidc.net.request.TLSSocketFactory
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.HttpsURLConnection


class HttpClientImpl internal constructor(private val userAgentTemplate: String) : OktaHttpClient {
    var urlConnection: HttpURLConnection? = null
        private set

    /*
     * TLS v1.1, v1.2 in Android supports starting from API 16.
     * But it enabled by default starting from API 20.
     * This method enable these TLS versions on API < 20.
     * */
    @SuppressLint("RestrictedApi")
    private fun enableTlsV1_2(urlConnection: HttpURLConnection) {
        try {
            (urlConnection as HttpsURLConnection).sslSocketFactory = TLSSocketFactory()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Cannot create SSLContext.", e)
        } catch (e: KeyManagementException) {
            throw RuntimeException("Cannot create SSLContext.", e)
        }
    }

    private val userAgent: String
        private get() {
            val sdkVersion = "okta-oidc-android/" + BuildConfig.VERSION_NAME
            return userAgentTemplate.replace("\$UPSTREAM_SDK", sdkVersion)
        }

    @Throws(IOException::class)
    protected fun openConnection(url: URL, params: ConnectionParameters): HttpURLConnection {
        val conn = url.openConnection() as HttpURLConnection
        if (urlConnection is HttpsURLConnection &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            enableTlsV1_2(urlConnection as HttpsURLConnection)
        }
        conn.connectTimeout = params.connectionTimeoutMs()
        conn.readTimeout = params.readTimeOutMs()
        conn.instanceFollowRedirects = false
        val requestProperties = params.requestProperties()
        val userAgent = userAgent
        requestProperties!![ConnectionParameters.USER_AGENT] = userAgent
        if (requestProperties != null) {
            for (property in requestProperties.keys) {
                conn.setRequestProperty(property, requestProperties[property])
            }
        }
        val requestMethod = params.requestMethod()
        val postParameters = params.postParameters()
        conn.requestMethod = requestMethod.name
        if (requestMethod == ConnectionParameters.RequestMethod.GET) {
            conn.doInput = true
        } else if (requestMethod == ConnectionParameters.RequestMethod.POST) {
            conn.doOutput = true
            if (postParameters != null && !postParameters.isEmpty()) {
                val out = DataOutputStream(conn.outputStream)
                out.write(params.encodedPostParameters)
                out.close()
            }
        }
        return conn
    }

    @Throws(Exception::class)
    override fun connect(uri: Uri, params: ConnectionParameters): InputStream? {
        urlConnection = openConnection(URL(uri.toString()), params)
        urlConnection!!.connect()
        return try {
            urlConnection!!.inputStream
        } catch (e: IOException) {
            urlConnection!!.errorStream
        }
    }

    override fun cleanUp() {
        urlConnection = null
    }

    override fun cancel() {
        if (urlConnection != null) {
            urlConnection!!.disconnect()
        }
    }

    override fun getHeaderFields(): Map<String, List<String>> {
        return if (urlConnection != null) {
            urlConnection!!.headerFields
        } else mapOf()
    }

    override fun getHeader(header: String): String {
        return if (urlConnection != null) {
            urlConnection!!.getHeaderField(header)
        } else ""
    }

    @Throws(IOException::class)
    override fun getResponseCode(): Int {
        return if (urlConnection != null) {
            urlConnection!!.responseCode
        } else -1
    }

    override fun getContentLength(): Int {
        return if (urlConnection != null) {
            urlConnection!!.contentLength
        } else -1
    }

    @Throws(IOException::class)
    override fun getResponseMessage(): String {
        return if (urlConnection != null) {
            urlConnection!!.responseMessage
        } else ""
    }

}