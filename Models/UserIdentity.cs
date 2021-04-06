using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Security.Principal;
using System.Threading.Tasks;

namespace IM_Api.Models
{
    public class UserIdentity : IIdentity
    {
        
        public UserIdentity(string name)
        {
            Name = name;
        }
        
        public string AuthenticationType { get; }
        public bool IsAuthenticated { get; }
        public string Name { get; }
    }

    public class ApiUser : IPrincipal
    {
       
        public ApiUser(string name)
        {
            Identity = new UserIdentity(name);
        }

        public IIdentity Identity { get; }

        public bool IsInRole(string role)
        {
            throw new NotImplementedException();
        }

        
    }
}
