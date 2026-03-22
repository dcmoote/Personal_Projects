using API.Modules.Database.Models;
using API.Modules.Database.Services;
using API.Modules.Friends.Dtos;

using Microsoft.EntityFrameworkCore.ChangeTracking;

namespace API.Modules.Friends.Repositories;

public class FriendsRepository : IFriendsRepository
{
    private readonly DatabaseContext _context;

    public FriendsRepository(DatabaseContext context)
    {
        _context = context;
    }

    public Task<Friend> AddFriend(Friend friend)
    {
        EntityEntry<Friend> createdFriend = _context.Friends.Add(friend);

        _context.SaveChanges();

        return Task.FromResult(createdFriend.Entity);
    }

    public Task<Friend?> GetFriendByGuid(Guid id)
    {
        return Task.FromResult(_context.Friends.Find(id));
    }

    public Task<List<Friend>> GetFriends(Guid id)
    {
        return Task.FromResult(_context.Friends.Where(f => (f.UserId == id || f.FriendUserId == id) && !f.IsDeleted).ToList());
    }

    public Task<Friend?> UpdateFriend(Guid id, Friend friendUpdate)
    {
        Friend? existingFriend = _context.Friends.Find(id);

        if (existingFriend == null)
        {
            return Task.FromResult<Friend?>(null);
        }

        existingFriend.FriendRequestConfirmed = friendUpdate.FriendRequestConfirmed;
        _context.SaveChanges();
        return Task.FromResult(_context.Friends.Find(id));
    }

    public Task<Friend?> DeleteFriend(Guid id)
    {
        Friend? friend = _context.Friends.Find(id);

        if (friend == null)
        {
            return Task.FromResult<Friend?>(null);
        }

        friend.IsDeleted = true;
        _context.SaveChanges();
        return Task.FromResult(_context.Friends.Find(id));
    }
}
