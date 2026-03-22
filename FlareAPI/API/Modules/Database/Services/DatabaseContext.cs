using API.Modules.Database.Configurations;
using API.Modules.Database.Models;

using Microsoft.EntityFrameworkCore;

namespace API.Modules.Database.Services;

public class DatabaseContext : DbContext
{
    public virtual DbSet<User> Users { get; set; }
    public virtual DbSet<Friend> Friends { get; set; }
    public virtual DbSet<Flare> Flares { get; set; }
    public virtual DbSet<FlareRecipient> FlareRecipients { get; set; }
    public virtual DbSet<Event> Events { get; set; }

    public DatabaseContext() { } // Parameterless constructor for mocking.

    public DatabaseContext(DbContextOptions<DatabaseContext> options) : base(options)
    {

    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        new EventConfiguration().Configure(modelBuilder.Entity<Event>());
        new FlareConfiguration().Configure(modelBuilder.Entity<Flare>());
        new FlareRecipientConfiguration().Configure(modelBuilder.Entity<FlareRecipient>());
        new FriendConfiguration().Configure(modelBuilder.Entity<Friend>());
        new UserConfiguration().Configure(modelBuilder.Entity<User>());
    }
}
