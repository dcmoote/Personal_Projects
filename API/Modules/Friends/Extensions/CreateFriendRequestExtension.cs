using API.Modules.Database.Models;
using API.Modules.Friends.Dtos;

namespace API.Modules.Friends.Extensions;

public static class CreateFriendRequestDtoExtension
{
    public static Friend ToModel(this CreateFriendRequestDto dto, Guid userId)
    {
        return new Friend
        {
            Id = Guid.NewGuid(),
            UserId = userId,
            FriendUserId = dto.FriendUserId,
            FriendRequestConfirmed = false
        };
    }
}
