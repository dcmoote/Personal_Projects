using API.Controllers;
using API.Modules.Database.Models;
using API.Modules.Friends.Dtos;
using API.Modules.Friends.Services;
using API.Modules.Users.Services;
using API.Utils;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Moq;

namespace API.Tests.Friends;

[TestClass]
public class FriendsControllerTests
{
    private readonly Mock<IFriendsService> _mockFriendsService;
    private readonly Mock<IUsersService> _mockUsersService;
    private readonly Mock<IJwtHelper> _mockJwtHelper;
    private readonly FriendsController _friendsController;
    private readonly Mock<ILogger<FriendsController>> _mockLogger;

    private readonly HttpContext _httpContext;

    public FriendsControllerTests()
    {
        _mockFriendsService = new Mock<IFriendsService>();
        _mockUsersService = new Mock<IUsersService>();
        _mockJwtHelper = new Mock<IJwtHelper>();
        _mockLogger = new Mock<ILogger<FriendsController>>();

        _friendsController = new FriendsController(_mockFriendsService.Object, _mockUsersService.Object, _mockJwtHelper.Object, _mockLogger.Object);

        _httpContext = new DefaultHttpContext();
        _httpContext.Request.Headers.Authorization = "Bearer some-token";
        _friendsController.ControllerContext = new ControllerContext
        {
            HttpContext = _httpContext
        };
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

        _mockJwtHelper
            .Setup(mock => mock.GetUserIdFromToken(It.IsAny<string>()))
            .Returns(userId);

        _mockUsersService
            .Setup(mock => mock.GetUserByGuid(It.IsAny<Guid>()))
            .ReturnsAsync(It.IsAny<User>());

        _mockFriendsService
            .Setup(mock => mock.GetFriends(userId))
            .ReturnsAsync(friends);

        var result = await _friendsController.GetFriends() as OkObjectResult;

        // check that the result is an OKObjectResult and that the value is a list of FriendDtos
        Assert.IsInstanceOfType(result, typeof(OkObjectResult));
        Assert.IsInstanceOfType(result.Value, typeof(List<FriendDto>));

        // Assert taht the UserIds of the friends match the userId
        var friendDtos = result.Value as List<FriendDto>;
        Assert.IsNotNull(friendDtos);
    }

    [TestMethod]
    public async Task UpdateFriend_GivenFriendAndId_ReturnsFriend()
    {
        var friendId = new Guid();
        var user1 = new Guid();
        var user2 = new Guid();

        var friend = new Friend
        {
            Id = friendId,
            UserId = user1,
            FriendUserId = user2,
            FriendRequestConfirmed = false
        };

        var friendUpdate = new Friend
        {
            Id = friendId,
            UserId = user1,
            FriendUserId = user2,
            FriendRequestConfirmed = true
        };

        Friend expectedResponse = friendUpdate;

        _mockFriendsService
            .Setup(mock => mock.UpdateFriend(friendId, friendUpdate))
            .ReturnsAsync(friendUpdate);

        var response = await _friendsController.UpdateFriend(friendId, friendUpdate) as OkObjectResult;

        Assert.AreEqual(expectedResponse, response?.Value);
    }

    [TestMethod]
    public async Task UpdateFriend_WhereFriendDoesNotExist_ReturnsNotFound()
    {
        var friend = new Friend
        {
            Id = new Guid(),
            UserId = new Guid(),
            FriendUserId = new Guid(),
            FriendRequestConfirmed = false
        };

        Friend? expectedResponse = null;

        _mockFriendsService
            .Setup(mock => mock.UpdateFriend(friend.Id, friend))
            .ReturnsAsync(expectedResponse);

        var response = await _friendsController.UpdateFriend(friend.Id, friend) as OkObjectResult;

        Assert.AreEqual(expectedResponse, response?.Value);
    }
}
