package csa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void applicationEntryPointStaysAtRootPackage() {
		assertEquals("csa", BackendApplication.class.getPackageName());
	}

}
