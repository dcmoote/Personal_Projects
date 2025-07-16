using API.Modules.Database.Models;

namespace API.Modules.Friends.Repositories;

public interface IFriendsRepository
{
    Task<Friend> AddFriend(Friend friend);
    Task<Friend?> GetFriendByGuid(Guid id);
    Task<List<Friend>> GetFriends(Guid id);
    Task<Friend?> UpdateFriend(Guid id, Friend friendUpdate);
    Task<Friend?> DeleteFriend(Guid id);
}
