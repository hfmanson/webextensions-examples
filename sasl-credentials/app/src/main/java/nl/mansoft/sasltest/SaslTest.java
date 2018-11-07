/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mansoft.sasltest;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslServerFactory;

/**
 *
 * @author hfman
 */
public class SaslTest {
    public static void showSaslServerFactories() {
        Enumeration<SaslServerFactory> serverFactories = Sasl.getSaslServerFactories();
        while (serverFactories.hasMoreElements()) {
            SaslServerFactory serverFactory = serverFactories.nextElement();
            System.out.println(serverFactory.getClass().getName());
            for (String mechanism : serverFactory.getMechanismNames(null)) {
                System.out.println(mechanism);
            }
        }
    }

    public static void processCallbacks(String who, Callback[] callbacks) {
        System.out.println(who);
        for (Callback callback : callbacks) {
            System.out.println(callback.getClass().getName());
            if (callback instanceof NameCallback) {
                NameCallback nameCallback = (NameCallback) callback;
                nameCallback.setName(nameCallback.getDefaultName());
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback passwordCallback = (PasswordCallback) callback;
                passwordCallback.setPassword(new char[] { '1', '2', '3', '4' });
            } else if (callback instanceof RealmCallback) {
                RealmCallback realmCallback = (RealmCallback) callback;
                realmCallback.setText(realmCallback.getDefaultText());
            } else if (callback instanceof AuthorizeCallback) {
                ((AuthorizeCallback) callback).setAuthorized(true);
            }
        }
    }

    public static String stringChallengeOrResponse(byte[] data) {
        return data == null ? "(null)" : new String(data);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            showSaslServerFactories();
            String mechanism = "DIGEST-MD5";
            //String mechanism = "CRAM-MD5";
            //String mechanism = "GSSAPI";
            SaslServer ss = Sasl.createSaslServer(mechanism, "http", "localhost", null, new CallbackHandler() {
                @Override
                public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    processCallbacks("server", callbacks);
                }
            });
            SaslClient sc = Sasl.createSaslClient(new String[] { mechanism }, "henri", "http", "localhost", null, new CallbackHandler() {
                @Override
                public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    processCallbacks("client", callbacks);
                }
            });
            byte[] response = new byte[0];
            while (!ss.isComplete()) {
                byte[] challenge = ss.evaluateResponse(response);
                System.out.println("challenge: " + stringChallengeOrResponse(challenge));
                response = sc.evaluateChallenge(challenge);
                System.out.println("response: " + stringChallengeOrResponse(response));
            }
        } catch (SaslException ex) {
            Logger.getLogger(SaslTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
