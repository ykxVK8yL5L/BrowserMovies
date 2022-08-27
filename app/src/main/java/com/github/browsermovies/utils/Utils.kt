package com.github.browsermovies.utils



import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.net.wifi.WifiManager
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import java.io.IOException
import java.io.InputStream
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

/**
 * Created by susnata on 3/17/18.
 */


public class Utils {

    companion object {
        @JvmStatic
        fun convertDpToPixel(ctx: Context, dp: Int): Int {
            val density = ctx.resources.displayMetrics.density
            return Math.round(dp.toFloat() * density)
        }

        /**
         * Will read the content from a given [InputStream] and return it as a [String].
         *
         * @param inputStream The [InputStream] which should be read.
         * @return Returns `null` if the the [InputStream] could not be read. Else
         * returns the content of the [InputStream] as [String].
         */
        @JvmStatic
        fun inputStreamToString(inputStream: InputStream): String? {
            try {
                val bytes = ByteArray(inputStream.available())
                inputStream.read(bytes, 0, bytes.size)
                return String(bytes)
            } catch (e: IOException) {
                return null
            }
        }

        @JvmStatic
        fun getResourceUri(context: Context, resID: Int): Uri {
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.resources.getResourcePackageName(resID) + '/' +
                    context.resources.getResourceTypeName(resID) + '/' +
                    context.resources.getResourceEntryName(resID))
        }

        @JvmStatic
        fun getIpAddress(context: Context): String? {
            val wifiManager =
                context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = wifiManager.connectionInfo
            if (info != null && info.ipAddress == 0) {
                try {
                    val networkInterfaces: Enumeration<*> =
                        NetworkInterface.getNetworkInterfaces()
                    while (networkInterfaces.hasMoreElements()) {
                        val networkInterface =
                            networkInterfaces.nextElement() as NetworkInterface
                        val displayName = networkInterface.displayName
                        if (displayName == "eth0" || displayName == "wlan0") {
                            val inetAddresses: Enumeration<*> =
                                networkInterface.inetAddresses
                            while (inetAddresses.hasMoreElements()) {
                                val inetAddress =
                                    inetAddresses.nextElement() as InetAddress
                                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                    return inetAddress.getHostAddress()
                                }
                            }
                            continue
                        }
                    }
                } catch (e: Throwable) {
                    Log.e("", "获取本地IP出错", e)
                }
                return "0.0.0.0"
            }
            return String.format(
                "%d.%d.%d.%d", *arrayOf<Any>(
                    Integer.valueOf(info!!.ipAddress and 255),
                    Integer.valueOf(info.ipAddress shr 8 and 255),
                    Integer.valueOf(info.ipAddress shr 16 and 255),
                    Integer.valueOf(info.ipAddress shr 24 and 255)
                )
            )
        }


        /**
         * dip转为 px
         */
        @JvmStatic
        fun dip2px(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }

        @JvmStatic
        fun myInputMethodIsActive(context: Context): Boolean {
            for (packageName in (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).enabledInputMethodList) {
                if (packageName.packageName == context.packageName) {
                    return true
                }
            }
            return false
        }

        @JvmStatic
        fun myInputMethodIsDefault(context: Context): Boolean {
            val string = Settings.Secure.getString(
                context.contentResolver,
                "default_input_method"
            )
            return if (string == null || !string.startsWith(context.packageName)) {
                false
            } else true
        }


    }
}
