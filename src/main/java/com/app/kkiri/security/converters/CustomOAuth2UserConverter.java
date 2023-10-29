package com.app.kkiri.security.converters;

public interface CustomOAuth2UserConverter<T, R> {

	R convert(T t);
}
