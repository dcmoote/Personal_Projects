using API.Modules.Database.Models;
using API.Modules.Database.Services;
using API.Modules.Users.Repositories;

using Microsoft.EntityFrameworkCore;

using Moq;

namespace API.Tests.Users;

[TestClass]
public class UsersRepositoryTests
{
    private readonly Mock<DatabaseContext> _mockContext;
    private readonly UsersRepository _repository;

    public UsersRepositoryTests()
    {
        _mockContext = new Mock<DatabaseContext>();
        _repository = new UsersRepository(_mockContext.Object);
    }

    [TestMethod]
    public async Task AddUser_GivenUser_AddsUserToContext()
    {
        var userGuid = new Guid();
        var user = new User
        {
            Id = userGuid,
            Username = "John Doe",
            Email = "john.doe@example.com",
            Password = "password",
            FirstName = "John",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
        };
        var mockSet = new Mock<DbSet<User>>();

        mockSet
          .Setup(m => m.Find(It.IsAny<Guid>()))
          .Returns(user);

        _mockContext
          .Setup(m => m.Users)
          .Returns(mockSet.Object);

        // Act
        User? result = await _repository.GetUserByGuid(userGuid);

        // Assert
        Assert.AreEqual(user, result);

    }

    [TestMethod]
    public async Task GetUserByGuid_GivenUserId_ReturnsUser()
    {
        var userGuid = new Guid();
        var user = new User
        {
            Id = userGuid,
            Username = "John Doe",
            Email = "john.doe@example.com",
            Password = "password",
            FirstName = "John",
            LastName = "Doe",
            BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
        };
        var mockSet = new Mock<DbSet<User>>();

        mockSet
          .Setup(m => m.Find(It.IsAny<Guid>()))
          .Returns(user);

        _mockContext
          .Setup(m => m.Users)
          .Returns(mockSet.Object);

        User? result = await _repository.GetUserByGuid(userGuid);

        Assert.AreEqual(user, result);
    }
}
