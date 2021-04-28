using IM_Api.Db;
using IM_Api.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.SignalR;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace IM_Api.Hubs
{

    //[EnableCors("any")]
    [Authorize]
    public class MessageHub : Hub
    {

        private UserManager<IdentityUser> userManager;
        
        private UserDbContext dbcontext;


        public MessageHub(UserDbContext _context, UserManager<IdentityUser> _userManager)
        {
            this.userManager = _userManager;
            this.dbcontext = _context;
        }

        public override Task OnConnectedAsync()
        {
                
            return base.OnConnectedAsync();
        }

 

        public override Task OnDisconnectedAsync(Exception exception)
        {
            var email = Context.User.Identity.Name;
            Clients.All.SendAsync("RemoveUser", email);
            if (UsersList.ChatUsers.Contains(email))
            {
                UsersList.ChatUsers.Remove(email);
            }
            if (UsersList.BroadcastUsers.Contains(email))
            {
                UsersList.BroadcastUsers.Remove(email);
            }
            if (UsersList.ChatRoomUsers.Contains(email))
            {
                UsersList.ChatRoomUsers.Remove(email);
            }
            Groups.RemoveFromGroupAsync(Context.ConnectionId, "mygroup");
            return base.OnDisconnectedAsync(exception);
        }

        public async Task SetOnline(string type)
        {
            string email = Context.User.Identity.Name;

            List<string> curuser = new List<string>();
            curuser.Add(email);


            if (type == "chat")
            {
                List<string> users = UsersList.ChatUsers;
                
                if (!UsersList.ChatUsers.Contains(email))
                {
                    await Clients.All.SendAsync("GetChatUsers", curuser);
                    await Clients.Caller.SendAsync("GetChatUsers", users);
                    UsersList.ChatUsers.Add(email);
                }
            }
            else if(type == "broadcast")
            {
                List<string> users = UsersList.BroadcastUsers;
               
                if (!UsersList.BroadcastUsers.Contains(email))
                {
                    await Clients.All.SendAsync("GetBroadcastUsers", curuser);
                    await Clients.Caller.SendAsync("GetBroadcastUsers", users);
                    UsersList.BroadcastUsers.Add(email);
                }
                
            }
            else
            {
                List<string> users = UsersList.ChatRoomUsers;
                
                if (!UsersList.ChatRoomUsers.Contains(email))
                {
                    await Clients.All.SendAsync("GetChatRoomUsers", curuser);
                    await Clients.Caller.SendAsync("GetChatRoomUsers", users);
                    UsersList.ChatRoomUsers.Add(email);
                }
                await Groups.AddToGroupAsync(Context.ConnectionId, "mygroup");
            }         
        }


        public Task SendMessage(Message message)
         {
            //var target = userManager.FindByEmailAsync(message.To);
            string name = Context.User.Identity.Name;
            //Clients.All.SendAsync("ReceiveMessage", message);
            if(message.Type == "chatroom")
                return Clients.Group("mygroup").SendAsync("ReceiveMessage", message);
            else
                return Clients.Users(message.To).SendAsync("ReceiveMessage", message);
        }

    }
}
