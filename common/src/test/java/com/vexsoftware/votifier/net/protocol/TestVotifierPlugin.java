package com.vexsoftware.votifier.net.protocol;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.net.VotifierSession;
import com.vexsoftware.votifier.platform.LoggingAdapter;
import com.vexsoftware.votifier.platform.VotifierPlugin;
import com.vexsoftware.votifier.platform.scheduler.VotifierScheduler;
import com.vexsoftware.votifier.util.KeyCreator;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TestVotifierPlugin implements VotifierPlugin {
    public static final TestVotifierPlugin I = new TestVotifierPlugin();
    private static final byte[] PUBLIC_KEY;
    private static final byte[] PRIVATE_KEY;

    static {
        try {
            PUBLIC_KEY = r("/public.key");
            PRIVATE_KEY = r("/private.key");
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private final Map<String, Key> keyMap = new HashMap<>();
    private final KeyPair keyPair;

    public TestVotifierPlugin() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                    PUBLIC_KEY);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                    PRIVATE_KEY);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            keyPair = new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        keyMap.put("default", KeyCreator.createKeyFrom("test"));
    }

    static byte[] r(String u) throws Exception {
        URL resourceUrl = TestVotifierPlugin.class.getResource(u);
        Path resourcePath = Paths.get(resourceUrl.toURI());

        return Base64.getDecoder().decode(new String(Files.readAllBytes(resourcePath), StandardCharsets.UTF_8));
    }

    public static TestVotifierPlugin getI() {
        return I;
    }

    @Override
    public Map<String, Key> getTokens() {
        return keyMap;
    }

    @Override
    public KeyPair getProtocolV1Key() {
        return keyPair;
    }

    @Override
    public LoggingAdapter getPluginLogger() {
        return null;
    }

    @Override
    public VotifierScheduler getScheduler() {
        return null;
    }

    @Override
    public boolean isDebug() {
        return true;
    }

    public void specificKeysOnly() {
        keyMap.clear();
        keyMap.put("Test", KeyCreator.createKeyFrom("test"));
    }

    public void restoreDefault() {
        keyMap.clear();
        keyMap.put("default", KeyCreator.createKeyFrom("test"));
    }

    @Override
    public void onVoteReceived(Vote vote, VotifierSession.ProtocolVersion protocolVersion, String remoteAddress) throws Exception {

    }
}
