using API.Modules.Database.Models;
using API.Modules.Flares.Repositories;
using API.Modules.Flares.Services;

using Moq;

namespace API.Tests.Flares;

[TestClass]
public class FlaresServiceTests
{
    private readonly Mock<IFlaresRepository> _mockFlaresRepository;
    private readonly FlaresService _flaresService;

    public FlaresServiceTests()
    {
        _mockFlaresRepository = new Mock<IFlaresRepository>();
        _flaresService = new FlaresService(_mockFlaresRepository.Object);
    }

    [TestMethod]
    public async Task AddFlare_GivenFlare_ReturnsFlare()
    {
        var ownerGuid = new Guid();
        var flare = new Flare
        {
            Id = new Guid(),
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
        Flare expectedResponse = flare;

        _mockFlaresRepository
          .Setup(mock => mock.AddFlare(flare))
          .ReturnsAsync(expectedResponse);

        Flare response = await _flaresService.AddFlare(flare);

        Assert.AreEqual(expectedResponse, response);
    }

    [TestMethod]
    public async Task GetFlare_GivenFlareId_ReturnsFlare()
    {
        var ownerGuid = new Guid();
        var flare = new Flare
        {
            Id = new Guid(),
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
        Flare? expectedResponse = flare;

        _mockFlaresRepository
          .Setup(mock => mock.GetFlare(flare.Id))
          .ReturnsAsync(expectedResponse);

        Flare? response = await _flaresService.GetFlare(flare.Id);

    Assert.AreEqual(expectedResponse, response);
  }

  [TestMethod]
  public async Task UpdateFlare_GivenFlareAndId_ReturnsFlare()
  {
    var ownerGuid = new Guid();
    var flareGuid = new Guid();
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
    var flareChange = new Flare
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
      Latitude = 32,
      Longitude = 32,
      StartTime = 0.0,
      EndTime = 0.0,
      Description = "this is a flare"
    };

    Flare? expectedResponse = flareChange;

    _mockFlaresRepository
      .Setup(mock => mock.UpdateFlare(flare.Id, flareChange))
      .ReturnsAsync(expectedResponse);

    Flare? response = await _flaresService.UpdateFlare(flare.Id, flareChange);

    Assert.AreEqual(expectedResponse, response);
  }

  [TestMethod]
  public async Task GetFlares_GivenUserId_ReturnsListOfFlares()
  {
      var expectedResponse = new List<Flare>();

      _mockFlaresRepository
        .Setup(mock => mock.GetFlares(It.IsAny<Guid>()))
        .ReturnsAsync(expectedResponse);

      List<Flare> response = await _flaresService.GetFlares(It.IsAny<Guid>());

      Assert.AreEqual(expectedResponse, response);
  }

  [TestMethod]
  public async Task GetFlareRecipients_GivenFlareId_ReturnsListOfFlareRecipients()
  {
      var expectedResponse = new List<FlareRecipient>();

      _mockFlaresRepository
        .Setup(mock => mock.GetFlareRecipients(It.IsAny<Guid>()))
        .ReturnsAsync(expectedResponse);

      List<FlareRecipient> response = await _flaresService.GetFlareRecipients(It.IsAny<Guid>());

      Assert.AreEqual(expectedResponse, response);
  }

  [TestMethod]
  public async Task DeleteFlare_GivenId_ReturnsFlare()
  {
    var ownerGuid = new Guid();
    var flareGuid = new Guid();
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

    var flareChange = new Flare
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

    Flare? expectedResponse = flareChange;

    _mockFlaresRepository
      .Setup(mock => mock.DeleteFlare(flare.Id))
      .ReturnsAsync(expectedResponse);

    Flare? response = await _flaresService.DeleteFlare(flare.Id);

    Assert.AreEqual(expectedResponse, response);
  }
}
