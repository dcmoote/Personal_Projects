using API.Modules.Database.Extensions;
using API.Modules.Database.Models;
using API.Modules.Flares.Dtos;
using API.Modules.Flares.Services;
using API.Utils;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers;

[Authorize]
[ApiController]
[Route(Constants.Resources.V1.Flares)]
public class FlaresController : ControllerBase
{
    private readonly IFlaresService _flareService;
    private readonly IJwtHelper _jwtHelper;

    public FlaresController(IFlaresService flaresService, IJwtHelper jwtHelper)
    {
        _flareService = flaresService;
        _jwtHelper = jwtHelper;
    }

    [ProducesResponseType(typeof(FlareDto), StatusCodes.Status200OK)]
    [HttpPost]
    public async Task<IActionResult> AddFlare([FromBody] FlareDto flare)
    {
        string jwt = HttpContext.Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
        Guid userId = _jwtHelper.GetUserIdFromToken(jwt);
        Flare flareModel = flare.ToModel();
        var recipients = flare.Recipients.Select(r => r.ToModel(userId)).ToList();

        if (flareModel.OwnerId == Guid.Empty)
        {
            flareModel.OwnerId = userId;
        }

        Flare newFlare = await _flareService.AddFlare(flareModel);
        List<FlareRecipient> newRecipients = await _flareService.AddFlareRecipients(recipients);

        return Ok(newFlare.ToDto(newRecipients));
    }

    [ProducesResponseType(typeof(FlareDto), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status404NotFound)]
    [HttpGet("{id}")]
    public async Task<IActionResult> GetFlare(Guid id)
    {
        Flare? flare = await _flareService.GetFlare(id);

        if (flare == null)
        {
            return NotFound("A flare with that id was not found.");
        }

        List<FlareRecipient> recipients = await _flareService.GetFlareRecipients(flare.Id);

        return Ok(flare.ToDto(recipients));
    }

    [ProducesResponseType(typeof(Flare), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status404NotFound)]
    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateFlare(Guid id, [FromBody] Flare flareUpdate)
    {
        Flare? result = await _flareService.UpdateFlare(id, flareUpdate);

        if (result == null)
        {
            return NotFound("A flare with that id was not found.");
        }

        return Ok(result);
    }

    [ProducesResponseType(typeof(List<FlareDto>), StatusCodes.Status200OK)]
    [HttpGet()]
    public async Task<IActionResult> GetFlares()
    {
        string jwt = HttpContext.Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
        Guid userId = _jwtHelper.GetUserIdFromToken(jwt);
        List<Flare> flares = await _flareService.GetFlares(userId);
        var flareDtos = new List<FlareDto>();

        foreach (Flare flare in flares)
        {
            List<FlareRecipient> recipients = await _flareService.GetFlareRecipients(flare.Id);
            flareDtos.Add(flare.ToDto(recipients));
        }

        return Ok(flareDtos);
    }

    [ProducesResponseType(typeof(List<FlareRecipientDto>), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status404NotFound)]
    [HttpPost("{id}/recipients/{rid}")]
    public async Task<IActionResult> UpdateFlareRecipients(Guid id, Guid rid, [FromBody] List<FlareRecipientDto> recipients)
    {
        Flare? flare = await _flareService.GetFlare(id);
        if (flare == null)
        {
            return NotFound("A flare with that id was not found.");
        }
        
        var recipientModels = recipients.Select(r => r.ToModel(flare.OwnerId)).ToList();
        List<FlareRecipient> updatedRecipients = await _flareService.UpdateFlareRecipients(id, recipientModels);

        return Ok(updatedRecipients.Select(r => r.ToDto()).ToList());
    }

    [ProducesResponseType(typeof(Flare), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status404NotFound)]
    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteFlare(Guid id)
    {
        Flare? result = await _flareService.DeleteFlare(id);

        if (result == null)
        {
            return NotFound("A flare with that id could not be found.");
        }

        return Ok(result);
    }

    [ProducesResponseType(typeof(Flare), StatusCodes.Status200OK)]
    [ProducesResponseType(typeof(string), StatusCodes.Status404NotFound)]
    [HttpDelete("{id}/recipients/{rid}")]
    public async Task<IActionResult> DeleteRecipient(Guid id, Guid rid)
    {
        FlareRecipient? recipient = await _flareService.DeleteRecipient(id, rid);
        if (recipient == null)
        {
            return NotFound("A recipient with that id could not be found.");
        }

        return Ok(recipient);
    }

    [ProducesResponseType(typeof(EventDto), StatusCodes.Status200OK)]
    [HttpPost("events")]
    public async Task<IActionResult> AddEvent([FromBody] EventDto flareEvent)
    {
        string jwt = HttpContext.Request.Headers["Authorization"].ToString().Replace("Bearer ", "");
        Guid userId = _jwtHelper.GetUserIdFromToken(jwt);
        Event eventModel = flareEvent.ToModel();

        if (eventModel.Flare.OwnerId == Guid.Empty)
        {
            eventModel.Flare.OwnerId = userId;
        }

        Event newEvent = await _flareService.AddEvent(eventModel);

        return Ok(newEvent.ToDto(newEvent.Flare.Recipients));
    }
}
