package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.ConversationDTO;
import com.letslive.letslearnbackend.entities.Conversation;

public class ConversationMapper {
    public static ConversationDTO toDTO(Conversation conversation) {
        ConversationDTO.ConversationDTOBuilder builder = ConversationDTO.builder();
        builder.id(conversation.getId());
        builder.user1(UserMapper.mapToDTO(conversation.getUser1()));
        builder.user2(UserMapper.mapToDTO(conversation.getUser2()));
        builder.messages(conversation.getMessages().stream().map(MessageMapper::mapToGetMessageDTO).toList());
        return builder.build();
    }
}
