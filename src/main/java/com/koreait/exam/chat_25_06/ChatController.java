package com.koreait.exam.chat_25_06;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    private List<ChatMessage> chatMessages = new ArrayList<>();

    public record writeChatMessageResponse(long id, String authorName, String content){

    }

    public record writeChatMessageRequest(String authorName, String content){

    }

    @PostMapping("/writeMessage")
    @ResponseBody
    public RsData<writeChatMessageResponse> writeMessage(@RequestBody writeChatMessageRequest req) {
        ChatMessage message = new ChatMessage(req.authorName,  req.content);

        chatMessages.add(message);

        return new RsData<>(
                "S-1",
                "메시지 작성됨",
               new writeChatMessageResponse(message.getId(), message.getAuthorName(), message.getContent())
        );
    }

    public record messagesRequest(Long fromId) {

    }

    public record messagesResponse(List<ChatMessage> chatMessages, long count) {

    }

    @GetMapping("/messages")
    @ResponseBody
    public RsData<messagesResponse> showMessages(messagesRequest req) {

        List<ChatMessage> messages = chatMessages;

        log.debug("req: {}", req);

        // 번호가 같이 입력되는 경우
        if(req.fromId != null){

            // 해당 번호의 채팅 메시지가 전체 리스트의 몇 번째 리스트인지
            int index = IntStream.range(0, chatMessages.size())
                    .filter(i -> chatMessages.get(i).getId() == req.fromId)
                    .findFirst()
                    .orElse(-1);

            if(index != -1){
                // 만약, index가 -1이 아니라면 -> 0번부터 index번까지 제거한 리스트 생성
                messages = chatMessages.subList(index + 1, chatMessages.size());
            }
        }

        return new RsData<>(
                "S-1",
                "성공",
                new messagesResponse(messages, messages.size())
        );
    }


}
