using API.Modules.Database.Extensions;
using API.Modules.Database.Models;
using API.Modules.Friends.Dtos;
using API.Modules.Friends.Extensions;
using API.Modules.Friends.Services;
using API.Modules.Users.Services;
using API.Utils;

using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers;

[Authorize]
[ApiController]
[Route(Constants.Resources.V1.Friends)]
public class FriendsController : ControllerBase
{
    private readonly IFriendsService _friendService;
    private readonly IUsersService _usersService;
    private readonly IJwtHelper _jwtHelper;
    private readonly ILogger<FriendsController> _logger;

    public FriendsController(IFriendsService friendsService, IUsersService usersService, IJwtHelper jwtHelper, ILogger<FriendsController> logger)
    {
        _friendService = friendsService;
        _usersService = usersService;
        _jwtHelper = jwtHelper;
        _logger = logger;
    }

    [ProducesResponseType(typeof(Friend), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status400BadRequest)]
    [HttpPost]
    public async Task<IActionResult> AddFriend([FromBody] CreateFriendRequestDto friend)
    {
        string jwt = HttpContext.Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
        Guid userId = _jwtHelper.GetUserIdFromToken(jwt);
        Friend friendModel = friend.ToModel(userId);

        return Ok(await _friendService.AddFriend(friendModel));
    }

    [ProducesResponseType(typeof(Friend), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status404NotFound)]
    [HttpGet("{id}")]
    public async Task<IActionResult> GetFriend(Guid id)
    {
        Friend? friend = await _friendService.GetFriendByGuid(id);

        if (friend == null)
        {
            return NotFound("A friend with that id was not found.");
        }

        return Ok(friend);
    }

    [ProducesResponseType(typeof(List<FriendDto>), StatusCodes.Status200OK)]
    [HttpGet()]
    public async Task<IActionResult> GetFriends()
    {
        string jwt = HttpContext.Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
        Guid userId = _jwtHelper.GetUserIdFromToken(jwt);
        List<Friend> friends = await _friendService.GetFriends(userId);
        var friendDtos = new List<FriendDto>();

        foreach (Friend friend in friends)
        {
            Guid id = friend.UserId == userId ? friend.FriendUserId : friend.UserId;
            User? friendUser = await _usersService.GetUserByGuid(id);

            if (friendUser != null)
            {
                friendDtos.Add(friend.ToDto(friendUser));
            }
        }

        return Ok(friendDtos);
    }

    [ProducesResponseType(typeof(Friend), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status404NotFound)]
    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateFriend(Guid id, [FromBody] Friend friendUpdate)
    {
        Friend? result = await _friendService.UpdateFriend(id, friendUpdate);

        if (result == null)
        {
            return NotFound("A friend with that id was not found.");
        }

        return Ok(result);
    }

    [ProducesResponseType(typeof(Friend), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status404NotFound)]
    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteFriend(Guid id)
    {
        Friend? result = await _friendService.DeleteFriend(id);

        if (result == null)
        {
            return NotFound("A friend with that id was not found.");
        }

        return Ok(result);
    }
}
