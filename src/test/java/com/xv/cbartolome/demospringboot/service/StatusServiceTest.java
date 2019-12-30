package com.xv.cbartolome.demospringboot.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "status.name=status-test" })
class StatusServiceTest {

	@Autowired
	private StatusService service;

	@Test
	void getStatusTest() {
		// given

		// when
		final String value = service.getStatus();

		// then
		assertThat(value).isEqualTo("status-test");
	}

}