using IM_Api.Db;
using IM_Api.Filters;
using IM_Api.Models;
using IM_Api.Tools;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Cors;

namespace IM_Api.Controllers
{
    [EnableCors("any")]
    [Route("[controller]")]
    [ApiController]
    public class AccountController : ControllerBase
    {
        private UserManager<ImUser> userManager;
        private SignInManager<ImUser> signinManager;
        private UserDbContext context;

        public AccountController(UserDbContext _context, UserManager<ImUser> _userManager,
            SignInManager<ImUser> _signinManager)
        {
            this.userManager = _userManager;
            this.signinManager = _signinManager;
            this.context = _context;
        }

        [HttpPost("Register")]
        public async Task<ContentResult> Register(RegisterViewModel model)
        {
            if (ModelState.IsValid)
            {
                var user = new ImUser
                {
                    UserName = model.Username,
                    Email = model.Email,
                    Sex = model.Sex,
                    BirthDay = model.BirthDay,
                    Place = model.Place,
                    Classes = new List<Class>(),
                    UnreadMessages = new List<Message>()
                };

                var result = await userManager.CreateAsync(user, model.Password);

                if (result.Succeeded)
                {
                    ImUser curuser = await userManager.FindByEmailAsync(model.Email);

                    Class initialclass = new Class
                    {
                        Id = Guid.NewGuid().ToString(),
                        OwnerId = curuser.Id,
                        Name = "My Friends",
                        Num = 0,
                        Friends = new List<ImUser>()
                    };

                    context.Classes.Add(initialclass);

                    // context.Entry(initialclass).State = EntityState.Modified;

                    try
                    {
                        await context.SaveChangesAsync();

                    }
                    catch (DbUpdateConcurrencyException)
                    {
                        throw;
                    }



                    return Content("ok");
                }
                else
                {
                    return Content("err1");
                }
            }
            else
            {
                return Content("err2");
            }
        }

        [HttpPost("Login")]
        public async Task<ContentResult> Login(LoginViewModel model)
        {

            if (ModelState.IsValid)
            {
                var user = await userManager.FindByEmailAsync(model.Email);

                var result = userManager.PasswordHasher.VerifyHashedPassword(user, user.PasswordHash, model.Password);


                //var res = await signinManager.PasswordSignInAsync(user, model.Password, false, false);


                if (result == PasswordVerificationResult.Success)
                //if(res == Microsoft.AspNetCore.Identity.SignInResult.Success)
                {
                    if(model.Client == "web")
                    {
                        user.IsWebOnline = true;
                    }
                    else if(model.Client == "android")
                    {
                        user.IsAndroidOnline = true;
                    }
                    else
                    {
                        user.IsPcOnline = true;
                    }

                    try
                    {
                        await context.SaveChangesAsync();
                    }
                    catch (DbUpdateConcurrencyException)
                    {
                        throw;
                    }

                    

                    return Content(JwtTools.GenerateJwtToken(model.Email, "web"));
                }
                else
                {
                    return Content("err1");
                }
            }
            else
            {
                return Content("err2");
            }
        }

        [HttpGet("Logout")]
        [Authorize]
        public async Task<ContentResult> Logout(string client)
        {
            var curuser = await userManager.FindByEmailAsync(User.Identity.Name);
            if (client == "web")
            {
                curuser.IsWebOnline = false;
            }
            else if(client == "android")
            {
                curuser.IsAndroidOnline = false;
            }
            else
            {
                curuser.IsPcOnline = false;
            }
            try
            {
                await context.SaveChangesAsync();
                return Content("ok");
            }
            catch (DbUpdateConcurrencyException)
            {
                throw;
                return Content("err");
            }
        }
    }
}

