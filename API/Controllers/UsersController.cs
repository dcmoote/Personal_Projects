using API.Modules.Database.Extensions;
using API.Modules.Database.Models;
using API.Modules.Users.Dtos;
using API.Modules.Users.Extensions;
using API.Modules.Users.Services;
using API.Utils;

using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;


namespace API.Controllers;

[Authorize]
[ApiController]
[Route(Constants.Resources.V1.Users)]
public class UsersController : ControllerBase
{
    private readonly IUsersService _userService;
    private readonly IJwtHelper _jwtHelper;
    private readonly ILogger<UsersController> _logger;

    public UsersController(IUsersService usersService, IJwtHelper jwtHelper, ILogger<UsersController> logger)
    {
        _userService = usersService;
        _jwtHelper = jwtHelper;
        _logger = logger;
    }

    [ProducesResponseType(typeof(UserDto), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status404NotFound)]
    [HttpGet("{id}")]
    public async Task<IActionResult> GetUser(Guid id)
    {
        if (await _userService.GetUserByGuid(id) == null)
        {
            return NotFound("No user with that id exists");
        }

        User? user = await _userService.GetUserByGuid(id);

        return Ok(user?.ToDto());
    }

    [AllowAnonymous]
    [ProducesResponseType(typeof(UserDto), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status400BadRequest)]
    [HttpPost]
    public async Task<IActionResult> AddUser([FromBody] CreateUserRequestDto user)
    {
        bool isValidUsername = await _userService.IsValidUsername(user.Username);
        bool isValidEmail = await _userService.IsValidEmail(user.Email);

        if (!isValidUsername)
        {
            return BadRequest("The username provided is already in use.");
        }

        if (!isValidEmail)
        {
            return BadRequest("The email provided is already in use.");
        }

        User newUser = await _userService.AddUser(user.ToModel());

        return Ok(newUser.ToDto());
    }

    [ProducesResponseType(typeof(UserDto), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(typeof(string), StatusCodes.Status400BadRequest)]
    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateUser(Guid id, [FromBody] User user)
    {
        string jwt = HttpContext.Request.Headers["Authorization"].ToString().Replace("Bearer ", "");

        if (!_jwtHelper.ValidateUserId(jwt, id))
        {
            _logger.LogInformation($"This user is not authorized to update user with id {id}");
            return Unauthorized($"This user is not authorized to update the user.");
        }

        _logger.LogDebug($"Updating user0: {user.ToJson()}");

        if (user != null)
        {
            _logger.LogDebug($"Updating user1: {user.ToJson()}");
            User? dbUser = await _userService.GetUserByGuid(id);

            if (dbUser == null)
            {
                return NotFound();
            }

            _logger.LogDebug($"Updating user: {dbUser.ToJson()}");
            User updatedUser = await _userService.UpdateUser(user);

            return Ok(updatedUser);
        }

        return BadRequest("There was a problem with the request.");
    }
}
