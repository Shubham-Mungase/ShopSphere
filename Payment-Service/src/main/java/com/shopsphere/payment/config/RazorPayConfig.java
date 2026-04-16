package com.shopsphere.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Configuration
public class RazorPayConfig {

	@Value("${razor.pay.key}")
	private String razorPayKey;
	@Value("${razor.pay.secret}")
	private String razorPaySecret;

	@Bean
	 RazorpayClient razorpayClient()
	{
		
		try {
			return new RazorpayClient(razorPayKey, razorPaySecret);
		} catch (RazorpayException e) {
			e.printStackTrace();
		throw new RuntimeException(e);
		}
	}
}
