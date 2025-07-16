using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

using API.Modules.Auth.Dtos;
using API.Modules.Auth.Services;
using API.Modules.Database.Models;
using API.Modules.Users.Services;

using Microsoft.IdentityModel.Tokens;

using Moq;

namespace API.Tests.Auth;

[TestClass]
public class AuthServiceTest
{
    private readonly Mock<IUsersService> _mockUsersService;
    private readonly JwtSettings _jwtSettings;
    private readonly AuthService _authService;

    public AuthServiceTest()
    {
        _mockUsersService = new Mock<IUsersService>();
        _jwtSettings = new JwtSettings
        {
            Secret = "your-256-bit-secret-that-needs-to-be-long-enough",
            Issuer = "your-issuer",
            Audience = "your-audience",
            ExpiryInMinutes = 60
        };
        _authService = new AuthService(_mockUsersService.Object, _jwtSettings);
    }

    [TestMethod]
    public async Task Login_UserDoesNotExist_ReturnsNull()
    {
        // Arrange
        var request = new LoginRequestDto
        {
            Login = "nonexistentuser",
            Password = "password"
        };
        _mockUsersService.Setup(service => service.GetUserByUsernameOrEmail(request.Login)).ReturnsAsync((User?)null);

        // Act
        string? result = await _authService.Login(request);

        // Assert
        Assert.IsNull(result);
    }

    [TestMethod]
    public async Task Login_InvalidPassword_ReturnsNull()
    {
        // Arrange
        var request = new LoginRequestDto
        {
            Login = "existinguser",
            Password = "wrongpassword"
        };
        var user = new User
        {
            Id = new Guid(),
            Email = "email@test.com",
            Username = "existinguser",
            Password = "correctpassword",
            FirstName = "John",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
        };
        _mockUsersService.Setup(service => service.GetUserByUsernameOrEmail(request.Login)).ReturnsAsync(user);

        // Act
        string? result = await _authService.Login(request);

        // Assert
        Assert.IsNull(result);
    }

    [TestMethod]
    public async Task Login_ValidCredentials_ReturnsToken()
    {
        // Arrange
        var request = new LoginRequestDto
        {
            Login = "existinguser",
            Password = "correctpassword",
            UserId = new Guid()
        };
        var user = new User
        {
            Id = new Guid(),
            Email = "test@email.com",
            Username = "existinguser",
            Password = "correctpassword",
            FirstName = "John",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
        };
        _mockUsersService.Setup(service => service.GetUserByUsernameOrEmail(request.Login)).ReturnsAsync(user);

        // Act
        string? result = await _authService.Login(request);

        // Assert
        Assert.IsNotNull(result);
        var tokenHandler = new JwtSecurityTokenHandler();
        byte[] key = Encoding.UTF8.GetBytes(_jwtSettings.Secret);
        SecurityToken validatedToken;
        ClaimsPrincipal principal = tokenHandler.ValidateToken(result, new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = _jwtSettings.Issuer,
            ValidAudience = _jwtSettings.Audience,
            IssuerSigningKey = new SymmetricSecurityKey(key)
        }, out validatedToken);

        Assert.IsNotNull(validatedToken);
        Assert.IsNotNull(principal);
        Assert.AreEqual(request.UserId.ToString(), principal.FindFirst(JwtRegisteredClaimNames.Jti)?.Value);
        // spent forever on this. for some reason JwtRegisteredClaimNames.Sub maps to this url in the API but not in the test?
        Assert.AreEqual(request.Login, principal.FindFirst("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier")?.Value);
        Assert.IsNotNull(principal.FindFirst(JwtRegisteredClaimNames.Iat));
        Assert.IsNotNull(principal.FindFirst(JwtRegisteredClaimNames.Exp));


    }
}
