using API.Modules.Auth.Dtos;
using API.Modules.Auth.Services;
using API.Modules.Database.Models;
using API.Modules.Users.Services;

using Microsoft.AspNetCore.Mvc;

using Moq;

namespace API.Tests.Auth;

[TestClass]
public class AuthControllerTests
{
    private readonly Mock<IAuthService> _mockAuthService;
    private readonly Mock<IUsersService> _mockUsersService;
    private readonly AuthController _authController;

    public AuthControllerTests()
    {
        _mockAuthService = new Mock<IAuthService>();
        _authController = new AuthController(_mockAuthService.Object);
        _mockUsersService = new Mock<IUsersService>();
    }

    [TestMethod]
    public async Task Login_GivenValidLoginInfo_ReturnsToken()
    {
        var request = new LoginRequestDto
        {
            Login = "jdoe",
            Password = "password"
        };
        string expectedToken = "token";

        _mockUsersService
            .Setup(mock => mock.GetUserByUsernameOrEmail(request.Login))
            .ReturnsAsync(new User
            {
                Id = new Guid(),
                Username = request.Login,
                Email = request.Login,
                Password = "password",
                FirstName = "John",
                LastName = "Doe",
                BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
            });

        _mockAuthService
            .Setup(mock => mock.Login(request))
            .ReturnsAsync(expectedToken);


        var response = await _authController.Login(request) as OkObjectResult;

        object? responseValue = response?.Value;
        System.Reflection.PropertyInfo? tokenProperty = responseValue?.GetType().GetProperty("Token");
        string? actualToken = tokenProperty?.GetValue(responseValue, null) as string;

        Assert.AreEqual(expectedToken, actualToken);
    }

    [TestMethod]
    public async Task Login_GivenInvalidLoginInfo_ReturnsUnauthorized()
    {
        var request = new LoginRequestDto
        {
            Login = "jdoe",
            Password = "password"
        };

        _mockUsersService
            .Setup(mock => mock.GetUserByUsernameOrEmail(request.Login))
            .ReturnsAsync(new User
            {
                Id = new Guid(),
                Username = request.Login,
                Email = request.Login,
                Password = "password",
                FirstName = "John",
                LastName = "Doe",
                BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
            });

        _mockAuthService
            .Setup(mock => mock.Login(request))
            .ReturnsAsync((string?)null);

        var response = await _authController.Login(request) as UnauthorizedResult;

        Assert.IsNotNull(response);
    }
}
