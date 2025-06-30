package com.shurona.chat.mytalk.chat.application;

import static com.shurona.chat.mytalk.chat.common.ChatErrorCode.BAD_REQUEST;
import static java.util.stream.Collectors.toMap;

import com.shurona.chat.mytalk.chat.application.cache.ChatCacheInfo;
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
import com.shurona.chat.mytalk.chat.presentation.dto.ChatLogResponseDto;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.ArrayList;
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

    private final ChatCacheInfo chatCacheInfo;

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

        List<User> withUsers = new ArrayList<>(invitedUserList);
        withUsers.add(user);

//        // 이미 존재하는지 확인한다.
        List<ChatRoom> privateRoomsWithUsers = chatRoomRepository.findPrivateRoomContainingExactUsers(
            withUsers, type, withUsers.size());
        if (type.equals(RoomType.PRIVATE) && !privateRoomsWithUsers.isEmpty()) {
//            System.out.println(privateRoomsWithUsers.size());
            return privateRoomsWithUsers.getFirst();
        }

        // 개인룸 검증 툴
        chatRoomValidator.createPrivateChatRoomValidator(user, invitedUserList, type);

        // 생성
        ChatRoom chatRoom = ChatRoom.createChatRoom(name, type, user, invitedUserList);

        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public ChatRoom findPrivateChatRoomByUser(User user, User partner) {

//        Optional<ChatRoom> room = chatRoomRepository.findByUserAndPartnerOfPrivateRoom(user,
//            partner);

        return null;
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
        ChatLog chatLog = chatLogRepository.save(ChatLog.createLog(room, user, chatData, type));

        // 메모리에서 최근 시간을 업데이트 해준다.
        // TODO: Redis로 변경
        boolean isUpdate = chatCacheInfo.checkLastMessageUpdate(room, chatLog);

        if (isUpdate) {
            // 최근 메시지 업데이트
            chatRoomRepository.updateLastMessageAndTime(
                room.getId(), chatLog.getContent(), chatLog.getCreatedAt());
        }

        return chatLog;
    }

    @Transactional
    @Override
    public List<ChatLogResponseDto> readChatLog(User user, ChatRoom room, Pageable pageable) {

        List<ChatLog> logs = chatLogRepository.findByRoom(room, pageable);

        Optional<ChatUser> chatUser = chatUserRepository.findByUserAndRoom(user, room);
        if (chatUser.isEmpty()) {
            throw new ChatException(BAD_REQUEST);
        }

        // 현재 채팅방에 내용이 없으면 패스
        if (logs.isEmpty()) {
            return new ArrayList<>();
        }

        // 최근 읽은 기록 업데이트
        chatUser.get().updateRecentRead(logs.getFirst().getId());

        // 읽지 않은 사람을 계산하기 위한 Map 자료구조
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
