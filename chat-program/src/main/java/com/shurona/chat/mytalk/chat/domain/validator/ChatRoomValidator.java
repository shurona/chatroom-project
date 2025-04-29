package com.shurona.chat.mytalk.chat.domain.validator;

import static com.shurona.chat.mytalk.chat.common.ChatErrorCode.PRIVATE_CHAT_FRIEND_REQUIRED;
import static com.shurona.chat.mytalk.chat.common.ChatErrorCode.PRIVATE_CHAT_JUST_ONE_REQUIRED;
import static com.shurona.chat.mytalk.chat.common.ChatErrorCode.USER_NOT_INCLUDE_ROOM;

import com.shurona.chat.mytalk.chat.common.ChatException;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.friend.domain.model.type.FriendRequest;
import com.shurona.chat.mytalk.friend.domain.service.FriendChecker;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatRoomValidator {

    private final FriendChecker friendChecker;

    /*
        개인 채팅방 생성 제한
     */
    public void createPrivateChatRoomValidator(User user, List<User> invitedUserList,
        RoomType type) {
        // 개인 톡인 경우에는 친구 끼리만 만들 수 있다.
        if (RoomType.PRIVATE.equals(type)) {
            if (invitedUserList.size() != 1) {
                throw new ChatException(PRIVATE_CHAT_JUST_ONE_REQUIRED);
            }

            User first = invitedUserList.getFirst();
            // 친구 있는 지 확인하고 null 이면 에러 처리
            Optional<Friend> friendByUserAndFriend = friendChecker.findFriendByUserAndFriend(user,
                invitedUserList.getFirst());
            if (friendByUserAndFriend.isEmpty()
                || !friendByUserAndFriend.get().getRequest().equals(FriendRequest.ACCEPTED)) {
                throw new ChatException(PRIVATE_CHAT_FRIEND_REQUIRED);
            }
        }
    }

    public void writeChatValidator(ChatRoom room, User user) {
        Set<Long> userIdSet = room.getChatUserList().stream()
            .map(chatUser -> chatUser.getUser().getId())
            .collect(Collectors.toSet());

        // 유저가 속해 있는지 확인한다.
        if (!userIdSet.contains(user.getId())) {
            throw new ChatException(USER_NOT_INCLUDE_ROOM);
        }
    }

}
