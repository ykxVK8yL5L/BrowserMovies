package com.github.browsermovies.libs

import android.os.Build
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.GeneralSecurityException
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * @author TongChen
 * @date 2019/10/29  11:34
 * <p>
 * Desc: 使OkHttp在5.0以下也支持TLS1.1、TLS1.2
 */
class SSLSocketFactoryCompat(tm: X509TrustManager?) : SSLSocketFactory() {
    private var defaultFactory: SSLSocketFactory? = null

    init {
        try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, if (tm != null) arrayOf(tm) else null, null)
            defaultFactory = sslContext.socketFactory
        } catch (e: GeneralSecurityException) {
            throw AssertionError() // The system has no TLS. Just give up.
        }

    }

    private fun upgradeTLS(ssl: SSLSocket) {
        // GankResult 5.0+ (API level21) provides reasonable default settings
        // but it still allows SSLv3
        // https://developer.android.com/about/versions/android-5.0-changes.html#ssl
        if (protocols != null) {
            ssl.enabledProtocols =
                protocols
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && cipherSuites != null) {
            ssl.enabledCipherSuites =
                cipherSuites
        }
    }

    override fun getDefaultCipherSuites(): Array<String>? {
        return cipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String>? {
        return cipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        val ssl = defaultFactory!!.createSocket(s, host, port, autoClose)
        if (ssl is SSLSocket)
            upgradeTLS(ssl)
        return ssl
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int): Socket {
        val ssl = defaultFactory!!.createSocket(host, port)
        if (ssl is SSLSocket)
            upgradeTLS(ssl)
        return ssl
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(
        host: String,
        port: Int,
        localHost: InetAddress,
        localPort: Int
    ): Socket {
        val ssl = defaultFactory!!.createSocket(host, port, localHost, localPort)
        if (ssl is SSLSocket)
            upgradeTLS(ssl)
        return ssl
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket {
        val ssl = defaultFactory!!.createSocket(host, port)
        if (ssl is SSLSocket)
            upgradeTLS(ssl)
        return ssl
    }

    @Throws(IOException::class)
    override fun createSocket(
        address: InetAddress,
        port: Int,
        localAddress: InetAddress,
        localPort: Int
    ): Socket {
        val ssl = defaultFactory!!.createSocket(address, port, localAddress, localPort)
        if (ssl is SSLSocket)
            upgradeTLS(ssl)
        return ssl
    }

    companion object {
        // GankResult 5.0+ (API level21) provides reasonable default settings
        // but it still allows SSLv3
        // https://developer.android.com/about/versions/android-5.0-changes.html#ssl
        internal var protocols: Array<String>? = null
        internal var cipherSuites: Array<String>? = null

        init {
            try {
                val socket = SSLSocketFactory.getDefault().createSocket() as SSLSocket
                if (socket != null) {
                    /* set reasonable protocol versions */
                    // - enable all supported protocols (enables TLSv1.1 and TLSv1.2 on GankResult <5.0)
                    // - remove all SSL versions (especially SSLv3) because they're insecure now
                    val protocols = LinkedList<String>()
                    for (protocol in socket.supportedProtocols)
                        if (!protocol.toUpperCase().contains("SSL"))
                            protocols.add(protocol)
                    Companion.protocols = protocols.toTypedArray()
                    /* set up reasonable cipher suites */
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        // choose known secure cipher suites
                        val allowedCiphers = Arrays.asList(
                            // TLS 1.2
                            "TLS_RSA_WITH_AES_256_GCM_SHA384",
                            "TLS_RSA_WITH_AES_128_GCM_SHA256",
                            "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                            "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                            "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
                            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
                            "TLS_ECHDE_RSA_WITH_AES_128_GCM_SHA256",
                            // maximum interoperability
                            "TLS_RSA_WITH_3DES_EDE_CBC_SHA",
                            "TLS_RSA_WITH_AES_128_CBC_SHA",
                            // additionally
                            "TLS_RSA_WITH_AES_256_CBC_SHA",
                            "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
                            "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
                            "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",
                            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA"
                        )
                        val availableCiphers = Arrays.asList(*socket.supportedCipherSuites)
                        // take all allowed ciphers that are available and put them into preferredCiphers
                        val preferredCiphers = HashSet(allowedCiphers)
                        preferredCiphers.retainAll(availableCiphers)
                        /* For maximum security, preferredCiphers should *replace* enabled ciphers (thus disabling
                     * ciphers which are enabled by default, but have become unsecure), but I guess for
                     * the security level of DAVdroid and maximum compatibility, disabling of insecure
                     * ciphers should be a server-side task */
                        // add preferred ciphers to enabled ciphers
                        preferredCiphers.addAll(HashSet(Arrays.asList(*socket.enabledCipherSuites)))
                        cipherSuites = preferredCiphers.toTypedArray()
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

        }
    }
}