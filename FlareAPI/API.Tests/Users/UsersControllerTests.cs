using API.Controllers;
using API.Modules.Database.Extensions;
using API.Modules.Database.Models;
using API.Modules.Users.Dtos;
using API.Modules.Users.Extensions;
using API.Modules.Users.Services;
using API.Utils;

using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

using Moq;

namespace API.Tests.Users;

[TestClass]
public class UsersControllerTests
{
    private readonly Mock<IUsersService> _mockUsersService;

    private readonly Mock<IJwtHelper> _mockJwtHelper;
    private readonly Mock<ILogger<UsersController>> _mockLogger;
    private readonly UsersController _usersController;

    private User _user;

    private readonly DefaultHttpContext _httpContext;

    public UsersControllerTests()
    {
        _mockLogger = new Mock<ILogger<UsersController>>();
        _mockJwtHelper = new Mock<IJwtHelper>();
        _mockUsersService = new Mock<IUsersService>();
        _usersController = new UsersController(_mockUsersService.Object, _mockJwtHelper.Object, _mockLogger.Object);
        _user = new User
        {
            Id = Guid.NewGuid(),
            Username = "John Doe",
            Email = "johndoetest@email.com",
            Password = "password",
            FirstName = "John",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
        };

        // mock the http context with a bearer token
        _httpContext = new DefaultHttpContext();
        _httpContext.Request.Headers.Authorization = "Bearer some-token";
        _usersController.ControllerContext = new ControllerContext
        {
            HttpContext = _httpContext
        };
    }

    // before each test, reset the user object
    [TestInitialize]
    public void TestInitialize()
    {
        _mockUsersService.Reset();
        _user = new User
        {
            Id = Guid.NewGuid(),
            Username = "John Doe",
            Email = "johndoetest@email.com",
            Password = "password",
            FirstName = "John",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
        };
    }

    [TestMethod]
    public async Task AddUser_GivenUser_ReturnsUser()
    {
        var createUserDto = new CreateUserRequestDto
        {
            Username = "John Doe",
            Email = "johndoetest@email.com",
            Password = "password",
            FirstName = "John",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
        };
        User expectedResponse = createUserDto.ToModel();

        _mockUsersService
            .Setup(mock => mock.IsValidUsername(It.IsAny<string>()))
            .ReturnsAsync(true);

        _mockUsersService
            .Setup(mock => mock.IsValidEmail(It.IsAny<string>()))
            .ReturnsAsync(true);

        _mockUsersService
            .Setup(mock => mock.AddUser(It.IsAny<User>()))
            .ReturnsAsync(expectedResponse);

        var response = await _usersController.AddUser(createUserDto) as OkObjectResult;

        Assert.AreEqual(expectedResponse.ToDto().Id, (response?.Value as UserDto).Id);
    }

    [TestMethod]
    public async Task AddUser_GivenUserWithInvalidEmail_Returns400Status()
    {
        var createUserRequestDto = new CreateUserRequestDto
        {
            Username = "John Doe",
            Email = "emailalreadyexists@email.com",
            FirstName = "John",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds(),
            Password = "password"
        };
        string expectedResponse = "The email provided is already in use.";

        _mockUsersService
            .Setup(mock => mock.IsValidUsername(It.IsAny<string>()))
            .ReturnsAsync(true);
            
        _mockUsersService
            .Setup(mock => mock.IsValidEmail(It.IsAny<string>()))
            .ReturnsAsync(false);

        var response = await _usersController.AddUser(createUserRequestDto) as BadRequestObjectResult;

        Assert.AreEqual(expectedResponse, response?.Value);
    }

    [TestMethod]
    public async Task AddUser_GivenUserWithInvalidUsername_Returns400Status()
    {
        var createUserRequestDto = new CreateUserRequestDto
        {
            Username = "John Doe",
            Email = "johndoetest@email.com",
            FirstName = "John",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds(),
            Password = "password"
        };
        string expectedResponse = "The username provided is already in use.";

        _mockUsersService
            .Setup(mock => mock.IsValidUsername(It.IsAny<string>()))
            .ReturnsAsync(false);

        var response = await _usersController.AddUser(createUserRequestDto) as BadRequestObjectResult;

        Assert.AreEqual(expectedResponse, response?.Value);
    }

    [TestMethod]
    public async Task GetUserByGuid_GivenUserId_ReturnsUser()
    {
        User expectedResponse = _user;

        _mockUsersService
            .Setup(mock => mock.GetUserByGuid(_user.Id))
            .ReturnsAsync(expectedResponse);

        var response = await _usersController.GetUser(_user.Id) as OkObjectResult;

        Assert.AreEqual(expectedResponse.ToDto().Id, (response?.Value as UserDto)?.Id);
    }

    [TestMethod]
    public async Task UpdateUser_WhenUserIsNotAuthorized_ReturnsUnauthorized()
    {
        _mockJwtHelper
            .Setup(helper => helper.ValidateUserId(It.IsAny<string>(), It.IsAny<Guid>()))
            .Returns(false);

        var response = await _usersController.UpdateUser(_user.Id, _user) as UnauthorizedObjectResult;

        Assert.IsInstanceOfType(response, typeof(UnauthorizedObjectResult));
    }

    [TestMethod]
    public async Task UpdateUser_WhenUserIsNotFound_ReturnsNotFound()
    {
        _mockJwtHelper
            .Setup(helper => helper.ValidateUserId(It.IsAny<string>(), It.IsAny<Guid>()))
            .Returns(true);

        _mockUsersService
            .Setup(mock => mock.GetUserByGuid(_user.Id))
            .ReturnsAsync((User?)null);

        IActionResult response = await _usersController.UpdateUser(_user.Id, _user);

        Assert.IsInstanceOfType(response, typeof(NotFoundResult));
    }


    [TestMethod]
    public async Task UpdateUser_GivenUpdatedUser_ReturnsUpdatedUser()
    {
        // copy the _user object to a new updatedUser object, and update the username
        var updatedUser = new User
        {
            Id = _user.Id,
            Username = "Jane Doe",
            Email = "janedoe@gmail.com",
            Password = "password",
            FirstName = "Jane",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
        };

        _mockJwtHelper.Setup(helper => helper.ValidateUserId(It.IsAny<string>(), It.IsAny<Guid>()))
            .Returns(true);

        _mockUsersService
            .Setup(mock => mock.GetUserByGuid(_user.Id))
            .ReturnsAsync(_user);

        // Act
        IActionResult response = await _usersController.UpdateUser(_user.Id, updatedUser);

        // Assert
        // Expect UsersService.UpdateUser to be called with the updatedUser object (makes sure each property is updated)
        _mockUsersService.Verify(mock => mock.UpdateUser(updatedUser), Times.Once);
        Assert.IsInstanceOfType(response, typeof(OkObjectResult));
    }

    [TestMethod]
    public async Task GetUser_NoUserFound_Returns404()
    {
        User? user = null;
        User? expectedResponse = user;

        _mockUsersService
            .Setup(mock => mock.GetUserByGuid(It.IsAny<Guid>()))
            .ReturnsAsync(expectedResponse);

        var response = await _usersController.GetUser(It.IsAny<Guid>()) as NotFoundObjectResult;
        Assert.IsInstanceOfType(response ,typeof(NotFoundObjectResult));
        Assert.AreEqual(response?.Value, "No user with that id exists");
    }
}






