package com.shortener.shortener;

import com.shortener.shortener.dto.UrlLongRequestDTO;
import com.shortener.shortener.repository.ManagementRepository;
import com.shortener.shortener.service.ManagementService;
import com.shortener.shortener.service.RedirectService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class ShortenerApplicationTests {

	@InjectMocks
	private ManagementService managementService;
	@Mock
	private ManagementRepository managementRepository;
	@Mock
	private RedirectService redirectService;

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void CreateShortUrlTest(){
		UrlLongRequestDTO request = new UrlLongRequestDTO();
		request.setLongUrl("https://www.google.com/");
		var shortUrl = managementService.createShortUrl(request);
		Assert.assertEquals(shortUrl.getHashIdentification(), "cac87a2c");
	}

	// more tests..
}
