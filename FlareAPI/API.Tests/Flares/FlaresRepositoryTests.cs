using API.Modules.Database.Models;
using API.Modules.Database.Services;
using API.Modules.Flares.Repositories;
using Microsoft.EntityFrameworkCore;
using Moq;

namespace API.Tests.Flares;

[TestClass]
public class FlaresRepositoryTests
{
    private readonly Mock<DatabaseContext> _mockContext;
    private readonly FlaresRepository _repository;

    public FlaresRepositoryTests()
    {
        _mockContext = new Mock<DatabaseContext>();
        _repository = new FlaresRepository(_mockContext.Object);
    }

    [TestMethod]
    public async Task AddFlare_GivenFlare_AddsFlareToContext()
    {
        var flareGuid = new Guid();
        var ownerGuid = new Guid();
        var flare = new Flare
        {
            Id = flareGuid,
            OwnerId = ownerGuid,
            Owner = new User
            {
                Id = ownerGuid,
                Username = "John Doe",
                Email = "johndoetest@email.com",
                FirstName = "John",
                LastName = "Doe",
                Password = "password",
                BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
            },
            Latitude = 40.281152,
            Longitude = -111.718095,
            StartTime = 0,
            EndTime = 0,
            Description = "this is a flare"
        };
        var mockSet = new Mock<DbSet<Flare>>();
        mockSet
          .Setup(m => m.Find(It.IsAny<Guid>()))
          .Returns(flare);
        _mockContext
          .Setup(m => m.Flares)
          .Returns(mockSet.Object);

        var repository = new FlaresRepository(_mockContext.Object);

        // Act
        Flare? result = await repository.GetFlare(flareGuid);

        // Assert
        Assert.AreEqual(flare, result);

    }

    [TestMethod]
    public async Task GetFlare_GivenFlareId_ReturnsFlare()
    {
        var flareGuid = new Guid();
        var ownerGuid = new Guid();
        var flare = new Flare
        {
            Id = flareGuid,
            OwnerId = ownerGuid,
            Owner = new User
            {
                Id = ownerGuid,
                Username = "John Doe",
                Email = "johndoetest@email.com",
                FirstName = "John",
                LastName = "Doe",
                Password = "password",
                BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
            },
            Latitude = 40.281152,
            Longitude = -111.718095,
            StartTime = 0,
            EndTime = 0,
            Description = "this is a flare"
        };
        var mockSet = new Mock<DbSet<Flare>>();
        mockSet
          .Setup(m => m.Find(It.IsAny<Guid>()))
          .Returns(flare);
        _mockContext
          .Setup(m => m.Flares)
          .Returns(mockSet.Object);

        var repository = new FlaresRepository(_mockContext.Object);

        Flare? result = await repository.GetFlare(flareGuid);

    Assert.AreEqual(flare, result);
  }

  [TestMethod]
  public async Task UpdateFlare_GivenFlareAndId_ReturnsFlare()
  {
    var flareGuid = new Guid();
    var ownerGuid = new Guid();
    var flare = new Flare
    {
      Id = flareGuid,
      OwnerId = ownerGuid,
      Owner = new User
      {
        Id = ownerGuid,
        Username = "John Doe",
        Email = "johndoetest@email.com",
        FirstName = "John",
        LastName = "Doe",
        Password = "password",
        BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
      },
      Latitude = 40.281152,
      Longitude = -111.718095,
      StartTime = 0.0,
      EndTime = 0.0,
      Description = "this is a flare"
    };

    var updateFlare = new Flare
    {
      Id = flareGuid,
      OwnerId = ownerGuid,
      Owner = new User
      {
        Id = ownerGuid,
        Username = "John Doe",
        Email = "johndoetest@email.com",
        FirstName = "John",
        LastName = "Doe",
        Password = "password",
        BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
      },
      Latitude = 75.7667,
      Longitude = 98.7833,
      StartTime = 0.0,
      EndTime = 0.0,
      Description = "this is a different flare"
    };

    var mockSet = new Mock<DbSet<Flare>>();
    mockSet
      .Setup(m => m.Find(It.IsAny<Guid>()))
      .Returns(updateFlare);
    _mockContext
      .Setup(m => m.Flares)
      .Returns(mockSet.Object);

    var repository = new FlaresRepository(_mockContext.Object);

    Flare? result = await repository.UpdateFlare(flareGuid, updateFlare);

    Assert.AreEqual(updateFlare, result);
  }

  [TestMethod]
  public async Task DeleteFlare_GivenId_ReturnsFlare()
  {
    var flareGuid = new Guid();
    var ownerGuid = new Guid();
    var flare = new Flare
    {
      Id = flareGuid,
      OwnerId = ownerGuid,
      Owner = new User
      {
        Id = ownerGuid,
        Username = "John Doe",
        Email = "johndoetest@email.com",
        FirstName = "John",
        LastName = "Doe",
        Password = "password",
        BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
      },
      Latitude = 40.281152,
      Longitude = -111.718095,
      StartTime = 0.0,
      EndTime = 0.0,
      Description = "this is a flare"
    };

    var updateFlare = new Flare
    {      
      Id = flareGuid,
      OwnerId = ownerGuid,
      Owner = new User
      {
        Id = ownerGuid,
        Username = "John Doe",
        Email = "johndoetest@email.com",
        FirstName = "John",
        LastName = "Doe",
        Password = "password",
        BirthDate = new DateTimeOffset().ToUnixTimeSeconds()
      },
      Latitude = 40.281152,
      Longitude = -111.718095,
      StartTime = 0.0,
      EndTime = 0.0,
      Description = "this is a flare",
      IsDeleted = true
    };

    var mockSet = new Mock<DbSet<Flare>>();
    mockSet
      .Setup(m => m.Find(It.IsAny<Guid>()))
      .Returns(updateFlare);
    _mockContext
      .Setup(m => m.Flares)
      .Returns(mockSet.Object);

    var repository = new FlaresRepository(_mockContext.Object);

    Flare? result = await repository.DeleteFlare(flareGuid);

    Assert.AreEqual(updateFlare, result);
  }
}
