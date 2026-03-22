using API.Modules.Database.Models;
using API.Modules.Database.Services;

using Microsoft.EntityFrameworkCore.ChangeTracking;

namespace API.Modules.Flares.Repositories;

public class FlaresRepository : IFlaresRepository
{
    private readonly DatabaseContext _context;

    public FlaresRepository(DatabaseContext context)
    {
        _context = context;
    }

    public Task<Flare> AddFlare(Flare flare)
    {
        EntityEntry<Flare> createdFlare = _context.Flares.Add(flare);
        _context.SaveChanges();

        return Task.FromResult(createdFlare.Entity);
    }

    public Task<Flare?> GetFlare(Guid id)
    {
        Flare? flare = _context.Flares.Find(id);
        if (flare == null || flare.IsDeleted) {
            return Task.FromResult<Flare?>(null);
        }
        return Task.FromResult(_context.Flares.Find(id));
    }

    public Task<Flare?> UpdateFlare(Guid id, Flare flareUpdate)
    {
        Flare? existingFlare = _context.Flares.Find(id);

        if (existingFlare == null || existingFlare.IsDeleted)
        {
            return Task.FromResult<Flare?>(null);
        }

        existingFlare.Latitude = flareUpdate.Latitude;
        existingFlare.Longitude = flareUpdate.Longitude;
        existingFlare.StartTime = flareUpdate.StartTime;
        existingFlare.EndTime = flareUpdate.EndTime;
        existingFlare.Description = flareUpdate.Description;

        _context.SaveChanges();

        return Task.FromResult(_context.Flares.Find(id));
    }

    public Task<List<FlareRecipient>> AddFlareRecipients(List<FlareRecipient> flareRecipients)
    {
        var createdFlareRecipients = new List<EntityEntry<FlareRecipient>>();

        foreach (FlareRecipient recipient in flareRecipients)
        {
            createdFlareRecipients.Add(_context.FlareRecipients.Add(recipient));
        }

        _context.SaveChanges();

        return Task.FromResult(createdFlareRecipients.Select(fr => fr.Entity).ToList());
    }

    public Task<List<FlareRecipient>> GetFlareRecipients(Guid flareId)
    {
        return Task.FromResult(_context.FlareRecipients.Where(fr => fr.FlareId == flareId && !fr.IsDeleted).ToList());
    }

    public async Task<List<FlareRecipient>> UpdateFlareRecipients(Guid flareId, List<FlareRecipient> flareRecipients){
        //Empty current recipients
        List<FlareRecipient> currentFlareRecipients = await GetFlareRecipients(flareId);
        foreach (FlareRecipient recipient in currentFlareRecipients)
        {
            recipient.IsDeleted = true;
        }

        //Add and return new recipients
        return await AddFlareRecipients(flareRecipients); //This calls _context.saveChanges
    }
    public Task<List<Flare>> GetFlares(Guid id)
    {
        var flareIds = _context.FlareRecipients
            .Where(recipient => recipient.OwnerId == id || recipient.RecipientId == id)
            .Select(recipient => recipient.FlareId)
            .ToList();

        return Task.FromResult(_context.Flares.Where(flare => !flare.IsDeleted && (flareIds.Contains(flare.Id) || flare.OwnerId == id)).ToList());
    }

    public Task<Flare?> DeleteFlare(Guid id)
    {
        Flare? flare = _context.Flares.Find(id);

        if (flare == null)
        {
            return Task.FromResult<Flare?>(null);
        }

        flare.IsDeleted = true;
        _context.SaveChanges();

        return Task.FromResult<Flare?>(flare);
    }

    public Task<FlareRecipient?> DeleteRecipient(Guid flareId, Guid recipientId)
    {
        FlareRecipient? recipient = _context.FlareRecipients
            .Where(fr => fr.FlareId == flareId && !fr.IsDeleted)
            .FirstOrDefault(fr => fr.RecipientId == recipientId);

        if (recipient == null)
        {
            return Task.FromResult<FlareRecipient?>(null);
        }

        recipient.IsDeleted = true;
        _context.SaveChanges();

        return Task.FromResult<FlareRecipient?>(recipient);
    }

    public async Task<Event> AddEvent(Event flareEvent)
    {
        EntityEntry<Event> createdEvent = _context.Events.Add(flareEvent);
        _context.SaveChanges();

        return createdEvent.Entity;
    }
}
