package com.socketserver.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.socketserver.dto.ChatRequest;
import com.socketserver.dto.ChatResponse;
import com.socketserver.dto.TestRequest;
import com.socketserver.dto.TestResponse;

@RestController
public class TestController {
	
	private final SimpMessagingTemplate simpMessagingTemplate;
	
	public TestController(SimpMessagingTemplate simpMessagingTemplate) {
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@MessageMapping("/ping")
	@SendTo("/topic/ping")
	public TestResponse pingCheck(TestRequest message) {
		return TestResponse.builder().data("Received!").build();
	}
	
	@MessageMapping("/chat/{room}")
	@SendTo("/topic/message/{room}")
	public ChatResponse chatMessage(@DestinationVariable String room, ChatRequest request) {
		return ChatResponse.builder()
				.name(request.getName())
				.message(request.getMessage())
				.timeStamp("" + System.currentTimeMillis())
				.build();
	}
	
	@MessageMapping("/privateChat/{room}/{userId}")
	@SendTo("/topic/privateMessage/{room}/{userId}")
	public void privateChatMessage(@DestinationVariable String room, @DestinationVariable String userId,
			ChatRequest request) {
		ChatResponse chatResponse = ChatResponse.builder()
				.name(request.getName())
				.message(request.getMessage())
				.timeStamp("" + System.currentTimeMillis())
				.build();

		simpMessagingTemplate.convertAndSend("/queue/privateChat/" + room, chatResponse);
	}
}
