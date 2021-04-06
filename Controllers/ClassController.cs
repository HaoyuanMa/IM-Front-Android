using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using IM_Api.Db;
using IM_Api.Models;
using IM_Api.Filters;
using Microsoft.AspNetCore.Authorization;

namespace IM_Api.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class ClassController : ControllerBase
    {
        private readonly UserDbContext _context;

        public ClassController(UserDbContext context)
        {
            _context = context;
        }       

        
        [HttpGet]
        [Authorize]
        public async Task<ActionResult<List<Class>>> GetClass()
        {      
            ImUser curuser = _context.Users.Where(r => r.Email == User.Identity.Name).FirstOrDefault();

            if (curuser == null)
            {
                return NotFound();
            }

            var classes = _context.Classes.Where(r => r.OwnerId == curuser.Id).ToList();

            return classes;
        }

        [HttpPut("{classname}")]
        [Authorize]
        public async Task<IActionResult> PutClass(string classname)
        {

            ImUser curuser = _context.Users.Where(r => r.Email == User.Identity.Name).FirstOrDefault();

            Class newclass = new Class
            {
                Id = Guid.NewGuid().ToString(),
                OwnerId = curuser.Id,
                Name = classname,
                Num = 0,
                Friends = new List<ImUser>()
            };

            _context.Classes.Add(newclass);


            try
            {
                await _context.SaveChangesAsync();
            }
            catch (DbUpdateConcurrencyException)
            {
                throw;
            }

            return Content("ok");
        }

        [HttpGet("Rename")]
        [Authorize]
        public async Task<ActionResult<Class>> Rename(RenameViewModel model)
        {
            Class curclass = await _context.Classes.FindAsync(model.Id);

            curclass.Name = model.NewName;

            await _context.SaveChangesAsync();

            return Content("ok");
        }

        
        [HttpDelete("{id}")]
        [Authorize]
        public async Task<IActionResult> DeleteClass(string id)
        {
            var @class = await _context.Classes.FindAsync(id);
            if (@class == null)
            {
                return NotFound();
            }

            _context.Classes.Remove(@class);
            await _context.SaveChangesAsync();

            return Content("ok");
        }
      

        private bool ClassExists(string id)
        {
            return _context.Classes.Any(e => e.Id == id);
        }
    }
}
