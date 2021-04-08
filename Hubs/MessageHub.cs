using IM_Api.Db;
using IM_Api.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.SignalR;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace IM_Api.Hubs
{

    //[EnableCors("any")]
    [Authorize]
    public class MessageHub : Hub
    {

        private UserManager<ImUser> userManager;
        
        private UserDbContext dbcontext;


        public MessageHub(UserDbContext _context, UserManager<ImUser> _userManager)
        {
            this.userManager = _userManager;
            this.dbcontext = _context;
        }

        public override Task OnConnectedAsync()
        {
            string email = Context.User.Identity.Name;
            List<string> curuser = new List<string>();
            curuser.Add(email);
            Clients.All.SendAsync("GetUsers", curuser);

            List<Email> users = dbcontext.LoginUsers.ToList();
            List<string> emails = new List<string>();
            foreach(Email u in users)
            {
                emails.Add(u.UserEmail);
            }
            Clients.Caller.SendAsync("GetUsers", emails);

            if(dbcontext.LoginUsers.Find(email)==null)
            {
                dbcontext.LoginUsers.Add(new Email { UserEmail = email });
                dbcontext.SaveChanges();
            }
                
            return base.OnConnectedAsync();
        }

 

        public override Task OnDisconnectedAsync(Exception exception)
        {
            Clients.All.SendAsync("RemoveUser", Context.User.Identity.Name);
            Email user = dbcontext.Find<Email>(Context.User.Identity.Name);
            dbcontext.LoginUsers.Remove(user);
            dbcontext.SaveChangesAsync();
            Groups.RemoveFromGroupAsync(Context.ConnectionId, "mygroup");
            return base.OnDisconnectedAsync(exception);
        }

        public async Task JoinChatroom(string user)
        {
            
            await Groups.AddToGroupAsync(Context.ConnectionId, "mygroup");
                //Clients.Group("mygroup").SendAsync("GetChatroomUser", Context.User.Identity.Name);
        }

        public async Task RemoveFromChatroom(string user)
        {
            
            await Groups.RemoveFromGroupAsync(Context.ConnectionId, "mygroup");
                //Clients.Group("mygroup").SendAsync("RemoveChatroomUser", Context.User.Identity.Name);
        }

        public Task SendMessage(Message message)
        {
            //var target = userManager.FindByEmailAsync(message.To);
            string name = Context.User.Identity.Name;
            //Clients.All.SendAsync("ReceiveMessage", message);
            return Clients.User(message.To).SendAsync("ReceiveMessage", message);
        }

        public Task SendBroadcast(Broadcast broadcast)
        {

            return Clients.Users(broadcast.To).SendAsync("ReceiveMessage", broadcast);
        }

        public Task SendChatroom(Message message)
        {
            return Clients.Group("mygroup").SendAsync("ReceiveMessage", message);
        }
    }
}
