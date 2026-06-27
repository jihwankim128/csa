plugins {
	id("csa.spring-boot-application")
}

dependencies {
	implementation(project(":domain"))
	implementation(project(":infrastructure"))
	implementation("org.springframework.boot:spring-boot-h2console")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	runtimeOnly("com.h2database:h2")

	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}
