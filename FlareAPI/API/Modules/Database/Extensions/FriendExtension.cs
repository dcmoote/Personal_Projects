using API.Modules.Database.Models;
using API.Modules.Friends.Dtos;

namespace API.Modules.Database.Extensions;

public static class FriendExtension
{
    public static FriendDto ToDto(this Friend model, User user)
    {
        return new FriendDto
        {
            Id = model.Id,
            UserId = user.Id,
            FirstName = user.FirstName,
            LastName = user.LastName,
            Username = user.Username,
            FriendRequestConfirmed = model.FriendRequestConfirmed
        };
    }

    public static Friend ToModel(this FriendDto dto)
    {
        return new Friend
        {
            Id = dto.Id,
            UserId = dto.UserId,
            FriendRequestConfirmed = dto.FriendRequestConfirmed
        };
    }
}
