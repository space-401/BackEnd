package com.app.kkiri.security.configurer;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

public final class CustomSecurityContextConfigurer <H extends HttpSecurityBuilder<H>>
	extends AbstractHttpConfigurer<CustomSecurityContextConfigurer<H>, H> {

	private boolean requireExplicitSave;

	public CustomSecurityContextConfigurer() {
	}

	public CustomSecurityContextConfigurer<H> securityContextRepository(SecurityContextRepository securityContextRepository) {
		getBuilder().setSharedObject(SecurityContextRepository.class, securityContextRepository);
		return this;
	}

	public CustomSecurityContextConfigurer<H> requireExplicitSave(boolean requireExplicitSave) {
		this.requireExplicitSave = requireExplicitSave;
		return this;
	}

	public boolean isRequireExplicitSave() {
		return this.requireExplicitSave;
	}

	public SecurityContextRepository getSecurityContextRepository() {
		SecurityContextRepository securityContextRepository = getBuilder()
			.getSharedObject(SecurityContextRepository.class);
		if (securityContextRepository == null) {
			securityContextRepository = new HttpSessionSecurityContextRepository();
		}
		return securityContextRepository;
	}
}
