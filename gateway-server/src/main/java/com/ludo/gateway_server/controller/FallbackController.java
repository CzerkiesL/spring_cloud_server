package com.ludo.gateway_server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

	// Controller utiliser comme fallback si un server ne repond pas

}
