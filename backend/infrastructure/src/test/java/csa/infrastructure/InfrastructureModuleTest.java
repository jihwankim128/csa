package csa.infrastructure;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import csa.domain.DomainModule;
import org.junit.jupiter.api.Test;

class InfrastructureModuleTest {

	@Test
	void infrastructureModuleCanReferenceDomainModule() {
		assertNotNull(DomainModule.class);
		assertNotNull(InfrastructureModule.class);
	}

}
