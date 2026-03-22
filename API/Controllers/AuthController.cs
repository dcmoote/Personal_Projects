using API.Modules.Auth.Dtos;
using API.Modules.Auth.Services;

using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route(Constants.Resources.V1.Auth)]
public class AuthController : ControllerBase
{
    private readonly IAuthService _authService;

    public AuthController(IAuthService authService)
    {
        _authService = authService;
    }

    [ProducesResponseType(typeof(object), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [HttpPost()]
    public async Task<IActionResult> Login(
        [FromBody] LoginRequestDto request)
    {
        string? token = await _authService.Login(request);
        if (token == null)
        {
            return Unauthorized();
        }

        return Ok(new { Token = token });
    }
}

