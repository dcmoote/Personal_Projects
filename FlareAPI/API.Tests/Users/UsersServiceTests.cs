using API.Modules.Database.Models;
using API.Modules.Users.Dtos;
using API.Modules.Users.Extensions;
using API.Modules.Users.Repositories;
using API.Modules.Users.Services;

using Moq;

namespace API.Tests.Users;

[TestClass]
public class UsersServiceTests
{
    private readonly Mock<IUsersRepository> _mockUsersRepository;
    private readonly UsersService _usersService;

    public UsersServiceTests()
    {
        _mockUsersRepository = new Mock<IUsersRepository>();
        _usersService = new UsersService(_mockUsersRepository.Object);
    }

    [TestMethod]
    public async Task AddUser_GivenUser_ReturnsUser()
    {
        var createUserDto = new CreateUserRequestDto
        {
            Username = "John Doe",
            Email = "johndoe@email.com",
            Password = "password",
            FirstName = "John",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
        };
        User expectedResponse = createUserDto.ToModel();

        _mockUsersRepository
          .Setup(mock => mock.AddUser(It.IsAny<User>()))
          .ReturnsAsync(expectedResponse);

        User response = await _usersService.AddUser(It.IsAny<User>());

        Assert.AreEqual(expectedResponse, response);
    }

    [TestMethod]
    public async Task GetUserByGuid_GivenUserId_ReturnsUser()
    {
        var user = new User
        {
            Id = new Guid(),
            Username = "John Doe",
            Email = "johndoe@email.com",
            Password = "password",
            FirstName = "John",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
        };
        User expectedResponse = user;

        _mockUsersRepository
          .Setup(mock => mock.GetUserByGuid(user.Id))
          .ReturnsAsync(expectedResponse);

        User? response = await _usersService.GetUserByGuid(user.Id);

        Assert.AreEqual(expectedResponse, response);
    }

    [TestMethod]
    public async Task IsValidEmail_GivenEmail_ReturnsBool()
    {
        _mockUsersRepository
          .Setup(mock => mock.IsValidEmail(It.IsAny<string>()))
          .ReturnsAsync(true);

        bool response = await _usersService.IsValidEmail(It.IsAny<string>());

        Assert.IsTrue(response);
    }

    [TestMethod]
    public async Task IsValidUsername_GivenUsername_ReturnsBool()
    {
        _mockUsersRepository
          .Setup(mock => mock.IsValidUsername(It.IsAny<string>()))
          .ReturnsAsync(true);

        bool response = await _usersService.IsValidUsername(It.IsAny<string>());

        Assert.IsTrue(response);
    }
}
