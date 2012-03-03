package org.easy.struts.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD })
public @interface Forward {
	/**
	 * Module-relative or context-relative URI to which control should be
	 * forwarded, or an absolute or relative URI to which control should be
	 * redirected
	 */
	String path();
	
	/**
	 * Set to true if the controller servlet should call
	 * HttpServletResponse.sendRedirect() on the associated path; otherwise
	 * false
	 */
	boolean redirect() default false;
}
