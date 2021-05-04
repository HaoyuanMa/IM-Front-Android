using IM_Api.Db;
using IM_Api.Hubs;
using IM_Api.Tools;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.IdentityModel.Tokens;
using System.Text;
using System.Threading.Tasks;

namespace IM_Api
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddDbContextPool<UserDbContext>(
             options => options.UseSqlServer(Configuration["ConnectionStrings"]));

            services.AddIdentity<IdentityUser, IdentityRole>().AddEntityFrameworkStores<UserDbContext>();

            services.Configure<IdentityOptions>(options =>
            {
                options.ClaimsIdentity.UserNameClaimType = "name";
            });

            services.AddAuthentication(x =>
            {
                x.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
                x.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
            }).AddJwtBearer(
                  options =>
                  {
                      options.MapInboundClaims = true;

                      options.TokenValidationParameters = new TokenValidationParameters()
                      {
                          ValidateIssuer = true,
                          ValidateAudience = false,
                          ValidateLifetime = true,
                          ValidateIssuerSigningKey = true,
                          IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("SKDJF5D4SG65WE8SD56G4A8SD5FWE5")),
                          ValidIssuer = "mahaoyuan",
                          NameClaimType = "name"
                      };
                      //options.Authority = "/Hubs/Message";
                      options.Events = new JwtBearerEvents
                      {
                          OnMessageReceived = context =>
                          {
                              var accessToken = context.Request.Query["access_token"];

                              // If the request is for our hub...
                              var path = context.HttpContext.Request.Path;
                              if (!string.IsNullOrEmpty(accessToken) &&
                                  (path.StartsWithSegments("/Hubs/MessageHub")))
                              {
                                  // Read the token out of the query string
                                  context.Token = accessToken;
                              }
                              return Task.CompletedTask;
                          },
                         /* OnTokenValidated = context =>
                          {

                          }*/

                          
                      };

                  }
                  );
            services.AddCors(option => option.AddPolicy("cors", policy => policy.AllowAnyHeader().AllowAnyMethod().AllowCredentials().WithOrigins(new []{ "http://localhost:8080" })));

            /*services.AddCors(options => {
                options.AddPolicy("any", builder => { builder.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader(); });
            });*/


            services.AddAuthorization();

            services.AddSignalR(options=>
            {
                //options.AddFilter<AuthAttribute>();
                options.MaximumReceiveMessageSize = 200 * 1024;
            });
            services.AddSingleton<IUserIdProvider, NameUserIdProvider>();


            services.AddControllers();
            
           // services.AddMvc(a => { a.Filters.Add<AuthAttribute>()});
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            
            app.UseStaticFiles(new StaticFileOptions { 
                OnPrepareResponse = c =>
                {
                    c.Context.Response.Headers.Add("Access-Control-Allow-Origin", "http://localhost:8080");
                }
            });

            app.UseRouting();

            app.UseCors("cors");

            app.UseAuthentication();
            app.UseAuthorization();
            

            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
                endpoints.MapHub<MessageHub>("/Hubs/MessageHub");
            });
        }
    }
}
