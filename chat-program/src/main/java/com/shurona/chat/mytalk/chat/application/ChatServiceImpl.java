package com.shurona.chat.mytalk.chat.application;

import static com.shurona.chat.mytalk.chat.common.ChatErrorCode.BAD_REQUEST;
import static java.util.stream.Collectors.toMap;

import com.shurona.chat.mytalk.chat.common.ChatException;
import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.model.ChatUser;
import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import com.shurona.chat.mytalk.chat.domain.validator.ChatRoomValidator;
import com.shurona.chat.mytalk.chat.infrastructure.ChatLogJpaRepository;
import com.shurona.chat.mytalk.chat.infrastructure.ChatRoomJpaRepository;
import com.shurona.chat.mytalk.chat.infrastructure.ChatUserJpaRepository;
import com.shurona.chat.mytalk.chat.presentation.dtos.ChatLogResponseDto;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatServiceImpl implements ChatService {

    // repository
    private final ChatRoomJpaRepository chatRoomRepository;
    private final ChatLogJpaRepository chatLogRepository;
    private final ChatUserJpaRepository chatUserRepository;

    // domain service
    private final ChatRoomValidator chatRoomValidator;

    /*
        // 일단 개인톡 만들고 그룹톡을 만들어봅시다.
        // TODO: 그룹톡 개선해보기
     */
    @Transactional
    @Override
    public ChatRoom createChatRoom(User user, List<User> invitedUserList, RoomType type,
        String name) {

        // 개인룸 검증 툴
        chatRoomValidator.createPrivateChatRoomValidator(user, invitedUserList, type);

        // 생성
        ChatRoom chatRoom = ChatRoom.createChatRoom(name, type, user, invitedUserList);

        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public ChatRoom findChatRoomById(Long id) {
        return chatRoomRepository.findById(id).orElseThrow(
            () -> new ChatException(BAD_REQUEST));
    }

    @Override
    public List<ChatRoom> findChatRoomListByUser(User user) {

        return chatRoomRepository.findByChatUserList_User(user);
    }

    @Transactional
    @Override
    public ChatLog writeChat(ChatRoom room, User user, String chatData, ChatContentType type) {

        // 채팅을 기록할 해도 되는지 검증한다.
        chatRoomValidator.writeChatValidator(room, user);

        // 저장
        return chatLogRepository.save(ChatLog.createLog(room, user, chatData, type));
    }

    @Transactional
    @Override
    public List<ChatLogResponseDto> readChatLog(User user, ChatRoom room, Pageable pageable) {

        List<ChatLog> logs = chatLogRepository.findByRoom(room, pageable);

        Optional<ChatUser> chatUser = chatUserRepository.findByUserAndRoom(user, room);
        if (chatUser.isEmpty()) {
            throw new ChatException(BAD_REQUEST);
        }

        // 최근 읽은 기록 업데이트
        chatUser.get().updateRecentRead(logs.getFirst().getId());

        // 읽지 않은 사람을 계산하려고 합니다.
        Map<Long, Long> userRecentReadMap = chatUserRepository.findByRoom(room).stream()
            .collect(toMap(
                ChatUser::getId,
                ChatUser::getLastReadMessageId
            ));

        // 여기서 읽은 메시지 별로 ResponseDto를 매핑해준다.
        return logs.stream().map((log) -> {
            int unreadCount = (int) userRecentReadMap.values().stream()
                .filter(recentReadId -> recentReadId < log.getId())
                .count();

            return ChatLogResponseDto.of(log, unreadCount);
        }).toList();
    }

    @Override
    public ChatRoom inviteUser(User user, User friend, ChatRoom room) {
        return null;
    }
}
