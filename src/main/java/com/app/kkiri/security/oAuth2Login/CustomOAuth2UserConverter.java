package com.app.kkiri.security.oAuth2Login;

public interface CustomOAuth2UserConverter<T, R> {

	R convert(T t);
}
