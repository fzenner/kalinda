package com.fzenner.jaccess;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.fzenner.datademo.entity.taco.TacoAssistant;
import com.kewebsi.util.EnumUtils;

@SpringBootTest
class JaccessApplicationTests {

	@Test
	void contextLoads() {
	}
	
	
	public static final void myTest() {
		var result = EnumUtils.getEnumFromString(TacoAssistant.TacoFields.tacoId, "tacoName");
		System.out.println(result);
	}

}
