using API.Modules.Database.Models;
using API.Modules.Friends.Repositories;
using API.Modules.Friends.Services;
using Moq;

namespace API.Tests.Friends;

[TestClass]
public class FriendsServiceTests
{
    private readonly Mock<IFriendsRepository> _mockFriendsRepository;
    private readonly FriendsService _friendsService;

    public FriendsServiceTests()
    {
        _mockFriendsRepository = new Mock<IFriendsRepository>();
        _friendsService = new FriendsService(_mockFriendsRepository.Object);
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

        _mockFriendsRepository
            .Setup(mock => mock.GetFriends(userId))
            .ReturnsAsync(friends);

        List<Friend> response = await _friendsService.GetFriends(userId);

        Assert.AreEqual(friends, response);
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

        Friend expectedResponse = friend;

        _mockFriendsRepository
            .Setup(mock => mock.UpdateFriend(friend.Id, friend))
            .ReturnsAsync(expectedResponse);

        Friend? response = await _friendsService.UpdateFriend(friend.Id, friend);

        Assert.AreEqual(expectedResponse, response);
    }
}
