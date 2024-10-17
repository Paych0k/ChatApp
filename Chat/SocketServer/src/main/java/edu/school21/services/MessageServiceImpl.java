package edu.school21.services;

import edu.school21.models.Message;
import edu.school21.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void saveMessage(String text, Long usernameId, Long chatRoomId, LocalDateTime localDateTime) {
        Message message = new Message(null, usernameId, text, localDateTime, chatRoomId);
        messageRepository.save(message);
    }

    public List<Message> getRecentMessages(Long chatRoomId, int limit) {
        return messageRepository.findRecentMessagesByChatRoomId(chatRoomId, limit);
    }
}
