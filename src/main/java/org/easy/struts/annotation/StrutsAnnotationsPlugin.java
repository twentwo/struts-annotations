package org.easy.struts.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;

/**
 * Struts plugin that enables use of {@link Config} and {@link Forward}
 * annotations to configuring a Struts action instead of struts-config.xml
 */
public abstract class StrutsAnnotationsPlugin implements PlugIn {

	protected static final Log LOG = LogFactory.getLog(StrutsAnnotationsPlugin.class);

	public static final String PLUGIN_ID = "StrutsAnnotationsPlugin";

	private int formsAddedCount, actionsAddedCount;

	public void destroy() {
		// Nothing to destroy
	}

	public void init(ActionServlet servlet, ModuleConfig config) throws ServletException {
		LOG.debug("Initializing StrutsAnnotationsPlugin");
		long initializedIn = System.currentTimeMillis();

		Collection<Class<? extends Action>> actionClasses = defineActionClasses(servlet.getServletContext());
		for (Class<? extends Action> actionClass : actionClasses) {
			addActionConfig(actionClass, config);
		}

		initializedIn = System.currentTimeMillis() - initializedIn;
		LOG.info(MessageFormat.format("[{0}] actions and [{1}] forms has been added in {2}ms",
				actionsAddedCount, formsAddedCount, initializedIn));
	}

	/**
	 * Tries to define all action classes in this class path under the provided
	 * root package
	 * 
	 * @param context
	 * 			the servlet context 
	 * @return a list with all found action classes
	 */
	protected abstract Collection<Class<? extends Action>> defineActionClasses(ServletContext context);

	private void addActionConfig(Class<? extends Action> actionClass, ModuleConfig moduleConfig) {
		Config actionMapping = actionClass.getAnnotation(Config.class);
		if (actionMapping == null) {
			return;
		}

		ActionConfig actionConfig = createActionConfig(actionClass, actionMapping);
		actionConfig.setName(findOrAddFormConfig(actionMapping, moduleConfig));
		addActionForwards(actionClass, actionConfig);
		moduleConfig.addActionConfig(actionConfig);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Added Struts action: " + actionConfig);
		}
		actionsAddedCount++;
	}

	private ActionConfig createActionConfig(Class<? extends Action> acctionClass, Config actionMapping) {
		// Struts throws ClassCastException if ActionConfig class is used
		ActionConfig actionConfig = new org.apache.struts.action.ActionMapping();
		actionConfig.setPath(actionMapping.path());
		actionConfig.setType(StringUtils.trimToNull(acctionClass.getName()));
		// input is optional with default value an empty string
		actionConfig.setInput(StringUtils.trimToNull(actionMapping.input()));
		actionConfig.setScope(actionMapping.scope());
		actionConfig.setValidate(actionMapping.validate());
		actionConfig.setCancellable(actionMapping.cancellable());
		actionConfig.setParameter(StringUtils.trimToNull(actionMapping.parameter()));
		return actionConfig;
	}

	private String findOrAddFormConfig(Config acctionMapping, ModuleConfig moduleConfig) {
		String formName = acctionMapping.name();
		Class<? extends ActionForm> formClass = acctionMapping.form();

		// Form name + form definition in struts-config.xml
		if (isSet(formName) && !isSet(formClass)) {
			FormBeanConfig formConfig = moduleConfig.findFormBeanConfig(formName);
			if (formConfig == null) {
				throw new IllegalArgumentException(MessageFormat.format(
						"Failed to register form with name [{0}]. Form class has to be provided "
								+ "or a form with such name has to be defined in struts-config.xml", formName));
			}

			return formName;
		}

		// Form name + form class
		if (isSet(formName) && isSet(formClass)) {
			FormBeanConfig formConfig = moduleConfig.findFormBeanConfig(formName);
			if (formConfig != null) {
				if (formClass.getName().equals(formConfig.getType())) {
					return formName;
				} else {
					throw new IllegalStateException(MessageFormat.format(
							"Failed to register form with name [{0}] and type [{1}]. Form with the "
									+ "same name but different type [{2}] already exists in struts-config.xml",
							formName, formClass.getName(), formConfig.getType()));
				}
			}

			formConfig = createFormConfig(formName, formClass);
			moduleConfig.addFormBeanConfig(formConfig);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Added Struts form: " + formConfig);
			}
			formsAddedCount++;
			return formName;
		}

		// Form class only
		if (!isSet(formName) && isSet(formClass)) {
			formName = formClass.getSimpleName();
			FormBeanConfig formConfig = createFormConfig(formName, formClass);
			moduleConfig.addFormBeanConfig(formConfig);
			formsAddedCount++;
			return WordUtils.uncapitalize(formName);
		}

		return null;
	}

	private FormBeanConfig createFormConfig(String formName, Class<? extends ActionForm> formClass) {
		FormBeanConfig formConfig = new FormBeanConfig();
		formConfig.setName(formName);
		formConfig.setType(formClass.getName());
		return formConfig;
	}

	private void addActionForwards(Class<?> clazz, ActionConfig actionConfig) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Forward actionForward = field.getAnnotation(Forward.class);
			if (actionForward == null) {
				continue;
			}

			// Only final string filed can be used as local forwards
			if (!String.class.equals(field.getType())
					|| !(Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()))) {
				continue;
			}

			ForwardConfig forwardConfig = new org.apache.struts.action.ActionForward();
			// Read of private field may cause IllegalAccessException if the JVM
			// is started with Security Manager enabled
			field.setAccessible(true);
			try {
				forwardConfig.setName((String) field.get(null));
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(MessageFormat.format("Can't read filed [{0}] value", field), e);
			}
			forwardConfig.setPath(actionForward.path());
			forwardConfig.setRedirect(actionForward.redirect());
			actionConfig.addForwardConfig(forwardConfig);
		}
	}

	private static boolean isSet(String string) {
		return string != null && string.length() > 0;
	}

	private static boolean isSet(Class<?> clazz) {
		return !ActionForm.class.equals(clazz);
	}
}
