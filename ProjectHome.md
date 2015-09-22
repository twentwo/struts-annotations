You can forget about struts-config.xml file and make your Actions annotated with @StrutsAction and @StrutsForward annotations.



# Introduction #
Add annotations support to your Struts 1.x web applications.


# Details #
1)Copy the struts-annotations.jar to your WEB-INF\lib folder.

2)Register the plugin in struts-config.xml
> 

Unknown end tag for &lt;/plug-in&gt;


> > 

&lt;plug-in className="org.easy.struts.action.StrutsAnnotationsPlugin"&gt;



> 

&lt;/plug-in&gt;



3)Have fun using @StrutsAction and @StrutsForward
example :


```
package org.joke.actions.security.user.login;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.easy.struts.anno.StrutsAction;
import org.easy.struts.anno.StrutsForward;
import org.apache.struts.action.Action;
import org.joke.beans.security.user.registration.UserBean;

//if you dont specify the formBean name, if name is null and only form is set then the name is fully qualified name example : org.joke.beans.security.user.registration.UserBean
@StrutsAction(name = "loginForm", form = UserBean.class, path = "/security/user/login/login", cancellable = true, input = "/tiles/user/security/login/form", scope = "request")
public class LoginAction extends Action {

	@StrutsForward(path = "/posts/load.do")
	public static final String AF_SUCCESS = "success";

	@StrutsForward(path = "/tiles/user/security/login/form")
	public static final String AF_NOT_FOUND = "not_found";

	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (isCancelled(request)) {
			return mapping.findForward(AF_SUCCESS);
		}
                //.....
		//check is user on database ....
                //.....
		if ((user != null) && (user.size() > 0)) {

			return mapping.findForward(AF_SUCCESS);
		}
		ActionMessages messages = new ActionMessages();
		messages.add(null, new ActionMessage("security.user.not.found"));
		saveMessages(request, messages);
		return mapping.findForward(AF_NOT_FOUND);
	}
}
```
The UserBean is plain class extending ActionForm or ValidatorForm dont have annotations.


