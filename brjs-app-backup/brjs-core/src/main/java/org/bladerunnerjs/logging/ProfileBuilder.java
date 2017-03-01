package org.bladerunnerjs.logging;

import java.util.Map;

import org.bladerunnerjs.logger.LogLevel;

public class ProfileBuilder {
	private final Map<String, LogLevel> logProfile;
	
	public ProfileBuilder(Map<String, LogLevel> logProfile) {
		this.logProfile = logProfile;
	}
	
	public ProfilePackageBuilder pkg(String packageName) {
		return new ProfilePackageBuilder(this, packageName);
	}
	
	public class ProfilePackageBuilder {
		private final String packageName;
		private final ProfileBuilder profileBuilder;
		
		public ProfilePackageBuilder(ProfileBuilder profileBuilder, String packageName) {
			this.profileBuilder = profileBuilder;
			this.packageName = packageName;
		}
		
		public ProfileBuilder logsAt(LogLevel logLevel) {
			logProfile.put(packageName, logLevel);
			return profileBuilder;
		}
	}
}
