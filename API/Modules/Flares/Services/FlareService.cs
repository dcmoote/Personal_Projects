using API.Modules.Database.Models;
using API.Modules.Flares.Repositories;

using Microsoft.AspNetCore.Http.HttpResults;

namespace API.Modules.Flares.Services;

public class FlaresService : IFlaresService
{
    private readonly IFlaresRepository _flaresRepository;

    public FlaresService(IFlaresRepository flaresRepository)
    {
        _flaresRepository = flaresRepository;
    }

    public Task<Flare> AddFlare(Flare flare)
    {
        return _flaresRepository.AddFlare(flare);
    }

    public Task<Flare?> GetFlare(Guid id)
    {
        return _flaresRepository.GetFlare(id);
    }

    public Task<Flare?> UpdateFlare(Guid id, Flare flareUpdate)
    {
        return _flaresRepository.UpdateFlare(id, flareUpdate);
    }

    public Task<List<FlareRecipient>> AddFlareRecipients(List<FlareRecipient> flareRecipients)
    {

        return _flaresRepository.AddFlareRecipients(flareRecipients);
    }

    public Task<List<FlareRecipient>> UpdateFlareRecipients(Guid flareId, List<FlareRecipient> flareRecipients)
    {

        return _flaresRepository.UpdateFlareRecipients(flareId, flareRecipients);
    }

    public Task<List<FlareRecipient>> GetFlareRecipients(Guid flareId)
    {

        return _flaresRepository.GetFlareRecipients(flareId);
    }

    public Task<List<Flare>> GetFlares(Guid id)
    {
        return _flaresRepository.GetFlares(id);
    }

    public Task<Flare?> DeleteFlare(Guid id)
    {
        return _flaresRepository.DeleteFlare(id);
    }

    public Task<FlareRecipient?> DeleteRecipient(Guid flareId, Guid recipientId)
    {
        return _flaresRepository.DeleteRecipient(flareId, recipientId);
    }

    public Task<Event> AddEvent(Event flareEvent)
    {
        return _flaresRepository.AddEvent(flareEvent);
    }
}
