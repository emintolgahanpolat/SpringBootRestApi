package com.emintolgahanpolat.api.configuration.captcha;


import net.jodah.expiringmap.ExpiringMap;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.TransparentBackgroundProducer;
import nl.captcha.noise.CurvedLineNoiseProducer;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;

import java.util.Map;
import java.util.concurrent.TimeUnit;


public class CaptchaService {
    private static final long CAPTCHA_EXPIRY_TIME = 600;
    private static Map<String, Captcha> captchaCodeMap = ExpiringMap.builder().expiration(CAPTCHA_EXPIRY_TIME, TimeUnit.SECONDS).build();
    private int WIDTH = 200;
    private int HEIGHT = 50;


    public Captcha generateCaptcha() {
        Captcha captcha = new Captcha.Builder(WIDTH, HEIGHT)
                .addBackground(new TransparentBackgroundProducer())
                .addText(new DefaultTextProducer(), new DefaultWordRenderer())
                .addNoise(new CurvedLineNoiseProducer()).addBorder().build();
        captchaCodeMap.put(captcha.getAnswer(), captcha);
        return captcha;
    }

    public String generateCaptchaBase64(){
        return CaptchaUtils.encodeBase64(generateCaptcha());
    }

    public boolean validateCaptcha(String captcha) {
        Captcha captcha1 = captchaCodeMap.remove(captcha);
        return captcha1 != null;
    }


}