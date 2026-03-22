
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

using API.Modules.Auth.Dtos;
using API.Modules.Database.Models;
using API.Modules.Users.Services;

using Microsoft.IdentityModel.Tokens;

namespace API.Modules.Auth.Services;

public class AuthService : IAuthService
{

    private readonly IUsersService _usersService;
    private readonly JwtSettings _jwtSettings;

    public AuthService(IUsersService usersService, JwtSettings jwtSettings)
    {
        _jwtSettings = jwtSettings;
        _usersService = usersService;
    }
    public async Task<string?> Login(LoginRequestDto request)
    {
        // Check if user exists and password is correct
        User? user = await _usersService.GetUserByUsernameOrEmail(request.Login);
        if (user == null || user.Password != request.Password)
        {
            return null;
        }

        // Generate token
        byte[] key = Encoding.UTF8.GetBytes(_jwtSettings.Secret);
        var claims = new List<Claim>
        {
            new(JwtRegisteredClaimNames.Jti, user.Id.ToString()),
            new(JwtRegisteredClaimNames.Sub, user.Username),
            new(JwtRegisteredClaimNames.Iat, EpochTime.GetIntDate(DateTime.UtcNow).ToString(), ClaimValueTypes.Integer64),
            new(JwtRegisteredClaimNames.Exp, DateTime.UtcNow.AddMinutes(_jwtSettings.ExpiryInMinutes).ToString())
        };

        var token = new JwtSecurityToken(
            _jwtSettings.Issuer,
            _jwtSettings.Audience,
            claims,
            expires: DateTime.UtcNow.AddMinutes(_jwtSettings.ExpiryInMinutes),
            signingCredentials: new SigningCredentials(
                new SymmetricSecurityKey(key),
                SecurityAlgorithms.HmacSha256Signature)
        );

        string writtenToken = new JwtSecurityTokenHandler().WriteToken(token);

        return writtenToken;
    }
}
