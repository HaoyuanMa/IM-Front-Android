using Microsoft.AspNetCore.Mvc.Filters;
using System;
using System.Net;
using Microsoft.AspNetCore.Mvc;
using IM_Api.Tools;
using IM_Api.Models;
using System.Security.Claims;
using IM_Api.Db;
using Microsoft.AspNetCore.SignalR;
//using System.Web.Mvc;
//using System.Web.Http.Filters;

namespace IM_Api.Filters
{
    [AttributeUsage(AttributeTargets.Method | AttributeTargets.Class)]
    public class AuthAttribute : ActionFilterAttribute, IAuthorizationFilter
    {

        private string Email;

        

        public void OnAuthorization(AuthorizationFilterContext context)
        {
            Microsoft.Extensions.Primitives.StringValues token;

            //context.
           
            if (context.HttpContext.Request.Headers.TryGetValue("JwtToken", out token))
            {
                string email;
                if(JwtTools.DecodeJwtToken(token, out email))
                {
                    Email = email;
                    return;
                }
            }
            context.Result = new StatusCodeResult((int)HttpStatusCode.Unauthorized);
           // throw new HttpResponseException(HttpStatusCode.Unauthorized);
        }

        public override void OnActionExecuting(ActionExecutingContext context)
        {
            context.HttpContext.User = new ClaimsPrincipal(new UserIdentity(Email));
            
            base.OnActionExecuting(context);
        }

    }
}
