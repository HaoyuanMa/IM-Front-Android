using IM_Api.Models;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace IM_Api.Db
{
    public class UserDbContext : IdentityDbContext
    {
        public UserDbContext(DbContextOptions<UserDbContext> options) : base(options)
        {

        }

        public DbSet<ImUser> Users { get; set; }

        public DbSet<Group> Groups { get; set; }

        public DbSet<Class> Classes { get; set; }

        public DbSet<Message> Messages { get; set; }

        public DbSet<Email> LoginUsers { get; set; }


    }
}
