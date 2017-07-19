package ru.alvioneurope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController

public class HelloController {
	@Autowired
	private CallTrackingService callTrackingService;

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public  @ResponseBody  String printWelcome() {
		return "hello";
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody String printInfo() {
		String res="Result :";
		Optional<DCTInfo> info0 = callTrackingService.getDCTUserInfoByDynamicNumber("7007374958784576");
		res+="\n{74958784576:"+info0.toString()+"}"+"\n";
		Optional<DCTInfo> info1 = callTrackingService.getDCTUserInfoByDynamicNumber("74958784576");
		res+="\n{74958784576:"+info1.toString()+"}"+"\n";
		Optional<DCTInfo> info3 = callTrackingService.getDCTUserInfoByDynamicNumber("74994506632");
		res+="\n{74994904112:"+info3.toString()+"}"+"\n";

		return res;
	}
}

