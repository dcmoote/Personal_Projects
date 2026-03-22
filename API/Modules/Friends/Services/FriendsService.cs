using API.Modules.Database.Models;
using API.Modules.Friends.Repositories;

namespace API.Modules.Friends.Services;

public class FriendsService(IFriendsRepository friendsRepository) : IFriendsService
{
    private readonly IFriendsRepository _friendsRepository = friendsRepository;

    public Task<Friend> AddFriend(Friend friend)
    {
        return _friendsRepository.AddFriend(friend);
    }

    public Task<Friend?> GetFriendByGuid(Guid id)
    {
        return _friendsRepository.GetFriendByGuid(id);
    }

    public Task<List<Friend>> GetFriends(Guid id)
    {
        return _friendsRepository.GetFriends(id);
    }

    public Task<Friend?> UpdateFriend(Guid id, Friend friendUpdate)
    {
        return _friendsRepository.UpdateFriend(id, friendUpdate);
    }

    public Task<Friend?> DeleteFriend(Guid id)
    {
        return _friendsRepository.DeleteFriend(id);
    }
}
