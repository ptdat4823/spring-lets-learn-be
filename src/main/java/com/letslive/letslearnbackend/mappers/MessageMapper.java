package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.GetMessageDTO;
import com.letslive.letslearnbackend.entities.Message;

public class MessageMapper {
    public static GetMessageDTO mapToGetMessageDTO(Message message) {
        GetMessageDTO.GetMessageDTOBuilder builder = GetMessageDTO.builder();
        builder.id(message.getId());
        builder.sender(UserMapper.mapToDTO(message.getSender()));
        builder.timestamp(message.getTimestamp());
        builder.content(message.getContent());
        return builder.build();
    }
}
