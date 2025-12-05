package com.popjub.aiservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

public class KeyTest {

	@Value("${gemini.api-key}")
	String key;

	@Test
	void printKey() {
		System.out.println(key);
	}
}
