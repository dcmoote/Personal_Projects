using API.Modules.Database.Models;
using API.Modules.Database.Services;
using API.Modules.Friends.Repositories;
using Microsoft.EntityFrameworkCore;
using Moq;

namespace API.Tests.Friends;

[TestClass]
public class FriendsRepositoryTests
{
    private readonly Mock<DatabaseContext> _mockContext;
    private readonly FriendsRepository _repository;

    public FriendsRepositoryTests()
    {
        _mockContext = new Mock<DatabaseContext>();
        _repository = new FriendsRepository(_mockContext.Object);
    }

    [TestMethod]
    public async Task GetFriends_GivenGuid_ReturnsFriends()
    {
        var userId = new Guid();
        var friendOne = new Friend
        {
            Id = new Guid(),
            UserId = userId,
            FriendUserId = new Guid(),
            FriendRequestConfirmed = true
        };
        var friendTwo = new Friend
        {
            Id = new Guid(),
            UserId = userId,
            FriendUserId = new Guid(),
            FriendRequestConfirmed = true
        };
        var friends = new List<Friend>
        {
            friendOne,
            friendTwo
        };

        var mockSet = new Mock<DbSet<Friend>>();
        mockSet.As<IQueryable<Friend>>().Setup(m => m.Provider).Returns(friends.AsQueryable().Provider);
        mockSet.As<IQueryable<Friend>>().Setup(m => m.Expression).Returns(friends.AsQueryable().Expression);
        mockSet.As<IQueryable<Friend>>().Setup(m => m.ElementType).Returns(friends.AsQueryable().ElementType);
        mockSet.As<IQueryable<Friend>>().Setup(m => m.GetEnumerator()).Returns(friends.AsQueryable().GetEnumerator());

        _mockContext.Setup(c => c.Friends).Returns(mockSet.Object);

        List<Friend> result = await _repository.GetFriends(userId);

        Assert.AreEqual(2, result.Count);
        Assert.AreEqual(friendOne.Id, result[0].Id);
        Assert.AreEqual(friendTwo.Id, result[1].Id);

    }

    [TestMethod]
    public async Task UpdateFriend_GivenFriendAndId_ReturnsFriend()
    {
        var friend = new Friend
        {
            Id = new Guid(),
            UserId = new Guid(),
            FriendUserId = new Guid(),
            FriendRequestConfirmed = false
        };

        var mockSet = new Mock<DbSet<Friend>>();
        mockSet
            .Setup(m => m.Find(It.IsAny<Guid>()))
            .Returns(friend);
        _mockContext
            .Setup(m => m.Friends)
            .Returns(mockSet.Object);

        var repository = new FriendsRepository(_mockContext.Object);

        Friend? result = await repository.UpdateFriend(friend.Id, friend);

        Assert.AreEqual(friend, result);
    }
}
