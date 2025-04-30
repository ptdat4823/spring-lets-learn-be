package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.ConversationDTO;
import com.letslive.letslearnbackend.security.JwtTokenVo;
import com.letslive.letslearnbackend.security.SecurityUtils;
import com.letslive.letslearnbackend.services.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user/conversation")
@RequiredArgsConstructor
public class MessageController {
    private final ConversationService conversationService;

    @GetMapping
    public List<ConversationDTO> getMessages() {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return conversationService.getAllByUserId(vo.getUserID());
    }

    @PostMapping()
    public ConversationDTO createOrGetConversation(@RequestParam(required = true) UUID otherUserId) {
        JwtTokenVo vo = SecurityUtils.GetJwtTokenVoFromPrinciple(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return conversationService.getOrCreateConversation(vo.getUserID(), otherUserId);
    }
}
