#!/usr/bin/ruby

# We may need nexus to be available in order to build...
exit 1 unless system( "mvn clean install" )

version = "1.1-SNAPSHOT"
plugins = ["nexus-audit-plugin"]

exit 5 unless system( "~/apps/nexus/current/bin/jsw/macosx-universal-32/nexus stop" )

plugins.each do |plugin|
  exit 2 unless system( "rm -rf ~/apps/nexus/sonatype-work/nexus/plugin-repository/#{plugin}-#{version}" )
end

plugins.each do |plugin|
	exit 3 unless system( "unzip #{plugin}/target/#{plugin}-#{version}-bundle.zip -d ~/apps/nexus/sonatype-work/nexus/plugin-repository" )
end

exit 4 unless system( "~/apps/nexus/current/bin/jsw/macosx-universal-32/nexus start" )

system( "tail -f ~/apps/nexus/current/logs/wrapper.log" )
