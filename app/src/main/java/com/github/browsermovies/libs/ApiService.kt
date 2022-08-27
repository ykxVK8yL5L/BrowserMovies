package com.github.browsermovies.libs

import javax.net.ssl.X509TrustManager


class ApiService {
    fun provideTrustManager(): X509TrustManager {
        // 自定义一个信任所有证书的TrustManager
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>?,authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>?,authType: String?
            ) {

            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        }
    }

    fun DefaultSSLSocketFactory():SSLSocketFactoryCompat{
        return   SSLSocketFactoryCompat(provideTrustManager())
    }

    fun DefaultUserAgent():String{
        return   "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36"
    }

    fun MobileUserAgent():String{
        return   "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0_2 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Version/8.0 Mobile/12A366 Safari/600.1.4"
    }



}