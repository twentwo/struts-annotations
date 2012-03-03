package org.easy.struts.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.struts.action.ActionForm;

/**
 * Describes an ActionMapping object that is to be used to process a request for
 * a specific module-relative URI.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Config {
	
	/**
	 * The module-relative path of the submitted request, starting with a "/"
	 * character, and without the filename extension if extension mapping is
	 * used.
	 */
	String path() default "";
	
	/**
	 * Class of the form bean, if any, that is associated with this action
	 * mapping.
	 */
	Class<? extends ActionForm> form() default ActionForm.class;
	
	/**
	 * Name of the form bean, if any, that is associated with this action
	 * mapping.
	 */
	String name() default "";
	
	/**
	 * The context ("request" or "session") that is used to access our
	 * ActionForm bean, if any. Optional if "name" is specified, else not valid.
	 */
	String scope() default "request";
	
	/**
	 * Set to {@code true} if the validate method of the ActionForm bean should
	 * be called prior to calling the Action object for this action mapping, or
	 * set to {@code false} if you do not want the validate method called.
	 */
	boolean validate() default true;
	
	/**
	 * Module-relative path of the action or other resource to which control
	 * should be returned if a validation error is encountered. Valid only when
	 * "name" is specified. Required if "name" is specified and the input bean
	 * returns validation errors. Optional if "name" is specified and the input
	 * bean does not return validation errors.
	 */
	String input() default "";
	
	/**
	 * Set to {@code true} if the Actio} can be cancelled. By default, when an
	 * Action is cancelled, validation is bypassed and the Action should not
	 * execute the business operation. If a request tries to cancel an Action
	 * when cancellable is not set, a {@code InvalidCancelException} is thrown.
	 * Default value is {@code false}
	 */
	boolean cancellable() default false;
	
	/**
	 * General-purpose configuration parameter that can be used to pass extra
	 * information to the Action object selected by this action mapping. For
	 * example {@code DispatchAction} is using this property
	 */
	String parameter() default "";
	
}
