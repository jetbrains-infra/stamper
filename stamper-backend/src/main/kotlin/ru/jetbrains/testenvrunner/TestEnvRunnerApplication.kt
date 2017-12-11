package ru.jetbrains.testenvrunner

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso
import org.springframework.scheduling.annotation.EnableScheduling
import java.net.Socket
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*


@EnableScheduling
@SpringBootApplication
@EnableOAuth2Sso
class TestEnvRunnerApplication

fun main(args: Array<String>) {
    disableSSLSertificateCheck()
    SpringApplication.run(TestEnvRunnerApplication::class.java, *args)
}

private fun disableSSLSertificateCheck() {
    val trustAllCerts = disableSalSilicateCheck()
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
}

private fun disableSalSilicateCheck(): Array<TrustManager> {
    return arrayOf(object : X509ExtendedTrustManager() {

        override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate>? {
            return null
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String, socket: Socket) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String, socket: Socket) {
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String, sslEngine: SSLEngine) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String, sslEngine: SSLEngine) {
        }
    })
}

