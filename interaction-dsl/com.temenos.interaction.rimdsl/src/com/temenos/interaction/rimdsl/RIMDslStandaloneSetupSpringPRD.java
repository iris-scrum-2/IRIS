/*
* generated by Xtext
*/
package com.temenos.interaction.rimdsl;

import org.eclipse.xtext.generator.IGenerator;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.temenos.interaction.rimdsl.generator.RIMDslGeneratorSpringPRD;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class RIMDslStandaloneSetupSpringPRD extends RIMDslStandaloneSetup{

	public static void doSetup() {
		new RIMDslStandaloneSetupSpringPRD().createInjectorAndDoEMFRegistration();
	}
	
	public Injector createInjector() {
		return Guice.createInjector(new com.temenos.interaction.rimdsl.RIMDslRuntimeModule() {
			
			@Override
			public Class<? extends IGenerator> bindIGenerator() {
				return RIMDslGeneratorSpringPRD.class;
			}
			
		});
	}
}

