using API.Controllers;
using API.Modules.Database.Extensions;
using API.Modules.Database.Models;
using API.Modules.Flares.Dtos;
using API.Modules.Flares.Services;
using API.Utils;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Moq;

namespace API.Tests.Flares;

[TestClass]
public class FlaresControllerTests
{
    private readonly Mock<IFlaresService> _mockFlaresService;
    private readonly Mock<IJwtHelper> _mockJwtHelper;
    private readonly FlaresController _flaresController;
    private readonly HttpContext _httpContext;

    public FlaresControllerTests()
    {
        _mockFlaresService = new Mock<IFlaresService>();
        _mockJwtHelper = new Mock<IJwtHelper>();
        _flaresController = new FlaresController(_mockFlaresService.Object, _mockJwtHelper.Object);

        _httpContext = new DefaultHttpContext();
        _httpContext.Request.Headers.Authorization = "Bearer some-token";
        _flaresController.ControllerContext = new ControllerContext
        {
            HttpContext = _httpContext
        };
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
                BirthDate = new DateTimeOffset().ToUnixTimeSeconds(),
            },
            Latitude = 40.281152,
            Longitude = -111.718095,
            StartTime = 0,
            EndTime = 0,
            Description = "this is a flare",
        };
        FlareDto flareDto = flare.ToDto(new List<FlareRecipient>());

        _mockJwtHelper
            .Setup(mock => mock.GetUserIdFromToken(It.IsAny<string>()))
            .Returns(ownerGuid);

        _mockFlaresService
            .Setup(mock => mock.AddFlare(It.IsAny<Flare>()))
            .ReturnsAsync(flare);

        _mockFlaresService
            .Setup(mock => mock.AddFlareRecipients(It.IsAny<List<FlareRecipient>>()))
            .ReturnsAsync(new List<FlareRecipient>());

        var response = await _flaresController.AddFlare(flareDto) as OkObjectResult;

        Assert.AreEqual(flareDto.Id, (response?.Value as FlareDto)?.Id);
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
                BirthDate = new DateTimeOffset().ToUnixTimeSeconds(),
            },
            Latitude = 40.281152,
            Longitude = -111.718095,
            StartTime = 0,
            EndTime = 0,
            Description = "this is a flare",
        };
        FlareDto flareDto = flare.ToDto(new List<FlareRecipient>());

        _mockFlaresService
            .Setup(mock => mock.GetFlare(flare.Id))
            .ReturnsAsync(flare);

        _mockFlaresService
            .Setup(mock => mock.GetFlareRecipients(It.IsAny<Guid>()))
            .ReturnsAsync(new List<FlareRecipient>());

        var response = await _flaresController.GetFlare(flare.Id) as OkObjectResult;

        Assert.AreEqual(flareDto.Id, (response?.Value as FlareDto)?.Id);
    }

    [TestMethod]
    public async Task GetFlare_WhenFlareNotFound_ReturnsNotFound()
    {
        _mockFlaresService
            .Setup(mock => mock.GetFlare(It.IsAny<Guid>()))
            .ReturnsAsync(null as Flare);

        var response = await _flaresController.GetFlare(It.IsAny<Guid>()) as NotFoundObjectResult;

        Assert.AreEqual("A flare with that id was not found.", response?.Value);
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
                BirthDate = new DateTimeOffset().ToUnixTimeSeconds(),
            },
            Latitude = 40.281152,
            Longitude = -111.718095,
            StartTime = 0.0,
            EndTime = 0.0,
            Description = "this is a flare",
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
                BirthDate = new DateTimeOffset().ToUnixTimeSeconds(),
            },
            Latitude = 42,
            Longitude = 42,
            StartTime = 0.0,
            EndTime = 0.0,
            Description = "this is not a flare ;)",
        };
        Flare expectedResponse = flareChange;

        _mockFlaresService
            .Setup(mock => mock.UpdateFlare(flare.Id, flareChange))
            .ReturnsAsync(expectedResponse);

        var response = await _flaresController.UpdateFlare(flare.Id, flareChange) as OkObjectResult;

        Assert.AreEqual(expectedResponse, response?.Value);
    }

    [TestMethod]
    public async Task UpdateFlare_WhereFlareDoesNotExist_ReturnsNotFound()
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
                BirthDate = new DateTimeOffset().ToUnixTimeSeconds(),
            },
            Latitude = 40.281152,
            Longitude = -111.718095,
            StartTime = 0.0,
            EndTime = 0.0,
            Description = "this is a flare",
        };

        Flare? expectedResponse = null;

        _mockFlaresService
            .Setup(mock => mock.UpdateFlare(flare.Id, flare))
            .ReturnsAsync(expectedResponse);

        var response = await _flaresController.UpdateFlare(flare.Id, flare) as OkObjectResult;

        Assert.AreEqual(expectedResponse, response?.Value);
    }

    [TestMethod]
    public async Task GetFlares_WhenCalled_ReturnsListOfFlares()
    {
        _mockJwtHelper
            .Setup(helper => helper.GetUserIdFromToken(It.IsAny<string>()))
            .Returns(Guid.NewGuid());

        _mockFlaresService
            .Setup(mock => mock.GetFlares(It.IsAny<Guid>()))
            .ReturnsAsync(new List<Flare>());

        _mockFlaresService
            .Setup(mock => mock.GetFlareRecipients(It.IsAny<Guid>()))
            .ReturnsAsync(new List<FlareRecipient>());

        var response = await _flaresController.GetFlares() as OkObjectResult;

        Assert.IsInstanceOfType(response?.Value, typeof(List<FlareDto>));
    }

    [TestMethod]
    public async Task DeleteFlare_GivenId_ReturnFlare()
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

        var expectedResponse = new Flare{
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

        _mockFlaresService
            .Setup(mock => mock.DeleteFlare(flare.Id))
            .ReturnsAsync(expectedResponse);

        var response = await _flaresController.DeleteFlare(flare.Id) as OkObjectResult;

        Assert.AreEqual(expectedResponse, response?.Value);
    }
}
