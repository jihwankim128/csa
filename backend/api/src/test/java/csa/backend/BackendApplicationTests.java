package csa.backend;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import csa.domain.DomainModule;
import csa.infrastructure.InfrastructureModule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void apiModuleCanReferenceDomainAndInfrastructureModules() {
		assertNotNull(DomainModule.class);
		assertNotNull(InfrastructureModule.class);
	}

}
