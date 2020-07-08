package com.yanlong.im.utils.socket;

import android.text.TextUtils;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateExpiredException;
import javax.security.cert.CertificateNotYetValidException;

public class EasyX509TrustManager implements X509TrustManager {
    private X509TrustManager standardTrustManager = null;

    public EasyX509TrustManager(KeyStore keystore)
            throws NoSuchAlgorithmException, KeyStoreException {
        super();
        try {
            TrustManagerFactory factory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init(keystore);
            TrustManager[] trustmanagers = factory.getTrustManagers();
            if (trustmanagers.length == 0) {
                throw new NoSuchAlgorithmException(
                        "SunX509 trust manager not supported");
            }
            this.standardTrustManager = (X509TrustManager) trustmanagers[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void checkClientTrusted(X509Certificate[] certificates,
                                   String authType) throws CertificateException {
        this.standardTrustManager.checkClientTrusted(certificates, authType);
    }

    public void checkServerTrusted(X509Certificate[] certificates,
                                   String authType) throws CertificateException {
        try {
            if ((certificates != null) && (certificates.length == 1)) {
                X509Certificate certificate = certificates[0];
                certificate.checkValidity();
            } else {
                this.standardTrustManager.checkServerTrusted(certificates, authType);
            }
        } catch (Exception e) {
            Throwable t = e;
            String cause = "";
            if (t.getCause() != null) {
                cause = t.getCause().toString();
            }
            //证书过期，不管
            while (t != null) {
                if (t instanceof CertificateExpiredException || t instanceof CertificateNotYetValidException) {
                    return;
                } else if (!TextUtils.isEmpty(cause) && cause.contains("ExtCertPathValidatorException")) {
                    return;
                }
            }
            throw e;
        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return this.standardTrustManager.getAcceptedIssuers();
    }
}
