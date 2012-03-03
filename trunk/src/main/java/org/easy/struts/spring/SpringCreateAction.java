package org.easy.struts.spring;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.chain.Constants;
import org.apache.struts.chain.commands.AbstractCreateAction;
import org.apache.struts.chain.contexts.ActionContext;
import org.apache.struts.chain.contexts.ServletActionContext;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ModuleConfig;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringCreateAction extends AbstractCreateAction {
	/**
	 * Locally stored web application context
	 */
	private WebApplicationContext springContext;

	@Override
	protected Action getAction(ActionContext context, String type, ActionConfig actionConfig) throws Exception {

		String actionPath = actionConfig.getPath();
		Map<String, Action> actions = getCachedActions(context, actionConfig.getModuleConfig());

		// Copy & paste from the Struts original CreateAction
		// May be it could be improved using
		// java.util.concurrent.ConcurrentHashMap
		Action action = null;
		synchronized (actions) {
			action = (Action) actions.get(actionPath);
			if (action == null) {
				action = lookUpAction(context, actionConfig);
				actions.put(actionPath, action);
			}
		}

		return action;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Action> getCachedActions(ActionContext context, ModuleConfig moduleConfig) {
		String actionsKey = Constants.ACTIONS_KEY + moduleConfig.getPrefix();
		Map<String, Action> actions = (Map<String, Action>) context.getApplicationScope().get(actionsKey);
		if (actions == null) {
			actions = new HashMap<String, Action>();
			context.getApplicationScope().put(actionsKey, actions);
		}

		return actions;
	}

	private WebApplicationContext getContext(ActionContext context) {
		// No need of synchronization. It isn't critical if the first few
		// requests
		// don't use the locally stored context, but get it from the Servlet
		// context
		if (springContext != null) {
			return springContext;
		}

		ActionServlet actionServlet = ((ServletActionContext) context).getActionServlet();
		springContext = WebApplicationContextUtils.getWebApplicationContext(actionServlet.getServletContext());
		if (springContext instanceof ConfigurableWebApplicationContext) {
			((ConfigurableWebApplicationContext) springContext).getBeanFactory()
				.ignoreDependencyType(ActionServlet.class);
		}

		return springContext;
	}

	private Action lookUpAction(ActionContext context, ActionConfig actionConfig) {
		// {@link StrutsActionBeanNameGenerator} names the beans after the
		// action path
		// The same applies to XDoclet generated struts-config-context.xml
		WebApplicationContext webAppSpringContext = getContext(context);
		Action action = (Action) webAppSpringContext.getBean(actionConfig.getPath());
		if (action != null) {
			action.setServlet(((ServletActionContext) context).getActionServlet());
			return action;
		}

		// No fallback search by type, because it may hide missconfiguration
		throw new IllegalStateException(MessageFormat.format(
				"Can't find Spring bean with name [{0}] of type [{1}]",
				actionConfig.getPath(), actionConfig.getType()));
	}
}