package com.shurona.chat.mytalk.chat.application;

import static com.shurona.chat.mytalk.chat.common.exception.ChatErrorCode.BAD_REQUEST;
import static java.util.stream.Collectors.toMap;

import com.shurona.chat.mytalk.chat.application.cache.ChatCacheInfo;
import com.shurona.chat.mytalk.chat.common.exception.ChatException;
import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.model.ChatUser;
import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import com.shurona.chat.mytalk.chat.domain.validator.ChatRoomValidator;
import com.shurona.chat.mytalk.chat.infrastructure.ChatLogJpaRepository;
import com.shurona.chat.mytalk.chat.infrastructure.ChatRoomJpaRepository;
import com.shurona.chat.mytalk.chat.infrastructure.ChatUserJpaRepository;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    /**
     * 일단 개인톡 만들고 그룹톡을 만들어봅시다.
     * TODO: 그룹톡 개선해보기
     */
    @Transactional
    @Override
    public ChatRoom createChatRoom(
        User user, List<User> invitedUserList, RoomType type, String name) {

        // 채팅창에 초대한 유저 목록
        List<User> withUsers = new ArrayList<>(invitedUserList);
        withUsers.add(user); // 자기자신을 채팅장 목록에 추가한다.

        // 이미 존재하는지 확인한다.
        List<ChatRoom> privateRoomsWithUsers = chatRoomRepository.findPrivateRoomContainingExactUsers(
            withUsers, type, withUsers.size());

        // 생성하는 채팅이 개인톡이고 이미 채팅방이 존재하면 추가로 생성하지 않는다.
        if (type.equals(RoomType.PRIVATE) && !privateRoomsWithUsers.isEmpty()) {
            // System.out.println(privateRoomsWithUsers.size());
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
    public Page<ChatLog> readChatLog(User user, ChatRoom room, Pageable pageable) {

        Page<ChatLog> logs = chatLogRepository.findByRoom(room, pageable);

        // 유저가 채팅방에 속했는 지 확인한다.
        Optional<ChatUser> chatUser = chatUserRepository.findByUserAndRoom(user, room);
        if (chatUser.isEmpty()) {
            throw new ChatException(BAD_REQUEST);
        }

        // 현재 채팅방에 내용이 없으면 패스
        if (logs.isEmpty()) {
            return Page.empty();
        }

        // 최근 읽은 기록 업데이트
        chatUser.get().updateRecentRead(logs.toList().getFirst().getId());

        return logs;
    }

    public Map<Long, Long> findUserRecentReadMap(ChatRoom room) {
        return chatUserRepository.findByRoom(room).stream()
            .collect(toMap(
                ChatUser::getId,
                ChatUser::getLastReadMessageId
            ));
    }

    @Override
    public ChatRoom inviteUser(User user, User friend, ChatRoom room) {
        return null;
    }
}
